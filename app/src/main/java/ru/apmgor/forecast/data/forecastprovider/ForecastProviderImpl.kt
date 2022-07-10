package ru.apmgor.forecast.data.forecastprovider

import ru.apmgor.forecast.BuildConfig
import ru.apmgor.forecast.data.errors.Errors
import ru.apmgor.forecast.data.models.Coordinates
import javax.inject.Inject

class ForecastProviderImpl @Inject constructor(
    private val forecastService: ForecastService
) : ForecastProvider {

    private val _queryForecastService = mutableMapOf(
        Pair("appid", BuildConfig.API_KEY),
        Pair("units", UNITS),
        Pair("lang", LANGUAGE)
    )

    override suspend fun getForecast(coordinates: Coordinates) : ForecastRet {
        _queryForecastService += mapOf(
            "lat" to coordinates.latitude.toString(),
            "lon" to coordinates.longitude.toString()
        )
        val response = forecastService.getForecast(_queryForecastService.toMap())
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Errors.APIError(response.code())
        }
    }

    companion object {
        const val UNITS = "metric"
        const val LANGUAGE = "ru"
    }
}