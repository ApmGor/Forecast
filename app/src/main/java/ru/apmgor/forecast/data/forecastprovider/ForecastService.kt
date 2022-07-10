package ru.apmgor.forecast.data.forecastprovider

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ForecastService {
    @GET("data/2.5/onecall")
    suspend fun getForecast(@QueryMap options: Map<String, String>) : Response<ForecastRet>
}


data class ForecastRet(
    val timezone_offset: Long,
    val current: CurrentForecastRet,
    val minutely: List<MinutelyForecastRet>?,
    val hourly: List<HourlyForecastRet>,
    val daily: List<DailyForecastRet>,
    val alerts: List<AlertsForecastRet>?
)

data class CurrentForecastRet(
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<Weather>
)

data class Weather(
    val description: String,
    val icon: String
)

data class MinutelyForecastRet(
    val dt: Long,
    val precipitation: Double
)

data class HourlyForecastRet(
    val dt: Long,
    val temp: Double,
    val weather: List<Weather>
)

data class DailyForecastRet(
    val dt: Long,
    val weather: List<Weather>,
    val temp: Temperature,
    val rain: Double?,
    val pop: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val pressure: Int,
    val humidity: Int,
    val uvi: Double,
    val sunrise: Long,
    val sunset: Long
)

data class Temperature(
    val day: Double,
    val night: Double
)

data class AlertsForecastRet(
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)