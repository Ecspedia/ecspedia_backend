package com.grupo3.dto.hotel

import com.grupo3.model.Hotel


object HotelMapper {


    fun toEntity(dto: HotelCreateDto) = Hotel(
        name = dto.name,
        location = dto.location,
        image = dto.image,
        isAvailable = dto.isAvailable,
        rating = dto.rating,
        reviewCount = dto.reviewCount,
        pricePerNight = dto.pricePerNight,
        latitude = dto.latitude,
        longitude = dto.longitude
    )


    fun toResponseDto(hotel: Hotel) = HotelResponseDto(
        id = hotel.id!!,
        name = hotel.name!!,
        location = hotel.location!!,
        image = hotel.image,
        isAvailable = hotel.isAvailable,
        rating = hotel.rating,
        reviewCount = hotel.reviewCount,
        pricePerNight = hotel.pricePerNight!!,
        latitude = hotel.latitude,
        longitude = hotel.longitude
    )


    fun updateEntity(hotel: Hotel, dto: HotelUpdateDto) {
        dto.name?.let { hotel.name = it }
        dto.location?.let { hotel.location = it }
        dto.image?.let { hotel.image = it }
        dto.isAvailable?.let { hotel.isAvailable = it }
        dto.rating?.let { hotel.rating = it }
        dto.reviewCount?.let { hotel.reviewCount = it }
        dto.pricePerNight?.let { hotel.pricePerNight = it }
        dto.latitude?.let { hotel.latitude = it }
        dto.longitude?.let { hotel.longitude = it }
    }
}
