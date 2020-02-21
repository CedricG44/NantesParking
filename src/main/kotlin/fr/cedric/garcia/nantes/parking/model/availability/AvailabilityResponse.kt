package fr.cedric.garcia.nantes.parking.model.availability

import fr.cedric.garcia.nantes.parking.model.Links

data class AvailabilityResponse(

        val total_count: Int,
        val links: List<Links>,
        val records: List<AvailabilityRecords>
)

data class AvailabilityRecords(

        val links: List<Links>,
        val record: AvailabilityRecord
)

data class AvailabilityRecord(

        val id: String,
        val timestamp: String,
        val size: Int,
        val fields: AvailabilityFields
)
