package com.grupo3.model.hotel

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "locations",
    uniqueConstraints = [
        UniqueConstraint(
            name = "fk_country_city",  // Constraint name (helpful for debugging)
            columnNames = ["country", "city"]   // Combination must be unique
        )
    ]
)
data class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    var id: String? = null,

    @Column(nullable = false, length = 10)
    var code: String = "",

    @Column(nullable = false)
    var city: String = "",

    @Column(nullable = false)
    var country: String = "",

    var state: String? = null,

    var latitude: Double? = null,

    var longitude: Double? = null,

    @Column(nullable = false, columnDefinition = "boolean default false")
    var isPopular: Boolean = false
)
