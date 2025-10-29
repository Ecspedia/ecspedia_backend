package com.grupo3.controller

import com.grupo3.dto.LoginDto
import com.grupo3.dto.LoginResponseDto
import com.grupo3.dto.UserRegistrationDto
import com.grupo3.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun registerUser(
        @Valid @RequestBody registrationDto: UserRegistrationDto
    ): ResponseEntity<String> {
        return try {
            val user = userService.registerNewUser(registrationDto)
            ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")
        } catch (ex: RuntimeException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
        }
    }

    @PostMapping("/login")
    fun loginUser(
        @Valid @RequestBody loginDto: LoginDto
    ): ResponseEntity<LoginResponseDto> {
        return try {
            val loginResponse = userService.loginUser(loginDto)
            ResponseEntity.ok(loginResponse)
        } catch (ex: RuntimeException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                LoginResponseDto(
                    message = ex.message ?: "Login failed",
                    user = com.grupo3.dto.UserInfoDto(0, "", "")
                )
            )
        }
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
