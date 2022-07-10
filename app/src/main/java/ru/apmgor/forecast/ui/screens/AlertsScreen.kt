package ru.apmgor.forecast.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.apmgor.forecast.R
import ru.apmgor.forecast.data.adapters.toDay
import ru.apmgor.forecast.data.adapters.toTime
import ru.apmgor.forecast.data.models.AlertsForecast
import ru.apmgor.forecast.data.models.Units

@Composable
fun AlertsScreen(
    timezone_offset: Long,
    units: List<Units>,
    alerts: List<AlertsForecast>,
    navigateBackToMainScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface {
        Column(modifier = modifier.fillMaxSize()) {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = navigateBackToMainScreen
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.icon_back_arrow_desc)
                        )
                    }
                },
                title = { Text(text = stringResource(R.string.title_alerts_screen)) },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.emblem_of_roshydromet),
                        contentDescription = stringResource(R.string.emblem_of_roshydromet),
                        modifier = Modifier.padding(all = 8.dp)
                    )
                }
            )
            LazyColumn {
                items(alerts) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = it.event, fontSize = 14.sp)
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(
                                text = "${(units[5].revert(it.start) as Long).toTime(units[5].unit,timezone_offset)}, " +
                                        "${it.start.toDay(true, timezone_offset)} -" +
                                        " ${(units[5].revert(it.end) as Long).toTime(units[5].unit,timezone_offset)}, " +
                                        it.end.toDay(true,timezone_offset),
                                fontSize = 12.sp
                            )
                        }

                        Text(text = it.description, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                    Divider(startIndent = 16.dp)
                }
            }
        }
    }
}