package fr.cedric.garcia.nantes.parking.model.pricing

data class Pricing(

        val parkingId: Int,

        val dayHours: HashMap<String, Double>,
        val nightHours: HashMap<String, Double>
) {
    companion object {

        fun mapToPricing(pricingFields: PricingFields): Pricing {
            val dayHours = hashMapOf(
                    "10min" to (pricingFields.`10min` ?: -1.0),
                    "20min" to (pricingFields.`20min` ?: -1.0),
                    "30min" to (pricingFields.`30min` ?: -1.0),
                    "40min" to (pricingFields.`40min` ?: -1.0),
                    "50min" to (pricingFields.`50min` ?: -1.0),
                    "1h" to (pricingFields.`1h` ?: -1.0),
                    "1h30" to (pricingFields.`1h30` ?: -1.0),
                    "2h" to (pricingFields.`2h` ?: -1.0),
                    "2h30" to (pricingFields.`2h30` ?: -1.0),
                    "3h" to (pricingFields.`3h` ?: -1.0),
                    "11h" to (pricingFields.`11h` ?: -1.0)
            )

            val nightHours = hashMapOf(
                    "10min" to (pricingFields.nuit_10min ?: -1.0),
                    "20min" to (pricingFields.nuit_20min ?: -1.0),
                    "30min" to (pricingFields.nuit_30min ?: -1.0),
                    "40min" to (pricingFields.nuit_40min ?: -1.0),
                    "50min" to (pricingFields.nuit_50min ?: -1.0),
                    "1h" to (pricingFields.nuit_1h ?: -1.0),
                    "1h30" to (pricingFields.nuit_1h30 ?: -1.0),
                    "2h" to (pricingFields.nuit_2h ?: -1.0),
                    "2h30" to (pricingFields.nuit_2h30 ?: -1.0),
                    "3h+" to (pricingFields.nuit_3h_et ?: -1.0)
            )

            return Pricing(pricingFields.idobj, dayHours, nightHours)
        }
    }
}
