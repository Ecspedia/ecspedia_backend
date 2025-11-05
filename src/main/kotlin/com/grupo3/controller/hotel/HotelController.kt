package com.grupo3.controller.hotel

import com.grupo3.dto.hotel.HotelCreateDto
import com.grupo3.dto.hotel.HotelResponseDto
import com.grupo3.service.hotel.HotelService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin

@Controller
@Validated
@CrossOrigin(origins = ["*"])
class HotelController(private val hotelService: HotelService) {

    @QueryMapping
    fun hotels(): List<HotelResponseDto> = hotelService.getAllHotels()

    @QueryMapping
    fun hotelsByLocation(@Argument location: String): List<HotelResponseDto> =
        hotelService.getHotelsByLocation(location)

    @QueryMapping
    fun hotelsByLocationName(
        @Argument location: String,
        @Argument checkIn: String,
        @Argument checkOut: String,
        @Argument adults: Int
    ): String = hotelService.hotelsByLocationName(location, checkIn, checkOut, adults)

    @MutationMapping
    fun createHotel(@Argument @Valid hotelCreateDto: HotelCreateDto): HotelResponseDto =
        hotelService.createHotel(hotelCreateDto)
}
