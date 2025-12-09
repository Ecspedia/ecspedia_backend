package com.grupo3.service.booking

import com.grupo3.dto.booking.BookingCreateDto
import com.grupo3.dto.booking.BookingMapper
import com.grupo3.dto.booking.BookingResponseDto
import com.grupo3.exception.customException.BookingIdNotFoundException
import com.grupo3.model.User
import com.grupo3.model.Booking
import com.grupo3.repository.UserRepository
import com.grupo3.repository.BookingRepository
import com.grupo3.repository.HotelRepository
import com.grupo3.service.EmailService
import com.grupo3.service.UserService
import com.grupo3.service.hotel.HotelService
import com.grupo3.util.BookingEmailUtils
import com.grupo3.util.DateTimeUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CompletableFuture

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val emailService: EmailService,
    private val hotelService: HotelService,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(BookingService::class.java)

    @Transactional
    fun createBooking(bookingCreateDto: BookingCreateDto): BookingResponseDto {
        // Ensure hotel exists in DB (fetch from API if needed)
        hotelService.saveHotelFromApi(bookingCreateDto.hotelId)
            ?: throw IllegalArgumentException("Hotel ${bookingCreateDto.hotelId} doesn't exist")

        // Get the managed Hotel entity from repository
        val hotel = hotelService.getHotelByIdModel(bookingCreateDto.hotelId)
            ?: throw IllegalArgumentException("Hotel ${bookingCreateDto.hotelId} doesn't exist")

        val user = userService.findUserById(bookingCreateDto.userId)
            ?: throw IllegalArgumentException("User ${bookingCreateDto.userId} doesn't exist")

        val booking = BookingMapper.toDomain(bookingCreateDto, hotel, user)

        val savedBooking = bookingRepository.save(booking)
//        CompletableFuture.runAsync { notifyUserOfBooking(user, savedBooking) }
        return BookingMapper.toResponseDto(savedBooking)
    }

    fun getAllBooking(): List<BookingResponseDto> =
        bookingRepository.findAll().map(BookingMapper::toResponseDto)

    fun getBookingByUserEmail(userEmail: String): List<BookingResponseDto> =
        bookingRepository.findAllByUserEmailOrderByCreatedAtDesc(userEmail)
            .map(BookingMapper::toResponseDto)

    fun removeBookingById(bookingId: String) {
        return try {
            bookingRepository.deleteById(bookingId)
        }catch(ex:IllegalArgumentException){
            throw BookingIdNotFoundException("Booking not found with id: $bookingId")
        }

    }


    private fun notifyUserOfBooking(user: User, booking: Booking) {
        val hotelName = booking.hotel.name ?: "Your hotel"
        val guestName = BookingEmailUtils.buildGuestName(booking.firstNameGuest, booking.lastNameGuest)
        val priceSummary = BookingEmailUtils.formatPrice(booking.price, booking.currency)
        val bookingId = booking.id ?: "N/A"

        runCatching {
            emailService.sendBookingConfirmationEmail(
                username = user.username,
                email = user.email,
                hotelName = hotelName,
                guestName = guestName,
                checkInDate = BookingEmailUtils.formatInstantForEmail(booking.startTime),
                checkOutDate = BookingEmailUtils.formatInstantForEmail(booking.endTime),
                bookingId = bookingId,
                priceSummary = priceSummary
            )
        }.onFailure { ex ->
            logger.warn("Failed to send booking confirmation email to ${user.email}", ex)
        }
    }




}
