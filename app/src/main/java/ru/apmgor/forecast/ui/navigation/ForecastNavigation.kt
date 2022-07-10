package ru.apmgor.forecast.ui.navigation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import ru.apmgor.forecast.activities.*
import ru.apmgor.forecast.data.errors.Errors
import ru.apmgor.forecast.data.models.City
import ru.apmgor.forecast.data.models.WeatherForecast
import ru.apmgor.forecast.ui.screens.*

sealed class Routes(val rout: String) {
    object Main: Routes("main")
    object Settings: Routes("settings")
    object Alerts: Routes("alerts")
    object Search: Routes("search")
}

@Composable
fun ForecastNavigation() {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<ForecastViewModel>()
    val searchCitiesState by viewModel.citiesState.collectAsState()
    val weatherForecastState by viewModel.weatherForecastState.collectAsState()
    val units by viewModel.unitsState.collectAsState()
    val cityFlow by remember { mutableStateOf(viewModel.cityState) }
    val scope = rememberCoroutineScope()
    val requestPerm = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        scope.launch {
            cityFlow.collect { city ->
                if (city == null) {
                    if (it.values.contains(true)) {
                        viewModel.getWeatherForecastByCoordinates()
                    } else {
                        viewModel.getWeatherForecastByCity(City())
                    }
                } else {
                    viewModel.getWeatherForecastByCity(city)
                }
            }
        }
    }

    LaunchedEffect(true) {
        requestPerm.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val systemUiController = rememberSystemUiController()
    if (isSystemInDarkTheme()) {
        systemUiController.setSystemBarsColor(
            color = MaterialTheme.colors.background
        )
    } else {
        systemUiController.setSystemBarsColor(
            color = MaterialTheme.colors.background
        )
    }


    NavHost(navController = navController, startDestination = Routes.Main.rout) {
        composable(Routes.Main.rout) {
            MainScreen(
                units = units,
                weatherForecastState = weatherForecastState,
                navigateToSettings = { navController.navigate(Routes.Settings.rout) },
                navigateToAlerts = { navController.navigate(Routes.Alerts.rout) },
                navigateToSearch = { navController.navigate(Routes.Search.rout) },
                getScope = { scope },
                getWeatherForecastByCity = {
                    scope.launch {
                        cityFlow.collect {
                            if (it != null) {
                                viewModel.getWeatherForecastByCity(it)
                            } else {
                                viewModel.getWeatherForecastByCity(City())
                            }
                        }
                    }
                },
                getForecastByCoordinates = { viewModel.getWeatherForecastByCoordinates() },
                navigateToMain = { navController.navigate(Routes.Main.rout) { popUpTo(Routes.Main.rout) { inclusive = true } } }
            )
        }
        composable(Routes.Settings.rout) {
            SettingsScreen(
                units = units,
                saveUnit = { unit ->
                    viewModel.saveUnits(units.map { if (it::class == unit::class) unit else it })
                },
                navigateBackToMainScreen = { navController.popBackStack() }
            )
        }
        composable(Routes.Alerts.rout) {
            val forecast = (weatherForecastState as UiState.Content<WeatherForecast>).content
            AlertsScreen(
                timezone_offset = forecast.city.timezone_offset!!,
                units = units,
                alerts = forecast.listAlertsForecast,
                navigateBackToMainScreen = { navController.popBackStack() }
            )
        }
        composable(Routes.Search.rout) {
            SearchScreen(
                searchCitiesState = searchCitiesState,
                navigateBackToMainScreen = { navController.popBackStack() },
                getForecastByCity = { city -> viewModel.getWeatherForecastByCity(city) },
                getCitiesByName = { cityName -> viewModel.getCitiesByName(cityName) },
                getForecastByCoordinates = { viewModel.getWeatherForecastByCoordinates() },
                resetSearchResult = { viewModel.addChangedCitiesState(UiState.NoContent) },
                permissionError = { viewModel.addChangedCitiesState(UiState.Error(Errors.LocationPermissionError)) }
            )
        }
    }
}