package ru.apmgor.forecast.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.apmgor.forecast.R
import ru.apmgor.forecast.activities.UiState
import ru.apmgor.forecast.data.locationprovider.isLocationPermissionGranted
import ru.apmgor.forecast.data.models.City
import ru.apmgor.forecast.ui.components.LoadingContent
import ru.apmgor.forecast.ui.theme.ForecastTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    searchCitiesState: UiState<List<City>>,
    navigateBackToMainScreen: () -> Unit,
    getForecastByCity: (City) -> Unit,
    getCitiesByName: (String) -> Unit,
    getForecastByCoordinates: () -> Unit,
    resetSearchResult: () -> Unit,
    permissionError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val scaffoldState = rememberScaffoldState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val requestPerm = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.values.contains(true)) {
            getForecastByCoordinates()
            resetSearchResult()
            navigateBackToMainScreen()
        } else {
            permissionError()
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 20.sp
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search,
                            capitalization = KeyboardCapitalization.Words
                        ),
                        keyboardActions = KeyboardActions {
                            if (text.isNotBlank())
                                getCitiesByName(text)
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                    ) {
                        if (text.isBlank()) {
                            Text(
                                text = stringResource(id = R.string.search_placeholder),
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                                letterSpacing = (-1).sp
                            )
                        }
                        it()
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            resetSearchResult()
                            navigateBackToMainScreen()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.icon_back_arrow_desc)
                        )
                    }
                },
                actions = {
                    if (text.isNotBlank()) {
                        IconButton(
                            onClick = {
                                text = ""
                                focusManager.clearFocus()
                                resetSearchResult()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(id = R.string.icon_clear_text_field_desc)
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            if (isLocationPermissionGranted(context)) {
                                getForecastByCoordinates()
                                resetSearchResult()
                                navigateBackToMainScreen()
                            } else {
                                requestPerm.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = stringResource(id = R.string.icon_gps_desc)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) {
        when (searchCitiesState) {
            is UiState.NoContent -> {}
            is UiState.Loading -> LoadingContent()
            is UiState.Content -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (searchCitiesState.content.isEmpty())
                        item {
                            Text(text = stringResource(R.string.empty_search_result))
                        }
                    items(searchCitiesState.content) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    getForecastByCity(it)
                                    resetSearchResult()
                                    navigateBackToMainScreen()
                                }
                                .padding(all = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = stringResource(id = R.string.icon_search_result_desc)
                            )
                            Text(
                                text = buildAnnotatedString {
                                    append("${it.name}, ")
                                    if (it.state != null)
                                        append("${it.state}, ")
                                    append(it.country)
                                },
                                modifier = Modifier.padding(start = 24.dp)
                            )
                        }

                    }
                }
            }
            is UiState.Error -> LaunchedEffect(searchCitiesState) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = searchCitiesState.error.localizedMessage!!,
                    duration = SnackbarDuration.Long
                )
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SearchScreenPreview() {
    ForecastTheme {
        //SearchScreen(SearchCitiesState.Loading,{},{},{},{},{})
    }
}