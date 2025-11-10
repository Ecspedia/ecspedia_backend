package com.grupo3.repository.booking

import com.grupo3.model.booking.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookingRepository : JpaRepository<Booking, String>{


    fun  findAllByUserEmail(email:String):List<Booking>
}
