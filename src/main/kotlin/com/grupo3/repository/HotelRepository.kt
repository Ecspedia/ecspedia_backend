package com.grupo3.repository

import com.grupo3.model.Hotel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface HotelRepository : JpaRepository<Hotel, String> {


    fun findByLocation(location: String): List<Hotel>
    fun findByIsAvailable(isAvailable: Boolean): List<Hotel>
    fun findByPricePerNightLessThanEqual(maxPrice: Double): List<Hotel>
}
