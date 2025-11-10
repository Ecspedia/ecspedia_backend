package com.grupo3.service.hotel

import com.grupo3.model.hotel.Location
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.util.Locale

@Service
class HotelClient(
    private val restTemplate: RestTemplate
) {

    fun searchHotels(location: Location): String {
        val cityName = location.city
        val countryCode = location.code

        val uri = UriComponentsBuilder
            .fromPath("/data/hotels")
            .queryParam("countryCode", countryCode)
            .queryParam("cityName", cityName)
            .build()
            .toUriString()

        return try {
            val response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String::class.java
            )
            response.body.orEmpty()
        } catch (ex: RestClientResponseException) {
            throw IllegalStateException(
                "LiteAPI error: ${ex.statusCode.value()} ${ex.responseBodyAsString}",
                ex
            )
        }
    }


}
