package com.grupo3.controller

import com.grupo3.model.Hotel
import com.grupo3.service.HotelService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = ["*"])
class HotelController(private val hotelService: HotelService) {

    @PostMapping
    fun createHotel(@Valid @RequestBody hotel: Hotel): ResponseEntity<Hotel> {
        val createdHotel = hotelService.createHotel(hotel)
        return ResponseEntity(createdHotel, HttpStatus.CREATED)
    }


    @GetMapping
    fun getAllHotels(): ResponseEntity<List<Hotel>> {
        val hotels = hotelService.getAllHotels()
        return ResponseEntity(hotels, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getHotelById(@PathVariable id: String): ResponseEntity<Hotel> {
        val hotel = hotelService.getHotelById(id)
        return if (hotel != null) {
            ResponseEntity(hotel, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/location/{location}")
    fun getHotelsByLocation(@PathVariable location: String): ResponseEntity<Any> {
        val hotels = hotelService.getHotelsByLocation(location)

        return if (hotels.isEmpty()) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf(
                    "status" to 404,
                    "message" to "No hotels found in location: $location",
                    "data" to emptyList<Hotel>()
                )
            )
        } else {
            ResponseEntity.ok(hotels)
        }


    }

    // CUSTOM - GET /api/hotels/available
    @GetMapping("/available")
    fun getAvailableHotels(): ResponseEntity<List<Hotel>> {
        val hotels = hotelService.getAvailableHotels()
        return ResponseEntity(hotels, HttpStatus.OK)
    }

}