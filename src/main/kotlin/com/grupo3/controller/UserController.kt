package com.grupo3.controller

import com.grupo3.dto.user.UserRegistrationDto
import com.grupo3.model.User
import com.grupo3.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import graphql.GraphQLException
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserController(private val userService: UserService) {

    @MutationMapping
    fun registerUser(
        @Argument userRegistrationDto: UserRegistrationDto
    ): User {
        return try {
            userService.registerUser(userRegistrationDto)
        } catch (ex: RuntimeException) {
            throw GraphQLException(ex.message ?: "Registration failed")
        }
    }

    @QueryMapping
    fun getUserById(@Argument id: Long): User {
        return userService.findUserById(id)
            ?: throw GraphQLException("User not found with id: $id")
    }

    @QueryMapping
    fun getAllUsers(): List<User> {
        return userService.findAllUsers()
    }
    
    @PostMapping("/forgot-password")
    fun forgotPassword(
        @RequestParam email: String
    ): ResponseEntity<Map<String, Any>> {
        val success = userService.initiatePasswordReset(email)
        return ResponseEntity.ok(mapOf(
            "success" to success,
            "message" to if (success) "Password reset email sent" else "Email not found or failed to send"
        ))
    }
    
    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestParam email: String,
        @RequestParam token: String,
        @RequestParam newPassword: String
    ): ResponseEntity<Map<String, Any>> {
        val success = userService.resetPassword(email, token, newPassword)
        return ResponseEntity.ok(mapOf(
            "success" to success,
            "message" to if (success) "Password reset successfully" else "Failed to reset password"
        ))
    }
}
