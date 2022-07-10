package ru.apmgor.forecast.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.apmgor.forecast.data.ForecastRepo
import ru.apmgor.forecast.data.models.*
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val repo: ForecastRepo,
) : ViewModel() {

    val unitsState = repo.getUnitsFromDB()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Units.defaultUnitsList
        )

    val cityState = repo.getCityFromDb()

    private val _citiesState =
        MutableStateFlow<UiState<List<City>>>(UiState.NoContent)
    val citiesState = _citiesState.asStateFlow()

    private val _weatherForecastState =
        MutableStateFlow<UiState<WeatherForecast>>(UiState.Loading)
    val weatherForecastState = _weatherForecastState.asStateFlow()


    fun getCitiesByName(cityName: String) {
        _citiesState.value = UiState.Loading
        viewModelScope.launch {
            _citiesState.value = try {
                UiState.Content(repo.getCitiesByName(cityName))
            } catch (error: Throwable) {
                UiState.Error(error)
            }
        }
    }

    fun addChangedCitiesState(state: UiState<List<City>>) {
        _citiesState.value = state
    }

    fun getWeatherForecastByCoordinates() {
        _weatherForecastState.value = UiState.Loading
        viewModelScope.launch {
            _weatherForecastState.value = try {
                UiState.Content(
                    repo.getDataForecast(
                        repo.getCityByCoordinates(
                            repo.getCoordinates()
                        )
                    )
                )
            } catch (error: Throwable) {
                UiState.Error(error)
            }
        }
    }


    fun getWeatherForecastByCity(city: City) {
        _weatherForecastState.value = UiState.Loading
        viewModelScope.launch {
            viewModelScope.launch {
                _weatherForecastState.value = try {
                    UiState.Content(
                        repo.getDataForecast(city)
                    )
                } catch (error: Throwable) {
                    UiState.Error(error)
                }
            }
        }
    }

    fun saveUnits(units: List<Units>) {
        viewModelScope.launch {
            repo.saveUnitsToDB(units)
        }
    }
}

sealed interface UiState<out T> {
    object NoContent : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Content<T>(val content: T) : UiState<T>
    data class Error(val error: Throwable) : UiState<Nothing>
}