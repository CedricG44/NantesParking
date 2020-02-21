package fr.cedric.garcia.nantes.parking.model.parking

import fr.cedric.garcia.nantes.parking.model.Links

data class ParkingResponse(

        val total_count: Int,
        val links: List<Links>,
        val records: List<ParkingRecords>
)

data class ParkingRecords(

        val links: List<Links>,
        val record: ParkingRecord
)

data class ParkingRecord(

        val id: String,
        val timestamp: String,
        val size: Int,
        val fields: ParkingFields
)
