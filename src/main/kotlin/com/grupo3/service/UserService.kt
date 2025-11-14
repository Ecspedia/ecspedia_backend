package com.grupo3.service

import com.grupo3.dto.user.UserRegistrationDto
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
}
