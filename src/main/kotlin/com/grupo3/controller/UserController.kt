package com.grupo3.controller

import com.grupo3.dto.auth.AuthRequestDto
import com.grupo3.dto.auth.AuthResponseDto
import com.grupo3.dto.auth.PasswordResetResponseDto
import com.grupo3.dto.user.UserRegistrationDto
import com.grupo3.model.User
import com.grupo3.service.AuthService
import com.grupo3.service.UserService
import graphql.GraphQLException
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserController(
    private val userService: UserService,
    private val authService: AuthService
) {

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

    @MutationMapping
    fun login(
        @Argument authRequest: AuthRequestDto
    ): AuthResponseDto {
        return try {
            authService.authenticate(authRequest)
        } catch (ex: RuntimeException) {
            throw GraphQLException(ex.message ?: "Authentication failed")
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
    
    @MutationMapping
    fun forgotPassword(@Argument email: String): PasswordResetResponseDto {
        return try {
            val success = userService.initiatePasswordReset(email)
            PasswordResetResponseDto(
                success = success,
                message = if (success) "Password reset email sent" else "Email not found or failed to send"
            )
        } catch (ex: RuntimeException) {
            throw GraphQLException(ex.message ?: "Failed to send password reset email")
        }
    }
    
    @MutationMapping
    fun resetPassword(
        @Argument email: String,
        @Argument token: String,
        @Argument newPassword: String
    ): PasswordResetResponseDto {
        return try {
            val success = userService.resetPassword(email, token, newPassword)
            PasswordResetResponseDto(
                success = success,
                message = if (success) "Password reset successfully" else "Failed to reset password"
            )
        } catch (ex: RuntimeException) {
            throw GraphQLException(ex.message ?: "Failed to reset password")
        }
    }
}
