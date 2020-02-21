package fr.cedric.garcia.nantes.parking.model.parking

import fr.cedric.garcia.nantes.parking.model.Location

data class ParkingFields(

        val idobj: Int,
        val nom_complet: String,
        val presentation: String?,
        val libtype: String?,
        val libcategorie: String,
        val exploitant: String?,
        val telephone: String,
        val site_web: String,
        val adresse: String,
        val commune: String,
        val code_postal: Int,
        val location: Location,
        val moyen_paiement: String?,

        val service_velo: String?,
        val stationnement_velo: String?,
        val stationnement_velo_securise: String?,
        val capacite_velo: Int?,

        val capacite_moto: Int?,
        val capacite_voiture: Int,
        val capacite_vehicule_electrique: Int?,

        val acces_pmr: String,
        val capacite_pmr: Int?,

        val infos_complementaires: String?,
        val acces_transports_communs: String?
)
