package com.grupo3.model.hotel

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "hotels")
data class Hotel(
    @Id
    @Column(nullable = false, updatable = false)
    var id: String = UUID.randomUUID().toString(),

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

    var longitude: Double? = null,

    @Column(columnDefinition = "TEXT")
    var hotelDescription: String? = null,

    var hotelTypeId: Int? = null,

    var chain: String? = null,

    var currency: String? = null,

    var country: String? = null,

    var city: String? = null,

    var address: String? = null,

    var zip: String? = null,

    @Column(length = 500)
    var mainPhoto: String? = null,

    @Column(length = 500)
    var thumbnail: String? = null,

    var stars: Int? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var facilityIds: List<Int>? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var accessibilityAttributes: HotelAccessibilityAttributes? = null,

    var deletedAt: Instant? = null
)

data class HotelAccessibilityAttributes(
    var attributes: List<String>? = null,
    var showerChair: Boolean? = null,
    var entranceType: String? = null,
    var petFriendly: String? = null,
    var rampAngle: Int? = null,
    var rampLength: Int? = null,
    var entranceDoorWidth: Int? = null,
    var roomMaxGuestsNumber: Int? = null,
    var distanceFromTheElevatorToTheAccessibleRoom: Int? = null
)
