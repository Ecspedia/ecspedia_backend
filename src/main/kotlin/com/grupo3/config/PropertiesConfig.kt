package com.grupo3.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    value = [
        HotelbedsProperties::class,
        GoogleSearchProperties::class,
        AwsS3Properties::class
    ]
)
class PropertiesConfig