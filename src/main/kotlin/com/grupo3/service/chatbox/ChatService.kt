package com.grupo3.service.chatbox

import ai.koog.agents.core.agent.entity.AIAgentGraphStrategy
import com.grupo3.agent.HotelGraph
import com.grupo3.dto.chat.ChatResponseDto
import com.grupo3.dto.chat.ChatResponseType
import kotlinx.serialization.json.Json
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val geminiApiService: GeminiApiService,
) {
//    @Cacheable(value = ["sendMessage"], key = "#message.toLowerCase()")
    fun sendMessageToApi(message: String): ChatResponseDto {
        return geminiApiService.sendMessageCustomStrategy(message)
    }
}

