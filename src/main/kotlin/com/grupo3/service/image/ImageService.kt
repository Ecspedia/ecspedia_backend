package com.grupo3.service.image

import com.grupo3.config.AwsS3Properties
import com.grupo3.dto.image.ImageUpdateResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI
import java.time.Instant
import java.util.UUID

@Service
class ImageService(
    private val webClientBuilder: WebClient.Builder,
    private val s3Client: S3Client,
    private val s3Properties: AwsS3Properties
) {

    private val logger = LoggerFactory.getLogger(ImageService::class.java)

    fun updateImage(imageId: String, imageUrl: String): ImageUpdateResponse {
        require(s3Properties.bucket.isNotBlank()) { "AWS S3 bucket is not configured" }
        val bytes = downloadImage(imageUrl)
        val key = buildObjectKey(imageId, imageUrl)

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.bucket)
            .key(key)
            .contentType(guessContentType(imageUrl))
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes))

        val publicUrl = buildPublicUrl(key)
        logger.info("Uploaded image {} to bucket {} with key {}", imageId, s3Properties.bucket, key)

        return ImageUpdateResponse(
            imageId = imageId,
            imageUrl = publicUrl,
            message = "Image uploaded to S3 successfully"
        )
    }

    private fun downloadImage(imageUrl: String): ByteArray =
        webClientBuilder.build()
            .get()
            .uri(imageUrl)
            .retrieve()
            .bodyToMono(ByteArray::class.java)
            .onErrorResume { ex ->
                logger.error("Failed to download image from {}", imageUrl, ex)
                Mono.error(IllegalStateException("Unable to download image from provided URL"))
            }
            .block() ?: throw IllegalStateException("Received empty body when downloading image")

    private fun buildObjectKey(imageId: String, imageUrl: String): String {
        val sanitizedPrefix = s3Properties.prefix.trim().trimEnd('/')
        val extension = extractExtension(imageUrl)
        val uniqueSuffix = UUID.randomUUID().toString()
        val timestamp = Instant.now().epochSecond
        val baseKey = buildString {
            if (sanitizedPrefix.isNotEmpty()) {
                append(sanitizedPrefix)
                append('/')
            }
            append(imageId)
            append('-')
            append(timestamp)
            append('-')
            append(uniqueSuffix)
        }
        return if (extension.isNotBlank()) "$baseKey.$extension" else baseKey
    }

    private fun extractExtension(imageUrl: String): String {
        val path = runCatching { URI(imageUrl).path }.getOrNull() ?: return ""
        val dotIndex = path.lastIndexOf('.')
        return if (dotIndex != -1 && dotIndex + 1 < path.length) {
            path.substring(dotIndex + 1)
        } else {
            ""
        }
    }

    private fun guessContentType(imageUrl: String): String =
        when (extractExtension(imageUrl).lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "avif" -> "image/avif"
            else -> "application/octet-stream"
        }

    private fun buildPublicUrl(key: String): String =
        "https://${s3Properties.bucket}.s3.${s3Properties.region}.amazonaws.com/$key"
}
