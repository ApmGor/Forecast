package ru.apmgor.forecast.ui.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.apmgor.forecast.R
import ru.apmgor.forecast.data.models.City
import ru.apmgor.forecast.ui.theme.ForecastTheme

@Composable
fun TopBar(
    city: City,
    navigateToSearch: () -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(text = city.name)
        },
        navigationIcon = {
            IconButton(
                onClick = navigateToSearch
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.icon_search_desc)
                )
            }
        },
        actions = {
            IconButton(
                onClick = navigateToSettings
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(id = R.string.icon_settings_desc)
                )
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun TopBarPreview() {
    ForecastTheme {
        //TopBar({}, { "" })
    }
}