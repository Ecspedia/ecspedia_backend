package com.grupo3.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestTemplate
import java.security.MessageDigest
import java.time.Instant

@Configuration
@EnableConfigurationProperties(HotelbedsProperties::class)
class HotelbedsConfig {

    @Bean
    fun hotelbedsRestTemplate(
        builder: RestTemplateBuilder,
        properties: HotelbedsProperties
    ): RestTemplate {
        return builder
            .rootUri(properties.baseUrl)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Api-Key", properties.key)
            .additionalInterceptors(HotelbedsSignatureInterceptor(properties))
            .build()
    }

    private class HotelbedsSignatureInterceptor(
        private val properties: HotelbedsProperties
    ) : ClientHttpRequestInterceptor {

        override fun intercept(
            request: org.springframework.http.HttpRequest,
            body: ByteArray,
            execution: ClientHttpRequestExecution
        ): ClientHttpResponse {
            val timestamp = Instant.now().epochSecond
            request.headers.contentType = MediaType.APPLICATION_JSON
            request.headers.set("X-Signature", buildSignature(timestamp))
            return execution.execute(request, body)
        }

        private fun buildSignature(timestamp: Long): String {
            val payload = properties.key + properties.secret + timestamp
            val digest = MessageDigest.getInstance("SHA-256")
            return digest.digest(payload.toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
        }

    }
}
