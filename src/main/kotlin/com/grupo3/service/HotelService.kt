package com.grupo3.service

import com.grupo3.dto.hotel.HotelCreateDto
import com.grupo3.dto.hotel.HotelResponseDto
import com.grupo3.dto.hotel.HotelMapper
import com.grupo3.repository.HotelRepository
import org.springframework.stereotype.Service

@Service
class HotelService(private val hotelRepository: HotelRepository) {


    fun createHotel(hotelDto: HotelCreateDto): HotelResponseDto {
        val hotel = HotelMapper.toEntity(hotelDto)
        val savedHotel = hotelRepository.save(hotel)
        return HotelMapper.toResponseDto(savedHotel)
    }


    fun getAllHotels(): List<HotelResponseDto> {
        return hotelRepository.findAll()
            .map { HotelMapper.toResponseDto(it) }
    }


    fun getHotelsByLocation(location: String): List<HotelResponseDto> {
        return hotelRepository.findByLocation(location)
            .map { HotelMapper.toResponseDto(it) }
    }
}