package com.grupo3.dto.booking

import com.grupo3.model.RoomType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero

data class BookingCreateDto(
    @field:NotBlank
    val hotelId: String,

    @field:Positive
    val userId: Long,

    val roomType: RoomType = RoomType.STANDARD,

    @field:NotBlank
    val firstNameGuest: String,

    @field:NotBlank
    val lastNameGuest: String,

    @field:Email
    val emailGuest: String,

    val phoneNumberGuest: String? = null,

    @field:NotBlank
    val startTimeIso: String,

    @field:NotBlank
    val endTimeIso: String,

    @field:PositiveOrZero
    val price: Long? = null,

    val currency: String? = "USD"
)