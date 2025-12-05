package com.grupo3.service.hotel

import com.grupo3.exception.customException.HotelApiException
import com.grupo3.model.Location
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class LiteApiClient(
    private val restTemplate: RestTemplate
) {
    private val logger = LoggerFactory.getLogger(LiteApiClient::class.java)

    fun searchHotels(location: Location): String {
        logger.debug("Searching hotels for city: {}, country: {}", location.city, location.code)
        val uri = buildUri("/data/hotels") {
            queryParam("countryCode", location.code)
            queryParam("cityName", location.city)
        }
        return executeGet(uri)

    }

    fun searchHotelByNaturalLanguage(naturalLanguage: String): String {
        logger.debug("Searching hotels by natural language: {}", naturalLanguage)
        val uri = buildUri("/data/hotels") {
            queryParam("aiSearch", naturalLanguage)
            queryParam("limit", 10)
        }
        return executeGet(uri)
    }

    fun askHotelQuestion(query: String,hotelId: String): String {
        logger.debug("Asking question for hotel {}: {}", hotelId, query)

        val uri = buildUri("/data/hotel/ask") {
            queryParam("hotelId", hotelId)
            queryParam("query", query)
            queryParam("allowWebSearch", true)
        }

        return executeGet(uri)
    }

    // ============ HELPER METHODS ============

    private fun buildUri(path: String, block: UriComponentsBuilder.() -> Unit): String {
        return UriComponentsBuilder
            .fromPath(path)
            .apply(block)
            .build()
            .toUriString()
    }

    private fun executeGet(uri: String): String {
        logger.debug("Executing GET request: {}", uri)

        return try {
            val response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String::class.java
            )

            logger.debug("Response received: {} bytes", response.body?.length ?: 0)
            response.body.orEmpty()

        } catch (ex: RestClientResponseException) {
            logger.error("Hotel API error: {} - {}", ex.statusCode.value(), ex.responseBodyAsString)
            throw HotelApiException(
                statusCode = ex.statusCode.value(),
                errorBody = ex.responseBodyAsString
            )
        } catch (ex: Exception) {
            logger.error("Unexpected error calling Hotel API", ex)
            throw HotelApiException(
                statusCode = 500,
                errorBody = ex.message ?: "Unknown error"
            )
        }
    }

}
