package ru.apmgor.forecast.data.adapters

import ru.apmgor.forecast.data.database.*
import ru.apmgor.forecast.data.forecastprovider.*

fun CurrentForecastRet.toCurrentForecastEntity() = CurrentForecastEntity(
    temp = this.temp,
    feelsLike = this.feels_like,
    pressure = this.pressure,
    humidity = this.humidity,
    dewPoint = this.dew_point,
    uvi = this.uvi,
    visibility = this.visibility,
    windSpeed = this.wind_speed,
    windDeg = this.wind_deg,
    weatherDesc = this.weather[0].description,
    weatherIcon = this.weather[0].icon
)

fun MinutelyForecastRet.toMinutelyForecastEntity(index: Int) = MinutelyForecastEntity(
    id = index,
    dt = this.dt,
    precipitation = this.precipitation
)

fun HourlyForecastRet.toHourlyForecastEntity(index: Int) = HourlyForecastEntity(
    id = index,
    dt = this.dt,
    temp = this.temp,
    weatherIcon = this.weather[0].icon
)

fun DailyForecastRet.toDailyForecastEntity(index: Int) = DailyForecastEntity(
    id = index,
    dt = this.dt,
    weatherDesc = this.weather[0].description,
    weatherIcon = this.weather[0].icon,
    tempDay = this.temp.day,
    tempNight = this.temp.night,
    rain = this.rain,
    pop = this.pop,
    windSpeed = this.wind_speed,
    windDeg = this.wind_deg,
    pressure = this.pressure,
    humidity = this.humidity,
    uvi = this.uvi,
    sunrise = this.sunrise,
    sunset = this.sunset
)

fun AlertsForecastRet.toAlertsForecastEntity(index: Int) = AlertsForecastEntity(
    id = index,
    event = this.event,
    start = this.start,
    end = this.end,
    description = this.description
)




