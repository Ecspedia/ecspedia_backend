package com.grupo3.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.grupo3.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
    ) {
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject(user.username)
            .withClaim("userId", user.id)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + expiration))
            .sign(algorithm)
    }
}