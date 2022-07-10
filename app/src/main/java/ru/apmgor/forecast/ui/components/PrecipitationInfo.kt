package ru.apmgor.forecast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.apmgor.forecast.R
import ru.apmgor.forecast.data.models.MinutelyForecast

@Composable
fun PrecipitationInfo(
    minutely: List<MinutelyForecast>,
    modifier: Modifier = Modifier
) {
    val resources = LocalContext.current.resources

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(all = 8.dp)
        ) {
            when {
                minutely.isEmpty() ||
                        minutely.all { it.precipitation == 0.0 } ->
                    Text(
                        text = stringResource(id = R.string.no_precipitation),
                        fontSize = 14.sp
                    )
                minutely[0].precipitation != 0.0 &&
                        minutely[minutely.size - 1].precipitation != 0.0 ->
                    Text(
                        text = stringResource(id = R.string.full_hour_precipitation),
                        fontSize = 14.sp
                    )
                minutely[0].precipitation != 0.0 -> {
                    val quantity = minutely.indexOf(minutely.first { it.precipitation == 0.0 })
                    Text(
                        text = resources.getQuantityString(
                            R.plurals.number_of_minutes_1,
                            quantity,
                            quantity
                        ),
                        fontSize = 14.sp
                    )
                }
                else -> {
                    val quantity = minutely.indexOf(minutely.first { it.precipitation != 0.0 })
                    Text(
                        text = resources.getQuantityString(
                            R.plurals.number_of_minutes_2,
                            quantity,
                            quantity
                        ),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}