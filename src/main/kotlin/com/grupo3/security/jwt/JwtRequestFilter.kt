package com.grupo3.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtRequestFilter(
    private val jwtTokenService: JwtTokenService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        val token = when {
            header != null && header.startsWith("Bearer ") -> header.substring(7)
            request.cookies != null -> request.cookies.firstOrNull { it.name == "auth_token" }?.value
            else -> null
        }

        if (token.isNullOrBlank()) {
            chain.doFilter(request, response)
            return
        }

        val jwt = jwtTokenService.validateToken(token)

        if (jwt == null || jwt.subject == null) {
            chain.doFilter(request, response)
            return
        }

        val userDetails = try {
            userDetailsService.loadUserByUsername(jwt.subject)
        } catch (userNotFoundEx: UsernameNotFoundException) {
            chain.doFilter(request, response)
            return
        }

        val authentication = UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.authorities
        ).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request)
        }

        SecurityContextHolder.getContext().authentication = authentication

        chain.doFilter(request, response)
    }
}
