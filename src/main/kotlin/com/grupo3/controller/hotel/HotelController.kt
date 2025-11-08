package com.grupo3.controller.hotel

import com.grupo3.dto.hotel.hotelbeds.CachedHotelQueryDto
import com.grupo3.service.hotel.HotelService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin

@Controller
@Validated
@CrossOrigin(origins = ["*"])
class HotelController(private val hotelService: HotelService) {

    @QueryMapping
    fun searchHotelsByLocation(
        @Argument location: String,
        @Argument checkIn: String,
        @Argument checkOut: String,
        @Argument adults: Int
    ): CachedHotelQueryDto = hotelService.getHotelsByLocation(location, checkIn, checkOut, adults)

    @QueryMapping
    fun cachedHotelQueries(): List<CachedHotelQueryDto> = hotelService.getAllCacheQuerys()
}