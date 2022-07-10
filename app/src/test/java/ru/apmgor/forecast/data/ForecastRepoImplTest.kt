package ru.apmgor.forecast.data

import android.location.Location
import android.os.SystemClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.*
import ru.apmgor.forecast.activities.testedList
import ru.apmgor.forecast.data.database.*
import ru.apmgor.forecast.data.database.units.UnitsDAO
import ru.apmgor.forecast.data.database.units.UnitsEntity
import ru.apmgor.forecast.data.errors.Errors
import ru.apmgor.forecast.data.forecastprovider.CurrentForecastRet
import ru.apmgor.forecast.data.forecastprovider.ForecastProvider
import ru.apmgor.forecast.data.forecastprovider.ForecastRet
import ru.apmgor.forecast.data.locationprovider.LocalNames
import ru.apmgor.forecast.data.locationprovider.LocationInternet
import ru.apmgor.forecast.data.locationprovider.LocationProvider
import ru.apmgor.forecast.data.models.*

@OptIn(ExperimentalCoroutinesApi::class)
class ForecastRepoImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeLocProvider: LocationProvider
    private lateinit var fakeForecastProv: ForecastProvider
    private lateinit var fakeDB: ForecastDatabase
    private lateinit var fakeUnitsDAO: UnitsDAO
    private lateinit var fakeForecastDAO: ForecastDAO
    private lateinit var repo: ForecastRepo
    private val testUnitsEntity = UnitsEntity(
        0,
        0,
        0,
        0,
        0,
        0,
        0
    )
    private val testListLocInternet = listOf(
        LocationInternet("Москва",null,55.751244,37.618423,"RU",null),
        LocationInternet("Москва", LocalNames(null),55.751244,37.618423,"RU",null),
        LocationInternet("Москва", LocalNames("Moscow"),55.751244,37.618423,"RU",null),
    )
    private val  testWeatherForecastEntity = WeatherForecastEntity(
        CityEntity(0,"Москва",null,"RU",55.751244,37.618423,0),
        CurrentForecastEntity(0,0,0.0,0.0,0,0,0.0,0.0,0,0.0,0,"",""),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList()
    )

    private val testWeatherForecast = WeatherForecast()
        .copy(city = City().copy(timezone_offset = 0),currentForecast = CurrentForecast()
            .copy(weather = Weather("","f"))
        )

    private val testForecastRet = ForecastRet(
        0,
        CurrentForecastRet(
            0.0,
            0.0,
            0,
            0,
            0.0,
            0.0,
            0,
            0.0,
            0,
            listOf(ru.apmgor.forecast.data.forecastprovider.Weather("",""))
        ),
        emptyList(),
        emptyList(),
        emptyList(),
        null
    )

    @Before
    fun setUp() {
        fakeLocProvider = mock()
        fakeForecastProv = mock()
        fakeUnitsDAO = mock()
        fakeForecastDAO = mock()
        fakeDB = mock {
            on { getUnitsDAO() } doReturn fakeUnitsDAO
            on { getForecastDAO() } doReturn fakeForecastDAO
        }
        repo = ForecastRepoImpl(
            fakeLocProvider,
            fakeForecastProv,
            fakeDB,
            testDispatcher
        )
    }

    @Test
    fun testingGetUnitsFromDB() = runTest {
        fakeUnitsDAO.stub {
            on {
                getUnits()
            } doReturn flowOf(testUnitsEntity) doReturn flowOf(null)
        }
        val listUnits = repo.getUnitsFromDB().first()
        val nullableListUnits = repo.getUnitsFromDB().firstOrNull()
        verify(fakeDB, times(2)).getUnitsDAO()
        verify(fakeUnitsDAO, times(2)).getUnits()
        assertEquals(Units.defaultUnitsList, listUnits)
        assertEquals(null, nullableListUnits)
    }

    @Test
    fun testingSaveUnitsToDB() = runTest(testDispatcher.scheduler) {
        argumentCaptor {
            doNothing().whenever(fakeUnitsDAO).saveUnits(capture())
            repo.saveUnitsToDB(Units.defaultUnitsList)
            assertEquals(testUnitsEntity, firstValue)
        }
        verify(fakeUnitsDAO).saveUnits(eq(testUnitsEntity))
    }

    @Test(expected = Errors.APIError::class)
    fun testingGetCitiesByName() = runTest {
        argumentCaptor {
            fakeLocProvider.stub {
                onBlocking { getLocationsByCityName(capture()) } doReturn testListLocInternet doThrow Errors.APIError(401)
            }
            assertEquals(
                testedList.mapIndexed { index, city ->
                    if (index == 2) city.copy(name = "Moscow") else city
                },
                repo.getCitiesByName("Москва")
            )
            assertEquals("Москва",firstValue)
            repo.getCitiesByName("Москва")
        }
        verify(fakeLocProvider).getLocationsByCityName(eq("Москва"))
    }

    @Test(expected = Errors.APIError::class)
    fun testingGetDataForecast() = runTest(testDispatcher.scheduler) {
        fakeForecastDAO.stub {
            on { getWeatherForecast() } doReturn testWeatherForecastEntity doReturn null doReturn null
            on { getCity() } doReturn flowOf(testWeatherForecastEntity.city)
            on { getTimeout() } doReturn TimeoutEntity(0,0)
        }
        mockStatic(SystemClock::class.java).use {
            whenever(SystemClock.elapsedRealtime()).thenReturn(0)
            val forecast1 = repo.getDataForecast(City())
            assertEquals(testWeatherForecast, forecast1)
            verify(fakeForecastProv, never()).getForecast(any())

            argumentCaptor {
                fakeForecastProv.stub {
                    onBlocking { getForecast(capture()) } doReturn testForecastRet doThrow Errors.APIError(401)
                }
                val forecast2 = repo.getDataForecast(City())
                assertEquals(City().coordinates,firstValue)
                assertEquals(testWeatherForecast,forecast2)
                verify(fakeForecastDAO).saveTimeout(any())
                verify(fakeForecastDAO).saveForecast(capture(),capture(),capture(),capture(),capture(),capture())
                assertEquals(listOf(
                    testWeatherForecastEntity.component1(),
                    testWeatherForecastEntity.component2(),
                    testWeatherForecastEntity.component3(),
                    testWeatherForecastEntity.component4(),
                    testWeatherForecastEntity.component5(),
                    testWeatherForecastEntity.component6()
                )
                    ,allValues.subList(1,allValues.size)
                )
            }
        }
        repo.getDataForecast(City())
    }

    @Test(expected = Errors.GPSError::class)
    fun testingGetCoordinates() = runTest {
        val location = mock<Location> {
            on { latitude } doReturn 55.751244
            on { longitude } doReturn 37.618423
        }
        fakeLocProvider.stub {
            on { getLocationCoordinates() } doReturn flowOf(location) doThrow Errors.GPSError
        }
        assertEquals(Coordinates(),repo.getCoordinates())
        verify(location).latitude
        verify(location).longitude
        verify(fakeLocProvider).getLocationCoordinates()
        repo.getCoordinates()
    }

    @Test(expected = Errors.InternetConnectionError::class)
    fun testingGetCityByCoordinates() = runTest {
        argumentCaptor {
            fakeLocProvider.stub {
                onBlocking {
                    getLocationsByCoordinates(capture())
                } doReturn testListLocInternet doThrow Errors.InternetConnectionError
            }
            val city = repo.getCityByCoordinates(eq(Coordinates()))
            assertEquals(Coordinates(),firstValue)
            assertEquals(City(),city)
        }
        repo.getCityByCoordinates(any())
    }
}