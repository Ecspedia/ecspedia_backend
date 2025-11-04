package com.grupo3.repository.hotel

import com.grupo3.model.hotel.HotelbedsSearchCache
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface HotelbedsSearchCacheRepository : JpaRepository<HotelbedsSearchCache, String> {

    @Query(
        """
            SELECT CASE WHEN COUNT(cache) > 0 THEN true ELSE false END
            FROM HotelbedsSearchCache cache
            WHERE cache.location = :location
              AND cache.checkIn = :checkIn
              AND cache.checkOut = :checkOut
              AND cache.adults = :adults
        """
    )
    fun existsForSearch(
        @Param("location") location: String,
        @Param("checkIn") checkIn: String,
        @Param("checkOut") checkOut: String,
        @Param("adults") adults: Int
    ): Boolean

    @Query(
        """
            SELECT cache FROM HotelbedsSearchCache cache
            WHERE cache.location = :location
              AND cache.checkIn = :checkIn
              AND cache.checkOut = :checkOut
              AND cache.adults = :adults
              AND cache.expiresAt > :currentTime
            ORDER BY cache.fetchedAt DESC
        """
    )
    fun findValidCacheEntry(
        @Param("location") location: String,
        @Param("checkIn") checkIn: String,
        @Param("checkOut") checkOut: String,
        @Param("adults") adults: Int,
        @Param("currentTime") currentTime: Instant
    ): HotelbedsSearchCache?
}

