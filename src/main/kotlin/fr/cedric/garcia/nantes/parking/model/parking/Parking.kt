package fr.cedric.garcia.nantes.parking.model.parking

import fr.cedric.garcia.nantes.parking.model.Location

data class Parking(

        val id: Int,
        val fullName: String,
        val presentation: String,
        val type: String,
        val category: String,
        val owner: String,
        val phone: String,
        val website: String,
        val address: String,
        val city: String,
        val postcode: Int,
        val location: Location,
        val meansOfPayment: String,

        val bikeParking: Boolean,
        val secureBikeParking: Boolean,
        val bikeCapacity: Int,

        val motorbikeCapacity: Int,
        val carCapacity: Int,
        val electricCarCapacity: Int,

        val disabledAccess: Boolean,
        val disabledCapacity: Int,

        val publicTransportationAccess: String
) {
    companion object {
        fun mapToParking(parkingFields: ParkingFields): Parking =
                Parking(
                        parkingFields.idobj,
                        parkingFields.nom_complet,
                        parkingFields.presentation ?: "",
                        parkingFields.libtype ?: "",
                        parkingFields.libcategorie ?: "",
                        parkingFields.exploitant ?: "",
                        parkingFields.telephone,
                        parkingFields.site_web,
                        parkingFields.adresse,
                        parkingFields.commune,
                        parkingFields.code_postal,
                        parkingFields.location,
                        parkingFields.moyen_paiement ?: "",
                        mapToBoolean(parkingFields.stationnement_velo),
                        mapToBoolean(parkingFields.stationnement_velo_securise),
                        parkingFields.capacite_velo ?: 0,
                        parkingFields.capacite_moto ?: 0,
                        parkingFields.capacite_voiture,
                        parkingFields.capacite_vehicule_electrique ?: 0,
                        mapToBoolean(parkingFields.acces_pmr),
                        parkingFields.capacite_pmr ?: 0,
                        parkingFields.acces_transports_communs ?: ""
                )

        private fun mapToBoolean(attribute: String?) =
                if (!attribute.isNullOrEmpty()) "OUI" == attribute && "NON" != attribute else false
    }
}
