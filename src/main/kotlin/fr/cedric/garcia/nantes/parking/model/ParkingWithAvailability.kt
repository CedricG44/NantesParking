package fr.cedric.garcia.nantes.parking.model

import fr.cedric.garcia.nantes.parking.model.availability.Availability
import fr.cedric.garcia.nantes.parking.model.parking.Parking

data class ParkingWithAvailability(val parking: Parking, val availability: Availability)
