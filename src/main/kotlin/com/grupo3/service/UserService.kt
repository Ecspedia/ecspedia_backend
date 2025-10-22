package com.grupo3.service

import com.grupo3.dto.LoginDto
import com.grupo3.dto.LoginResponseDto
import com.grupo3.dto.UserInfoDto
import com.grupo3.dto.UserRegistrationDto
import com.grupo3.model.User
import com.grupo3.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
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

        return userRepository.save(user)
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
}
