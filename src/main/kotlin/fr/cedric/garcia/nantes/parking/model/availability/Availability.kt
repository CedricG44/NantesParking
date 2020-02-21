package fr.cedric.garcia.nantes.parking.model.availability

data class Availability(

        val parkingId: Int,
        val parkingName: String,
        val status: Int,
        val nbSpotsBeforeFull: Int,
        val availableSpots: Int,
        val totalSpots: Int,
        val timestamp: String
) {
    companion object {

        fun mapToAvailability(availabilityFields: AvailabilityFields): Availability =
                Availability(
                        availabilityFields.idobj,
                        availabilityFields.grp_nom,
                        availabilityFields.grp_statut,
                        availabilityFields.grp_complet,
                        availabilityFields.grp_disponible,
                        availabilityFields.grp_exploitation,
                        availabilityFields.grp_horodatage.replace("+00:00", ".0000"))
    }
}
