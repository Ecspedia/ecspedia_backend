package com.grupo3.security.jwt

import com.grupo3.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    companion object {
        const val ROLE_USER = "USER"
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username/email: $username")

        return User
            .withUsername(user.username)
            .password(user.password)
            .roles(ROLE_USER)
            .build()
    }
}