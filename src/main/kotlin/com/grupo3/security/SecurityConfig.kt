package com.grupo3.security

import com.grupo3.security.jwt.JwtRequestFilter
import com.grupo3.security.jwt.JwtUserDetailsService
import com.grupo3.security.jwt.JwtTokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val jwtTokenService: JwtTokenService,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
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
    fun authenticationManager(
        authenticationConfiguration: AuthenticationConfiguration
    ): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    fun jwtRequestFilter(): JwtRequestFilter {
        return JwtRequestFilter(jwtTokenService, jwtUserDetailsService)
    }

    @Bean
    fun filterChain(
        http: HttpSecurity,
    ): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
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
            .sessionManagement { it.sessionCreationPolicy((SessionCreationPolicy.STATELESS)) }
            .addFilterBefore(
                jwtRequestFilter(),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}