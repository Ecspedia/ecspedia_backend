package com.grupo3.controller.chat

import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.agents.core.agent.AIAgent
import com.grupo3.config.GeminiApiConfig
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated

@Controller
@Validated
class ChatController(
    private val geminiApiConfig: GeminiApiConfig,
) {


    @QueryMapping
    fun sendMessage(@Argument @Valid message: String): String {
        val agent = AIAgent(
            promptExecutor = simpleGoogleAIExecutor(geminiApiConfig.key),
            systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
            llmModel = GoogleModels.Gemini2_5Flash
        )

        val result = runBlocking {
            agent.run(message)
        }
        return result
    }
}
