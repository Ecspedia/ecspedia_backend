package com.grupo3.config

import ai.koog.agents.core.tools.ToolRegistry
import com.grupo3.service.hotel.HotelService
import com.grupo3.util.AskHotelQuestionTool
import com.grupo3.util.GetHotelBySemanticQueryTool
import com.grupo3.util.AskHotelQuestionArgs
import kotlinx.serialization.serializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KoogConfig(private val hotelService: HotelService) {

    @Bean
    fun toolRegistry(): ToolRegistry {
        return ToolRegistry {
            tool(
                GetHotelBySemanticQueryTool(
                    hotelService = hotelService,

                )
            )
            tool(AskHotelQuestionTool(hotelService = hotelService))
        }
    }
}