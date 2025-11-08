package com.grupo3.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.grupo3.model.hotel.Hotel
import com.grupo3.model.hotel.HotelbedsSearchCache
import com.grupo3.model.hotel.Location
import com.grupo3.repository.hotel.HotelRepository
import com.grupo3.repository.hotel.HotelbedsSearchCacheRepository
import com.grupo3.repository.hotel.LocationRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant

@Component
class DataLoader(
    private val hotelRepository: HotelRepository,
    private val locationRepository: LocationRepository,
    private val hotelbedsSearchCacheRepository: HotelbedsSearchCacheRepository,
    private val resourceLoader: ResourceLoader
) : CommandLineRunner {

    private val objectMapper = jacksonObjectMapper()

    override fun run(vararg args: String?) {
        seedHotels()
        seedLocations()
        seedHotelbedsCache()
    }

    private fun seedHotels() {
        if (hotelRepository.count() > 0) {
            return
        }

        val hotels = listOf(
            Hotel(
                name = "Hotel El Poblado Plaza",
                location = "Medellin, Colombia",
                image = "https://expediastorage.s3.us-east-1.amazonaws.com/hotel_mock.avif",
                isAvailable = true,
                rating = 4.7,
                reviewCount = 342,
                pricePerNight = 330.0,
                latitude = 6.2088,
                longitude = -75.5673
            ),
            Hotel(
                name = "Laureles Executive Hotel",
                location = "Medellin, Colombia",
                image = "https://expediastorage.s3.us-east-1.amazonaws.com/img2.webp",
                isAvailable = true,
                rating = 4.5,
                reviewCount = 256,
                pricePerNight = 450.0,
                latitude = 6.2443,
                longitude = -75.5912
            ),
            Hotel(
                name = "Centro Historico Inn",
                location = "Medellin, Colombia",
                image = "https://expediastorage.s3.us-east-1.amazonaws.com/img3.webp",
                isAvailable = true,
                rating = 4.3,
                reviewCount = 189,
                pricePerNight = 250.0,
                latitude = 6.2518,
                longitude = -75.5636
            ),
            Hotel(
                name = "Envigado Boutique Hotel",
                location = "Envigado, Colombia",
                image = "https://expediastorage.s3.us-east-1.amazonaws.com/img5.avif",
                isAvailable = true,
                rating = 4.8,
                reviewCount = 423,
                pricePerNight = 500.0,
                latitude = 6.1701,
                longitude = -75.5838
            )
        )

        hotelRepository.saveAll(hotels)
        println("Database initialized with ${hotels.size} sample hotels")
    }

    private fun seedLocations() {
        val resource = resourceLoader.getResource("classpath:data/locations.json")
        if (!resource.exists()) {
            return
        }

        val seedRecords = resource.inputStream.use { stream ->
            objectMapper.readValue(stream, object : TypeReference<List<LocationSeedRecord>>() {})
        }

        val newLocations = seedRecords
            .filter { record -> !locationRepository.existsByCode(record.code.uppercase()) }
            .map { record ->
                Location(
                    code = record.code.uppercase(),
                    city = record.city,
                    country = record.country,
                    state = record.state,
                    latitude = record.latitude,
                    longitude = record.longitude,
                    isPopular = record.isPopular
                )
            }

        if (newLocations.isNotEmpty()) {
            locationRepository.saveAll(newLocations)
            println("Seeded ${newLocations.size} locations")
        }
    }

    private fun seedHotelbedsCache() {
        val checkIn = "2025-11-05"
        val checkOut = "2025-11-10"
        val adults = 1
        val locationName = "Santiago"

        if (hotelbedsSearchCacheRepository.existsForSearch(
                locationName,
                checkIn,
                checkOut,
                adults
            )
        ) {
            return
        }

        val resource = resourceLoader.getResource("classpath:data/hotelbeds_default_cache.json")
        if (!resource.exists()) {
            return
        }

        val payload = resource.inputStream.use { input ->
            input.readBytes().toString(StandardCharsets.UTF_8)
        }

        val now = Instant.now()
        val cacheEntry = HotelbedsSearchCache(
            checkIn = checkIn,
            checkOut = checkOut,
            adults = adults,
            location = locationName,
            responsePayload = payload,
            fetchedAt = now,
            expiresAt = now.plus(Duration.ofDays(1)),
            success = true
        )

        hotelbedsSearchCacheRepository.save(cacheEntry)
        println("Seeded default Hotelbeds cache entry for Santiago")
    }

    private data class LocationSeedRecord(
        val code: String,
        val city: String,
        val country: String,
        val state: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val isPopular: Boolean = false
    )
}
