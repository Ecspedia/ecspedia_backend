package com.grupo3.service

import com.grupo3.dto.user.UserRegistrationDto
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

        return userRepository.save(newUser)
    }

    fun findUserById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }

    fun findAllUsers(): List<User> {
        return userRepository.findAll()
    }
}
