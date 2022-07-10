package ru.apmgor.forecast.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.apmgor.forecast.data.locationprovider.isLocationPermissionGranted

@Composable
fun ErrorScreen(
    error: Throwable,
    getWeatherForecastByCity: () -> Unit,
    getForecastByCoordinates: () -> Unit,
    navigateToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = error.message!!,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(all = 16.dp)
            )
            Button(
                onClick = {
                    if (isLocationPermissionGranted(context)) {
                        getForecastByCoordinates()
                    } else {
                        getWeatherForecastByCity()
                    }
                    navigateToMain()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Повторить")
            }
        }
    }
}