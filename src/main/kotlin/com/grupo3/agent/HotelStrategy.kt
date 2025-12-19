package com.grupo3.agent

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.builder.forwardTo
import com.grupo3.agent.dto.ClassifiedMessage
import com.grupo3.agent.enums.UserIntent
import com.grupo3.agent.util.MessageParser
import com.grupo3.dto.booking.BookingCreateDto
import com.grupo3.dto.chat.BookingData
import com.grupo3.dto.chat.ChatResponseDto
import com.grupo3.dto.chat.ChatResponseType
import com.grupo3.service.booking.BookingService
import com.grupo3.service.hotel.HotelService


class HotelGraph(private val hotelService: HotelService, private val bookingService: BookingService) {

    fun create() = strategy<String, ChatResponseDto>("simple-hotel") {

        // Node 1: Classify user intent
        val classify by node<String, ClassifiedMessage>("classify") { input ->
            println("[Node: classify] Input: '$input'")
            val extracted = MessageParser.extractIds(input)
            println("[Node: classify] Extracted - userId: ${extracted.userId}, hotelId: ${extracted.hotelId}, query: '${extracted.cleanQuery}'")

            val response = llm.writeSession {
                appendPrompt {
                    system(CLASSIFY_PROMPT)
                    user(extracted.cleanQuery)
                }
                requestLLMWithoutTools()
            }

            val intent = when {
                response.content.contains("BOOKING", ignoreCase = true) -> UserIntent.BOOKING
                response.content.contains("SPECIFIC", ignoreCase = true) -> UserIntent.SPECIFIC_QUESTION
                response.content.contains("GENERAL", ignoreCase = true) -> UserIntent.GENERAL_SEARCH
                else -> UserIntent.OTHER
            }

            println("[Node: classify] Intent: $intent")

            ClassifiedMessage(
                originalMessage = input,
                userId = extracted.userId,
                hotelId = extracted.hotelId,
                query = extracted.cleanQuery,
                intent = intent
            )
        }

        // Node: Handle Search
        val handleSearch by node<ClassifiedMessage, ChatResponseDto>("handle_search") { hotelData ->
            println("[Node: handleSearch] Executing search for query: '${hotelData.query}'")
            try {
                val hotels = hotelService.searchHotelsByNaturalLanguageAsDto(hotelData.query)
                println("[Node: handleSearch] Found ${hotels.size} hotels")
                ChatResponseDto(
                    success = true,
                    chatResponseType = ChatResponseType.SEARCH_RESULTS,
                    searchData = hotels,
                    questionData = "",
                    otherData = "",

                    errorData = ""
                )
            } catch (e: Exception) {
                println("[Node: handleSearch] Error: ${e.message}")
                ChatResponseDto(
                    success = false,
                    chatResponseType = ChatResponseType.ERROR,
                    searchData = emptyList(),
                    questionData = "",
                    otherData = "",

                    errorData = "Search failed: ${e.message}"
                )
            }
        }

        // Node: Handle Question
        val handleQuestion by node<ClassifiedMessage, ChatResponseDto>("handle_question") { hotelData ->
            println("[Node: handleQuestion] HotelId: ${hotelData.hotelId}, Query: ${hotelData.query}")

            if (hotelData.hotelId == null) {
                return@node ChatResponseDto(
                    success = false,
                    chatResponseType = ChatResponseType.ERROR,
                    searchData = emptyList(),
                    questionData = "",
                    otherData = "",

                    errorData = "Hotel ID is required for questions about a specific hotel"

                )
            }

            try {
                val answer = hotelService.askHotelQuestionParsed(hotelData.query, hotelData.hotelId)
                println("[Node: handleQuestion] Answer: $answer")
                ChatResponseDto(
                    success = true,
                    chatResponseType = ChatResponseType.QUESTION_ANSWER,
                    searchData = emptyList(),
                    questionData = answer,
                    otherData = "",

                    errorData = ""
                )
            } catch (e: Exception) {
                println("[Node: handleQuestion] Error: ${e.message}")
                ChatResponseDto(
                    success = false,
                    chatResponseType = ChatResponseType.ERROR,
                    searchData = emptyList(),
                    questionData = "",
                    otherData = "",

                    errorData = "Failed to answer question: ${e.message}"
                )
            }
        }

        // Node: Handle Booking
        val handleBooking by node<ClassifiedMessage, ChatResponseDto>("handle_booking") { hotelData ->
            println("[Node: handleBooking] Booking request for hotelId: ${hotelData.hotelId} and userId: ${hotelData.userId}")

            if (hotelData.userId == null) {
                return@node ChatResponseDto(
                    success = false,
                    chatResponseType = ChatResponseType.BOOKING,
                    searchData = emptyList(),
                    questionData = "",
                    otherData = "",
                    bookingData = BookingData(message = "You need to log in first to make a booking."),
                    errorData = ""
                )
            }

            if (hotelData.hotelId == null) {
                return@node ChatResponseDto(
                    success = false,
                    chatResponseType = ChatResponseType.BOOKING,
                    searchData = emptyList(),
                    questionData = "",
                    otherData = "",
                    bookingData = BookingData(message = "You need to select a hotel first before making a booking."),
                    errorData = ""
                )
            }

            // Parse booking details using AI
            val response = llm.writeSession {
                appendPrompt {
                    system(buildParserBookingPrompt())
                    user(hotelData.originalMessage)
                }
                requestLLMWithoutTools()
            }

            println("[Node: handleBooking] AI Response: ${response.content}")

            // Parse the JSON response
            val bookingDetails = MessageParser.parseBookingDetails(response.content)
            println("[Node: handleBooking] Parsed details: $bookingDetails")

            // Validate required fields
            val missingFields = MessageParser.validateBookingDetails(bookingDetails)

            if (missingFields.isNotEmpty()) {
                val missingFieldsMessage = missingFields.joinToString(", ")
                return@node ChatResponseDto(
                    success = false,
                    chatResponseType = ChatResponseType.BOOKING,
                    searchData = emptyList(),
                    questionData = "",
                    otherData = "",
                    bookingData = BookingData(message = "To complete your booking, I still need the following information: $missingFieldsMessage. Please provide these details."),
                    errorData = ""
                )
            }

            // All required fields are present - create BookingCreateDto
            try {
                val bookingCreateDto = BookingCreateDto(
                    hotelId = hotelData.hotelId,
                    userId = hotelData.userId.toLong(),
                    firstNameGuest = bookingDetails.firstNameGuest!!,
                    lastNameGuest = bookingDetails.lastNameGuest!!,
                    emailGuest = bookingDetails.emailGuest ?: "",
                    phoneNumberGuest = bookingDetails.phoneNumberGuest,
                    startTimeIso = bookingDetails.startDate!!,
                    endTimeIso = bookingDetails.endDate!!,
                    price = null,
                    currency = "USD"
                )

                val bookingResponse = bookingService.createBooking(bookingCreateDto)
                println("[Node: handleBooking] Booking created successfully: ${bookingResponse.id}")

                ChatResponseDto(
                    success = true,
                    chatResponseType = ChatResponseType.BOOKING,
                    searchData = emptyList(),
                    questionData = "",
                    otherData = "",
                    bookingData = BookingData(
                        message = "Your booking has been created successfully!",
                        bookingResponseDto = bookingResponse
                    ),
                    errorData = ""
                )
            } catch (e: Exception) {
                println("[Node: handleBooking] Error creating booking: ${e.message}")
                ChatResponseDto(
                    success = false,
                    chatResponseType = ChatResponseType.ERROR,
                    searchData = emptyList(),
                    questionData = "",
                    otherData = "",
                    bookingData = BookingData(message = "Failed to create booking: ${e.message}"),
                    errorData = ""
                )
            }
        }



        // Node: Handle Other (non-hotel requests)
        val handleOther by node<ClassifiedMessage, ChatResponseDto>("handle_other") { messageData ->
            println("[Node: handleOther] Message: ${messageData.originalMessage}")
            ChatResponseDto(
                success = true,
                chatResponseType = ChatResponseType.OTHER,
                searchData = emptyList(),
                questionData = "",
                otherData = "Sorry, I'm not able to answer that. I'm a hotel assistant. I can search for hotels, answer questions about specific hotels, and help you make bookings. How can I help you?",
                bookingData = null,
                errorData = ""
            )
        }

        // Define edges (flow)
        edge(nodeStart forwardTo classify)

        edge(classify forwardTo handleOther onCondition { it.intent == UserIntent.OTHER })
        edge(classify forwardTo handleSearch onCondition { it.intent == UserIntent.GENERAL_SEARCH })
        edge(classify forwardTo handleQuestion onCondition { it.intent == UserIntent.SPECIFIC_QUESTION })
        edge(classify forwardTo handleBooking onCondition { it.intent == UserIntent.BOOKING })

        // End nodes
        edge(handleBooking forwardTo nodeFinish)
        edge(handleSearch forwardTo nodeFinish)
        edge(handleQuestion forwardTo nodeFinish)
        edge(handleOther forwardTo nodeFinish)
    }

    companion object {
        private val CLASSIFY_PROMPT = """
            You are an intent classifier for a hotel assistant.
            Classify the user's message into exactly ONE of these categories:

            - GENERAL_SEARCH: User wants to find/search/browse hotels, ask general questions about hotels in an area, compare options, or get recommendations.
              Examples: "Find hotels in Paris", "What hotels have pools?", "Show me cheap hotels", "Hotels near the beach"

            - SPECIFIC_QUESTION: User has a question about a PARTICULAR hotel (amenities, policies, rooms, location details, etc.)
              Examples: "What time is check-in?", "Does this hotel have parking?", "Tell me about the rooms", "What's the cancellation policy?"

            - BOOKING: User wants to make a reservation, book a room, or is providing booking details (dates, guest info, payment).
              Examples: "I want to book a room", "Reserve for 2 nights", "Book from June 1 to June 5", "My name is John Smith, booking for tomorrow"

            - OTHER: Message is NOT related to hotels or travel at all.
              Examples: "What's the weather?", "Tell me a joke", "How do I cook pasta?"

            Respond with ONLY one word: GENERAL_SEARCH, SPECIFIC_QUESTION, BOOKING, or OTHER
        """.trimIndent()
        private fun buildParserBookingPrompt(): String {
            val today = java.time.LocalDate.now()
            return """
                You are a booking information extractor for a hotel reservation system.
                Extract booking details from the user's message and return them as a JSON object.

                IMPORTANT: Today's date is $today. Use this to calculate relative dates.

                Extract the following fields (use null for any field not mentioned):
                - firstNameGuest: Guest's first name
                - lastNameGuest: Guest's last name
                - emailGuest: Guest's email address
                - phoneNumberGuest: Guest's phone number
                - startDate: Check-in date in FULL ISO-8601 format with time and UTC timezone
                - endDate: Check-out date in FULL ISO-8601 format with time and UTC timezone
                - numberOfGuests: Number of guests

                Important date parsing rules:
                - TODAY IS $today - use this for calculating "tomorrow", "next week", etc.
                - Convert relative dates like "tomorrow", "next Monday", "in 3 days" to actual dates based on today ($today)
                - If user says "for X nights" starting from a date, calculate the end date
                - MUST use FULL ISO-8601 format with time: YYYY-MM-DDTHH:MM:SSZ (e.g., 2024-12-15T14:00:00Z)
                - Default check-in time is 14:00:00Z (2 PM UTC)
                - Default check-out time is 11:00:00Z (11 AM UTC)
                - If year is not specified, assume the current or next occurrence of that date

                Examples:
                User: "Book for John Smith from December 15 to December 20"
                Response: {"firstNameGuest": "John", "lastNameGuest": "Smith", "emailGuest": null, "phoneNumberGuest": null, "startDate": "2024-12-15T14:00:00Z", "endDate": "2024-12-20T11:00:00Z", "numberOfGuests": null}

                User: "I want to reserve, my name is Maria Garcia, email maria@email.com, checking in Jan 5 for 3 nights"
                Response: {"firstNameGuest": "Maria", "lastNameGuest": "Garcia", "emailGuest": "maria@email.com", "phoneNumberGuest": null, "startDate": "2025-01-05T14:00:00Z", "endDate": "2025-01-08T11:00:00Z", "numberOfGuests": null}

                User: "Book a room please"
                Response: {"firstNameGuest": null, "lastNameGuest": null, "emailGuest": null, "phoneNumberGuest": null, "startDate": null, "endDate": null, "numberOfGuests": null}

                Respond with ONLY the JSON object, no additional text or markdown formatting.
            """.trimIndent()
        }
    }
}