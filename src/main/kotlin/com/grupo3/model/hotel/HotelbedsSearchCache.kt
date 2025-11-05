package com.grupo3.model.hotel

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant

@Entity
@Table(name = "hotelbeds_search_cache")
data class HotelbedsSearchCache(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @Column(nullable = false, length = 30)
    val checkIn: String,

    @Column(nullable = false, length = 30)
    val checkOut: String,

    @Column(nullable = false)
    val adults: Int,

    @Column(nullable = false, length = 100)
    val location: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    val responsePayload: String,

    @Column(nullable = false)
    val fetchedAt: Instant,

    @Column(nullable = false)
    val expiresAt: Instant,

    @Column(nullable = false)
    val success: Boolean
)

