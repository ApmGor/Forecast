package ru.apmgor.forecast.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.apmgor.forecast.data.database.units.UnitsDAO
import ru.apmgor.forecast.data.database.units.UnitsEntity

@Database(entities = [CityEntity::class,
    CurrentForecastEntity::class,
    MinutelyForecastEntity::class,
    HourlyForecastEntity::class,
    DailyForecastEntity::class,
    AlertsForecastEntity::class,
    TimeoutEntity::class,
    UnitsEntity::class],
    version = 1)
abstract class ForecastDatabase : RoomDatabase() {
    abstract fun getForecastDAO(): ForecastDAO
    abstract fun getUnitsDAO(): UnitsDAO
}