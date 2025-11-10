package com.grupo3.service.hotel.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.grupo3.dto.hotel.HotelAccessibilityAttributesDto
import com.grupo3.dto.hotel.HotelResponseDto

@JsonIgnoreProperties(ignoreUnknown = true)
data class LiteApiSearchResponse(
    val data: List<LiteApiHotelDto> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LiteApiHotelDto(
    val id: String,
    val name: String? = null,
    val hotelDescription: String? = null,
    val hotelTypeId: Int? = null,
    val chainId: Int? = null,
    val chain: String? = null,
    val currency: String? = null,
    val country: String? = null,
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val zip: String? = null,
    @JsonProperty("main_photo")
    val mainPhoto: String? = null,
    val thumbnail: String? = null,
    val stars: Int? = null,
    val rating: Double? = null,
    val reviewCount: Int? = null,
    val facilityIds: List<Int>? = null,
    val accessibilityAttributes: LiteApiAccessibilityAttributesDto? = null,
    val deletedAt: String? = null
)

data class LiteApiAccessibilityAttributesDto(
    val attributes: List<String>? = null,
    val showerChair: Boolean? = null,
    val entranceType: String? = null,
    val petFriendly: String? = null,
    val rampAngle: Double? = null,
    val rampLength: Double? = null,
    val entranceDoorWidth: Double? = null,
    val roomMaxGuestsNumber: Double? = null,
    val distanceFromTheElevatorToTheAccessibleRoom: Double? = null
)

fun LiteApiHotelDto.toResponseDto(fallbackCurrency: String? = "EUR"): HotelResponseDto {
    val locationLabel = listOfNotNull(
        city?.takeIf { it.isNotBlank() },
        country?.takeIf { it.isNotBlank() }?.uppercase()
    ).joinToString(", ")

    return HotelResponseDto(
        id = id,
        name = name ?: locationLabel.ifBlank { "Hotel $id" },
        location = locationLabel,
        image = mainPhoto ?: thumbnail,
        isAvailable = true,
        rating = rating,
        reviewCount = reviewCount,
        pricePerNight = calculateDisplayPrice(stars, rating),
        latitude = latitude,
        longitude = longitude,
        hotelDescription = hotelDescription,
        hotelTypeId = hotelTypeId,
        chain = chain ?: chainId?.let { "Chain #$it" },
        currency = currency ?: fallbackCurrency,
        country = country,
        city = city,
        address = address,
        zip = zip,
        mainPhoto = mainPhoto,
        thumbnail = thumbnail,
        stars = stars,
        facilityIds = facilityIds,
        accessibilityAttributes = accessibilityAttributes?.toDto(),
        deletedAt = null
    )
}

private fun calculateDisplayPrice(stars: Int?, rating: Double?): Double {
    val starBased = stars?.takeIf { it > 0 }?.times(70.0)
    val ratingBased = rating?.takeIf { it > 0 }?.times(18.0)
    return (starBased ?: ratingBased ?: 150.0).coerceIn(80.0, 650.0)
}

private fun LiteApiAccessibilityAttributesDto.toDto() = HotelAccessibilityAttributesDto(
    attributes = attributes,
    showerChair = showerChair,
    entranceType = entranceType,
    petFriendly = petFriendly,
    rampAngle = rampAngle?.toInt(),
    rampLength = rampLength?.toInt(),
    entranceDoorWidth = entranceDoorWidth?.toInt(),
    roomMaxGuestsNumber = roomMaxGuestsNumber?.toInt(),
    distanceFromTheElevatorToTheAccessibleRoom = distanceFromTheElevatorToTheAccessibleRoom?.toInt()
)
