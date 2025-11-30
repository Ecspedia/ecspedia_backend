package com.grupo3.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.util.UUID

@Service
class S3FileStorageService(
    private val s3Client: S3Client?,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    @Value("\${aws.s3.profile-photo-prefix}") private val prefix: String,
    @Value("\${aws.s3.region:us-east-2}") private val region: String
) {
    
    fun uploadProfilePhoto(
        imageBytes: ByteArray,
        contentType: String,
        fileExtension: String,
        userId: Long
    ): String {
        if (s3Client == null) {
            throw RuntimeException("AWS S3 is not configured. Please set AWS credentials.")
        }
        
        val key = "${prefix}${userId}_${UUID.randomUUID()}.$fileExtension"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            // Note: ACLs are disabled on this bucket. Use bucket policy for public access instead.
            .build()

        s3Client.putObject(
            putObjectRequest,
            RequestBody.fromInputStream(ByteArrayInputStream(imageBytes), imageBytes.size.toLong())
        )

        // Return public URL (using region-specific format)
        return "https://${bucketName}.s3.${region}.amazonaws.com/$key"
    }

    fun deleteProfilePhoto(fileUrl: String) {
        if (s3Client == null) {
            return // Silently fail if S3 is not configured
        }
        
        try {
            // Extract key from URL: https://bucket.s3.amazonaws.com/key
            val key = if (fileUrl.contains("s3.amazonaws.com/")) {
                fileUrl.substringAfter("s3.amazonaws.com/")
                    .substringAfter("${bucketName}/")
            } else {
                // Handle different URL formats
                fileUrl.substringAfterLast("/")
            }
            
            val deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()
            s3Client.deleteObject(deleteRequest)
        } catch (e: Exception) {
            println("Failed to delete file from S3: ${e.message}")
        }
    }
}

