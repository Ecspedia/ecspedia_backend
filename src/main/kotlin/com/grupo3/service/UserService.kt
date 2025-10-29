package com.grupo3.service

import com.grupo3.dto.LoginDto
import com.grupo3.dto.LoginResponseDto
import com.grupo3.dto.UserInfoDto
import com.grupo3.dto.UserRegistrationDto
import com.grupo3.model.User
import com.grupo3.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService
) {
    
    @Value("\${app.email.base-url}")
    private lateinit var baseUrl: String
    @Transactional
    fun registerNewUser(registrationDto: UserRegistrationDto): User {
        if (userRepository.existsByUsername(registrationDto.username)) {
            throw RuntimeException("Username is already taken")
        }

        if (userRepository.existsByEmail(registrationDto.email)) {
            throw RuntimeException("Email is already in use")
        }

        val user = User(
            username = registrationDto.username,
            email = registrationDto.email,
            password = passwordEncoder.encode(registrationDto.password)
        )

        val savedUser = userRepository.save(user)
        
        // Send welcome email asynchronously
        try {
            emailService.sendWelcomeEmail(savedUser.username, savedUser.email)
        } catch (e: Exception) {
            // Log the error but don't fail the registration
            // Email sending failure shouldn't prevent user registration
            println("Failed to send welcome email to ${savedUser.email}: ${e.message}")
        }
        
        return savedUser
    }

    @Transactional(readOnly = true)
    fun loginUser(loginDto: LoginDto): LoginResponseDto {
        val user = userRepository.findByUsername(loginDto.username)
            ?: throw RuntimeException("Invalid username or password")

        if (!passwordEncoder.matches(loginDto.password, user.password)) {
            throw RuntimeException("Invalid username or password")
        }

        val userInfo = UserInfoDto(
            id = user.id!!,
            username = user.username,
            email = user.email
        )

        return LoginResponseDto(
            message = "Login successful",
            user = userInfo
        )
    }
    
    /**
     * Initiates password reset process by sending reset email
     */
    fun initiatePasswordReset(email: String): Boolean {
        val user = userRepository.findByEmail(email)
            ?: return false // Don't reveal if email exists or not
        
        // Generate a simple reset token (in production, use a more secure method)
        val resetToken = java.util.UUID.randomUUID().toString()
        
        // In a real application, you would store this token in the database with expiration
        // For now, we'll just send the email
        
        return try {
            emailService.sendPasswordResetEmail(user.username, user.email, resetToken, baseUrl)
            true
        } catch (e: Exception) {
            println("Failed to send password reset email to ${user.email}: ${e.message}")
            false
        }
    }
    
    /**
     * Resets user password using reset token
     */
    @Transactional
    fun resetPassword(email: String, resetToken: String, newPassword: String): Boolean {
        // In a real application, you would validate the reset token from database
        // For now, we'll just find the user by email
        val user = userRepository.findByEmail(email) ?: return false
        
        try {
            userRepository.save(user.copy(password = passwordEncoder.encode(newPassword)))
            return true
        } catch (e: Exception) {
            println("Failed to reset password for ${user.email}: ${e.message}")
            return false
        }
    }
}
