package ru.apmgor.forecast.data.models


data class WeatherForecast(
    val city: City,
    val currentForecast: CurrentForecast,
    val listMinutelyForecast: List<MinutelyForecast>,
    val listHourlyPlusSunsetSunrise: List<HourlyPlusSunsetSunrise>,
    val listDailyForecast: List<DailyForecast>,
    val listAlertsForecast: List<AlertsForecast>
) {
    constructor() : this(
        City(),
        CurrentForecast(),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList()
    )

}

data class City(
    val coordinates: Coordinates,
    val name: String,
    val state: String? = null,
    val country: String,
    val timezone_offset: Long? = null
) {
    constructor() : this(
        coordinates = Coordinates(),
        name = "Москва",
        country = "RU"
    )
}

data class Coordinates(
    val latitude: Double,
    val longitude: Double
) {
    constructor() : this(
        55.751244,
        37.618423
    )
}

data class AlertsForecast(
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)

data class DailyForecast(
    val dt: Long,
    val weather: Weather,
    val temp: Temperature,
    val rain: Double?,
    val pop: Double,
    val wind: Wind,
    val pressure: Int,
    val humidity: Int,
    val uvi: Double,
    val sunrise: Long,
    val sunset: Long
)

data class Temperature(
    val tempDay: Int,
    val tempNight: Int
)

open class HourlyPlusSunsetSunrise(val dt: Long, val weatherIcon: String)

class HourlyForecast(
    dt: Long,
    val temp: Int,
    weatherIcon: String
) : HourlyPlusSunsetSunrise(dt, weatherIcon)

class Sunset(
    dt: Long,
) : HourlyPlusSunsetSunrise(dt,"sunset")

class Sunrise(
    dt: Long,
) : HourlyPlusSunsetSunrise(dt,"sunrise")


data class MinutelyForecast(
    val dt: Long,
    val precipitation: Double
)

data class CurrentForecast(
    val temp: Int,
    val feelsLike: Int,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val uvi: Double,
    val visibility: Int,
    val wind: Wind,
    val weather: Weather
) {
    constructor() : this(
        0,
        0,
        0,
        0,
        0.0,
        0.0,
        0,
        Wind(0.0,0),
        Weather("","")
    )
}

data class Weather(
    val weatherDesc: String,
    val weatherIcon: String
)

data class Wind(
    val windSpeed: Double,
    val windDeg: Int,
) {
    val windDirection = when(windDeg) {
        in (0..11) -> WindDirections.North
        in (12..33) -> WindDirections.NorthNorthEast
        in (34..56) -> WindDirections.NorthEast
        in (57..78) -> WindDirections.EastNorthEast
        in (79..101) -> WindDirections.East
        in (102..123) -> WindDirections.EastSouthEast
        in (124..146) -> WindDirections.SouthEast
        in (147..168) -> WindDirections.SouthSouthEast
        in (169..191) -> WindDirections.South
        in (192..213) -> WindDirections.SouthSouthWest
        in (214..236) -> WindDirections.SouthWest
        in (237..258) -> WindDirections.WestSouthWest
        in (259..281) -> WindDirections.West
        in (282..303) -> WindDirections.WestNorthWest
        in (304..326) -> WindDirections.NorthWest
        in (327..348) -> WindDirections.NorthNorthWest
        else -> WindDirections.North
    }

    val windName = when(windSpeed) {
        in (0.00..0.49) -> WindNames.Calm
        in (0.50..1.99) -> WindNames.LightAir
        in (2.00..3.49) -> WindNames.LightBreeze
        in (3.50..5.49) -> WindNames.GentleBreeze
        in (5.50..8.49) -> WindNames.ModerateBreeze
        in (8.50..10.99) -> WindNames.FreshBreeze
        in (11.00..13.99) -> WindNames.StrongBreeze
        in (14.00..16.99) -> WindNames.ModerateGale
        in (17.00..20.49) -> WindNames.FreshGale
        in (20.50..23.99) -> WindNames.StrongGale
        in (24.00..27.99) -> WindNames.WholeGale
        in (28.00..31.99) -> WindNames.Storm
        else -> WindNames.Hurricane
    }

    sealed class WindNames(val id: Int) {
        object Calm : WindNames(0)
        object LightAir : WindNames(1)
        object LightBreeze : WindNames(2)
        object GentleBreeze : WindNames(3)
        object ModerateBreeze : WindNames(4)
        object FreshBreeze : WindNames(5)
        object StrongBreeze : WindNames(6)
        object ModerateGale : WindNames(7)
        object FreshGale : WindNames(8)
        object StrongGale : WindNames(9)
        object WholeGale : WindNames(10)
        object Storm : WindNames(11)
        object Hurricane : WindNames(12)
    }

    sealed class WindDirections(val id: Int, val arrow: String, val arrow_white: String) {
        object North : WindDirections(0,"arrow_n","arrow_n_white")
        object NorthNorthEast : WindDirections(1,"arrow_nne","arrow_nne_white")
        object NorthEast : WindDirections(2,"arrow_ne","arrow_ne_white")
        object EastNorthEast : WindDirections(3,"arrow_ene","arrow_ene_white")
        object East : WindDirections(4,"arrow_e","arrow_e_white")
        object EastSouthEast : WindDirections(5,"arrow_ese","arrow_ese_white")
        object SouthEast : WindDirections(6,"arrow_se","arrow_se_white")
        object SouthSouthEast : WindDirections(7,"arrow_sse","arrow_sse_white")
        object South : WindDirections(8,"arrow_s","arrow_s_white")
        object SouthSouthWest : WindDirections(9,"arrow_ssw","arrow_ssw_white")
        object SouthWest : WindDirections(10,"arrow_sw","arrow_sw_white")
        object WestSouthWest : WindDirections(11,"arrow_wsw","arrow_wsw_white")
        object West : WindDirections(12,"arrow_w","arrow_w_white")
        object WestNorthWest : WindDirections(13,"arrow_wnw","arrow_wnw_white")
        object NorthWest : WindDirections(14,"arrow_nw","arrow_nw_white")
        object NorthNorthWest : WindDirections(15,"arrow_nnw","arrow_nnw_white")
    }
}