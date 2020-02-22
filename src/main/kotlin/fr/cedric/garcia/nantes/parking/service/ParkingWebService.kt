package fr.cedric.garcia.nantes.parking.service

import fr.cedric.garcia.nantes.parking.configuration.OpenDataNantesConfiguration
import fr.cedric.garcia.nantes.parking.exception.OpenDataNantesException
import fr.cedric.garcia.nantes.parking.model.availability.Availability
import fr.cedric.garcia.nantes.parking.model.availability.AvailabilityResponse
import fr.cedric.garcia.nantes.parking.model.parking.Parking
import fr.cedric.garcia.nantes.parking.model.parking.ParkingResponse
import fr.cedric.garcia.nantes.parking.model.pricing.Pricing
import fr.cedric.garcia.nantes.parking.model.pricing.PricingResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ParkingWebService(@Autowired private val configuration: OpenDataNantesConfiguration) {

    private val webClient = WebClient.create(configuration.datasetsBaseUrl)

    suspend fun getParkings(): Flux<Parking> =
            webClient.get()
                    .uri("/${configuration.publicParkingsKey}") {
                        it.path(RECORDS_PATH)
                                .queryParam("rows", 30)
                                .build()
                    }
                    .buildQueryForType(ParkingResponse::class.java)
                    .map { p -> p.records.map { Parking.mapToParking(it.record.fields) } }
                    .flatMapMany { Flux.fromIterable(it) }

    suspend fun getParking(parkingId: String): Mono<Parking> =
            webClient.get()
                    .uri("/${configuration.publicParkingsKey}") {
                        it.path(RECORDS_PATH)
                                .queryParam("where", "idobj like \"$parkingId\"")
                                .build()
                    }
                    .buildQueryForType(ParkingResponse::class.java)
                    .flatMap {
                        if (it.records.isNotEmpty())
                            Mono.just(Parking.mapToParking(it.records.first().record.fields))
                        else
                            Mono.empty<Parking>()
                    }

    suspend fun getAvailabilities(): Flux<Availability> =
            webClient.get()
                    .uri("/${configuration.publicParkingsAvailabilityKey}") {
                        it.path(RECORDS_PATH)
                                .queryParam("rows", 30)
                                .build()
                    }
                    .buildQueryForType(AvailabilityResponse::class.java)
                    .map { a -> a.records.map { Availability.mapToAvailability(it.record.fields) } }
                    .flatMapMany { Flux.fromIterable(it) }

    suspend fun getAvailability(parkingId: String): Mono<Availability> =
            webClient.get()
                    .uri("/${configuration.publicParkingsAvailabilityKey}") {
                        it.path(RECORDS_PATH)
                                .queryParam("where", "idobj like \"$parkingId\"")
                                .build()
                    }
                    .buildQueryForType(AvailabilityResponse::class.java)
                    .flatMap {
                        if (it.records.isNotEmpty())
                            Mono.just(Availability.mapToAvailability(it.records.first().record.fields))
                        else
                            Mono.empty<Availability>()
                    }

    suspend fun getPricings(): Flux<Pricing> =
            webClient.get()
                    .uri("/${configuration.publicParkingsPricingKey}") {
                        it.path(RECORDS_PATH)
                                .queryParam("rows", 30)
                                .build()
                    }
                    .buildQueryForType(PricingResponse::class.java)
                    .map { p -> p.records.map { Pricing.mapToPricing(it.record.fields) } }
                    .flatMapMany { Flux.fromIterable(it) }

    suspend fun getPricing(parkingId: String): Mono<Pricing> =
            webClient.get()
                    .uri("/${configuration.publicParkingsPricingKey}") {
                        it.path(RECORDS_PATH)
                                .queryParam("where", "idobj like \"$parkingId\"")
                                .build()
                    }
                    .buildQueryForType(PricingResponse::class.java)
                    .flatMap {
                        if (it.records.isNotEmpty())
                            Mono.just(Pricing.mapToPricing(it.records.first().record.fields))
                        else
                            Mono.empty<Pricing>()
                    }

    private fun <T> WebClient.RequestHeadersSpec<*>.buildQueryForType(responseClass: Class<T>): Mono<T> =
            this.accept(APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError) { handle4xxServerError(it.rawStatusCode()) }
                    .onStatus(HttpStatus::is5xxServerError) { handle5xxServerError(it.rawStatusCode()) }
                    .bodyToMono(ParameterizedTypeReference.forType(responseClass))

    private fun handle4xxServerError(status: Int): Mono<OpenDataNantesException> =
            when (HttpStatus.valueOf(status)) {
                HttpStatus.BAD_REQUEST ->
                    Mono.error(OpenDataNantesException(status, "Query is malformed"))
                else -> Mono.empty()
            }

    private fun handle5xxServerError(status: Int): Mono<OpenDataNantesException> =
            Mono.error(OpenDataNantesException(status, "Error while contacting https://data.nantesmetropole.fr/"))

    companion object {
        private const val RECORDS_PATH = "/records"
    }
}
