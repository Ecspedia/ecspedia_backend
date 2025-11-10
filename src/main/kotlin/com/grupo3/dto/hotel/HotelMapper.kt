package com.grupo3.dto.hotel

import com.grupo3.model.hotel.Hotel
import com.grupo3.model.hotel.HotelAccessibilityAttributes
import java.util.UUID

object HotelMapper {

    fun toEntity(dto: HotelCreateDto) = Hotel(
        id = dto.id ?: UUID.randomUUID().toString(),
        name = dto.name,
        location = dto.location,
        image = dto.image,
        isAvailable = dto.isAvailable,
        rating = dto.rating,
        reviewCount = dto.reviewCount,
        pricePerNight = dto.pricePerNight,
        latitude = dto.latitude,
        longitude = dto.longitude,
        hotelDescription = dto.hotelDescription,
        hotelTypeId = dto.hotelTypeId,
        chain = dto.chain,
        currency = dto.currency,
        country = dto.country,
        city = dto.city,
        address = dto.address,
        zip = dto.zip,
        mainPhoto = dto.mainPhoto,
        thumbnail = dto.thumbnail,
        stars = dto.stars,
        facilityIds = dto.facilityIds,
        accessibilityAttributes = dto.accessibilityAttributes.toEntity(),
        deletedAt = dto.deletedAt
    )

    fun toResponseDto(hotel: Hotel) = HotelResponseDto(
        id = hotel.id,
        name = hotel.name!!,
        location = hotel.location!!,
        image = hotel.image,
        isAvailable = hotel.isAvailable,
        rating = hotel.rating,
        reviewCount = hotel.reviewCount,
        pricePerNight = hotel.pricePerNight!!,
        latitude = hotel.latitude,
        longitude = hotel.longitude,
        hotelDescription = hotel.hotelDescription,
        hotelTypeId = hotel.hotelTypeId,
        chain = hotel.chain,
        currency = hotel.currency,
        country = hotel.country,
        city = hotel.city,
        address = hotel.address,
        zip = hotel.zip,
        mainPhoto = hotel.mainPhoto,
        thumbnail = hotel.thumbnail,
        stars = hotel.stars,
        facilityIds = hotel.facilityIds,
        accessibilityAttributes = hotel.accessibilityAttributes.toDto(),
        deletedAt = hotel.deletedAt
    )

    private fun HotelAccessibilityAttributesDto?.toEntity(): HotelAccessibilityAttributes? =
        this?.let {
            HotelAccessibilityAttributes(
                attributes = it.attributes,
                showerChair = it.showerChair,
                entranceType = it.entranceType,
                petFriendly = it.petFriendly,
                rampAngle = it.rampAngle,
                rampLength = it.rampLength,
                entranceDoorWidth = it.entranceDoorWidth,
                roomMaxGuestsNumber = it.roomMaxGuestsNumber,
                distanceFromTheElevatorToTheAccessibleRoom = it.distanceFromTheElevatorToTheAccessibleRoom
            )
        }

    private fun HotelAccessibilityAttributes?.toDto(): HotelAccessibilityAttributesDto? =
        this?.let {
            HotelAccessibilityAttributesDto(
                attributes = it.attributes,
                showerChair = it.showerChair,
                entranceType = it.entranceType,
                petFriendly = it.petFriendly,
                rampAngle = it.rampAngle,
                rampLength = it.rampLength,
                entranceDoorWidth = it.entranceDoorWidth,
                roomMaxGuestsNumber = it.roomMaxGuestsNumber,
                distanceFromTheElevatorToTheAccessibleRoom = it.distanceFromTheElevatorToTheAccessibleRoom
            )
        }
}
