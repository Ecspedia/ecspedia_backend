package com.grupo3.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "hotelbeds.api")
data class HotelbedsProperties(
    var baseUrl: String = "",
    var key: String = "",
    var secret: String = "",
    var timeoutMillis: Long = 5000,
    var enabled: Boolean = false
)

