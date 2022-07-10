package ru.apmgor.forecast.data

import android.os.SystemClock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.apmgor.forecast.data.adapters.*
import ru.apmgor.forecast.data.database.*
import ru.apmgor.forecast.data.database.units.UnitsEntity
import ru.apmgor.forecast.data.errors.Errors
import ru.apmgor.forecast.data.forecastprovider.ForecastRet
import ru.apmgor.forecast.data.forecastprovider.ForecastProvider
import ru.apmgor.forecast.data.locationprovider.LocationProvider
import ru.apmgor.forecast.data.locationprovider.LocationInternet
import ru.apmgor.forecast.data.models.City
import ru.apmgor.forecast.data.models.Coordinates
import ru.apmgor.forecast.data.models.Units
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ForecastRepoImpl @Inject constructor(
    private val locationProvider: LocationProvider,
    private val forecastProvider: ForecastProvider,
    private val database: ForecastDatabase,
    private val dispatcher: CoroutineDispatcher
) : ForecastRepo {

    //-------------------------------------------------------------------------------------------------------------------------
    // Сохраняем и получаем данные для экрана настроек
    override fun getUnitsFromDB() =
        database.getUnitsDAO().getUnits().filterNotNull().map(UnitsEntity::toUnitsList)

    override suspend fun saveUnitsToDB(units: List<Units>) = withContext(dispatcher) {
        database.getUnitsDAO().saveUnits(units.toUnitsEntity())
    }

    //-----------------------------------------------------------------------------------------------------------------------
    // Если пользователь решил искать город, для получения прогноза, по названию, то возвращаем ему список возможных вариантов.
    // API не позволяет получить более чем пять вариантов, для бесплатных тарифов
    override suspend fun getCitiesByName(cityName: String) : List<City> {
        try {
            return locationProvider.getLocationsByCityName(cityName).map(LocationInternet::toCity)
        } catch (e: Errors.APIError) {
            throw e
        } catch (e: Exception) {
            throw Errors.InternetConnectionError
        }
    }


    //------------------------------------------------------------------------------------------------------------------------
    // Метод позволяет получить прогноз погоды, как по названию города, так и по координатам
    override suspend fun getDataForecast(city: City) = withContext(dispatcher) {
        val forecastDB = database.getForecastDAO().getWeatherForecast()
        if (forecastDB != null && isSameCity(city) && isTimeout()) {
            forecastDB.toWeatherForecast()
        } else {
            try {
                val forecastRT = forecastProvider.getForecast(city.coordinates)
                launch {
                    setTimeout()
                    saveForecastToDb(forecastRT,city.copy(timezone_offset = forecastRT.timezone_offset))
                }
                forecastRT.toWeatherForecast(city.copy(timezone_offset = forecastRT.timezone_offset))
            } catch (e: Errors.APIError) {
                throw e
            } catch (e: Exception) {
                throw Errors.InternetConnectionError
            }
        }
    }

    //----------------------------------------------------------------------------------------------------------------------
    // Удаленный API отдает данные не чаще чем раз в 10 мин, а на бесплатных тарифах и вообще раз в 2-3 часа
    // Поэтому ставим минимально возможный таймаут
    private fun isTimeout() =
        (SystemClock.elapsedRealtime() - getTimeout()) < TimeUnit.MINUTES.toMillis(10)

    private fun setTimeout() {
        database.getForecastDAO().saveTimeout(TimeoutEntity(time = SystemClock.elapsedRealtime()))
    }

    private fun getTimeout() = database.getForecastDAO().getTimeout()?.time ?: SystemClock.elapsedRealtime()

    //-------------------------------------------------------------------------------------------------------------------------
    // Если пользователь изменил город в поиске или просто сменил местоположение, то надо сделать новый запрос
    // к удаленному API, а не брать данные из кэша
    private suspend fun isSameCity(city: City) = getCityFromDb().first()?.run {
        city.name == name && city.state == state && city.country == country
    } ?: false

    override fun getCityFromDb() = database.getForecastDAO().getCity().map { it?.toCity() }

    //--------------------------------------------------------------------------------------------------------------------------
    // Забираем координыты из GPS и вовращаем найденный по ним город
    override suspend fun getCoordinates() =
        locationProvider.getLocationCoordinates().first().toCoordinates()

    override suspend fun getCityByCoordinates(coordinates: Coordinates) : City {
        try {
            return locationProvider.getLocationsByCoordinates(coordinates).first().toCity()
        } catch (e: Errors.APIError) {
            throw e
        } catch (e: Exception) {
            throw Errors.InternetConnectionError
        }
    }



    //--------------------------------------------------------------------------------------------------------------------------------
    // Сохраняем данные прогноза погоды в базе данных
    private fun saveForecastToDb(forecast: ForecastRet, city: City) {
        with(forecast) {
            database.getForecastDAO().saveForecast(
                city.toCityEntity(),
                current.toCurrentForecastEntity(),
                // minutely и alerts частенько просто не приходят от API поэтому они nullable
                minutely?.mapIndexed { index, el -> el.toMinutelyForecastEntity(index) } ?: emptyList(),
                hourly.mapIndexed { index, el -> el.toHourlyForecastEntity(index) },
                daily.mapIndexed { index, el -> el.toDailyForecastEntity(index) },
                alerts?.mapIndexed { index, el -> el.toAlertsForecastEntity(index) } ?: emptyList()
            )
        }
    }
}