package ru.apmgor.forecast.data.forecastprovider

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.apmgor.forecast.BuildConfig
import ru.apmgor.forecast.data.forecastprovider.ForecastProviderImpl.Companion.LANGUAGE
import ru.apmgor.forecast.data.forecastprovider.ForecastProviderImpl.Companion.UNITS

@OptIn(ExperimentalCoroutinesApi::class)
class ForecastServiceTest {

    private val timezone_offset = 10800L

    private val current = CurrentForecastRet(
        temp=15.06,
        feels_like=14.76,
        pressure=1021,
        humidity=82,
        dew_point=12.01,
        uvi=0.0,
        visibility=10000,
        wind_speed=1.04,
        wind_deg=171,
        weather= listOf(Weather(description="переменная облачность", icon="03n"))
    )

    private val minutely = listOf(
        MinutelyForecastRet(dt=1654203420, precipitation=0.0),
        MinutelyForecastRet(dt=1654203480, precipitation=0.0)
    )

    private val hourly = listOf(
        HourlyForecastRet(dt=1654200000, temp=15.14, weather = listOf(Weather(description="переменная облачность", icon="03n"))),
        HourlyForecastRet(dt=1654203600, temp=15.06, weather = listOf(Weather(description="переменная облачность", icon="03n")))
    )

    private val daily = listOf(
        DailyForecastRet(
            dt=1654156800,
            weather= listOf(Weather(description="небольшой дождь", icon="10d")),
            temp=Temperature(day=25.14, night=15.14),
            rain=0.31,
            pop=0.36,
            wind_speed=2.61,
            wind_deg=215,
            pressure=1022,
            humidity=41,
            uvi=5.5,
            sunrise=1654126505,
            sunset=1654193438
        ),
        DailyForecastRet(
            dt=1654243200,
            weather=listOf(Weather(description="небольшой дождь", icon="10d")),
            temp=Temperature(day=25.34, night=17.65),
            rain=0.25,
            pop=0.44,
            wind_speed=5.01,
            wind_deg=107,
            pressure=1018,
            humidity=44,
            uvi=5.79,
            sunrise=1654212816,
            sunset=1654279947
        )
    )

    private val alerts = listOf(
        AlertsForecastRet(event="Fog", start=1654200000, end=1654236000, description=""),
        AlertsForecastRet(event="Туман", start=1654200000, end=1654236000, description="ночью и утром туман")
    )

    @get:Rule
    val mockWebServer = MockWebServer()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val forecastService by lazy {
        retrofit.create<ForecastService>()
    }

    private lateinit var _queryForecastService: MutableMap<String, String>

    @Before
    fun setUp() {
        _queryForecastService = mutableMapOf(
            Pair("appid", BuildConfig.API_KEY),
            Pair("units", UNITS),
            Pair("lang", LANGUAGE)
        )
    }

    @After
    fun tearDown() {
        _queryForecastService.clear()
    }

    @Test
    fun testingGetForecastSuccess() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(forecastJson)
                .setResponseCode(200)
        )
        _queryForecastService += mapOf(
            "lat" to "60.7619",
            "lon" to "46.3135"
        )
        val resp = forecastService.getForecast(_queryForecastService.toMap()).body()
        assertEquals(timezone_offset, resp?.timezone_offset)
        assertEquals(current, resp?.current)
        assertEquals(minutely,resp?.minutely?.subList(0,2))
        assertEquals(hourly, resp?.hourly?.subList(0,2))
        assertEquals(daily,resp?.daily?.subList(0,2))
        assertEquals(alerts,resp?.alerts?.subList(0,2))
        val path = mockWebServer.takeRequest().path
        Assert.assertTrue(
            path?.contentEquals("/data/2.5/onecall?appid=${BuildConfig.API_KEY}&units=$UNITS&lang=$LANGUAGE&lat=60.7619&lon=46.3135")
                ?: false
        )
    }

    @Test
    fun testingGetForecastFailure() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(forecastJson)
                .setResponseCode(401)
        )
        _queryForecastService += mapOf(
            "lat" to "60.7619",
            "lon" to "46.3135"
        )
        val resp = forecastService.getForecast(_queryForecastService.toMap())
        assertEquals(401,resp.code())
        assertFalse(resp.isSuccessful)
    }
}