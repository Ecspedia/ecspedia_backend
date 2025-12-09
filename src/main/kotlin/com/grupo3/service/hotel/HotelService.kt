package com.grupo3.service.hotel

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.grupo3.dto.hotel.HotelCreateDto
import com.grupo3.dto.hotel.HotelMapper
import com.grupo3.dto.hotel.HotelPartialResponseDto
import com.grupo3.dto.hotel.HotelResponseDto
import com.grupo3.dto.location.LocationMapper
import com.grupo3.model.Hotel
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

    fun searchHotelsByNaturalLanguage(naturalLanguage: String): String {
        val response = liteApiClient.searchHotelByNaturalLanguage(naturalLanguage)
        return response
    }

    fun searchHotelsByNaturalLanguageAsDto(naturalLanguage: String): List<HotelResponseDto> {
        val json = liteApiClient.searchHotelByNaturalLanguage(naturalLanguage)
        return try {
            val response: LiteApiSearchResponse = mapper.readValue(json)
            if (response.data.isEmpty()) emptyList() else response.data.map { it.toResponseDto() }
        } catch (e: Exception) {
            emptyList()
        }
    }



    fun askHotelQuestion(query: String, hotelId: String): String {
        return liteApiClient.askHotelQuestion(query, hotelId)
    }

    fun askHotelQuestionParsed(query: String, hotelId: String): String {
        val json = liteApiClient.askHotelQuestion(query, hotelId)
        return try {
            val root = mapper.readTree(json)
            root.path("data").path("answer").asText()
        } catch (e: Exception) {
            "Unable to get answer: ${e.message}"
        }
    }

    @Transactional
    fun saveHotel(hotelCreateDto: HotelCreateDto): HotelResponseDto {
        val hotel = HotelMapper.toEntity(hotelCreateDto)
        val savedHotel = hotelRepository.save(hotel)
        return HotelMapper.toResponseDto(savedHotel)
    }

    fun saveHotelFromApi(hotelId: String): HotelResponseDto? {
        val hotelDto = getHotelByIdFromApi(hotelId)
        if(hotelDto !== null) {
            hotelRepository.save(HotelMapper.toEntity(hotelDto))
        }
        return hotelDto
    }

    fun getHotelByIdFromApi(hotelId: String): HotelResponseDto? {
        //we get the data from API
        val json = liteApiClient.getHotelById(hotelId)
        val response: LiteApiSearchResponse = mapper.readValue(json)
        if (response.data.isEmpty()) {
            return null
        }
        return response.data.map { it.toResponseDto() }.find { it.id == hotelId }
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

    fun getHotelByIdModel(id: String): Hotel? =
        hotelRepository.findById(id)
            .orElse(null)

    fun hotelExists(id: String): Boolean = hotelRepository.existsById(id)
}
