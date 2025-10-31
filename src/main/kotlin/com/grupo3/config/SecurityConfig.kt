package com.grupo3.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
            val allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS")
            ?: "https://ecspedia.shop,https://dev.ecspedia.shop,http://localhost:3000"
           val allowedOrigins = allowedOriginsEnv.split(",").map { it.trim() }

           val config = CorsConfiguration()
           config.allowedOrigins = allowedOrigins
           config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
           config.allowedHeaders = listOf("*")
           config.allowCredentials = true

           val source = UrlBasedCorsConfigurationSource()
           source.registerCorsConfiguration("/**", config)
           return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/hello",
                    "/api/user/**",
                    "/api/auth/**",
                    "/api/hotels/**",
                    "/api/email/**",
                    "/graphql",
                    "/graphiql",
                ).permitAll()
                    .anyRequest().authenticated()
            }

        return http.build()
    }
}
