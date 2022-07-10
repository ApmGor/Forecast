package ru.apmgor.forecast.activities

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import ru.apmgor.forecast.data.ForecastRepo
import ru.apmgor.forecast.data.errors.Errors
import ru.apmgor.forecast.data.models.*

class FakeRepository(private val repo: ForecastRepo) : ForecastRepo by repo {
    private val flow = MutableSharedFlow<List<Units>>()
    suspend fun emit(value: List<Units>) = flow.emit(value)
    override fun getUnitsFromDB() = flow
}

val testedList = listOf(City(), City(), City())

@OptIn(ExperimentalCoroutinesApi::class)
class ForecastViewModelTest {

    private lateinit var fakeRepo: ForecastRepo
    private lateinit var viewModel: ForecastViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        fakeRepo = mock {
            on { getCityFromDb() } doReturn flowOf(null, City())
        }
        viewModel = ForecastViewModel(fakeRepo)
    }

    @Test
    fun testingUnitState() = runTest(mainDispatcherRule.testDispatcher) {
        val newUnitsList = listOf(
            TempUnits(3),
            WindSpeedUnits(),
            PressureUnits(),
            PrecipUnits(5),
            DistUnits(),
            TimeUnits(1)
        )
        val fakeRepo = FakeRepository(mock())
        val viewModel = ForecastViewModel(fakeRepo)
        val collectJob = launch { viewModel.unitsState.collect() }
        assertEquals(Units.defaultUnitsList, viewModel.unitsState.value)
        fakeRepo.emit(newUnitsList)
        assertEquals(newUnitsList, viewModel.unitsState.value)
        collectJob.cancel()
    }

    @Test
    fun testingCityState() = runTest {
        val list = viewModel.cityState.take(2).toList()
        verify(fakeRepo).getCityFromDb()
        assertTrue(list.size == 2)
        assertNull(list[0])
        assertEquals(City(), list[1])
    }

    @Test
    fun testingCitiesState() = runTest(mainDispatcherRule.testDispatcher) {
        val list = mutableListOf<UiState<List<City>>>()
        val job = launch { viewModel.citiesState.toList(list) }
        argumentCaptor {
            fakeRepo.stub {
                onBlocking {
                    getCitiesByName(capture())
                } doReturn testedList doThrow Errors.InternetConnectionError doThrow Errors.APIError(401)
            }
            repeat(3) {
                viewModel.getCitiesByName("Москва")
                if (it == 0) assertEquals("Москва", firstValue)
            }
            verify(fakeRepo, times(3)).getCitiesByName(eq("Москва"))


        }
        assertTrue(list.size == 7)
        assertTrue(list.contains(UiState.NoContent))
        assertEquals(3, list.filter { it == UiState.Loading }.size)
        assertTrue(list.contains(UiState.Content(testedList)))
        assertTrue(list.contains(UiState.Error(Errors.InternetConnectionError)))
        assertTrue(list.contains(UiState.Error(Errors.APIError(401))))
        job.cancel()
    }

    @Test
    fun testingWeatherForecastState() = runTest(mainDispatcherRule.testDispatcher) {
        val list = mutableListOf<UiState<WeatherForecast>>()
        val job = launch { viewModel.weatherForecastState.toList(list) }
        argumentCaptor {
            fakeRepo.stub {
                onBlocking {
                    getDataForecast(capture())
                } doReturn WeatherForecast() doThrow Errors.InternetConnectionError doThrow Errors.APIError(401) doThrow Errors.GPSError
                onBlocking { getCityByCoordinates(capture()) } doReturn City()
                onBlocking { getCoordinates() } doReturn Coordinates()
            }
            repeat(4) {
                viewModel.getWeatherForecastByCoordinates()
                if (it == 0) assertEquals(listOf(Coordinates(),City()),allValues)
            }
            verify(fakeRepo, times(4)).getCoordinates()
            verify(fakeRepo, times(4)).getCityByCoordinates(eq(Coordinates()))
            verify(fakeRepo, times(4)).getDataForecast(eq(City()))
        }
        assertTrue(list.size == 8)
        assertEquals(4, list.filter { it == UiState.Loading }.size)
        assertTrue(list.contains(UiState.Content(WeatherForecast())))
        assertTrue(list.contains(UiState.Error(Errors.InternetConnectionError)))
        assertTrue(list.contains(UiState.Error(Errors.APIError(401))))
        assertTrue(list.contains(UiState.Error(Errors.GPSError)))
        job.cancel()
    }

    @Test
    fun testingWeatherForecastStateByCity() = runTest(mainDispatcherRule.testDispatcher) {
        val list = mutableListOf<UiState<WeatherForecast>>()
        val job = launch { viewModel.weatherForecastState.toList(list) }
        argumentCaptor {
            fakeRepo.stub {
                onBlocking {
                    getDataForecast(capture())
                } doReturn WeatherForecast() doThrow Errors.InternetConnectionError doThrow Errors.APIError(401) doThrow Errors.GPSError
            }
            repeat(4) {
                viewModel.getWeatherForecastByCity(City())
                if (it == 0) assertEquals(City(),firstValue)
            }
            verify(fakeRepo, times(4)).getDataForecast(eq(City()))
        }
        assertTrue(list.size == 8)
        assertEquals(4, list.filter { it == UiState.Loading }.size)
        assertTrue(list.contains(UiState.Content(WeatherForecast())))
        assertTrue(list.contains(UiState.Error(Errors.InternetConnectionError)))
        assertTrue(list.contains(UiState.Error(Errors.APIError(401))))
        assertTrue(list.contains(UiState.Error(Errors.GPSError)))
        job.cancel()
    }

    @Test
    fun testingSaveUnits() = runTest {
        argumentCaptor {
            fakeRepo.stub {
                onBlocking { saveUnitsToDB(capture()) } doReturn Unit
            }
            viewModel.saveUnits(Units.defaultUnitsList)
            assertEquals(Units.defaultUnitsList, firstValue)
        }
        verify(fakeRepo).saveUnitsToDB(eq(Units.defaultUnitsList))
    }
}