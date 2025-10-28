package com.grupo3.dto.hotel

data class HotelResponseDto(
    val id: String,
    val name: String,
    val location: String,
    val image: String? = null,
    val isAvailable: Boolean,
    val rating: Double? = null,
    val reviewCount: Int? = null,
    val pricePerNight: Double,
    val latitude: Double? = null,
    val longitude: Double? = null
)
