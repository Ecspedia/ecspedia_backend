package com.grupo3.agent.util

import com.grupo3.agent.dto.BookingDetails
import kotlinx.serialization.json.Json

object MessageParser {

    private val userIdRegex = Regex("""userId:(\S+)""", RegexOption.IGNORE_CASE)
    private val hotelIdRegex = Regex("""hotelId:(\S+)""", RegexOption.IGNORE_CASE)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    data class ExtractedIds(
        val userId: String?,
        val hotelId: String?,
        val cleanQuery: String
    )

    fun extractIds(input: String): ExtractedIds {
        val userId = userIdRegex.find(input)?.groupValues?.get(1)
        val hotelId = hotelIdRegex.find(input)?.groupValues?.get(1)

        val cleanQuery = input
            .replace(Regex("""userId:\S+\s*""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""hotelId:\S+\s*""", RegexOption.IGNORE_CASE), "")
            .trim()

        return ExtractedIds(userId, hotelId, cleanQuery)
    }

    fun parseBookingDetails(rawJson: String): BookingDetails {
        val cleanJson = rawJson
            .replace(Regex("```json\\s*"), "")
            .replace(Regex("```\\s*"), "")
            .trim()

        return try {
            json.decodeFromString<BookingDetails>(cleanJson)
        } catch (e: Exception) {
            BookingDetails()
        }
    }

    fun validateBookingDetails(details: BookingDetails): List<String> {
        val missingFields = mutableListOf<String>()

        if (details.firstNameGuest.isNullOrBlank()) missingFields.add("guest first name")
        if (details.lastNameGuest.isNullOrBlank()) missingFields.add("guest last name")
        if (details.startDate.isNullOrBlank()) missingFields.add("check-in date")
        if (details.endDate.isNullOrBlank()) missingFields.add("check-out date")

        return missingFields
    }
}