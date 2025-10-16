package com.grupo3.service

import com.grupo3.model.Hotel
import com.grupo3.repository.HotelRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.constraints.Null
import org.springframework.dao.DataIntegrityViolationException
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

    fun updateHotelById(id: String, updated: Hotel): Hotel {
        val existing = hotelRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Hotel with id '$id' not found") }

        return try {
            existing.apply {
                name = updated.name
                location = updated.location
                image = updated.image
                isAvailable = updated.isAvailable
                rating = updated.rating
                reviewCount = updated.reviewCount
                pricePerNight = updated.pricePerNight
                latitude = updated.latitude
                longitude = updated.longitude
            }
            hotelRepository.save(existing)
        } catch (ex: DataIntegrityViolationException) {
            throw IllegalStateException("Failed to update hotel '$id' due to data integrity violation", ex)
        } catch (ex: Exception) {
            throw RuntimeException("Unexpected error while updating hotel '$id'", ex)
        }
    }






    fun deleteHotelById(id: String) {
        val hotel = hotelRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Hotel with id '$id' not found") }

        try {
            hotelRepository.delete(hotel)
        } catch (ex: DataIntegrityViolationException) {
            throw IllegalStateException("Cannot delete hotel with id '$id' because it is referenced elsewhere", ex)
        }
    }

}