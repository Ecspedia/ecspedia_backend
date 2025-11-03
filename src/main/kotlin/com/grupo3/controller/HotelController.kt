package com.grupo3.controller


import com.grupo3.dto.hotel.HotelCreateDto
import com.grupo3.dto.hotel.HotelResponseDto
import com.grupo3.service.HotelService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@Controller
@Validated
@CrossOrigin(origins = ["*"])
class HotelController(private val hotelService: HotelService) {

    @QueryMapping
    fun hotels(): List<HotelResponseDto> {
        return hotelService.getAllHotels()
    }

    @QueryMapping
    fun hotelsByLocation(@Argument location: String): List<HotelResponseDto> {
        return hotelService.getHotelsByLocation(location)
    }


    @MutationMapping
    fun createHotel(@Argument @Valid hotelCreateDto: HotelCreateDto): HotelResponseDto {
        return hotelService.createHotel(hotelCreateDto)
    }
}
