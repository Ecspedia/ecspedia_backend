package com.grupo3.controller

import com.grupo3.service.AuthService
import com.grupo3.dto.auth.AuthRequestDto
import com.grupo3.dto.auth.AuthResponseDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun authenticate(
        @Valid @RequestBody authRequest: AuthRequestDto
    ): AuthResponseDto {
        return authService.authenticate(authRequest)
    }
}