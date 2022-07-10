package ru.apmgor.forecast.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.apmgor.forecast.R
import ru.apmgor.forecast.data.models.*
import ru.apmgor.forecast.ui.theme.ForecastTheme

@Composable
fun SettingsScreen(
    units: List<Units>,
    saveUnit: (Units) -> Unit,
    navigateBackToMainScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val compare = mapOf(
        TempUnits::class to Pair(R.string.temp_unit_desc, R.array.temp_units),
        WindSpeedUnits::class to Pair(R.string.wind_unit_desc,R.array.wind_units),
        PressureUnits::class to Pair(R.string.pressure_unit_desc,R.array.pressure_units),
        PrecipUnits::class to Pair(R.string.precip_unit_desc,R.array.precip_units),
        DistUnits::class to Pair(R.string.dist_unit_desc,R.array.dist_units),
        TimeUnits::class to Pair(R.string.time_unit_desc,R.array.time_units)
    )

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
                title = { Text(text = stringResource(R.string.title_settings_screen)) }
            )
            units.forEach {
                SettingsElement(
                    unit = it,
                    unitsArray = stringArrayResource(id = compare[it::class]!!.second),
                    unitName = stringResource(id = compare[it::class]!!.first),
                    saveUnit = saveUnit
                )
            }
        }
    }
}

@Composable
fun ColumnScope.SettingsElement(
    unit: Units,
    unitsArray: Array<String>,
    unitName: String,
    saveUnit: (Units) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)) {
            Text(text = unitName, fontSize = 18.sp)
            Text(
                text = unit.getResName(unitsArray),
                fontSize = 14.sp,
                color = MaterialTheme.colors.primary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            unitsArray.forEachIndexed { unitValue, unitName ->
                DropdownMenuItem(
                    onClick = {
                        saveUnit(unit.copy(unitValue))
                        expanded = false
                    }
                ) {
                    Text(text = unitName)
                }
            }
        }
    }
    Divider(startIndent = 16.dp)
}

@Preview
@Composable
fun SettingsScreenPreview() {
    ForecastTheme {
        //SettingsScreen()
    }
}