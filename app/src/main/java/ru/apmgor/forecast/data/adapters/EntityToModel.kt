package ru.apmgor.forecast.data.adapters

import ru.apmgor.forecast.data.database.*
import ru.apmgor.forecast.data.database.units.UnitsEntity
import ru.apmgor.forecast.data.models.*
import kotlin.math.roundToInt

fun WeatherForecastEntity.toWeatherForecast() : WeatherForecast {
    val listDailyForecast = this.daily.toListModels { toDailyForecast() }
    return WeatherForecast(
        city = this.city.toCity(),
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

fun City.toCityEntity() = CityEntity(
    name = this.name,
    state = this.state,
    country = this.country,
    latitude = this.coordinates.latitude,
    longitude = this.coordinates.longitude,
    timezone_offset = this.timezone_offset!!
)

fun CityEntity.toCity() = City(
    name = this.name,
    state = this.state,
    country = this.country,
    coordinates = Coordinates(this.latitude,this.longitude),
    timezone_offset = this.timezone_offset
)

fun CurrentForecastEntity.toCurrentForecast() = CurrentForecast(
    temp = this.temp.roundToInt(),
    feelsLike = this.feelsLike.roundToInt(),
    pressure = this.pressure,
    humidity = this.humidity,
    dewPoint = this.dewPoint,
    uvi = this.uvi,
    visibility = this.visibility,
    wind = Wind(this.windSpeed,this.windDeg),
    weather = Weather(this.weatherDesc.replaceFirstChar { it.uppercase() },"f${this.weatherIcon}")
)

fun MinutelyForecastEntity.toMinutelyForecast() = MinutelyForecast(
    dt = this.dt,
    precipitation = this.precipitation
)

fun HourlyForecastEntity.toHourlyForecast() = HourlyForecast(
    dt = this.dt,
    temp = this.temp.roundToInt(),
    weatherIcon = "f${this.weatherIcon}"
)

fun DailyForecastEntity.toDailyForecast() = DailyForecast(
    dt = this.dt,
    weather = Weather(this.weatherDesc.replaceFirstChar { it.uppercase() }, "f${this.weatherIcon}"),
    temp = Temperature(this.tempDay.roundToInt(),this.tempNight.roundToInt()),
    rain = this.rain,
    pop = this.pop,
    wind = Wind(this.windSpeed,this.windDeg),
    pressure = this.pressure,
    humidity = this.humidity,
    uvi = this.uvi,
    sunrise = this.sunrise,
    sunset = this.sunset
)

fun AlertsForecastEntity.toAlertsForecast() = AlertsForecast(
    event = this.event,
    start = this.start,
    end = this.end,
    description = this.description
        .replaceFirstChar { it.uppercase() }
)

fun UnitsEntity.toUnitsList() = listOf(
    TempUnits(tempUnits),
    WindSpeedUnits(windUnits),
    PressureUnits(pressureUnits),
    PrecipUnits(precipUnits),
    DistUnits(distUnits),
    TimeUnits(timeUnits)
)