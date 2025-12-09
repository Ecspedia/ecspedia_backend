package com.grupo3.controller

import com.grupo3.dto.booking.BookingCreateDto
import com.grupo3.dto.booking.BookingResponseDto
import com.grupo3.service.booking.BookingService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin

@Controller
@Validated
@CrossOrigin(origins = ["*"])
class BookingController(
    private val bookingService: BookingService
) {

    @MutationMapping
    fun createBooking(@Argument @Valid bookingCreateDto: BookingCreateDto ): BookingResponseDto =
        bookingService.createBooking(bookingCreateDto)

    @QueryMapping
    fun bookings(): List<BookingResponseDto> =
        bookingService.getAllBooking()

    @QueryMapping
    fun bookingsByUserEmail(@Argument email: String): List<BookingResponseDto> {
        return bookingService.getBookingByUserEmail(email)
    }


    @MutationMapping
    fun deleteBookingById(@Argument bookingId: String): ResponseEntity<Any> {
        bookingService.removeBookingById(bookingId)
        return ResponseEntity.status(HttpStatus.OK).body(mapOf("message" to "Booking deleted successfully"))
    }

}
