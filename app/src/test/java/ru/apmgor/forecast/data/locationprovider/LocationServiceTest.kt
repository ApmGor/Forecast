package ru.apmgor.forecast.data.locationprovider

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.apmgor.forecast.BuildConfig
import ru.apmgor.forecast.data.locationprovider.LocationProviderImpl.Companion.NUM_CITY_IN_OUTPUT

@OptIn(ExperimentalCoroutinesApi::class)
class LocationServiceTest {

    @get:Rule
    val mockWebServer = MockWebServer()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val locationService by lazy {
        retrofit.create<LocationService>()
    }

    private lateinit var _queryLocationService: MutableMap<String, String>

    private val response = listOf(
        LocationInternet(
            name = "Tver",
            local_names = LocalNames(ru = "Тверь"),
            lat = 56.8596713,
            lon = 35.89524161906262,
            country = "RU",
            state = "Tver Oblast"
        )
    )

    @Before
    fun setUp() {
        _queryLocationService = mutableMapOf(
            Pair("appid", BuildConfig.API_KEY),
            Pair("limit", NUM_CITY_IN_OUTPUT),
        )
    }

    @After
    fun tearDown() {
        _queryLocationService.clear()
    }

    @Test
    fun testingGetLocationsByCityNameSuccess() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(cityJson)
                .setResponseCode(200)
        )
        _queryLocationService["q"] += "Tver"
        val resp = locationService.getLocationsByCityName(_queryLocationService.toMap()).body()
        assertEquals(response,resp)
        val path = mockWebServer.takeRequest().path
        assertTrue(path?.contentEquals("/geo/1.0/direct?appid=${BuildConfig.API_KEY}&limit=$NUM_CITY_IN_OUTPUT&q=nullTver") ?: false)
    }

    @Test
    fun testingGetLocationsByCityNameFailure() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(cityJson)
                .setResponseCode(401)
        )
        _queryLocationService["q"] += "Tver"
        val resp = locationService.getLocationsByCityName(_queryLocationService.toMap())
        assertEquals(401,resp.code())
        assertFalse(resp.isSuccessful)
    }

    @Test
    fun testingGetLocationsByCoordinatesSuccess() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(cityJson)
                .setResponseCode(200)
        )
        _queryLocationService += mapOf(
            "lat" to "56.8596713",
            "lon" to "35.89524161906262"
        )
        val resp = locationService.getLocationsByCoordinates(_queryLocationService.toMap()).body()
        assertEquals(response,resp)
        val path = mockWebServer.takeRequest().path
        assertTrue(path?.contentEquals(
            "/geo/1.0/reverse?appid=${BuildConfig.API_KEY}&limit=$NUM_CITY_IN_OUTPUT&lat=56.8596713&lon=35.89524161906262") ?:
            false
        )
    }

    @Test
    fun testingGetLocationsByCoordinatesFailure() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(cityJson)
                .setResponseCode(401)
        )
        _queryLocationService += mapOf(
            "lat" to "56.8596713",
            "lon" to "35.89524161906262"
        )
        val resp = locationService.getLocationsByCoordinates(_queryLocationService.toMap())
        assertEquals(401, resp.code())
        assertFalse(resp.isSuccessful)
    }

}