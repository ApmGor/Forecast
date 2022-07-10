package ru.apmgor.forecast.data.locationprovider

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import retrofit2.Response
import ru.apmgor.forecast.BuildConfig
import ru.apmgor.forecast.data.errors.Errors
import ru.apmgor.forecast.data.locationprovider.LocationProviderImpl.Companion.NUM_CITY_IN_OUTPUT
import ru.apmgor.forecast.data.models.Coordinates

@OptIn(ExperimentalCoroutinesApi::class)
class LocationProviderTest {

    private lateinit var locationService: LocationService
    private lateinit var locationProvider: LocationProvider

    private val query1 = mapOf(
        Pair("appid", BuildConfig.API_KEY),
        Pair("limit", NUM_CITY_IN_OUTPUT),
        Pair("q", "Moscow")
    )

    private val query2 = mapOf(
        Pair("appid", BuildConfig.API_KEY),
        Pair("limit", NUM_CITY_IN_OUTPUT),
        Pair("lat", "55.751244"),
        Pair("lon", "37.618423")
    )

    private val location = LocationInternet(
        name = "Moscow",
        local_names = LocalNames(ru = "Москва"),
        lat = 55.751244,
        lon = 37.618423,
        country = "RU",
        state = "Moscow"
    )

    @Before
    fun setUp() {
        locationService = mock()
        locationProvider = LocationProviderImpl(mock(),locationService,mock(),mock())
    }

    @Test(expected = Errors.APIError::class)
    fun testingGetLocationsByCityName() = runTest {
        argumentCaptor {
            locationService.stub {
                onBlocking {
                    getLocationsByCityName(capture())
                } doReturn Response.success(listOf(location)) doReturn Response.error(
                    401,"Client Error".toResponseBody("plain/text".toMediaTypeOrNull())
                )
            }
            assertEquals(
                listOf(location), locationProvider.getLocationsByCityName("Moscow")
            )
            assertEquals(query1, firstValue)
            verify(locationService).getLocationsByCityName(any())
        }
        locationProvider.getLocationsByCityName(any())
    }

    @Test(expected = Errors.APIError::class)
    fun testingGetLocationsByCoordinates() = runTest {
        argumentCaptor {
            locationService.stub {
                onBlocking {
                    getLocationsByCoordinates(capture())
                } doReturn Response.success(listOf(location)) doReturn Response.error(
                    401,"Client Error".toResponseBody("plain/text".toMediaTypeOrNull())
                )
            }
            assertEquals(
                listOf(location), locationProvider.getLocationsByCoordinates(Coordinates())
            )
            assertEquals(query2, firstValue)
            verify(locationService).getLocationsByCoordinates(any())
        }
        locationProvider.getLocationsByCoordinates(Coordinates())
    }
}