package com.grupo3.dto.hotel

import java.time.Instant

data class  HotelResponseDto(
    val id: String,
    val name: String,
    val location: String,
    val image: String? = null,
    val isAvailable: Boolean,
    val rating: Double? = null,
    val reviewCount: Int? = null,
    val pricePerNight: Double,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val hotelDescription: String? = null,
    val hotelTypeId: Int? = null,
    val chain: String? = null,
    val currency: String? = null,
    val country: String? = null,
    val city: String? = null,
    val address: String? = null,
    val zip: String? = null,
    val mainPhoto: String? = null,
    val thumbnail: String? = null,
    val stars: Int? = null,
    val facilityIds: List<Int>? = null,
    val accessibilityAttributes: HotelAccessibilityAttributesDto? = null,
    val deletedAt: Instant? = null
)
