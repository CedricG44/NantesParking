package fr.cedric.garcia.nantes.parking.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "opendata-nantes")
class OpenDataNantesConfiguration {

    lateinit var datasetsBaseUrl: String
    lateinit var publicParkingsKey: String
    lateinit var publicParkingsAvailabilityKey: String
    lateinit var publicParkingsPricingKey: String
}
