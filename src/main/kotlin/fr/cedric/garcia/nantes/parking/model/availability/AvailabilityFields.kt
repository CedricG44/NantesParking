package fr.cedric.garcia.nantes.parking.model.availability

data class AvailabilityFields(

        val idobj: Int,
        val grp_nom: String,
        val grp_identifiant: Int,
        val grp_statut: Int,
        val grp_complet: Int,
        val grp_disponible: Int,
        val grp_exploitation: Int,
        val grp_horodatage: String
)
