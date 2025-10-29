package com.grupo3.controller

import com.grupo3.dto.EmailTemplate
import com.grupo3.service.EmailService
import com.grupo3.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/email")
class EmailController(
    private val emailService: EmailService,
    private val userService: UserService
) {

    /**
     * Test email configuration
     */
    @PostMapping("/test")
    fun testEmailConfiguration(): ResponseEntity<Map<String, Any>> {
        val success = emailService.testEmailConfiguration()
        return ResponseEntity.ok(mapOf(
            "success" to success,
            "message" to if (success) "Email configuration test successful" else "Email configuration test failed"
        ))
    }

    /**
     * Send password reset email
     */
    @PostMapping("/password-reset")
    fun sendPasswordResetEmail(
        @RequestParam email: String,
        @RequestParam(defaultValue = "http://localhost:8080") baseUrl: String
    ): ResponseEntity<Map<String, Any>> {
        val success = userService.initiatePasswordReset(email)
        return ResponseEntity.ok(mapOf(
            "success" to success,
            "message" to if (success) "Password reset email sent" else "Failed to send password reset email"
        ))
    }

    /**
     * Send custom email
     */
    @PostMapping("/send")
    fun sendCustomEmail(
        @RequestParam to: String,
        @RequestParam subject: String,
        @RequestParam text: String
    ): ResponseEntity<Map<String, Any>> {
        val success = emailService.sendSimpleEmail(to, subject, text)
        return ResponseEntity.ok(mapOf(
            "success" to success,
            "message" to if (success) "Email sent successfully" else "Failed to send email"
        ))
    }
}

data class BulkEmailRequest(
    val recipients: List<String>,
    val subject: String,
    val htmlContent: String,
    val textContent: String
)
