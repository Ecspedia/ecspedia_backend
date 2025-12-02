package com.grupo3.controller.chat


import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.agents.core.agent.AIAgent
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import kotlinx.coroutines.runBlocking

@Controller
@Validated
class ChatController {


    @QueryMapping
     fun sendMessage(@Argument @Valid message:String): String {
        val agent = AIAgent(
            promptExecutor = simpleGoogleAIExecutor("AIzaSyD8pE5RlaQAy9M-GMT-8_HLiMvpMcC_KdE"),
            systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
            llmModel = GoogleModels.Gemini2_5Flash,

        )

        val result = runBlocking {
            agent.run(message)
        }
        return result
    }
}
