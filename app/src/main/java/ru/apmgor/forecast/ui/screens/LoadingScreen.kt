package ru.apmgor.forecast.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.apmgor.forecast.R
import ru.apmgor.forecast.ui.theme.ForecastTheme

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {

    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.rain_drops_logo),
                contentDescription = null,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .weight(1f)
                    .size(130.dp)
            )
            Text(
                text = "Загрузка...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun LoadingScreenPreview() {
    ForecastTheme {
        //LoadingScreen()
    }
}