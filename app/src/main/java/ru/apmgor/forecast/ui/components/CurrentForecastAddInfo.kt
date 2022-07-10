package ru.apmgor.forecast.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.apmgor.forecast.R
import ru.apmgor.forecast.data.models.CurrentForecast
import ru.apmgor.forecast.data.models.Units
import ru.apmgor.forecast.ui.theme.ForecastTheme

@Composable
fun CurrentForecastAddInfo(
    units: List<Units>,
    current: CurrentForecast,
    modifier: Modifier = Modifier
) {
    val tempUnit = stringArrayResource(id = R.array.temp_units)
    val windUnit = stringArrayResource(id = R.array.wind_units)
    val pressureUnit = stringArrayResource(id = R.array.pressure_units)
    val distUnit = stringArrayResource(id = R.array.dist_units)
    val windDir = stringArrayResource(id = R.array.wind_directions)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Column(modifier = Modifier.weight(1.3f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(
                            R.string.current_wind,
                            units[1].revert(current.wind.windSpeed),
                            units[1].getResName(windUnit),
                            windDir[current.wind.windDirection.id]
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (isSystemInDarkTheme()) {
                        Image(
                            painter = painterResource(
                                id = getDrawableResFromModel(
                                    current.wind.windDirection.arrow_white
                                )
                            ),
                            contentDescription = stringResource(R.string.arrow_wind_direction),
                            modifier = Modifier
                                .size(11.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(
                                id = getDrawableResFromModel(
                                    current.wind.windDirection.arrow
                                )
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(11.dp)
                        )
                    }
                }
                Text(
                    text = stringResource(
                        R.string.current_pressure,
                        units[2].revert(current.pressure),
                        units[2].getResName(pressureUnit)
                    ),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 16.dp))
            }
            Column(modifier = Modifier.weight(1.1f)) {
                Text(
                    text = stringResource(
                        R.string.current_humidity,
                        current.humidity
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = stringResource(
                        R.string.current_visibility,
                        units[4].revert(current.visibility),
                        units[4].getResName(distUnit)
                    ),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        R.string.current_uvi,
                        current.uvi
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = stringResource(
                        R.string.current_dew_point,
                        units[0].revert(current.dewPoint),
                        units[0].getResName(tempUnit)
                    ),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun CurrentForecastAddInfoPreview() {
    ForecastTheme {
        //CurrentForecastAddInfo()
    }
}