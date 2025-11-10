package com.grupo3.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "liteapi.api")
data class LittleApiProperties(
    var baseUrl: String = "",
    var key: String = ""
)

