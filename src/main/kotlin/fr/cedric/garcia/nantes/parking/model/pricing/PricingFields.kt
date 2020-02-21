package fr.cedric.garcia.nantes.parking.model.pricing

import fr.cedric.garcia.nantes.parking.model.Location

data class PricingFields(

        val idobj: Int,
        val code_court: String,
        val nom_du_parking: String,
        val location: Location,

        val `10min`: Double?,
        val `20min`: Double?,
        val `30min`: Double?,
        val `40min`: Double?,
        val `50min`: Double?,
        val `1h`: Double?,
        val `1h30`: Double?,
        val `2h`: Double?,
        val `2h30`: Double?,
        val `3h`: Double?,
        val `11h`: Double?,

        val nuit_10min: Double?,
        val nuit_20min: Double?,
        val nuit_30min: Double?,
        val nuit_40min: Double?,
        val nuit_50min: Double?,
        val nuit_1h: Double?,
        val nuit_1h30: Double?,
        val nuit_2h: Double?,
        val nuit_2h30: Double?,
        val nuit_3h_et: Double?
)
