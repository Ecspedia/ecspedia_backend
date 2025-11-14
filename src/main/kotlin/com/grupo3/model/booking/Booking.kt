package com.grupo3.model.booking

import com.grupo3.model.User
import com.grupo3.model.hotel.Hotel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "bookings",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_bookings_hotel_user_slot",
            columnNames = ["hotel_id", "start_time", "end_time", "user_id"]
        )
    ]
)
data class Booking(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    var hotel: Hotel,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false, length = 80)
    var firstNameGuest: String,

    @Column(nullable = false, length = 80)
    var lastNameGuest: String,

    @Column(nullable = false, length = 255)
    var emailGuest: String,

    @Column(length = 25)
    var phoneNumberGuest: String? = null,

    @Column(nullable = false)
    var startTime: Instant,

    @Column(nullable = false)
    var endTime: Instant,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: BookingStatus = BookingStatus.PENDING,

    @Column
    var price: Long? = null,

    @Column(length = 3)
    var currency: String? = null,

    @Column(length = 500)
    var notes: String? = null,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column
    var confirmedAt: Instant? = null,

    @Column
    var canceledAt: Instant? = null
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELED
}
