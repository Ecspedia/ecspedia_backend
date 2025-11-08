package com.grupo3.service.hotel

import com.grupo3.config.HotelbedsProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import java.security.MessageDigest
import java.time.Instant

@Service
class HotelbedsClient(
    private val restTemplate: RestTemplate
) {

    fun searchAvailability(payload: String): String {
        val request = HttpEntity(payload)
        return try {
            val response = restTemplate.exchange(
                "/hotels",
                HttpMethod.POST,
                request,
                String::class.java
            )
            response.body.orEmpty()
        } catch (ex: RestClientResponseException) {
            throw IllegalStateException(
                "Hotelbeds API error: ${ex.statusCode.value()} ${ex.responseBodyAsString}",
                ex
            )
        }
    }


}

