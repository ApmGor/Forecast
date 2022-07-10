package ru.apmgor.forecast.data.forecastprovider

import ru.apmgor.forecast.data.errors.Errors
import ru.apmgor.forecast.data.models.Coordinates
import kotlin.jvm.Throws

interface ForecastProvider {
    @Throws(Errors.APIError::class)
    suspend fun getForecast(coordinates: Coordinates) : ForecastRet
}