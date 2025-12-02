package com.grupo3.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gemini.api")
data class GeminiApiConfig (
    var key: String = ""
)