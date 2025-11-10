package com.grupo3.controller.booking

import com.grupo3.dto.booking.BookingMapper
import com.grupo3.dto.booking.BookingResponseDto
import com.grupo3.service.booking.BookingService
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
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
    fun createBooking(
        @Argument @NotBlank hotelId: String,
        @Argument @Positive userId: Long,
        @Argument @NotBlank startTime: String,
        @Argument @NotBlank endTime: String,
        @Argument @PositiveOrZero price: Long?,
        @Argument currency: String?
    ): BookingResponseDto =
        bookingService.createBooking(hotelId, userId, startTime, endTime, price, currency)

    @QueryMapping
    fun bookings(): List<BookingResponseDto> =
        bookingService.getAllBooking()

    @QueryMapping
    fun bookingsByUserEmail(@Argument email: String):List<BookingResponseDto>{
        return bookingService.getBookingByUserEmail(email)
    }

}
