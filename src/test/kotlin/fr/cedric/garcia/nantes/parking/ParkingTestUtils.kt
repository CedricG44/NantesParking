package fr.cedric.garcia.nantes.parking

import fr.cedric.garcia.nantes.parking.exception.OpenDataNantesException
import fr.cedric.garcia.nantes.parking.model.Location
import fr.cedric.garcia.nantes.parking.model.availability.Availability
import fr.cedric.garcia.nantes.parking.model.parking.Parking
import fr.cedric.garcia.nantes.parking.model.pricing.Pricing
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ParkingTestUtils {

    companion object {
        val parking1 = Parking(
                1046,
                "Parking Talensac",
                "Parking de Centre-ville.Parking Accessible 24h/24 et 7j/7 pour les abonnés.",
                "Elévation",
                "Parking Ouvrage",
                "NGE",
                "02 51 84 95 70",
                "https://www.parkings-nantes.fr/fr/parkings/talensac",
                "Rue Le Nôtre",
                "Nantes",
                44000,
                Location(47.220255974, -1.558357297),
                "CB en borne de sortie, Espèces, Total GR",
                true,
                true,
                43,
                6,
                351,
                2,
                true,
                7,
                "Station Tramway Lignes 3 \"50 Otages\".Station Bicloo n°1 \"Préfecture\" (équipée CB)." +
                        "Station Marguerite \"50 Otages\" (Véhicule en libre service)."
        )

        val parking2 = Parking(
                1043,
                "Parking Aristide Briand",
                "Parking de Centre-ville.Parking accessible du lundi au samedi 7h30-21h30 le dimanche et " +
                        "jours fériés de 8h-19h et 7j/7 pour les abonnés.",
                "Souterrain",
                "Parking Ouvrage",
                "EFFIA",
                "02 40 47 06 05",
                "https://www.effia.com/parking/parking-nantes-aristide-briand-effia",
                "Place Aristide Briand",
                "Nantes",
                44000,
                Location(47.217093598, -1.562936416),
                "CB en borne de sortie, Espèces, chèque, Total GR",
                false,
                false,
                0,
                10,
                307,
                0,
                true,
                9,
                ""
        )

        val availability1 = Availability(1046, "Talensac", 5, 9, 192, 339, "2020-02-24T08:20:45.0000")

        val availability2 = Availability(1043, "Aristide Briand", 5, 12, 154, 298, "2020-02-24T09:30:25.0000")

        val pricing1 = Pricing(1046, hashMapOf("10min" to 1.2), hashMapOf("10min" to 0.9))

        val pricing2 = Pricing(1043, hashMapOf("10min" to 1.15), hashMapOf("10min" to 0.85))

        val exception = OpenDataNantesException(500, "Error while contacting https://data.nantesmetropole.fr/")

        fun listOfParkings(): List<Parking> = listOf(parking1, parking2)

        fun listOfAvailabilities(): List<Availability> = listOf(availability1, availability2)

        fun listOfPricings(): List<Pricing> = listOf(pricing1, pricing2)

        fun monoOfParking(): Mono<Parking> = Mono.just(parking1)

        fun monoOfAvailability(): Mono<Availability> = Mono.just(availability1)

        fun monoOfPricing(): Mono<Pricing> = Mono.just(pricing1)

        fun fluxOfParkings(): Flux<Parking> = Flux.fromIterable(listOfParkings())

        fun fluxOfAvailabilities(): Flux<Availability> = Flux.fromIterable(listOfAvailabilities())

        fun fluxOfPricings(): Flux<Pricing> = Flux.fromIterable(listOfPricings())

        fun <T> monoError(): Mono<T> = Mono.error<T>(exception)

        fun <T> fluxError(): Flux<T> = Flux.error<T>(exception)
    }
}
