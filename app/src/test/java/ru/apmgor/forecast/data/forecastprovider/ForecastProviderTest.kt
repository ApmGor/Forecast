package ru.apmgor.forecast.data.forecastprovider

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
import ru.apmgor.forecast.data.forecastprovider.ForecastProviderImpl.Companion.LANGUAGE
import ru.apmgor.forecast.data.forecastprovider.ForecastProviderImpl.Companion.UNITS
import ru.apmgor.forecast.data.models.Coordinates

@OptIn(ExperimentalCoroutinesApi::class)
class ForecastProviderTest {

    private lateinit var forecastService: ForecastService
    private lateinit var forecastProvider: ForecastProvider
    private val forecastRet = ForecastRet(
        10800,
        CurrentForecastRet(
            0.0,
            0.0,
            0,
            0,
            0.0,
            0.0,
            0,
            0.0,
            0,
            emptyList()
        ),
        null,
        emptyList(),
        emptyList(),
        null
    )
    private val query = mapOf(
        "appid" to BuildConfig.API_KEY,
        "units" to UNITS,
        "lang" to LANGUAGE,
        "lat" to "55.751244",
        "lon" to "37.618423"
    )


    @Before
    fun setUp() {
        forecastService = mock()
        forecastProvider = ForecastProviderImpl(forecastService)
    }

    @Test(expected = Errors.APIError::class)
    fun testingGetForecast() = runTest {
        argumentCaptor {
            forecastService.stub {
                onBlocking {
                    getForecast(capture())
                } doReturn Response.success(forecastRet) doReturn Response.error(
                    401,"Client Error".toResponseBody("plain/text".toMediaTypeOrNull())
                )
            }
            assertEquals(forecastRet, forecastProvider.getForecast(Coordinates()))
            assertEquals(query, firstValue)
            verify(forecastService).getForecast(any())
        }
        forecastProvider.getForecast(Coordinates())
    }
}