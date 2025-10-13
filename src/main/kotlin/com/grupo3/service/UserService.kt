package com.grupo3.service

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
}
