package com.grupo3.agent

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import com.grupo3.service.hotel.HotelService
import com.grupo3.util.GetHotelBySemanticQueryTool
import org.springframework.stereotype.Service


data class MessageWithClassification(val originalMessage: String, val classification: String)
data class HotelMessageData(
    val originalMessage: String,
    val hotelId: String?,
    val query: String,
    val isQuestion: Boolean
)

class SimpleHotelGraph (private val hotelService: HotelService) {

    fun create() = strategy<String, String>("simple-hotel") {

        // Node 1: Decision Maker - Pass through original message with classification
        val classify by node<String, MessageWithClassification>("classify") { input ->
            println("[Node: classify] Input: '$input'")
            val response = llm.writeSession {
                appendPrompt {
                    system("""
                        You classify user messages.
                        If about hotels/travel, respond: HOTEL
                        If not, respond: OTHER
                        Respond with only one word.
                    """.trimIndent())
                    user(input)
                }
                requestLLMWithoutTools()
            }
            println("[Node: classify] LLM Response: '${response.content}'")
            MessageWithClassification(input, response.content)
        }

        // Node 2: Classify general question or specified about hotel
        val classifyHotelIntent by node<MessageWithClassification, HotelMessageData>("classify_hotel_intent") { messageData ->
            println("[Node: classifyHotelIntent] Input: $messageData")
            val input = messageData.originalMessage

            // Check if message has hotelId prefix
            val hotelIdRegex = Regex("""^hotelId:(\S+)\s*(.*)$""", RegexOption.IGNORE_CASE)
            val match = hotelIdRegex.find(input.trim())

            val result = if (match != null) {
                // Has hotelId prefix → specific question
                HotelMessageData(
                    originalMessage = input,
                    hotelId = match.groupValues[1],
                    query = match.groupValues[2].ifEmpty { "Tell me about this hotel" },
                    isQuestion = true
                )
            } else {
                // No hotelId → general search
                HotelMessageData(
                    originalMessage = input,
                    hotelId = null,
                    query = input,
                    isQuestion = false
                )
            }
            println("[Node: classifyHotelIntent] Output: $result")
            result
        }

        // Node 3: Handle Search - DIRECTLY CALL SERVICE (Bypassing LLM tool call issues)
        val handleSearch by node<HotelMessageData, String>("handle_search") { hotelData ->
            println("[Node: handleSearch] Executing direct search for query: '${hotelData.query}'")
            
            // Directly call the service instead of asking LLM to call the tool
            val result = hotelService.searchHotelsByNaturalLanguage(hotelData.query)
            
            println("[Node: handleSearch] Service Result: $result")
            result
        }

        // Node 4: Handle Question - DIRECTLY CALL SERVICE
        val handleQuestion by node<HotelMessageData, String>("handle_question") { hotelData ->
            println("[Node: handleQuestion] Executing direct question for HotelId: ${hotelData.hotelId}, Query: ${hotelData.query}")
            
            val result = if (hotelData.hotelId != null) {
                hotelService.askHotelQuestion(hotelData.query, hotelData.hotelId)
            } else {
                "Error: Hotel ID missing for question."
            }

            println("[Node: handleQuestion] Service Response: $result")
            result
        }

        // Node 5: Handle Other (non-hotel requests)
        val handleOther by node<MessageWithClassification, String>("handle_other") { messageData ->
            println("[Node: handleOther] Message: ${messageData.originalMessage}")

            val response = llm.writeSession {
                appendPrompt {
                    system("""
                        You are a hotel search assistant.
                        The user asked something not related to hotels.
                        Politely redirect them to ask about hotels.
                        
                        Return as JSON:
                        {
                            "message": "your polite redirect message"
                        }
                    """.trimIndent())
                    user(messageData.originalMessage)
                }
                requestLLMWithoutTools()
            }
            println("[Node: handleOther] Response: ${response.content}")
            response.content
        }

        // Define edges (flow)
        // First Node
        edge(nodeStart forwardTo classify)

        // Classify Topic → Hotel Intent OR Other
        edge(classify forwardTo classifyHotelIntent onCondition { output: MessageWithClassification ->
            println("[Edge] Checking condition for classify -> classifyHotelIntent: ${output.classification}")
            output.classification.contains("HOTEL")
        })
        edge(classify forwardTo handleOther)

        // Classify Hotel Intent → Search OR Question
        edge(classifyHotelIntent forwardTo handleQuestion onCondition { output: HotelMessageData ->
            println("[Edge] Checking condition for classifyHotelIntent -> handleQuestion: isQuestion=${output.isQuestion}")
            output.isQuestion
        })
        edge(classifyHotelIntent forwardTo handleSearch)

        // End
        edge(handleSearch forwardTo nodeFinish)
        edge(handleQuestion forwardTo nodeFinish)
        edge(handleOther forwardTo nodeFinish)

    }
}