package com.grupo3.model.hotel

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "locations")
data class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    @Column(nullable = false, unique = true, length = 10)
    var code: String = "",

    @Column(nullable = false)
    var city: String = "",

    @Column(nullable = false)
    var country: String = "",

    var state: String? = null,

    var latitude: Double? = null,

    var longitude: Double? = null,

    var isPopular: Boolean = false
)