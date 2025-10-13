package com.grupo3.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

import jakarta.persistence.Id

@Entity
@Table(name = "hotels")
data class Hotel(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    @field:NotBlank(message = "Name is required")
    @Column(nullable = false)
    var name: String? = null,

    @field:NotBlank(message = "Location is required")
    @Column(nullable = false)
    var location: String? = null,

    @Column(length = 500)
    var image: String? = null,

    @Column(nullable = false)
    var isAvailable: Boolean = true,

    @field:DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @field:DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    var rating: Double? = null,

    @field:Min(value = 0, message = "Review count cannot be negative")
    var reviewCount: Int? = null,

    @field:NotNull(message = "Price per night is required")
    @field:DecimalMin(value = "0.0", message = "Price must be positive")
    @Column(nullable = false)
    var pricePerNight: Double? = null,

    @field:DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @field:DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Column(nullable = true)
    var latitude: Double? = null,

    @field:DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @field:DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Column(nullable = true)
    var longitude: Double? = null

)