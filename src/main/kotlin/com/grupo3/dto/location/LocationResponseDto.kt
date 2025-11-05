package com.grupo3.dto.location

data class LocationResponseDto(
    val id: String,
    val code: String,
    val city: String,
    val country: String,
    val state: String?,
    val latitude: Double?,
    val longitude: Double?
)

