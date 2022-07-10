package ru.apmgor.forecast.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.apmgor.forecast.ui.theme.ForecastTheme
import ru.apmgor.forecast.R
import ru.apmgor.forecast.data.models.*
import ru.apmgor.forecast.data.models.CurrentForecast

@Composable
fun CurrentForecast(
    units: List<Units>,
    current: CurrentForecast,
    alerts: List<AlertsForecast>,
    navigateToAlerts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tempUnit = stringArrayResource(id = R.array.temp_units)
    val windName = stringArrayResource(id = R.array.wind_names)

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .heightIn(min = 200.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 30.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = getDrawableResFromModel(current.weather.weatherIcon)),
                    contentDescription = stringResource(R.string.weather_image),
                    modifier = Modifier.size(width = 40.dp, height = 27.dp)
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = current.weather.weatherDesc,
                        fontSize = 14.sp
                    )
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = windName[current.wind.windName.id],
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(
                        id = R.string.current_temp,
                        units[0].revert(current.temp),
                        units[0].getResName(tempUnit)
                    ),
                    fontSize = 70.sp
                )
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = stringResource(
                            id = R.string.current_feel_like,
                            units[0].revert(current.feelsLike),
                            units[0].getResName(tempUnit)
                        ),
                        fontSize = 12.sp
                    )
                }
                if (alerts.isNotEmpty()) {
                    Button(
                        onClick = navigateToAlerts,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.padding(top = 30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.height(15.dp)
                        )
                        Text(
                            text = alerts.first().event + if (alerts.size > 1) " + ${alerts.size - 1}" else "",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

        }
    }
}

fun getDrawableResFromModel(resName: String) : Int {
    val drawableClass = R::class.java.declaredClasses.find { it.simpleName == "drawable" }!!
    return drawableClass.getField(resName).getInt(drawableClass)
}

@Preview
@Composable
fun CurrentForecastPreview() {
    ForecastTheme {
//        CurrentForecast(
//            CurrentForecast(
//                temp = 13,
//                feelsLike = 15,
//                pressure = 1000,
//                humidity = 12,
//                dewPoint = 13.8,
//                uvi = 4.5,
//                visibility = 10000,
//                Wind(13.5,5),
//                Weather("Пасмурно","f11d")
//            ),
//            listOf(
//                AlertsForecast(
//                    event = "Ливень",
//                    start = 168979777876,
//                    end = 164324435778,
//                    description = "Сильный ливень"
//                )
//            )
//        )
    }
}