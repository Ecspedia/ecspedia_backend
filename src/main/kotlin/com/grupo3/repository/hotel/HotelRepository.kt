package com.grupo3.repository.hotel

import com.grupo3.model.hotel.Hotel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HotelRepository : JpaRepository<Hotel, String> {
    fun findByLocation(location: String): List<Hotel>
}

