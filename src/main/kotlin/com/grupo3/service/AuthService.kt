package com.grupo3.service

import com.grupo3.dto.auth.AuthRequestDto
import com.grupo3.dto.auth.AuthResponseDto
import com.grupo3.repository.UserRepository
import com.grupo3.security.jwt.JwtTokenService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.RuntimeException

@Service
class AuthService(
    private val jwtTokenService: JwtTokenService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional(readOnly = true)
    fun authenticate(authRequest: AuthRequestDto): AuthResponseDto {
        // Try to find user by username first, then by email
        val user = userRepository.findByUsername(authRequest.username)
            ?: userRepository.findByEmail(authRequest.username)
            ?: throw RuntimeException("User not found")

        if (!passwordEncoder.matches(authRequest.password, user.password)) {
            throw RuntimeException("Invalid credentials")
        }

        val token = jwtTokenService.generateToken(user)

        return AuthResponseDto(token)
    }
}