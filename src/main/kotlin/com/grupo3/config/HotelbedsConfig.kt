package com.grupo3.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

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
            .defaultHeader("Accept", "application/json")
            .build()
    }
}
