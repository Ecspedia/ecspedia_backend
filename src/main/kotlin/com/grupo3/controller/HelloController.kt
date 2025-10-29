package com.grupo3.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class HelloController {
    @GetMapping("/hello")
    fun hello(): String {
        return "Hello from Especedia Spring Boot!"
    }
}