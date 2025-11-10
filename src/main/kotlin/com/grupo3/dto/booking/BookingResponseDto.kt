package com.grupo3.dto.booking

import com.grupo3.model.booking.BookingStatus
import java.time.Instant

data class BookingResponseDto(
    val id: String,
    val hotelId: String,
    val userId: Long,
    val startTime: Instant,
    val endTime: Instant,
    val status: BookingStatus,
    val price: Long?,
    val currency: String?,
    val createdAt: Instant,
    val confirmedAt: Instant?,
    val canceledAt: Instant?
)
