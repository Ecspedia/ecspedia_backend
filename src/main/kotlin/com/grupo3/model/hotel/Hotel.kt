package com.grupo3.model.hotel

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "hotels")
data class Hotel(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    @Column(nullable = false)
    var name: String? = null,

    @Column(nullable = false)
    var location: String? = null,

    @Column(length = 500)
    var image: String? = null,

    @Column(nullable = false)
    var isAvailable: Boolean = true,

    var rating: Double? = null,

    var reviewCount: Int? = null,

    @Column(nullable = false)
    var pricePerNight: Double? = null,

    var latitude: Double? = null,

    var longitude: Double? = null
)

