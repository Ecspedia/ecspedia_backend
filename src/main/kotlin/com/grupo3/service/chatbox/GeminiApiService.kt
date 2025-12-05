package com.grupo3.service.chatbox


import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import com.grupo3.agent.SimpleHotelGraph
import com.grupo3.config.GeminiApiConfig
import com.grupo3.service.hotel.HotelService

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service


@Service
class GeminiApiService (
    private val geminiApiConfig: GeminiApiConfig,
    private val toolRegistry: ToolRegistry,
    private val hotelService: HotelService
){

    fun sendMessageCustomStrategy(message:String): String{
        println("DEBUG: toolRegistry = $toolRegistry")
        println("DEBUG: tools available = ${toolRegistry.tools}")

        val agent = AIAgent(
        promptExecutor = simpleGoogleAIExecutor(geminiApiConfig.key),
        toolRegistry = toolRegistry,
        llmModel = GoogleModels.Gemini2_5Flash,
            strategy= SimpleHotelGraph(hotelService).create()
    )


        val result = runBlocking {
            println("DEBUG: About to run agent.run() with custom strategy...")
            agent.run(message)
        }

        println("DEBUG: Agent result = $result")
        return result

    }

    fun sendMessage(message:String):String {
        println("DEBUG: toolRegistry = $toolRegistry")
        println("DEBUG: tools available = ${toolRegistry.tools}")
        println("DEBUG: message = $message")


        val systemPrompt = """
            <system>
                <role>You are a hotel search assistant</role>
                <purpose>Help users find and search for hotels, and answer questions about specific hotels</purpose>

                <tools>
                    <tool name="search_hotels">
                        <description>Use this tool for general hotel searches and queries</description>
                        <when_to_use>When user is searching for hotels without specifying a particular hotel ID</when_to_use>
                    </tool>
                   <tool name="ask_hotel_question">
                        <description>Use this tool to ask questions about a specific hotel</description>
                        <when_to_use>When user provides a hotelId and wants to ask questions about that specific hotel</when_to_use>
                        <required_params>hotelId, searchQuery</required_params>
                    </tool>
                </tools>

                <rules>
                    <rule priority="1">If user message starts with "hotelId:" (e.g., "hotelId:lp19cca ..."), ALWAYS use AskHotelQuestionTool with the extracted hotelId</rule>
                    <rule priority="2">If user provides a hotelId in the message, ALWAYS use AskHotelQuestionTool</rule>
                    <rule priority="3">If user is searching for hotels without a hotelId, ALWAYS use GetHotelBySemanticQueryTool</rule>
                    <rule priority="4">Never answer hotel questions without using the appropriate tool</rule>
                    <rule priority="5">Pass the user's complete search intent to the tool</rule>
                    <rule priority="6">Return the tool results directly to the user</rule>
                    <rule priority="7">If the query is not about hotels, redirect politely</rule>
                </rules>

                <examples>
                    <example>
                        <user_query>I need a 5-star hotel in Paris</user_query>
                        <action>Use GetHotelBySemanticQueryTool with "5-star hotel in Paris"</action>
                    </example>
                    <example>
                        <user_query>Show me luxury hotels near the beach</user_query>
                        <action>Use GetHotelBySemanticQueryTool with "luxury hotels near the beach"</action>
                    </example>
                    <example>
                        <user_query>Find budget hotels available now</user_query>
                        <action>Use GetHotelBySemanticQueryTool with "budget hotels available now"</action>
                    </example>
                    <example>
                        <user_query>Tell me more about hotel-001</user_query>
                        <action>Use AskHotelQuestionTool with hotelId="hotel-001" and searchQuery="Tell me more about this hotel"</action>
                    </example>
                    <example>
                        <user_query>What amenities does hotel-005 have?</user_query>
                        <action>Use AskHotelQuestionTool with hotelId="hotel-005" and searchQuery="What amenities does this hotel have"</action>
                    </example>
                    <example>
                        <user_query>hotelId:lp19cca What amenities does this hotel have?</user_query>
                        <action>Extract hotelId="lp19cca" from the message, then use AskHotelQuestionTool with hotelId="lp19cca" and searchQuery="What amenities does this hotel have"</action>
                    </example>
                </examples>

                <response_format>
                    <instruction>CRITICAL: Return ONLY valid JSON array with hotel objects. Include ONLY these fields:</instruction>
                    <required_fields>
                        <field>id</field>
                        <field>name</field>
                        <field>hotelDescription</field>
                        <field>city</field>
                        <field>country</field>
                        <field>latitude</field>
                        <field>longitude</field>
                        <field>address</field>
                        <field>mainPhoto</field>
                        <field>stars</field>
                        <field>rating</field>
                        <field>reviewCount</field>
                        <field>relevanceScore</field>
                    </required_fields>
                     <json_example>
                      {
                        "data": [
                          {
                            "id": "lp189d3c",
                            "name": "Tour Eiffel Beaugrenelle Theatre",
                            "hotelDescription": "Charming hotel near Eiffel Tower",
                            "city": "Paris",
                            "country": "FR",
                            "latitude": 48.84948,
                            "longitude": 2.28651,
                            "address": "32 rue du Théatre",
                            "mainPhoto": "https://example.com/photo.jpg",
                            "stars": 4,
                            "rating": 4.5,
                            "reviewCount": 250,
                            "relevanceScore": 0.85
                          }
                        ]
                      }
                  </json_example>
                    <ignore_these_fields>
                        <field>primaryHotelId</field>
                        <field>chainId</field>
                        <field>currency</field>
                        <field>zip</field>
                        <field>facilityIds</field>
                        <field>accessibilityAttributes</field>
                        <field>deletedAt</field>
                    </ignore_these_fields>
                    <critical_instructions>
                        <instruction>Return ONLY pure JSON object - absolutely NO markdown code blocks</instruction>
                        <instruction>DO NOT use triple backticks (```)</instruction>
                        <instruction>DO NOT use ```json formatting</instruction>
                        <instruction>Response MUST start with { and end with }</instruction>
                        <instruction>Hotels array MUST be under "data" key</instruction>
                        <instruction>Include relevanceScore (0-1) showing how well each hotel matches the user's query</instruction>
                        <instruction>Only include hotels from tool results - never invent data</instruction>
                        <instruction>Never add any text before or after the JSON object</instruction>
                        <instruction>The entire response should be valid, parseable JSON - nothing else</instruction>
                    </critical_instructions>
                </response_format>
            </system>
        """.trimIndent()


        val agent = AIAgent(
            promptExecutor = simpleGoogleAIExecutor(geminiApiConfig.key),
            systemPrompt = systemPrompt,
            toolRegistry = toolRegistry,
            llmModel = GoogleModels.Gemini2_5Flash

        )


        val result = runBlocking {
            println("DEBUG: About to run agent.run()...")
            agent.run(message)
        }

        return result
    }

}