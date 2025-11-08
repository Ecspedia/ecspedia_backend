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
            .sortedBy { it.city.lowercase() }
            .map { LocationMapper.toResponseDto(it) }
    }
    fun getTopLocations(): List<LocationResponseDto> {
        val popularDestinations =  locationRepository.findByPopularDestinations()
        return popularDestinations.map { LocationMapper.toResponseDto(it) }
    }

    fun getLocationByCode(code: String): LocationResponseDto {
        val location = locationRepository.findByCode(code.trim().uppercase())
            .orElseThrow { IllegalArgumentException("Location not found with code: $code") }
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