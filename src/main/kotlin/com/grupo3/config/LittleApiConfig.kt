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
@EnableConfigurationProperties(LittleApiProperties::class)

class LittleApiConfig {
    @Bean
    fun littleApiRestTemplate(
        builder: RestTemplateBuilder,
        properties: LittleApiProperties
    ): RestTemplate {
        return builder
            .rootUri(properties.baseUrl)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("X-API-Key", properties.key)
            .build()
    }
}
