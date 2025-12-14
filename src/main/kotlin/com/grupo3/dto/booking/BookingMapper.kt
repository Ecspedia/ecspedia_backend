package com.grupo3.dto.booking

import com.grupo3.model.Booking
import com.grupo3.model.Hotel
import com.grupo3.model.User
import com.grupo3.util.DateTimeUtils
import java.awt.print.Book

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
            canceledAt = entity.canceledAt,
            roomType = entity.roomType
        )

    fun toDomain(entity: BookingCreateDto, hotel: Hotel, user: User): Booking =
        Booking(
            hotel = hotel,
            user = user,
            firstNameGuest = entity.firstNameGuest,
            lastNameGuest = entity.lastNameGuest,
            emailGuest = entity.emailGuest,
            phoneNumberGuest = entity.phoneNumberGuest,
            startTime = DateTimeUtils.parseIsoInstant( entity.startTimeIso, "startTime"),
            endTime =  DateTimeUtils.parseIsoInstant( entity.endTimeIso, "endTime"),
            price = entity.price,
            currency = entity.currency,
            roomType = entity.roomType
        )
}
