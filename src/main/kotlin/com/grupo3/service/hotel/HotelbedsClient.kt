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
    private val restTemplate: RestTemplate,
    private val properties: HotelbedsProperties
) {

    fun searchAvailability(payload: String): String {
        val timestamp = Instant.now().epochSecond
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Accept", MediaType.APPLICATION_JSON_VALUE)
            set("Api-Key", properties.key)
            set("X-Signature", buildSignature(timestamp))
        }

        val request = HttpEntity(payload, headers)
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

    private fun buildSignature(timestamp: Long): String {
        val signaturePayload = properties.key + properties.secret + timestamp
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(signaturePayload.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

