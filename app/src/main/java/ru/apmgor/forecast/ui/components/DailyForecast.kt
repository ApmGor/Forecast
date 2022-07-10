package ru.apmgor.forecast.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.apmgor.forecast.R
import ru.apmgor.forecast.data.adapters.toDay
import ru.apmgor.forecast.data.adapters.toDayWeek
import ru.apmgor.forecast.data.models.DailyForecast
import ru.apmgor.forecast.data.models.Units

@Composable
fun DailyForecast(
    timezone_offset: Long,
    units: List<Units>,
    daily: List<DailyForecast>,
    openDailyForecastAddInfo: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tempUnit = stringArrayResource(id = R.array.temp_units)

    daily.forEachIndexed { index, it ->
        Column (
            modifier = modifier
                .padding(horizontal = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { openDailyForecastAddInfo(index) }
                    .padding(horizontal = 8.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "${it.dt.toDayWeek(timezone_offset)} ${it.dt.toDay(true,timezone_offset)}",
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${units[0].revert(it.temp.tempDay)} / ${units[0].revert(it.temp.tempNight)}" +
                            "°${units[0].getResName(tempUnit)}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Image(
                    painter = painterResource(id = getDrawableResFromModel(it.weather.weatherIcon)),
                    contentDescription = stringResource(R.string.weather_image),
                    modifier = Modifier
                        .size(width = 30.dp, height = 20.dp)
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
            Divider()
        }
    }
}