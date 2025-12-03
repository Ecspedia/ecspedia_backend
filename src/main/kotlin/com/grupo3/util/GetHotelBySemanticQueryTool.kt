package com.grupo3.util

import ai.koog.agents.core.tools.SimpleTool
import com.grupo3.service.hotel.HotelService
import kotlinx.serialization.KSerializer

class GetHotelBySemanticQueryTool(
    override val argsSerializer: KSerializer<String>,
    override val description: String,
    val hotelService: HotelService
) : SimpleTool<String>() {

    override suspend fun doExecute(args: String): String {
        val response = hotelService.searchHotelsByNaturalLanguage(args)
        return response
    }
}