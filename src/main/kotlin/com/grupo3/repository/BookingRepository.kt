package com.grupo3.repository

import com.grupo3.model.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookingRepository : JpaRepository<Booking, String> {
    fun findAllByUserEmailOrderByCreatedAtDesc(email: String): List<Booking>
}