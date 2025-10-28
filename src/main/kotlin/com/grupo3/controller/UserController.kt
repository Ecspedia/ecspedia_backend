package com.grupo3.controller

import com.grupo3.dto.user.UserRegistrationDto
import com.grupo3.model.User
import com.grupo3.service.UserService
import graphql.GraphQLException
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserController(private val userService: UserService) {

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

    @QueryMapping
    fun getUserById(@Argument id: Long): User {
        return userService.findUserById(id)
            ?: throw GraphQLException("User not found with id: $id")
    }

    @QueryMapping
    fun getAllUsers(): List<User> {
        return userService.findAllUsers()
    }
}
