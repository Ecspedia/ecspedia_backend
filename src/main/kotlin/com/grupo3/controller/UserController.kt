package com.grupo3.controller

import com.grupo3.dto.user.UserRegistrationDto
import com.grupo3.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    @PostMapping
    fun registerUser(
        @Valid @RequestBody userRegistrationDto: UserRegistrationDto
    ): ResponseEntity<String> {
        return try {
            userService.registerUser(userRegistrationDto)
            ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")
        } catch (ex: RuntimeException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
        }
    }
}
