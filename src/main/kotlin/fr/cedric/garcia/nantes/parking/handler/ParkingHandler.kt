package fr.cedric.garcia.nantes.parking.handler

import fr.cedric.garcia.nantes.parking.exception.OpenDataNantesException
import fr.cedric.garcia.nantes.parking.model.availability.Availability
import fr.cedric.garcia.nantes.parking.model.error.OpenDataNantesError
import fr.cedric.garcia.nantes.parking.model.parking.Parking
import fr.cedric.garcia.nantes.parking.model.pricing.Pricing
import fr.cedric.garcia.nantes.parking.service.ParkingWebService
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_STREAM_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class ParkingHandler(@Autowired private val parkingWebService: ParkingWebService) {

    suspend fun getParkings(request: ServerRequest): ServerResponse =
            ok().contentType(APPLICATION_STREAM_JSON)
                    .body(parkingWebService.getParkings(), Parking::class.java)
                    .awaitFirst()

    suspend fun getParking(request: ServerRequest): ServerResponse {
        val parkingId = request.pathVariable("id")
        val withAvailability = request.queryParam("withAvailability").orElseGet { "false" }.toBoolean()

        return if (!withAvailability)
            parkingWebService
                    .getParking(parkingId)
                    .flatMap { ok().contentType(APPLICATION_JSON).bodyValue(it) }
                    .switchIfEmpty(notFound().build())
                    .onErrorResume { handleError(it) }
                    .awaitFirst()
        else
            getParkingWithAvailability(parkingId).awaitFirst()
    }

    suspend fun getAvailabilities(request: ServerRequest): ServerResponse =
            ok().contentType(APPLICATION_STREAM_JSON)
                    .body(parkingWebService.getAvailabilities(), Availability::class.java)
                    .awaitFirst()

    suspend fun getAvailability(request: ServerRequest): ServerResponse {
        val parkingId = request.pathVariable("id")
        return parkingWebService.getAvailability(parkingId)
                .flatMap { ok().contentType(APPLICATION_JSON).bodyValue(it) }
                .switchIfEmpty(notFound().build())
                .onErrorResume { handleError(it) }
                .awaitFirst()
    }

    suspend fun getPricings(request: ServerRequest): ServerResponse =
            ok().contentType(APPLICATION_STREAM_JSON)
                    .body(parkingWebService.getPricings(), Pricing::class.java)
                    .awaitFirst()

    suspend fun getPricing(request: ServerRequest): ServerResponse {
        val parkingId = request.pathVariable("id")
        return parkingWebService.getPricing(parkingId)
                .flatMap { ok().contentType(APPLICATION_JSON).bodyValue(it) }
                .switchIfEmpty(notFound().build())
                .onErrorResume { handleError(it) }
                .awaitFirst()
    }

    suspend fun getParkingWithAvailability(parkingId: String): Mono<ServerResponse> =
            Mono.zip(
                    parkingWebService.getParking(parkingId).subscribeOn(Schedulers.elastic()),
                    parkingWebService.getAvailability(parkingId).subscribeOn(Schedulers.elastic())) { p, a ->
                mapOf("parking" to p, "availability" to a)
            }
                    .flatMap { ok().contentType(APPLICATION_JSON).bodyValue(it) }
                    .switchIfEmpty(notFound().build())
                    .onErrorResume { handleError(it) }

    private fun handleError(error: Throwable): Mono<ServerResponse> =
            when (error) {
                is OpenDataNantesException ->
                    status(error.status).bodyValue(OpenDataNantesError(error.status, error.message))
                else -> status(INTERNAL_SERVER_ERROR).build()
            }
}
