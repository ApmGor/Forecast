package ru.apmgor.forecast.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import ru.apmgor.forecast.ui.navigation.ForecastNavigation
import ru.apmgor.forecast.ui.theme.ForecastTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForecastTheme {
                ForecastNavigation()
            }
        }
    }
}