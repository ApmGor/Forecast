package ru.apmgor.forecast.data.locationprovider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.apmgor.forecast.BuildConfig
import ru.apmgor.forecast.data.adapters.LocationGps
import ru.apmgor.forecast.data.errors.Errors
import ru.apmgor.forecast.data.models.Coordinates
import javax.inject.Inject

class LocationProviderImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val locationService: LocationService,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest
) : LocationProvider {

    private val _queryLocationService = mutableMapOf(
        Pair("appid", BuildConfig.API_KEY),
        Pair("limit", NUM_CITY_IN_OUTPUT)
    )

    @SuppressLint("MissingPermission")
    override fun getLocationCoordinates(): Flow<LocationGps> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastLoc = locationResult.lastLocation
                if (lastLoc != null)
                    trySendBlocking(lastLoc)
            }
        }

        try {
            if (isGPSEnabled()) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    callback,
                    Looper.getMainLooper()
                )
            } else {
                throw Errors.GPSError
            }
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    override suspend fun getLocationsByCityName(cityName: String): List<LocationInternet> {
        _queryLocationService -= listOf("lat","lon")
        _queryLocationService["q"] = cityName
        val response = locationService.getLocationsByCityName(_queryLocationService.toMap())
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Errors.APIError(response.code())
        }
    }

    override suspend fun getLocationsByCoordinates(coordinates: Coordinates): List<LocationInternet> {
        _queryLocationService -= "q"
        _queryLocationService += mapOf(
            "lat" to coordinates.latitude.toString(),
            "lon" to coordinates.longitude.toString()
        )
        val response = locationService.getLocationsByCoordinates(_queryLocationService.toMap())
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Errors.APIError(response.code())
        }
    }

    companion object {
        const val NUM_CITY_IN_OUTPUT = "5"
    }

    private fun isGPSEnabled() =
        (appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
            LocationManager.GPS_PROVIDER)
}

fun isLocationPermissionGranted(context: Context) =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
