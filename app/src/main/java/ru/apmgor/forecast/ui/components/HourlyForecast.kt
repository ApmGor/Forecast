package ru.apmgor.forecast.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.apmgor.forecast.R
import ru.apmgor.forecast.data.adapters.toDay
import ru.apmgor.forecast.data.adapters.toTime
import ru.apmgor.forecast.data.models.*

@Composable
fun HourlyForecast(
    timezone_offset: Long,
    units: List<Units>,
    hourly: List<HourlyPlusSunsetSunrise>,
    modifier: Modifier = Modifier
) {
    val tempUnit = stringArrayResource(id = R.array.temp_units)

    LazyRow(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(hourly) {
            Card {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .width(70.dp)
                        .padding(vertical = 8.dp)
                ) {
                    val time = (units[5].revert(it.dt) as Long).toTime(units[5].unit,timezone_offset)
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        if (time == "00:00" || time == "12:00AM") {
                            Text(text = it.dt.toDay(true,timezone_offset), fontSize = 10.sp)
                        } else {
                            Text(text = time, fontSize = 10.sp)
                        }
                    }
                    if (isSystemInDarkTheme() &&
                        (it.weatherIcon == "sunset" ||
                                it.weatherIcon == "sunrise")
                    ) {
                        Image(
                            painter = painterResource(id = getDrawableResFromModel(it.weatherIcon + "_white")),
                            contentDescription = stringResource(id = R.string.weather_image),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .size(width = 30.dp, height = 20.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = getDrawableResFromModel(it.weatherIcon)),
                            contentDescription = stringResource(id = R.string.weather_image),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .size(width = 30.dp, height = 20.dp)
                        )
                    }
                    when(it) {
                        is HourlyForecast -> Text(
                            text = stringResource(
                                id = R.string.current_temp,
                                units[0].revert(it.temp),
                                units[0].getResName(tempUnit)
                            ),
                            fontSize = 14.sp
                        )
                        is Sunset -> Text(text = stringResource(R.string.sunset_desc),fontSize = 14.sp)
                        is Sunrise -> Text(text = stringResource(R.string.sunrise_desc),fontSize = 14.sp)
                    }
                }
            }
        }
    }
}