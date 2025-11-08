package com.grupo3.dto.hotel.hotelbeds

import java.time.Instant

data class CachedHotelQueryDto(
    val id: String,
    val location: String,
    val checkIn: String,
    val checkOut: String,
    val adults: Int,
    val responsePayload: HotelbedsHotelCollection?,
    val fetchedAt: Instant,
    val expiresAt: Instant,
    val success: Boolean,
    val auditData: HotelbedsAuditData?
)
