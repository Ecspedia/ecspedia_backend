package com.grupo3.agent.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookingDetails(
    val firstNameGuest: String? = null,
    val lastNameGuest: String? = null,
    val emailGuest: String? = null,
    val phoneNumberGuest: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val numberOfGuests: Int? = null
)

data class BookingRawResponse(
    val userId: String,
    val hotelId: String,
    val rawJson: String
)

data class ParsedBooking(
    val userId: String,
    val hotelId: String,
    val details: BookingDetails,
    val isComplete: Boolean,
    val missingFields: List<String>
)