package com.grupo3.dto.location

import com.grupo3.model.hotel.Location

object LocationMapper {

    fun toEntity(dto: LocationCreateDto) = Location(
        code = dto.code.trim().uppercase(),
        city = dto.city.trim(),
        country = dto.country.trim(),
        state = dto.state?.trim(),
        latitude = dto.latitude,
        longitude = dto.longitude
    )

    fun toResponseDto(entity: Location) = LocationResponseDto(
        id = entity.id!!,
        code = entity.code,
        city = entity.city,
        country = entity.country,
        state = entity.state,
        latitude = entity.latitude,
        longitude = entity.longitude,
        isPopular = entity.isPopular
    )
}
