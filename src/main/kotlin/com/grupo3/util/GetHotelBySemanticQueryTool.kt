
package com.grupo3.util

import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import com.grupo3.service.hotel.HotelService
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable
data class SemanticQueryArgs(
    val query: String
)

class GetHotelBySemanticQueryTool(
    private val hotelService: HotelService
) : Tool<SemanticQueryArgs, String>() {

    override val argsSerializer = SemanticQueryArgs.serializer()

    override val resultSerializer = String.serializer()

    override val name = "search_hotels"

    override val description = """
        Search for hotels using natural language.
        Use this when the user wants to find hotels by location, amenities, or preferences.
        Examples: "hotels in Paris", "luxury beach resorts", "cheap hotels near airport"
    """.trimIndent()

    override val descriptor = ToolDescriptor(
        name = name,
        description = description,
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "query",
                description = "The search query describing what kind of hotel the user wants",
                type = ToolParameterType.String
            )
        ),
        optionalParameters = emptyList()
    )

    override suspend fun execute(args: SemanticQueryArgs): String {
        println("DEBUG: About to run search hotel by natural language...")
        return hotelService.searchHotelsByNaturalLanguage(args.query)
    }
}