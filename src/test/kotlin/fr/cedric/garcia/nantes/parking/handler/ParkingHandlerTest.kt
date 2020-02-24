package fr.cedric.garcia.nantes.parking.handler

import com.ninjasquad.springmockk.MockkBean
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.availability1
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.availability2
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.exception
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.fluxError
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.fluxOfAvailabilities
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.fluxOfParkings
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.fluxOfPricings
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.monoError
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.monoOfAvailability
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.monoOfParking
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.monoOfPricing
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.parking1
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.parking2
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.pricing1
import fr.cedric.garcia.nantes.parking.ParkingTestUtils.Companion.pricing2
import fr.cedric.garcia.nantes.parking.model.ParkingWithAvailability
import fr.cedric.garcia.nantes.parking.model.availability.Availability
import fr.cedric.garcia.nantes.parking.model.parking.Parking
import fr.cedric.garcia.nantes.parking.model.pricing.Pricing
import fr.cedric.garcia.nantes.parking.router.ParkingRouter
import fr.cedric.garcia.nantes.parking.service.ParkingWebService
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_STREAM_JSON
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.returnResult
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@WebFluxTest
@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [ParkingRouter::class, ParkingHandler::class])
class ParkingHandlerTest {

    @Autowired
    private lateinit var context: ApplicationContext

    @MockkBean
    private lateinit var parkingWebService: ParkingWebService

    private lateinit var client: WebTestClient

    @Before
    fun setUp() {
        client = WebTestClient.bindToApplicationContext(context).build()
    }

    @Test
    fun `GET parking should return a list of parkings`() {
        coEvery { parkingWebService.getParkings() } returns fluxOfParkings()

        val result = client.get()
                .uri(BASE_URL)
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader().contentType(APPLICATION_STREAM_JSON)
                .returnResult<Parking>()

        StepVerifier.create(result.responseBody)
                .expectNext(parking1)
                .expectNext(parking2)
                .thenCancel()
                .verify()
    }

    @Test
    fun `GET parking should return an empty list`() {
        coEvery { parkingWebService.getParkings() } returns Flux.empty()

        client.get()
                .uri(BASE_URL)
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader().contentType(APPLICATION_STREAM_JSON)
                .expectBody()
                .isEmpty
    }

    @Test
    fun `GET parking should return an error`() {
        coEvery { parkingWebService.getParkings() } returns fluxError()

        client.get()
                .uri(BASE_URL)
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .is5xxServerError
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/parking")
                .jsonPath("$.status").isEqualTo(exception.status)
                .jsonPath("$.message").isEqualTo(exception.message)
                .jsonPath("$.error").isNotEmpty
                .jsonPath("$.timestamp").isNotEmpty

    }

    @Test
    fun `GET parking#id#withAvailability=false should return a parking`() {
        val id = "1046"
        coEvery { parkingWebService.getParking(id) } returns monoOfParking()
        coVerify { parkingWebService.getAvailability(id) wasNot called }

        client.get()
                .uri("$BASE_URL/$id?withAvailability=false")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody<Parking>()
                .isEqualTo(parking1)
    }

    @Test
    fun `GET parking#id#withAvailability=true should return a parking with its availability`() {
        val id = "1046"
        coEvery { parkingWebService.getParking(id) } returns monoOfParking()
        coEvery { parkingWebService.getAvailability(id) } returns monoOfAvailability()

        client.get()
                .uri("$BASE_URL/$id?withAvailability=true")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody<ParkingWithAvailability>()
                .isEqualTo(ParkingWithAvailability(parking1, availability1))
    }

    @Test
    fun `GET parking#id should return not found`() {
        val id = "1046"
        coEvery { parkingWebService.getParking(id) } returns Mono.empty()

        client.get()
                .uri("$BASE_URL/$id")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound
                .expectBody()
                .isEmpty
    }

    @Test
    fun `GET parking#id should return an error`() {
        val id = "1046"
        coEvery { parkingWebService.getParking(id) } returns monoError()

        client.get()
                .uri("$BASE_URL/$id")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is5xxServerError
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/parking/$id")
                .jsonPath("$.status").isEqualTo(exception.status)
                .jsonPath("$.message").isEqualTo(exception.message)
                .jsonPath("$.error").isNotEmpty
                .jsonPath("$.timestamp").isNotEmpty
    }

    @Test
    fun `GET parking#availability should return a list of availabilities`() {
        coEvery { parkingWebService.getAvailabilities() } returns fluxOfAvailabilities()

        val result = client.get()
                .uri("$BASE_URL/availability")
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader().contentType(APPLICATION_STREAM_JSON)
                .returnResult<Availability>()

        StepVerifier.create(result.responseBody)
                .expectNext(availability1)
                .expectNext(availability2)
                .thenCancel()
                .verify()
    }

    @Test
    fun `GET parking#availability should return an empty list`() {
        coEvery { parkingWebService.getAvailabilities() } returns Flux.empty()

        client.get()
                .uri("$BASE_URL/availability")
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader().contentType(APPLICATION_STREAM_JSON)
                .expectBody()
                .isEmpty
    }

    @Test
    fun `GET parking#availability should return an error`() {
        coEvery { parkingWebService.getAvailabilities() } returns fluxError()

        client.get()
                .uri("$BASE_URL/availability")
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .is5xxServerError
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/parking/availability")
                .jsonPath("$.status").isEqualTo(exception.status)
                .jsonPath("$.message").isEqualTo(exception.message)
                .jsonPath("$.error").isNotEmpty
                .jsonPath("$.timestamp").isNotEmpty

    }

    @Test
    fun `GET parking#id#availability should return a parking availability`() {
        val id = "1046"
        coEvery { parkingWebService.getAvailability(id) } returns monoOfAvailability()

        client.get()
                .uri("$BASE_URL/$id/availability")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody<Availability>()
                .isEqualTo(availability1)
    }

    @Test
    fun `GET parking#id#availability should return not found`() {
        val id = "1046"
        coEvery { parkingWebService.getAvailability(id) } returns Mono.empty()

        client.get()
                .uri("$BASE_URL/$id/availability")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound
                .expectBody()
                .isEmpty
    }

    @Test
    fun `GET parking#id#availability should return an error`() {
        val id = "1046"
        coEvery { parkingWebService.getAvailability(id) } returns monoError()

        client.get()
                .uri("$BASE_URL/$id/availability")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is5xxServerError
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/parking/$id/availability")
                .jsonPath("$.status").isEqualTo(exception.status)
                .jsonPath("$.message").isEqualTo(exception.message)
                .jsonPath("$.error").isNotEmpty
                .jsonPath("$.timestamp").isNotEmpty
    }

    @Test
    fun `GET parking#pricing should return a list of pricings`() {
        coEvery { parkingWebService.getPricings() } returns fluxOfPricings()

        val result = client.get()
                .uri("$BASE_URL/pricing")
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader().contentType(APPLICATION_STREAM_JSON)
                .returnResult<Pricing>()

        StepVerifier.create(result.responseBody)
                .expectNext(pricing1)
                .expectNext(pricing2)
                .thenCancel()
                .verify()
    }

    @Test
    fun `GET parking#pricing should return an empty list`() {
        coEvery { parkingWebService.getPricings() } returns Flux.empty()

        client.get()
                .uri("$BASE_URL/pricing")
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader().contentType(APPLICATION_STREAM_JSON)
                .expectBody()
                .isEmpty
    }

    @Test
    fun `GET parking#pricing should return an error`() {
        coEvery { parkingWebService.getPricings() } returns fluxError()

        client.get()
                .uri("$BASE_URL/pricing")
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .is5xxServerError
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/parking/pricing")
                .jsonPath("$.status").isEqualTo(exception.status)
                .jsonPath("$.message").isEqualTo(exception.message)
                .jsonPath("$.error").isNotEmpty
                .jsonPath("$.timestamp").isNotEmpty

    }

    @Test
    fun `GET parking#id#pricing should return a parking pricing`() {
        val id = "1046"
        coEvery { parkingWebService.getPricing(id) } returns monoOfPricing()

        client.get()
                .uri("$BASE_URL/$id/pricing")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody<Pricing>()
                .isEqualTo(pricing1)
    }

    @Test
    fun `GET parking#id#pricing should return not found`() {
        val id = "1046"
        coEvery { parkingWebService.getPricing(id) } returns Mono.empty()

        client.get()
                .uri("$BASE_URL/$id/pricing")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound
                .expectBody()
                .isEmpty
    }

    @Test
    fun `GET parking#id#pricing should return an error`() {
        val id = "1046"
        coEvery { parkingWebService.getPricing(id) } returns monoError()

        client.get()
                .uri("$BASE_URL/$id/pricing")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is5xxServerError
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/parking/$id/pricing")
                .jsonPath("$.status").isEqualTo(exception.status)
                .jsonPath("$.message").isEqualTo(exception.message)
                .jsonPath("$.error").isNotEmpty
                .jsonPath("$.timestamp").isNotEmpty
    }

    companion object {
        private const val BASE_URL = "http://localhost:8080/parking"
    }
}
