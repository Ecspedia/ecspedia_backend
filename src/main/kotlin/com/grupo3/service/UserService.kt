package com.grupo3.service

import com.grupo3.dto.user.UpdateUsernameDto
import com.grupo3.dto.user.UserRegistrationDto
import com.grupo3.model.User
import com.grupo3.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Base64

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
    private val s3FileStorageService: S3FileStorageService
) {
    
    @Value("\${app.email.base-url:http://localhost:3000}")
    private lateinit var baseUrl: String
    @Transactional
    fun registerUser(userRegistrationDto: UserRegistrationDto): User {
        if (userRepository.existsByUsername(userRegistrationDto.username)) {
            throw RuntimeException("Username is already taken")
        }

        if (userRepository.existsByEmail(userRegistrationDto.email)) {
            throw RuntimeException("Email is already in use")
        }

        val newUser = User(
            username = userRegistrationDto.username,
            email = userRegistrationDto.email,
            password = passwordEncoder.encode(userRegistrationDto.password)
        )

        val savedUser = userRepository.save(newUser)
        
        // Send welcome email asynchronously
        try {
            emailService.sendWelcomeEmail(savedUser.username, savedUser.email)
        } catch (e: Exception) {
            // Log the error but don't fail the registration
            // Email sending failure shouldn't prevent user registration
            println("Failed to send welcome email to ${savedUser.email}: ${e.message}")
        }
        
         return userRepository.save(newUser)
    }

    fun findUserById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }

    fun findUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun findAllUsers(): List<User> {
        return userRepository.findAll()
    }
    

    fun initiatePasswordReset(email: String): Boolean {
        val user = userRepository.findByEmail(email)
            ?: return false // Don't reveal if email exists or not
        
        // Generate a simple reset token (in production, use a more secure method)
        val resetToken = java.util.UUID.randomUUID().toString()

        
        return try {
            emailService.sendPasswordResetEmail(user.username, user.email, resetToken, baseUrl)
            true
        } catch (e: Exception) {
            println("Failed to send password reset email to ${user.email}: ${e.message}")
            false
        }
    }

    @Transactional
    fun resetPassword(email: String, resetToken: String, newPassword: String): Boolean {
        val user = userRepository.findByEmail(email) ?: return false
        
        try {
            userRepository.save(user.copy(password = passwordEncoder.encode(newPassword)))
            return true
        } catch (e: Exception) {
            println("Failed to reset password for ${user.email}: ${e.message}")
            return false
        }
    }

    @Transactional
    fun updateUsername(userId: Long, updateUsernameDto: UpdateUsernameDto): User {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }

        // Check if the new username is different from current
        if (user.username == updateUsernameDto.username) {
            throw RuntimeException("New username must be different from current username")
        }

        // Check if username is already taken
        if (userRepository.existsByUsername(updateUsernameDto.username)) {
            throw RuntimeException("Username is already taken")
        }

        return userRepository.save(user.copy(username = updateUsernameDto.username))
    }

    @Transactional
    fun updateProfilePhotoFromBase64(userId: Long, base64Image: String): String {
        // Parse base64 string (format: "data:image/jpeg;base64,/9j/4AAQ...")
        if (!base64Image.startsWith("data:image/")) {
            throw RuntimeException("Invalid image format. Expected data:image/...")
        }
        
        val base64Data = base64Image.substringAfter(",")
        val contentType = base64Image.substringAfter("data:").substringBefore(";base64")
        
        // Validate content type
        val allowedTypes = listOf("image/jpeg", "image/png", "image/gif", "image/webp")
        if (!allowedTypes.contains(contentType)) {
            throw RuntimeException("Invalid image type. Only JPEG, PNG, GIF, and WebP are allowed")
        }
        
        // Decode base64
        val imageBytes = try {
            Base64.getDecoder().decode(base64Data)
        } catch (e: IllegalArgumentException) {
            throw RuntimeException("Invalid base64 image data")
        }
        
        // Validate size (5MB)
        if (imageBytes.size > 5 * 1024 * 1024) {
            throw RuntimeException("Image too large. Maximum size is 5MB")
        }
        
        // Get file extension from content type
        val fileExtension = contentType.substringAfter("/")
        
        // Upload to S3 directly with bytes
        return s3FileStorageService.uploadProfilePhoto(imageBytes, contentType, fileExtension, userId)
    }

    @Transactional
    fun updateProfilePhoto(userId: Long, profilePhotoUrl: String): User {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        
        // Delete old photo from S3 if exists
        val oldPhotoUrl = user.profilePhotoUrl
        if (oldPhotoUrl != null && oldPhotoUrl.contains("s3.amazonaws.com")) {
            try {
                s3FileStorageService.deleteProfilePhoto(oldPhotoUrl)
            } catch (e: Exception) {
                println("Failed to delete old profile photo: ${e.message}")
            }
        }
        
        return userRepository.save(user.copy(profilePhotoUrl = profilePhotoUrl))
    }
}
