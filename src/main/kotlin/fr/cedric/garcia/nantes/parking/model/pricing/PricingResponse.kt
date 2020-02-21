package fr.cedric.garcia.nantes.parking.model.pricing

import fr.cedric.garcia.nantes.parking.model.Links

data class PricingResponse(

        val total_count: Int,
        val links: List<Links>,
        val records: List<PricingRecords>
)

data class PricingRecords(

        val links: List<Links>,
        val record: PricingRecord
)

data class PricingRecord(

        val id: String,
        val timestamp: String,
        val size: Int,
        val fields: PricingFields
)
