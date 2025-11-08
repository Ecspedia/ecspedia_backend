package com.grupo3.service.hotel.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.grupo3.dto.hotel.hotelbeds.HotelbedsAvailabilityResponse
import org.slf4j.LoggerFactory

object HotelbedsPayloadParser {

    private val logger = LoggerFactory.getLogger(HotelbedsPayloadParser::class.java)
    private val mapper = jacksonObjectMapper()

    fun parseAvailability(payload: String): HotelbedsAvailabilityResponse? =
        try {
            mapper.readValue<HotelbedsAvailabilityResponse>(payload)
        } catch (ex: Exception) {
            logger.warn("Unable to parse Hotelbeds availability payload", ex)
            null
        }
}
