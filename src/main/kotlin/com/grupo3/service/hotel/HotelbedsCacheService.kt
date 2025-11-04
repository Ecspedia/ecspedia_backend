package com.grupo3.service.hotel

import com.grupo3.model.hotel.HotelbedsSearchCache
import com.grupo3.repository.hotel.HotelbedsSearchCacheRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class HotelbedsCacheService(
    private val hotelbedsSearchCacheRepository: HotelbedsSearchCacheRepository
) {

    fun getCachedResponse(
        location: String,
        checkIn: String,
        checkOut: String,
        adults: Int
    ): HotelbedsSearchCache? {
        val normalizedLocation = location.trim()
        return hotelbedsSearchCacheRepository.findValidCacheEntry(
            normalizedLocation,
            checkIn,
            checkOut,
            adults,
            Instant.now()
        )
    }

    fun saveCacheEntry(
        location: String,
        checkIn: String,
        checkOut: String,
        adults: Int,
        responsePayload: String,
        success: Boolean,
        ttl: Duration = Duration.ofMinutes(10)
    ): HotelbedsSearchCache {
        val now = Instant.now()
        val cacheEntry = HotelbedsSearchCache(
            location = location.trim(),
            checkIn = checkIn,
            checkOut = checkOut,
            adults = adults,
            responsePayload = responsePayload,
            fetchedAt = now,
            expiresAt = now.plus(ttl),
            success = success
        )
        return hotelbedsSearchCacheRepository.save(cacheEntry)
    }
}

