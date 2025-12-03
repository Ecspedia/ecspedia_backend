package com.grupo3.dto.hotel

import java.time.Instant

data class HotelPartialResponseDto(
    val id: String,
    val name: String,
    val location: String,
    val image: String? = null,
    val rating: Double? = null,
    val reviewCount: Int? = null,
    val pricePerNight: Double,
    val latitude: Double? = null,
    val longitude: Double? = null,

    val country: String? = null,
    val city: String? = null,
    val address: String? = null,
    val stars: Int? = null,
    )
