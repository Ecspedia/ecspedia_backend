package com.grupo3.dto.chat

enum class ChatResponseType{
    NORMAL,
    HOTELS
}

data class ChatResponseDto(
    val response:String,    
    val typeOf:ChatResponseType
)