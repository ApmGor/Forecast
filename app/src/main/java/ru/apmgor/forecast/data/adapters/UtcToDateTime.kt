package ru.apmgor.forecast.data.adapters

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

const val SECOND_TO_MILLISECOND_MULT = 1000

@SuppressLint("SimpleDateFormat")
fun Long.toTime(unit: Int, timezone_offset: Long) =
    when(unit) {
        0 -> SimpleDateFormat().configSDF("HH:mm",(this + timezone_offset) * SECOND_TO_MILLISECOND_MULT)
        else -> SimpleDateFormat().configSDF("hh:mma",(this + timezone_offset) * SECOND_TO_MILLISECOND_MULT)
    }


@SuppressLint("SimpleDateFormat")
fun Long.toDay(withMonth: Boolean, timezone_offset: Long) =
    if (withMonth)
        SimpleDateFormat().configSDF("dd MMM",(this + timezone_offset) * SECOND_TO_MILLISECOND_MULT)
    else
        SimpleDateFormat().configSDF("dd",(this + timezone_offset) * SECOND_TO_MILLISECOND_MULT)


@SuppressLint("SimpleDateFormat")
fun Long.toDayWeek(timezone_offset: Long) =
    SimpleDateFormat().configSDF("EEE",(this + timezone_offset) * SECOND_TO_MILLISECOND_MULT)

fun SimpleDateFormat.configSDF(pattern: String, time: Long) = apply {
    applyPattern(pattern)
    timeZone = TimeZone.getTimeZone("UTC")
}.format(time)