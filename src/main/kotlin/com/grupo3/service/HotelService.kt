package com.grupo3.service

import com.grupo3.model.Hotel
import com.grupo3.repository.HotelRepository
import org.springframework.stereotype.Service


@Service
class HotelService(private val hotelRepository: HotelRepository) {

    // Create
    fun createHotel(hotel: Hotel): Hotel {
        return hotelRepository.save(hotel)
    }
    // Read All
    fun getAllHotels(): List<Hotel> {
        return hotelRepository.findAll()
    }

    // Read One
    fun getHotelById(id: String): Hotel? {
        return hotelRepository.findById(id).orElse(null)
    }
    fun getHotelsByLocation(location: String): List<Hotel> {
        return hotelRepository.findByLocation(location)
    }

    fun getAvailableHotels(): List<Hotel> {
        return hotelRepository.findByIsAvailable(true)
    }

}