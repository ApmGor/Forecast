package ru.apmgor.forecast.data.adapters

import ru.apmgor.forecast.data.database.units.UnitsEntity
import ru.apmgor.forecast.data.models.*

fun List<Units>.toUnitsEntity() = UnitsEntity(
    tempUnits = this[0].unit,
    windUnits = this[1].unit,
    pressureUnits = this[2].unit,
    precipUnits = this[3].unit,
    distUnits = this[4].unit,
    timeUnits = this[5].unit
)
