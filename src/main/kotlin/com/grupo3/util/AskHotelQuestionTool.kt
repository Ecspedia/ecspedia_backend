package com.grupo3.util

import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import com.grupo3.service.hotel.HotelService
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable
data class AskHotelQuestionArgs(
    val message: String  // Full message: "hotelId:xxx question here"
)

class AskHotelQuestionTool(
    private val hotelService: HotelService
) : Tool<AskHotelQuestionArgs, String>() {

    override val argsSerializer = AskHotelQuestionArgs.serializer()

    override val resultSerializer = String.serializer()

    override val name = "ask_hotel_question"

    override val description = """
        Ask a question about a specific hotel.
        Use this when the message starts with "hotelId:" prefix.
        The tool will extract the hotelId and question automatically.
        Example input: "hotelId:abc123 What amenities does this hotel have?"
    """.trimIndent()

    override val descriptor = ToolDescriptor(
        name = name,
        description = description,
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "message",
                description = "Full message starting with hotelId:xxx followed by the question",
                type = ToolParameterType.String
            )
        ),
        optionalParameters = emptyList()
    )

    override suspend fun execute(args: AskHotelQuestionArgs): String {
        val (hotelId, query) = parseMessage(args.message)

        if (hotelId == null) {
            return "Error: No hotelId found. Message must start with 'hotelId:xxx'"
        }

        return hotelService.askHotelQuestion(query, hotelId)
    }

    private fun parseMessage(message: String): Pair<String?, String> {
        val regex = Regex("""^hotelId:(\S+)\s+(.+)$""", RegexOption.IGNORE_CASE)
        val match = regex.find(message.trim())

        return if (match != null) {
            Pair(match.groupValues[1], match.groupValues[2])
        } else {
            Pair(null, message)
        }
    }
}