package fr.cedric.garcia.nantes.parking.handler

import fr.cedric.garcia.nantes.parking.exception.OpenDataNantesException
import fr.cedric.garcia.nantes.parking.model.availability.Availability
import fr.cedric.garcia.nantes.parking.model.parking.Parking
import fr.cedric.garcia.nantes.parking.model.pricing.Pricing
import fr.cedric.garcia.nantes.parking.service.ParkingWebService
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_STREAM_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class ParkingHandler(@Autowired private val parkingWebService: ParkingWebService) {

    suspend fun getParkings(request: ServerRequest): ServerResponse =
            ok().contentType(APPLICATION_STREAM_JSON)
                    .body(parkingWebService.getParkings()
                            .onErrorMap(this::handleError), Parking::class.java)
                    .awaitFirst()

    suspend fun getParking(request: ServerRequest): ServerResponse {
        val parkingId = request.pathVariable("id")
        val withAvailability = request.queryParam("withAvailability").orElseGet { "false" }.toBoolean()

        return if (!withAvailability)
            parkingWebService.getParking(parkingId)
                    .handleWebServiceResponse()
        else
            getParkingWithAvailability(parkingId)
    }

    suspend fun getAvailabilities(request: ServerRequest): ServerResponse =
            ok().contentType(APPLICATION_STREAM_JSON)
                    .body(parkingWebService.getAvailabilities()
                            .onErrorMap(this::handleError), Availability::class.java)
                    .awaitFirst()

    suspend fun getAvailability(request: ServerRequest): ServerResponse {
        val parkingId = request.pathVariable("id")
        return parkingWebService.getAvailability(parkingId)
                .handleWebServiceResponse()
    }

    suspend fun getPricings(request: ServerRequest): ServerResponse =
            ok().contentType(APPLICATION_STREAM_JSON)
                    .body(parkingWebService.getPricings()
                            .onErrorMap(this::handleError), Pricing::class.java)
                    .awaitFirst()

    suspend fun getPricing(request: ServerRequest): ServerResponse {
        val parkingId = request.pathVariable("id")
        return parkingWebService.getPricing(parkingId)
                .handleWebServiceResponse()
    }

    suspend fun getParkingWithAvailability(parkingId: String): ServerResponse =
            Mono.zip(
                    parkingWebService.getParking(parkingId).subscribeOn(Schedulers.elastic()),
                    parkingWebService.getAvailability(parkingId).subscribeOn(Schedulers.elastic())) { p, a ->
                mapOf("parking" to p, "availability" to a)
            }
                    .handleWebServiceResponse()

    private suspend fun Mono<*>.handleWebServiceResponse(): ServerResponse =
            this.flatMap(ok().contentType(APPLICATION_JSON)::bodyValue)
                    .switchIfEmpty(notFound().build())
                    .onErrorMap { handleError(it) }
                    .awaitFirst()

    private fun handleError(error: Throwable): ResponseStatusException =
            when (error) {
                is OpenDataNantesException ->
                    ResponseStatusException(HttpStatus.valueOf(error.status), error.message)
                else -> ResponseStatusException(INTERNAL_SERVER_ERROR)
            }
}
