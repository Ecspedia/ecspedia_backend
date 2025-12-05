package com.grupo3.service.hotel

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.grupo3.dto.hotel.HotelCreateDto
import com.grupo3.dto.hotel.HotelMapper
import com.grupo3.dto.hotel.HotelPartialResponseDto
import com.grupo3.dto.hotel.HotelResponseDto
import com.grupo3.dto.location.LocationMapper
import com.grupo3.repository.HotelRepository
import com.grupo3.service.LocationService
import com.grupo3.service.hotel.dto.LiteApiSearchResponse
import com.grupo3.service.hotel.dto.toResponseDto
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HotelService(
    private val locationService: LocationService,
    private val liteApiClient: LiteApiClient,
    private val hotelRepository: HotelRepository
) {

    private val mapper = jacksonObjectMapper()

    @Cacheable(value = ["hotelsByLocation"], key = "#locationQuery.toLowerCase()")
    fun searchHotelsByLocation(locationQuery: String): List<HotelResponseDto> {

        val location = locationService.getLocationByCity(locationQuery)
        val json = liteApiClient.searchHotels(LocationMapper.toEntity(location))
        val response: LiteApiSearchResponse = mapper.readValue(json)
        if (response.data.isEmpty()) {
            return emptyList()
        }
        return response.data.map { it.toResponseDto() }
    }

    fun searchHotelsByNaturalLanguage(naturalLanguage: String): String{
        val response = liteApiClient.searchHotelByNaturalLanguage(naturalLanguage)
        return response
    }



    fun askHotelQuestion(query: String,hotelId: String): String {
        return liteApiClient.askHotelQuestion(query, hotelId  )
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

    fun getAllHotels(): List<HotelResponseDto> =
        hotelRepository.findAll().map { HotelMapper.toResponseDto(it) }

        fun getAllPartialHotels(): List<HotelPartialResponseDto> =
            hotelRepository.findAll().map { HotelMapper.toPartialResponseDto(it) }


    fun getHotelById(id: String): HotelResponseDto? =
        hotelRepository.findById(id)
            .map { HotelMapper.toResponseDto(it) }
            .orElse(null)

    fun hotelExists(id: String): Boolean = hotelRepository.existsById(id)
}
