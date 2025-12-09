package com.grupo3.exception.customException

sealed class BookingExceptions(message: String) : RuntimeException(message)

class BookingIdNotFoundException(private val bookingId: String) : RuntimeException("Booking not found with id: $bookingId")