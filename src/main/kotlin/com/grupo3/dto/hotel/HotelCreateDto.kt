package com.grupo3.dto.hotel


import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class HotelCreateDto(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Location is required")
    val location: String,

    val image: String? = null,

    val isAvailable: Boolean = true,

    @field:DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @field:DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    val rating: Double? = null,

    @field:Min(value = 0, message = "Review count cannot be negative")
    val reviewCount: Int? = null,

    @field:NotNull(message = "Price per night is required")
    @field:DecimalMin(value = "0.0", message = "Price must be positive")
    var pricePerNight: Double,

    @field:DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @field:DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    val latitude: Double? = null,

    @field:DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @field:DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    val longitude: Double? = null
)


