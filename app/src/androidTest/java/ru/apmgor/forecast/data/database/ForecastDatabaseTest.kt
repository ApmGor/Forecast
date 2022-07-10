package ru.apmgor.forecast.data.database

import android.os.SystemClock
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import ru.apmgor.forecast.data.database.units.UnitsEntity
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class ForecastDatabaseTest {

    private val city = CityEntity(
        name = "Москва",
        country = "RU",
        state = null,
        latitude = 34.890,
        longitude = 64.45657,
        timezone_offset = 10800
    )
    private val current = CurrentForecastEntity(
        temp = 34.3,
        feelsLike = 35.1,
        pressure = 123,
        humidity = 567,
        dewPoint = 6.4,
        uvi = 0.2,
        visibility = 56,
        windSpeed = 56.8,
        windDeg = 34,
        weatherDesc = "",
        weatherIcon = ""
    )
    private val minutely = MinutelyForecastEntity(
        id = 0,
        dt = 12445657687,
        precipitation = 34.5
    )
    private val hourly = HourlyForecastEntity(
        id = 0,
        dt = 1345557578,
        temp = 43.4,
        weatherIcon = ""
    )
    private val daily = DailyForecastEntity(
        id = 0,
        dt = 16787789890,
        tempDay = 34.3,
        tempNight = 12.5,
        rain = null,
        pop = 2.4,
        pressure = 123,
        humidity = 567,
        sunset = 1456757768,
        uvi = 0.2,
        sunrise = 1354567578,
        windSpeed = 56.8,
        windDeg = 34,
        weatherDesc = "",
        weatherIcon = ""
    )
    private val alerts = AlertsForecastEntity(
        id = 0,
        event = "",
        start = 124345467,
        end = 14657647657,
        description = ""
    )
    private val forecast = WeatherForecastEntity(
        city = city,
        current = current,
        minutely = listOf(minutely,minutely.copy(1),minutely.copy(2)),
        hourly = listOf(hourly,hourly.copy(1),hourly.copy(2)),
        daily = listOf(daily,daily.copy(1),daily.copy(2)),
        alerts = listOf(alerts,alerts.copy(1),alerts.copy(2))
    )

    private val units = UnitsEntity(
        timeUnits = 1,
        windUnits = 0,
        pressureUnits = 1,
        precipUnits = 0,
        distUnits = 0,
        tempUnits = 1
    )

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: ForecastDatabase
    private lateinit var dao: ForecastDAO

    @Before
    fun setUp() {
        hiltRule.inject()
        dao = database.getForecastDAO()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testingCityForecastDAO() = runTest {
        assertNull(dao.getCity().first())
        dao.saveCity(city)
        assertEquals(city, dao.getCity().first())
        val city2 = city.copy(name = "Пермь", state = "Пермский край")
        dao.saveCity(city2)
        assertEquals(city2, dao.getCity().first())
    }

    @Test
    fun testingTimeoutForecastDAO() {
        assertNull(dao.getTimeout())
        val timeout = TimeoutEntity(time = SystemClock.elapsedRealtime())
        dao.saveTimeout(timeout)
        assertEquals(timeout,dao.getTimeout())
    }

    @Test
    fun testingWeatherForecastForecastDAO() {
        assertNull(dao.getWeatherForecast())
        dao.saveForecast(
            forecast.city,
            forecast.current,
            forecast.minutely,
            forecast.hourly,
            forecast.daily,
            forecast.alerts
        )
        assertEquals(forecast,dao.getWeatherForecast())
        dao.saveForecast(
            forecast.city.copy(name = "Пермь"),
            forecast.current,
            forecast.minutely.minusElement(minutely),
            forecast.hourly,
            forecast.daily,
            forecast.alerts
        )
        assertEquals(
            forecast.copy(
                city = city.copy(name = "Пермь"),
                minutely = forecast.minutely.minusElement(minutely)
            ),
            dao.getWeatherForecast()
        )
    }

    @Test
    fun testingUnitsUnitsDAO() = runTest {
        val dao = database.getUnitsDAO()
        assertNull(dao.getUnits().first())
        dao.saveUnits(units)
        assertEquals(units,dao.getUnits().first())
    }
}