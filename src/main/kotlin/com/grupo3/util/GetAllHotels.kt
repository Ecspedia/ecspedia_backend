package com.grupo3.util

import ai.koog.agents.core.tools.SimpleTool
import com.grupo3.dto.hotel.HotelResponseDto
import com.grupo3.model.hotel.Hotel
import com.grupo3.service.hotel.HotelService
import kotlinx.serialization.KSerializer



class GetAllHotels (
    private val hotelService: HotelService,
    override val argsSerializer: KSerializer<String>,
    override val description: String
): SimpleTool<String>() {

    override suspend fun doExecute(args: String): String {
        val hotels = hotelService.getAllPartialHotels()
        println(hotels.toString())
        return hotels.toString()
    }
}

