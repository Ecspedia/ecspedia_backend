package com.grupo3.service.hotel

import com.grupo3.dto.hotel.HotelCreateDto
import com.grupo3.dto.hotel.HotelMapper
import com.grupo3.dto.hotel.HotelResponseDto
import com.grupo3.repository.hotel.HotelRepository
import com.grupo3.repository.hotel.LocationRepository
import org.springframework.stereotype.Service

@Service
class HotelService(
    private val hotelRepository: HotelRepository,
    private val locationRepository: LocationRepository,
    private val hotelbedsClient: HotelbedsClient,
    private val hotelbedsCacheService: HotelbedsCacheService
) {

    fun createHotel(hotelDto: HotelCreateDto): HotelResponseDto {
        val hotel = HotelMapper.toEntity(hotelDto)
        val savedHotel = hotelRepository.save(hotel)
        return HotelMapper.toResponseDto(savedHotel)
    }

    fun getAllHotels(): List<HotelResponseDto> {
        return hotelRepository.findAll()
            .map { HotelMapper.toResponseDto(it) }
    }

    fun getHotelsByLocation(location: String): List<HotelResponseDto> {
        return hotelRepository.findByLocation(location)
            .map { HotelMapper.toResponseDto(it) }
    }

    fun hotelsByLocationName(location: String, checkIn: String, checkOut: String, adults: Int): String {
        val normalizedLocation = location.trim()
        val locationObject = locationRepository.findByCity(normalizedLocation)
            .orElseThrow { IllegalArgumentException("Location not found for city: $normalizedLocation") }

        val cached = hotelbedsCacheService.getCachedResponse(
            normalizedLocation,
            checkIn,
            checkOut,
            adults
        )
        if (cached != null) {
            return cached.responsePayload
        }

        val payload = """
            {
                "stay": {
                    "checkIn": "$checkIn",
                    "checkOut": "$checkOut"
                },
                "occupancies": [
                    {
                        "rooms": 1,
                        "adults": $adults,
                        "children": 0
                    }
                ],
                "geolocation": {
                    "latitude": ${locationObject.latitude},
                    "longitude": ${locationObject.longitude},
                    "radius": 20,
                    "unit": "km"
                },
                "filter": {
                    "maxCategory": 4,
                    "minCategory": 2,
                    "maxRooms": 1,
                    "maxHotels": 2
                }
            }
        """.trimIndent()

        val response = hotelbedsClient.searchAvailability(payload)
        hotelbedsCacheService.saveCacheEntry(
            location = normalizedLocation,
            checkIn = checkIn,
            checkOut = checkOut,
            adults = adults,
            responsePayload = response,
            success = true
        )
        return response
    }
}

