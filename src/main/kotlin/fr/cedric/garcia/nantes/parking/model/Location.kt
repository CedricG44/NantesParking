package fr.cedric.garcia.nantes.parking.model

import kotlin.math.abs
import kotlin.math.hypot

data class Location(

        val lat: Double,
        val lon: Double
) {
    companion object {

        fun distance(location: Location, parkingLocation: Location): Double =
                if (location.lat == parkingLocation.lat && location.lon == parkingLocation.lon)
                    0.0
                else
                    hypot(abs(location.lat - parkingLocation.lat), abs(location.lon - parkingLocation.lon))
    }
}
