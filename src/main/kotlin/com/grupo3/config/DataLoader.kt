package com.grupo3.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.grupo3.model.User
import com.grupo3.model.hotel.Hotel
import com.grupo3.model.hotel.HotelAccessibilityAttributes
import com.grupo3.model.hotel.Location
import com.grupo3.repository.UserRepository
import com.grupo3.repository.hotel.LocationRepository
import com.grupo3.repository.hotel.HotelRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ResourceLoader
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class DataLoader(
    private val locationRepository: LocationRepository,
    private val hotelRepository: HotelRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val resourceLoader: ResourceLoader
) : CommandLineRunner {

    private val objectMapper = jacksonObjectMapper()

    override fun run(vararg args: String?) {
        seedLocations()
        seedHotels()
        seedDefaultUser()
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

    private fun seedDefaultUser() {
        val defaultUsername = "demo_user"
        val defaultEmail = "sebastian.calderon@sansano.usm.cl"
        val defaultPassword = "password123"

        if (userRepository.existsByEmail(defaultEmail)) {
            return
        }

        val user = User(
            username = defaultUsername,
            email = defaultEmail,
            password = passwordEncoder.encode(defaultPassword)
        )

        userRepository.save(user)
        println("Seeded default user with email $defaultEmail")
    }

    private fun seedHotels() {
        val resource = resourceLoader.getResource("classpath:data/hotels.json")
        if (!resource.exists()) {
            return
        }

        val seedRecords = resource.inputStream.use { stream ->
            objectMapper.readValue(stream, object : TypeReference<List<HotelSeedRecord>>() {})
        }

        val newHotels = seedRecords.mapNotNull { record ->
            val trimmedName = record.name.trim()

            val exists = when {
                record.id != null -> hotelRepository.existsById(record.id)
                else -> hotelRepository.existsByNameIgnoreCase(trimmedName)
            }

            if (exists) {
                null
            } else {
                Hotel(
                    id = record.id ?: UUID.randomUUID().toString(),
                    name = trimmedName,
                    location = record.location.trim(),
                    image = record.image,
                    isAvailable = true,
                    rating = record.rating,
                    reviewCount = record.reviewCount,
                    pricePerNight = record.pricePerNight,
                    latitude = record.latitude,
                    longitude = record.longitude,
                    hotelDescription = record.hotelDescription,
                    hotelTypeId = record.hotelTypeId,
                    chain = record.chain,
                    currency = record.currency?.uppercase(),
                    country = record.country?.uppercase(),
                    city = record.city,
                    address = record.address,
                    zip = record.zip,
                    mainPhoto = record.mainPhoto,
                    thumbnail = record.thumbnail,
                    stars = record.stars,
                    facilityIds = record.facilityIds,
                    accessibilityAttributes = record.accessibilityAttributes?.toEntity(),
                    deletedAt = record.deletedAt?.let(Instant::parse)
                )
            }
        }

        if (newHotels.isNotEmpty()) {
            hotelRepository.saveAll(newHotels)
            println("Seeded ${newHotels.size} hotels")
        }
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
    private data class HotelSeedRecord(
        val id: String? = null,
        val name: String,
        val location: String,
        val hotelDescription: String?,
        val hotelTypeId: Int?,
        val chain: String?,
        val currency: String?,
        val country: String?,
        val city: String?,
        val latitude: Double?,
        val longitude: Double?,
        val address: String?,
        val zip: String?,
        val mainPhoto: String?,
        val thumbnail: String?,
        val image: String?,
        val stars: Int?,
        val rating: Double?,
        val reviewCount: Int?,
        val pricePerNight: Double,
        val facilityIds: List<Int>?,
        val accessibilityAttributes: HotelAccessibilityAttributesSeedRecord?,
        val deletedAt: String?
    )

    private data class HotelAccessibilityAttributesSeedRecord(
        val attributes: List<String>?,
        val showerChair: Boolean?,
        val entranceType: String?,
        val petFriendly: String?,
        val rampAngle: Int?,
        val rampLength: Int?,
        val entranceDoorWidth: Int?,
        val roomMaxGuestsNumber: Int?,
        val distanceFromTheElevatorToTheAccessibleRoom: Int?
    ) {
        fun toEntity(): HotelAccessibilityAttributes =
            HotelAccessibilityAttributes(
                attributes = attributes,
                showerChair = showerChair,
                entranceType = entranceType,
                petFriendly = petFriendly,
                rampAngle = rampAngle,
                rampLength = rampLength,
                entranceDoorWidth = entranceDoorWidth,
                roomMaxGuestsNumber = roomMaxGuestsNumber,
                distanceFromTheElevatorToTheAccessibleRoom = distanceFromTheElevatorToTheAccessibleRoom
            )
    }
}
