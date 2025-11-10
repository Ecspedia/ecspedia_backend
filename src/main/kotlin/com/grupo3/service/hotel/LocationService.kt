package com.grupo3.service.hotel

import com.grupo3.dto.location.LocationCreateDto
import com.grupo3.dto.location.LocationMapper
import com.grupo3.dto.location.LocationResponseDto
import com.grupo3.repository.hotel.LocationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationService(private val locationRepository: LocationRepository) {



    fun getAllLocations(): List<LocationResponseDto> {
        return locationRepository.findAll()

            .map { LocationMapper.toResponseDto(it) }
    }
    fun getTopLocations(): List<LocationResponseDto> {
        val popularDestinations = locationRepository.findTopPopularDestinations()
        return popularDestinations.map { LocationMapper.toResponseDto(it) }
    }
    fun getLocation(code: String, city: String): LocationResponseDto {
        val location = locationRepository.findLocation(code.trim().uppercase(),city.trim().lowercase())
            .orElseThrow { IllegalArgumentException("Location not found with code: $code") }
        return LocationMapper.toResponseDto(location)
    }

    fun getLocationByCity(city: String): LocationResponseDto {
        val location = locationRepository.findByCity(city.trim().lowercase()).
        orElseThrow { IllegalArgumentException("Location not found : $city") }

        return LocationMapper.toResponseDto(location)
    }


    @Transactional
    fun createLocation(locationCreateDto: LocationCreateDto): LocationResponseDto {
        val normalizedCode = locationCreateDto.code.trim().uppercase()
        if (locationRepository.existsByCode(normalizedCode)) {
            throw IllegalArgumentException("Location with code $normalizedCode already exists")
        }

        val location = LocationMapper.toEntity(locationCreateDto.copy(code = normalizedCode))
        val saved = locationRepository.save(location)
        return LocationMapper.toResponseDto(saved)
    }


}
