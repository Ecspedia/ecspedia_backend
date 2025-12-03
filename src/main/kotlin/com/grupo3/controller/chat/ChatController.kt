package com.grupo3.controller.chat

import com.grupo3.dto.chat.ChatResponseDto
import com.grupo3.service.chatbox.ChatService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated

@Controller
@Validated
class ChatController(
    private val chatService: ChatService,
) {
    @QueryMapping
    fun sendMessage(@Argument @Valid message: String): ChatResponseDto {
        return chatService.sendMessageToApi(message)
    }
}
