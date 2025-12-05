package com.grupo3.controller

import com.grupo3.dto.location.LocationCreateDto
import com.grupo3.dto.location.LocationResponseDto
import com.grupo3.service.LocationService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated

@Controller
@Validated
class LocationController(private val locationService: LocationService
    ) {
    private val logger= LoggerFactory.getLogger(LocationController::class.java)

    @QueryMapping
    fun locations(): List<LocationResponseDto> {
        return locationService.getAllLocations()
    }
    @MutationMapping
    fun createLocation(@Argument @Valid locationCreateDto: LocationCreateDto): LocationResponseDto {
        logger.info("Creating location: ${locationCreateDto.city}, ${locationCreateDto.country}")
        return locationService.createLocation(locationCreateDto)
    }
    @QueryMapping(name = "topLocations")
    fun topDestinations(): List<LocationResponseDto> {
        return locationService.getTopLocations()
    }

}