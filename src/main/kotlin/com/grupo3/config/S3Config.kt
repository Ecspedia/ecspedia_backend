package com.grupo3.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config(
    @Value("\${aws.access-key-id:}") private val accessKeyId: String,
    @Value("\${aws.secret-access-key:}") private val secretAccessKey: String,
    @Value("\${aws.s3.region:us-east-1}") private val region: String
) {
    @Bean
    fun s3Client(): S3Client? {
        // Check both environment variables and properties
        val envAccessKey = System.getenv("AWS_ACCESS_KEY_ID") ?: ""
        val envSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY") ?: ""
        
        // Use environment variables if properties are empty
        val finalAccessKey = if (accessKeyId.isNotBlank()) accessKeyId else envAccessKey
        val finalSecretKey = if (secretAccessKey.isNotBlank()) secretAccessKey else envSecretKey
        
        // Only create client if credentials are provided
        if (finalAccessKey.isBlank() || finalSecretKey.isBlank()) {
            println("Warning: AWS S3 credentials not configured. Profile photo uploads will not work.")
            return null
        }
        
        val credentials = AwsBasicCredentials.create(finalAccessKey, finalSecretKey)
        
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()
    }
}

