package ru.apmgor.forecast.data.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "city")
data class CityEntity(
    @PrimaryKey
    val id: Int = 0,
    val name: String,
    val state: String?,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone_offset: Long
)

@Entity(tableName = "current")
data class CurrentForecastEntity(
    @PrimaryKey
    val id: Int = 0,
    val cityId: Int = 0,
    val temp: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val uvi: Double,
    val visibility: Int,
    val windSpeed: Double,
    val windDeg: Int,
    val weatherDesc: String,
    val weatherIcon: String
)

@Entity(tableName = "minutely")
data class MinutelyForecastEntity(
    @PrimaryKey
    val id: Int,
    val cityId: Int = 0,
    val dt: Long,
    val precipitation: Double
)

@Entity(tableName = "hourly")
data class HourlyForecastEntity(
    @PrimaryKey
    val id: Int,
    val cityId: Int = 0,
    val dt: Long,
    val temp: Double,
    val weatherIcon: String
)

@Entity(tableName = "daily")
data class DailyForecastEntity(
    @PrimaryKey
    val id: Int,
    val cityId: Int = 0,
    val dt: Long,
    val weatherDesc: String,
    val weatherIcon: String,
    val tempDay: Double,
    val tempNight: Double,
    val rain: Double?,
    val pop: Double,
    val windSpeed: Double,
    val windDeg: Int,
    val pressure: Int,
    val humidity: Int,
    val uvi: Double,
    val sunrise: Long,
    val sunset: Long
)

@Entity(tableName = "alerts")
data class AlertsForecastEntity(
    @PrimaryKey
    val id: Int,
    val cityId: Int = 0,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)

@Entity(tableName = "timeout")
data class TimeoutEntity(
    @PrimaryKey
    val id: Int = 0,
    val time: Long
)

data class WeatherForecastEntity(
    @Embedded val city: CityEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "cityId"
    )
    val current: CurrentForecastEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "cityId"
    )
    val minutely: List<MinutelyForecastEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "cityId"
    )
    val hourly: List<HourlyForecastEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "cityId"
    )
    val daily: List<DailyForecastEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "cityId"
    )
    val alerts: List<AlertsForecastEntity>
)