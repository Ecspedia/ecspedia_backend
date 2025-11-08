package com.grupo3.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "google.search")
data class GoogleSearchProperties(
    var apiKey: String = "",
    var cx: String = "",
    var numResults: Int = 3
)