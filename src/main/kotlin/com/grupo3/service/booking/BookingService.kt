package com.grupo3.service.booking

import com.grupo3.dto.booking.BookingMapper
import com.grupo3.dto.booking.BookingResponseDto
import com.grupo3.model.booking.Booking
import com.grupo3.repository.UserRepository
import com.grupo3.repository.booking.BookingRepository
import com.grupo3.repository.hotel.HotelRepository
import com.grupo3.util.DateTimeUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookingService(
    private val hotelRepository: HotelRepository,
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository
) {

    @Transactional
    fun createBooking(
        hotelId: String,
        userId: Long,
        firstNameGuest: String,
        lastNameGuest: String,
        emailGuest: String,
        phoneNumberGuest: String?,
        startTimeIso: String,
        endTimeIso: String,
        price: Long?,
        currency: String?
    ): BookingResponseDto {
        val startTime = DateTimeUtils.parseIsoInstant(startTimeIso, "startTime")
        val endTime = DateTimeUtils.parseIsoInstant(endTimeIso, "endTime")

        require(endTime.isAfter(startTime)) { "endTime must be after startTime" }

        val hotel = hotelRepository.findById(hotelId)
            .orElseThrow { IllegalArgumentException("Hotel not found with id: $hotelId") }

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found with id: $userId") }

        val booking = Booking(
            hotel = hotel,
            user = user,
            firstNameGuest = firstNameGuest,
            lastNameGuest = lastNameGuest,
            emailGuest = emailGuest,
            phoneNumberGuest = phoneNumberGuest,
            startTime = startTime,
            endTime = endTime,
            price = price,
            currency = currency
        )

        val savedBooking = bookingRepository.save(booking)
        return BookingMapper.toResponseDto(savedBooking)
    }

    fun getAllBooking(): List<BookingResponseDto> =
        bookingRepository.findAll().map { BookingMapper.toResponseDto(it) }

    fun getBookingByUserEmail(userEmail: String): List<BookingResponseDto> =
        bookingRepository.findAllByUserEmailOrderByCreatedAtDesc(userEmail)
            .map { BookingMapper.toResponseDto(it) }
}
