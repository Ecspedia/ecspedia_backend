package com.grupo3.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws.s3")
data class AwsS3Properties(
    var bucket: String = "",
    var region: String = "us-east-1",
    var prefix: String = "hotel-images/"
)