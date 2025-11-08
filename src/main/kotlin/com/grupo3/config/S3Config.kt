package com.grupo3.config

import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config(
    private val awsS3Properties: AwsS3Properties,
    @Value("\${aws.access-key-id:}") private val awsAccessKeyId: String?,
    @Value("\${aws.secret-access-key:}") private val awsSecretAccessKey: String?
) {

    @Bean
    fun s3Client(): S3Client {
        val region = Region.of(awsS3Properties.region)
        val credentialsProvider = selectCredentialsProvider()

        return S3Client.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .build()
    }

    private fun selectCredentialsProvider(): AwsCredentialsProvider =
        if (!awsAccessKeyId.isNullOrBlank() && !awsSecretAccessKey.isNullOrBlank()) {
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey)
            )
        } else {
            DefaultCredentialsProvider.create()
        }
}
