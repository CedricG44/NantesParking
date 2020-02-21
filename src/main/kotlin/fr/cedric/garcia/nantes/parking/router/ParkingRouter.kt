package fr.cedric.garcia.nantes.parking.router

import fr.cedric.garcia.nantes.parking.handler.ParkingHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ParkingRouter {

    @Bean
    fun productRoutes(parkingHandler: ParkingHandler) = coRouter {
        "/parking".nest {
            GET("/pricing", parkingHandler::getPricings)
            GET("/availability", parkingHandler::getAvailabilities)
            GET("/{id}/pricing", parkingHandler::getPricing)
            GET("/{id}/availability", parkingHandler::getAvailability)
            GET("/{id}", parkingHandler::getParking)
            GET("/", parkingHandler::getParkings)
        }
    }
}
