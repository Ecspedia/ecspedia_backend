package com.grupo3.service.chatbox

import com.grupo3.dto.chat.ChatResponseDto
import com.grupo3.dto.chat.ChatResponseType
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val geminiApiService: GeminiApiService,
) {


    @Cacheable(value = ["sendMessage"], key = "#message.toLowerCase()")
    fun sendMessageToApi(message: String): ChatResponseDto{
        val response = geminiApiService.sendMessage(message)
        val typeOf = ChatResponseType.HOTELS
        return ChatResponseDto(response, typeOf)
    }
}