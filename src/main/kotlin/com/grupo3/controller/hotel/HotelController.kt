package com.grupo3.controller.hotel

import com.grupo3.dto.hotel.HotelCreateDto
import com.grupo3.dto.hotel.HotelResponseDto
import com.grupo3.service.hotel.HotelService
import jakarta.validation.Valid
import org.springframework.cache.annotation.Cacheable

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

    @Cacheable(value = ["hotelsByLocation"], key = "#locationQuery.toLowerCase()")
    @QueryMapping(name = "hotelsByLocation")
    fun searchHotelsByLocation(
        @Argument location: String
    ): List<HotelResponseDto> = hotelService.searchHotelsByLocation(location)

    @QueryMapping(name = "popularHotels")
    fun topPopularHotels(): List<HotelResponseDto> = hotelService.getTopPopularHotels()

    @QueryMapping(name = "hotels")
    fun getAllHotels(): List<HotelResponseDto> = hotelService.getAllHotels()

    @QueryMapping
    fun hotelExists(@Argument id: String): Boolean = hotelService.hotelExists(id)

    @MutationMapping(name = "createHotel")
    fun saveHotel(@Argument @Valid hotelCreateDto: HotelCreateDto): HotelResponseDto =
        hotelService.saveHotel(hotelCreateDto)


}
