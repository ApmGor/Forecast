package ru.apmgor.forecast.data.locationprovider

import kotlinx.coroutines.flow.Flow
import ru.apmgor.forecast.data.adapters.LocationGps
import ru.apmgor.forecast.data.errors.Errors
import ru.apmgor.forecast.data.models.Coordinates
import kotlin.jvm.Throws

interface LocationProvider {
    @Throws(Errors.GPSError::class)
    fun getLocationCoordinates(): Flow<LocationGps>
    @Throws(Errors.APIError::class)
    suspend fun getLocationsByCityName(cityName: String): List<LocationInternet>
    @Throws(Errors.APIError::class,Errors.InternetConnectionError::class)
    suspend fun getLocationsByCoordinates(coordinates: Coordinates): List<LocationInternet>
}