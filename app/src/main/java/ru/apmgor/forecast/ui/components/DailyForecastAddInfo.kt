package ru.apmgor.forecast.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.apmgor.forecast.R
import ru.apmgor.forecast.data.adapters.toDay
import ru.apmgor.forecast.data.adapters.toDayWeek
import ru.apmgor.forecast.data.adapters.toTime
import ru.apmgor.forecast.data.models.DailyForecast
import ru.apmgor.forecast.data.models.Units
import ru.apmgor.forecast.ui.screens.DAILY_ADD_INFO_CLOSE

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DailyForecastAddInfo(
    timezone_offset: Long,
    units: List<Units>,
    daily: List<DailyForecast>,
    getScope: () -> CoroutineScope,
    getSelectedTab: () -> Int,
    setSelectedTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(getSelectedTab())
    val tempUnit = stringArrayResource(id = R.array.temp_units)
    val precipUnit = stringArrayResource(id = R.array.precip_units)
    val windUnit = stringArrayResource(id = R.array.wind_units)
    val pressureUnit = stringArrayResource(id = R.array.pressure_units)
    val windName = stringArrayResource(id = R.array.wind_names)
    val windDir = stringArrayResource(id = R.array.wind_directions)
    val indicator = @Composable { tabPosition: List<TabPosition> ->
        CardIndicator(Modifier.tabIndicatorOffset(tabPosition[pagerState.currentPage]))
    }

    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                divider = {},
                indicator = indicator,
                backgroundColor = MaterialTheme.colors.background,
                modifier = Modifier.weight(1f)
            ) {
                daily.forEachIndexed { index, it ->
                    val selected = index == pagerState.currentPage
                    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                        Tab(
                            selected = selected,
                            onClick = { getScope().launch { pagerState.scrollToPage(index)} },
                            modifier = Modifier
                                .width(35.dp)
                                .padding(all = 8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (!selected) {
                                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                        Text(text = it.dt.toDayWeek(timezone_offset), fontSize = 12.sp)
                                        Text(text = it.dt.toDay(false,timezone_offset), fontSize = 12.sp)
                                    }
                                } else {
                                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                        Text(text = it.dt.toDayWeek(timezone_offset), fontSize = 12.sp)
                                    }
                                    Text(text = it.dt.toDay(false,timezone_offset), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
            IconButton(
                onClick = { setSelectedTab(DAILY_ADD_INFO_CLOSE) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.icon_close_daily_add_info)
                )
            }
        }
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
    HorizontalPager(
        count = daily.size,
        state = pagerState,
    ) { page ->
        val day = daily[page]
        Column(modifier = modifier.padding(horizontal = 8.dp)) {
            DailyRow(
                unitName = day.weather.weatherDesc,
                isTextNameBolded = true,
                unitSubName = windName[day.wind.windName.id],
                unitValue = "${units[0].revert(day.temp.tempDay)} / ${units[0].revert(day.temp.tempNight)}" +
                        "°${units[0].getResName(tempUnit)}",
                image = day.weather.weatherIcon
            )
            DailyRow(
                unitName = stringResource(R.string.daily_precipitation),
                unitValue = String.format("%1$.1f%2\$s",
                    units[3].revert(day.rain ?: 0.0) as Double,
                    units[3].getResName(precipUnit)
                )
            )
            DailyRow(
                unitName = stringResource(R.string.daily_pop),
                unitValue = String.format("%1$.1f%%",day.pop)
            )
            DailyRow(
                unitName = stringResource(R.string.daily_wind),
                unitValue = String.format("%1$.1f%2\$s %3\$s",
                    units[1].revert(day.wind.windSpeed),
                    units[1].getResName(windUnit),
                    windDir[day.wind.windDirection.id]
                ),
                image = if (isSystemInDarkTheme())
                    day.wind.windDirection.arrow_white
                else
                    day.wind.windDirection.arrow
            )
            DailyRow(
                unitName = stringResource(R.string.daily_pressure),
                unitValue = "${units[2].revert(day.pressure)}${units[2].getResName(pressureUnit)}"
            )
            DailyRow(
                unitName = stringResource(R.string.daily_humidity),
                unitValue = "${day.humidity}%"
            )
            DailyRow(
                unitName = stringResource(R.string.daily_uvi),
                unitValue = String.format("%1$.1f",day.uvi)
            )
            DailyRow(
                unitName = stringResource(id = R.string.sunrise_desc),
                unitValue = (units[5].revert(day.sunrise) as Long).toTime(units[5].unit,timezone_offset)
            )
            DailyRow(
                unitName = stringResource(id = R.string.sunset_desc),
                unitValue = (units[5].revert(day.sunset) as Long).toTime(units[5].unit,timezone_offset)
            )
        }
    }
}

@Composable
fun ColumnScope.DailyRow(
    unitName: String,
    isTextNameBolded: Boolean = false,
    unitSubName: String? = null,
    unitValue: String,
    image: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = unitName,
                fontWeight = if (isTextNameBolded) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
            if (unitSubName != null) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = unitSubName,
                        fontSize = 12.sp
                    )
                }
            }
        }
        Text(text = unitValue, fontSize = 14.sp)
        if (image != null) {
            Image(
                painter = painterResource(id = getDrawableResFromModel(image)),
                contentDescription = stringResource(R.string.weather_image),
                modifier = Modifier
                    .size(width = 30.dp, height = 20.dp)
                    .padding(start = 8.dp)
            )
        }
    }
    Divider()
}


@Composable
fun CardIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(width = 35.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.06f))
    )
}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleAlpha(0.0f,0.0f,0.0f,0.0f)
}