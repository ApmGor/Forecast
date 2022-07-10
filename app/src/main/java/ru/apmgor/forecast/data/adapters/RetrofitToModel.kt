package ru.apmgor.forecast.data.adapters

import ru.apmgor.forecast.data.forecastprovider.*
import ru.apmgor.forecast.data.models.*
import ru.apmgor.forecast.data.models.Temperature
import ru.apmgor.forecast.data.models.Weather
import kotlin.math.roundToInt

fun ForecastRet.toWeatherForecast(city: City) : WeatherForecast {
    val listDailyForecast = this.daily.toListModels { toDailyForecast() }
    return WeatherForecast(
        city = city,
        currentForecast = this.current.toCurrentForecast(),
        listMinutelyForecast = this.minutely.toListModels { toMinutelyForecast() },
        listHourlyPlusSunsetSunrise = getHourlyPlusSS(
            this.hourly.toListModels { toHourlyForecast() },
            listDailyForecast
        ),
        listDailyForecast = listDailyForecast,
        listAlertsForecast = this.alerts
            .toListModels { toAlertsForecast() }
            .filter { Regex("[а-яА-Я]").containsMatchIn(it.event) }
    )
}

fun <T, R> List<T>?.toListModels(adapter: T.() -> R) = this?.map { it.adapter() } ?: emptyList()

fun getHourlyPlusSS(listH: List<HourlyForecast>, listD: List<DailyForecast>) =
    if (listH.isEmpty() || listD.isEmpty()) {
        emptyList()
    } else {
        val minDt = listH.minOf { it.dt }
        val maxDt = listH.maxOf { it.dt }
        val listSunrises = listD.filter {
            it.sunrise in minDt..maxDt
        }.map { Sunrise(it.sunrise) }
        val listSunsets = listD.filter {
            it.sunset in minDt..maxDt
        }.map { Sunset(it.sunset) }
        (listH + listSunrises + listSunsets).sortedBy { it.dt }
    }

fun CurrentForecastRet.toCurrentForecast() = CurrentForecast(
    temp = this.temp.roundToInt(),
    feelsLike = this.feels_like.roundToInt(),
    pressure = this.pressure,
    humidity = this.humidity,
    dewPoint = this.dew_point,
    uvi = this.uvi,
    visibility = this.visibility,
    wind = Wind(this.wind_speed,this.wind_deg),
    weather = Weather(
        this.weather[0].description
            .replaceFirstChar { it.uppercase() },
        "f${this.weather[0].icon}"
    )
)

fun MinutelyForecastRet.toMinutelyForecast() = MinutelyForecast(
    dt = this.dt,
    precipitation = this.precipitation
)

fun HourlyForecastRet.toHourlyForecast() = HourlyForecast(
    dt = this.dt,
    temp = this.temp.roundToInt(),
    weatherIcon = "f${this.weather[0].icon}"
)

fun DailyForecastRet.toDailyForecast() = DailyForecast(
    dt = this.dt,
    weather = Weather(
        this.weather[0].description
            .replaceFirstChar { it.uppercase() },
        "f${this.weather[0].icon}"
    ),
    temp = Temperature(
        this.temp.day.roundToInt(),
        this.temp.night.roundToInt()
    ),
    rain = this.rain,
    pop = this.pop,
    wind = Wind(this.wind_speed,this.wind_deg),
    pressure = this.pressure,
    humidity = this.humidity,
    uvi = this.uvi,
    sunrise = this.sunrise,
    sunset = this.sunset
)

fun AlertsForecastRet.toAlertsForecast() = AlertsForecast(
    event = this.event,
    start = this.start,
    end = this.end,
    description = this.description
        .replaceFirstChar { it.uppercase() }
)

