package com.grupo3.service.hotel

import com.grupo3.model.hotel.HotelbedsSearchCache
import com.grupo3.dto.hotel.hotelbeds.CachedHotelQueryDto
import com.grupo3.repository.hotel.LocationRepository
import org.springframework.stereotype.Service
import com.grupo3.service.hotel.util.HotelbedsPayloadBuilder
import com.grupo3.service.hotel.util.HotelbedsPayloadParser

@Service
class HotelService(
    private val locationRepository: LocationRepository,
    private val hotelbedsClient: HotelbedsClient,
    private val hotelbedsCacheService: HotelbedsCacheService
) {

    fun getAllCacheQuerys(): List<CachedHotelQueryDto> {
        return hotelbedsCacheService.getAllCachedResponse()
            .map { it.toDto() }
    }

    fun getHotelsByLocation(location: String, checkIn: String, checkOut: String, adults: Int): CachedHotelQueryDto {
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
            println("Cached hotel by location: $location")
            return cached.toDto()
        }

        val payload = HotelbedsPayloadBuilder.buildAvailabilityPayload(
            location = locationObject,
            checkIn = checkIn,
            checkOut = checkOut,
            adults = adults
        )

        val response = hotelbedsClient.searchAvailability(payload)
        val hotelbedsSearchCache = hotelbedsCacheService.saveCacheEntry(
            location = normalizedLocation,
            checkIn = checkIn,
            checkOut = checkOut,
            adults = adults,
            responsePayload = response,
            success = true
        )
        return hotelbedsSearchCache.toDto()
    }

    private fun HotelbedsSearchCache.toDto(): CachedHotelQueryDto {
        val parsed = HotelbedsPayloadParser.parseAvailability(responsePayload)
        return CachedHotelQueryDto(
            id = requireNotNull(id) { "HotelbedsSearchCache id must not be null" },
            location = location,
            checkIn = checkIn,
            checkOut = checkOut,
            adults = adults,
            responsePayload = parsed?.hotels,
            fetchedAt = fetchedAt,
            expiresAt = expiresAt,
            success = success,
            auditData = parsed?.auditData
        )
    }
}
