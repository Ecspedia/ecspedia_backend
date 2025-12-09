package com.grupo3.agent.exception

sealed class AgentException(
    override val message: String,
    val errorCode: String
) : Exception(message)

class MissingUserIdException : AgentException(
    message = "User ID is required for this action",
    errorCode = "MISSING_USER_ID"
)

class MissingHotelIdException : AgentException(
    message = "Hotel ID is required for this action",
    errorCode = "MISSING_HOTEL_ID"
)

class BookingFailedException(details: String) : AgentException(
    message = "Failed to create booking: $details",
    errorCode = "BOOKING_FAILED"
)

class ParsingException(details: String) : AgentException(
    message = "Failed to parse response: $details",
    errorCode = "PARSING_ERROR"
)

class HotelNotFoundException(hotelId: String) : AgentException(
    message = "Hotel not found: $hotelId",
    errorCode = "HOTEL_NOT_FOUND"
)

class ServiceUnavailableException(service: String) : AgentException(
    message = "Service unavailable: $service",
    errorCode = "SERVICE_UNAVAILABLE"
)