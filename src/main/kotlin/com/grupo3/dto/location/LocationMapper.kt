package com.grupo3.dto.location

import com.grupo3.model.hotel.Location

object LocationMapper {

    fun toEntity(dto: LocationCreateDto) = Location(
        code = dto.code.normalize(),
        city = dto.city.trim(),
        country = dto.country.trim(),
        state = dto.state?.trim(),
        latitude = dto.latitude,
        longitude = dto.longitude
    )

    fun toResponseDto(entity: Location) = LocationResponseDto(
        id = entity.id!!,
        code = entity.code,
        city = entity.city.toTitleCase(),
        country = entity.country.toTitleCase(),
        state = entity.state,
        latitude = entity.latitude,
        longitude = entity.longitude,
        isPopular = entity.isPopular
    )

    fun toEntity(dto: LocationResponseDto) = Location(
        id = dto.id,
        code = dto.code.normalize(),
        city = dto.city.trim(),
        country = dto.country.trim(),
        state = dto.state?.trim(),
        latitude = dto.latitude,
        longitude = dto.longitude,
        isPopular = dto.isPopular
    )


    private fun String.normalize(): String = this.trim().uppercase()

    private fun String.toTitleCase(): String = this
        .lowercase()
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.titlecaseChar() }
        }
}