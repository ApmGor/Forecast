package ru.apmgor.forecast.data

import kotlinx.coroutines.flow.Flow
import ru.apmgor.forecast.data.errors.Errors
import ru.apmgor.forecast.data.models.*
import kotlin.jvm.Throws

interface ForecastRepo {
    @Throws(Errors.InternetConnectionError::class, Errors.APIError::class)
    suspend fun getCitiesByName(cityName: String): List<City>
    suspend fun getCoordinates(): Coordinates
    suspend fun getCityByCoordinates(coordinates: Coordinates): City
    fun getUnitsFromDB(): Flow<List<Units>>
    suspend fun saveUnitsToDB(units: List<Units>)
    fun getCityFromDb(): Flow<City?>
    @Throws(Errors.InternetConnectionError::class, Errors.APIError::class, Errors.GPSError::class)
    suspend fun getDataForecast(city: City): WeatherForecast
}