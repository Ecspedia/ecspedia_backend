package com.grupo3.dto.chat

import com.grupo3.dto.hotel.HotelResponseDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

enum class ChatResponseType {
    SEARCH_RESULTS,      // Hotel search results
    QUESTION_ANSWER,     // Answer to specific hotel question
    BOOKING,
    OTHER,
    ERROR                // System error
}


data class ChatResponseDto(
    val success: Boolean = true,
    val chatResponseType: ChatResponseType,
    val searchData: List<HotelResponseDto>,
    val questionData: String,
    val otherData: String,
    val bookingData: String,
    val errorData: String
)
