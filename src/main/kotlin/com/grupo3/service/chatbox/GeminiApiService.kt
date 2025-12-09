package com.grupo3.service.chatbox


import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import com.grupo3.agent.HotelGraph
import com.grupo3.config.GeminiApiConfig
import com.grupo3.dto.chat.ChatResponseDto
import com.grupo3.service.booking.BookingService
import com.grupo3.service.hotel.HotelService

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service


@Service
class GeminiApiService(
    private val geminiApiConfig: GeminiApiConfig,
    private val toolRegistry: ToolRegistry,
    private val hotelService: HotelService,
    private val bookingService: BookingService
) {



    fun sendMessageCustomStrategy(message: String): ChatResponseDto {
        println("DEBUG: toolRegistry = $toolRegistry")
        println("DEBUG: tools available = ${toolRegistry.tools}")
        // Configure the agent
        val agentConfig = AIAgentConfig(
            prompt = Prompt.build("hotel") {
                system(
                    """ you are hotel assistant
                """.trimIndent()
                )
            },
            model = GoogleModels.Gemini2_5Flash,
            maxAgentIterations = 4
        )

        val agent = AIAgent(
            promptExecutor = simpleGoogleAIExecutor(geminiApiConfig.key),
            toolRegistry = toolRegistry,
            agentConfig = agentConfig,
            strategy = HotelGraph(hotelService,bookingService).create()
        )


        val result = runBlocking {
            println("DEBUG: About to run agent.run() with custom strategy...")
            agent.run(message)
        }

        println("DEBUG: Agent result = $result")
        return result

    }





}