package ru.apmgor.forecast.data.locationprovider

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface LocationService {
    @GET("geo/1.0/direct")
    suspend fun getLocationsByCityName(
        @QueryMap options: Map<String, String>
    ): Response<List<LocationInternet>>

    @GET("geo/1.0/reverse")
    suspend fun getLocationsByCoordinates(
        @QueryMap options: Map<String, String>
    ): Response<List<LocationInternet>>
}

data class LocationInternet(
    val name: String,
    val local_names: LocalNames?,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
)

data class LocalNames(val ru: String?)



