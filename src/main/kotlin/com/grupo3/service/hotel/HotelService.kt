package com.grupo3.service.hotel

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.grupo3.dto.hotel.HotelCreateDto
import com.grupo3.dto.hotel.HotelMapper
import com.grupo3.dto.hotel.HotelResponseDto
import com.grupo3.dto.location.LocationMapper
import com.grupo3.repository.hotel.HotelRepository
import com.grupo3.service.hotel.dto.LiteApiSearchResponse
import com.grupo3.service.hotel.dto.toResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HotelService(
    private val locationService: LocationService,
    private val hotelClient: HotelClient,
    private val hotelRepository: HotelRepository
) {

    private val mapper = jacksonObjectMapper()

    fun searchHotelsByLocation(locationQuery: String): List<HotelResponseDto> {
        val location = locationService.getLocationByCity(locationQuery)
        val json = hotelClient.searchHotels(LocationMapper.toEntity(location))
        val response: LiteApiSearchResponse = mapper.readValue(json)
        if (response.data.isEmpty()) {
            return emptyList()
        }
        return response.data.map { it.toResponseDto() }
    }

    @Transactional
    fun saveHotel(hotelCreateDto: HotelCreateDto): HotelResponseDto {
        val hotel = HotelMapper.toEntity(hotelCreateDto)
        val savedHotel = hotelRepository.save(hotel)
        return HotelMapper.toResponseDto(savedHotel)
    }

    fun getTopPopularHotels(): List<HotelResponseDto> =
        hotelRepository.findTop10ByIsAvailableTrueOrderByRatingDesc()
            .map { HotelMapper.toResponseDto(it) }
}
