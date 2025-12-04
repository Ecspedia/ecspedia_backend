package com.grupo3.service.hotel

import com.grupo3.dto.location.LocationCreateDto
import com.grupo3.dto.location.LocationMapper
import com.grupo3.dto.location.LocationResponseDto
import com.grupo3.exception.customException.LocationAlreadyExistsException
import com.grupo3.exception.customException.LocationNotFoundException
import com.grupo3.repository.hotel.LocationRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationService(private val locationRepository: LocationRepository) {

    fun getAllLocations(): List<LocationResponseDto> {
        return locationRepository.findAll()
            .map { LocationMapper.toResponseDto(it) }
    }
    fun getTopLocations(): List<LocationResponseDto> {
        val popularDestinations = locationRepository.findByIsPopularTrueOrderByCityAsc()
        return popularDestinations.map { LocationMapper.toResponseDto(it) }
    }

    fun getLocationByCity(city: String): LocationResponseDto {
        val location = locationRepository.findByCity(city.normalize()).
        orElseThrow { LocationNotFoundException(city = city) }
        return LocationMapper.toResponseDto(location)
    }

    fun createLocation(locationCreateDto: LocationCreateDto): LocationResponseDto {
        val location = LocationMapper.toEntity(locationCreateDto)
        return try {
            val saved = locationRepository.save(location)
            LocationMapper.toResponseDto(saved)
        } catch (ex: DataIntegrityViolationException) {
            throw LocationAlreadyExistsException(location.city, location.country)
        }
    }


    private fun String.normalize(): String = this.trim().lowercase()


}
