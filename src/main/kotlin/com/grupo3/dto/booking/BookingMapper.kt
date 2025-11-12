package com.grupo3.dto.booking

import com.grupo3.model.booking.Booking

object BookingMapper {

    fun toResponseDto(entity: Booking): BookingResponseDto =
        BookingResponseDto(
            id = entity.id ?: error("Booking must have an id after persistence"),
            hotelId = entity.hotel.id,
            userId = entity.user.id ?: error("User must have an id"),
            firstNameGuest = entity.firstNameGuest,
            lastNameGuest = entity.lastNameGuest,
            emailGuest = entity.emailGuest,
            phoneNumberGuest = entity.phoneNumberGuest,
            startTime = entity.startTime,
            endTime = entity.endTime,
            status = entity.status,
            price = entity.price,
            currency = entity.currency,
            createdAt = entity.createdAt,
            confirmedAt = entity.confirmedAt,
            canceledAt = entity.canceledAt
        )
}
