package ru.apmgor.forecast.data.database.units

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "units")
data class UnitsEntity(
    @PrimaryKey
    val id: Int = 0,
    val tempUnits: Int,
    val windUnits: Int,
    val pressureUnits: Int,
    val precipUnits: Int,
    val distUnits: Int,
    val timeUnits: Int
)