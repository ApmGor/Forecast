package ru.apmgor.forecast.data.adapters

import android.location.Location
import ru.apmgor.forecast.data.locationprovider.LocationInternet
import ru.apmgor.forecast.data.models.City
import ru.apmgor.forecast.data.models.Coordinates

typealias LocationGps = Location

fun LocationGps.toCoordinates() = Coordinates(this.latitude, this.longitude)

fun LocationInternet.toCity() =
    City(
        coordinates = Coordinates(lat, lon),
        state = state,
        country = country,
        name = local_names?.ru ?: name
    )