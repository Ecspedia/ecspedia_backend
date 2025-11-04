package com.grupo3.controller.hotel

import com.grupo3.dto.location.LocationCreateDto
import com.grupo3.dto.location.LocationResponseDto
import com.grupo3.service.hotel.LocationService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated

@Controller
@Validated
class LocationController(private val locationService: LocationService) {

    @QueryMapping
    fun locations(): List<LocationResponseDto> {
        return locationService.getAllLocations()
    }

    @QueryMapping
    fun locationByCode(@Argument code: String): LocationResponseDto {
        return locationService.getLocationByCode(code)
    }

    @MutationMapping
    fun createLocation(@Argument @Valid locationCreateDto: LocationCreateDto): LocationResponseDto {
        return locationService.createLocation(locationCreateDto)
    }
}