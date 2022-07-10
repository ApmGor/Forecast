package ru.apmgor.forecast.data.models

import kotlin.math.roundToInt

// Все числовые значения unit заданы в соответствии с индексами массивов в строковых
// ресурсах, изменение их порядка здесь должно сочетаться с изменением в строковых
// ресурсах, и наоборот. Это сделано для существенного упрощения логики экрана Settings
abstract class Units(val unit: Int) {
    abstract fun getResName(units: Array<String>) : String
    abstract fun copy(unit: Int = this.unit) : Units
    abstract fun revert(unitValue: Number) : Any

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Units
        if (unit != other.unit) return false
        return true
    }

    override fun hashCode(): Int {
        return unit
    }

    override fun toString(): String {
        return "Units(unit=$unit)"
    }

    // НЕ МЕНЯТЬ порядок элементов в списке!!!
    companion object {
        val defaultUnitsList = listOf(
            TempUnits(),
            WindSpeedUnits(),
            PressureUnits(),
            PrecipUnits(),
            DistUnits(),
            TimeUnits()
        )
    }
}

class TempUnits(unit: Int = 0) : Units(unit) {

    // else это Фаренгейты - 1
    override fun getResName(units: Array<String>) =
        when(unit) {
            0 -> units[0]
            else -> units[1]
        }

    override fun copy(unit: Int) = TempUnits(unit)

    override fun revert(temp: Number) =
        when(unit) {
            0 -> temp.toInt()
            else -> (temp.toInt() * 1.8 + 32).roundToInt()
        }
}

class WindSpeedUnits(unit: Int = 0) : Units(unit) {

    // else это Мили в час - 2
    override fun getResName(units: Array<String>) =
        when(unit) {
            0 -> units[0]
            1 -> units[1]
            else -> units[2]
        }

    override fun copy(unit: Int) = WindSpeedUnits(unit)

    override fun revert(speed: Number) =
        when(unit) {
            0 -> speed.toDouble()
            1 -> speed.toDouble() * 3.6
            else -> speed.toDouble() * 2.24
        }
}

class PressureUnits(unit: Int = 0) : Units(unit) {

    // else это мм ртутного столба - 1
    override fun getResName(units: Array<String>) =
        when(unit) {
            0 -> units[0]
            else -> units[1]
        }

    override fun copy(unit: Int) = PressureUnits(unit)

    override fun revert(pressure: Number) =
        when(unit) {
            0 -> pressure.toInt()
            else -> (pressure.toInt() * 0.75).roundToInt()
        }
}

class PrecipUnits(unit: Int = 0) : Units(unit) {

    // else это дюймы - 1
    override fun getResName(units: Array<String>) =
        when(unit) {
            0 -> units[0]
            else -> units[1]
        }

    override fun copy(unit: Int) = PrecipUnits(unit)

    override fun revert(precipitation: Number) =
        when(unit) {
            0 -> precipitation.toDouble()
            else -> precipitation.toDouble() * 0.04
        }
}

class DistUnits(unit: Int = 0) : Units(unit)  {

    // else это мили - 1
    override fun getResName(units: Array<String>) =
        when(unit) {
            0 -> units[0]
            else -> units[1]
        }

    override fun copy(unit: Int) = DistUnits(unit)

    override fun revert(distance: Number) =
        when(unit) {
            0 -> distance.toDouble() * 0.001
            else -> distance.toDouble() * 0.0006
        }
}

class TimeUnits(unit: Int = 0) : Units(unit) {

    // else это 12-часовой режим - 1
    override fun getResName(units: Array<String>) =
        when(unit) {
            0 -> units[0]
            else -> units[1]
        }

    override fun copy(unit: Int) = TimeUnits(unit)

    override fun revert(time: Number) = time.toLong()
}