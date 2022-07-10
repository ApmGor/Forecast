package ru.apmgor.forecast.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.apmgor.forecast.activities.UiState
import ru.apmgor.forecast.data.models.Units
import ru.apmgor.forecast.data.models.WeatherForecast
import ru.apmgor.forecast.ui.components.*

const val DAILY_ADD_INFO_CLOSE = -1

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    units: List<Units>,
    weatherForecastState: UiState<WeatherForecast>,
    navigateToSettings: () -> Unit,
    navigateToAlerts: () -> Unit,
    navigateToSearch: () -> Unit,
    getScope: () -> CoroutineScope,
    getWeatherForecastByCity: () -> Unit,
    getForecastByCoordinates: () -> Unit,
    navigateToMain: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    var check by remember { mutableStateOf(false) }
    var tabNumber by remember { mutableStateOf(DAILY_ADD_INFO_CLOSE) }


    when(weatherForecastState) {
        is UiState.NoContent -> {}
        is UiState.Loading -> LoadingScreen()
        is UiState.Content -> {
            val currentForecast = weatherForecastState.content.currentForecast
            val listMinutelyForecast = weatherForecastState.content.listMinutelyForecast
            val listAlertsForecast = weatherForecastState.content.listAlertsForecast
            val listHourlyForecast =
                weatherForecastState.content.listHourlyPlusSunsetSunrise
            val listDailyForecast = weatherForecastState.content.listDailyForecast
            val city = weatherForecastState.content.city
            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    TopBar(
                        city = city,
                        navigateToSearch = navigateToSearch,
                        navigateToSettings = navigateToSettings
                    )
                }
            ) {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = check),
                    onRefresh = {
                        getScope().launch {
                            check = true
                            getWeatherForecastByCity()
                            check = false
                        }
                    }
                ) {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        CurrentForecast(
                            units = units,
                            current = currentForecast,
                            alerts = listAlertsForecast,
                            navigateToAlerts = navigateToAlerts
                        )
                        PrecipitationInfo(minutely = listMinutelyForecast)
                        if (
                            listMinutelyForecast.isNotEmpty() &&
                            listMinutelyForecast.any { it.precipitation != 0.0 }
                        ) {
                            PrecipitationChart(
                                units = units,
                                minutely = listMinutelyForecast
                            )
                        }
                        CurrentForecastAddInfo(
                            units = units,
                            current = currentForecast
                        )
                        HourlyForecast(
                            timezone_offset = city.timezone_offset!!,
                            units = units,
                            hourly = listHourlyForecast
                        )
                        if (tabNumber == -1) {
                            DailyForecast(
                                timezone_offset = city.timezone_offset!!,
                                units = units,
                                daily = listDailyForecast,
                                openDailyForecastAddInfo = { index -> tabNumber = index }
                            )
                        } else {
                            DailyForecastAddInfo(
                                timezone_offset = city.timezone_offset!!,
                                units = units,
                                daily = listDailyForecast,
                                getScope = getScope,
                                getSelectedTab = { tabNumber },
                                setSelectedTab = { index -> tabNumber = index }
                            )
                        }
                    }
                }
            }
        }
        is UiState.Error -> ErrorScreen(
            error = weatherForecastState.error,
            getWeatherForecastByCity = getWeatherForecastByCity,
            getForecastByCoordinates = getForecastByCoordinates,
            navigateToMain = navigateToMain
        )
    }
}