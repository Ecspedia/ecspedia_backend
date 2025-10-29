package com.grupo3.service

import com.grupo3.dto.EmailTemplate
import com.grupo3.dto.PasswordResetEmailTemplate
import com.grupo3.dto.WelcomeEmailTemplate
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {
    
    @Value("\${app.email.from}")
    private lateinit var fromEmail: String
    
    @Value("\${app.email.from.name}")
    private lateinit var fromName: String
    
    private val logger = LoggerFactory.getLogger(EmailService::class.java)
    
    /**
     * Sends a welcome email to a newly registered user
     */
    fun sendWelcomeEmail(username: String, email: String): Boolean {
        return try {
            val template = WelcomeEmailTemplate(username, email).toEmailTemplate()
            sendEmail(email, template)
            logger.info("Welcome email sent successfully to: $email")
            true
        } catch (e: Exception) {
            logger.error("Failed to send welcome email to: $email", e)
            false
        }
    }
    
    /**
     * Sends a password reset email
     */
    fun sendPasswordResetEmail(username: String, email: String, resetToken: String, baseUrl: String): Boolean {
        return try {
            val resetUrl = "$baseUrl/reset-password?token=$resetToken"
            val template = PasswordResetEmailTemplate(username, resetToken, resetUrl).toEmailTemplate()
            sendEmail(email, template)
            logger.info("Password reset email sent successfully to: $email")
            true
        } catch (e: Exception) {
            logger.error("Failed to send password reset email to: $email", e)
            false
        }
    }
    
    /**
     * Sends a custom email using a template
     */
    fun sendCustomEmail(to: String, template: EmailTemplate): Boolean {
        return try {
            sendEmail(to, template)
            logger.info("Custom email sent successfully to: $to")
            true
        } catch (e: Exception) {
            logger.error("Failed to send custom email to: $to", e)
            false
        }
    }
    
    /**
     * Sends a simple text email
     */
    fun sendSimpleEmail(to: String, subject: String, text: String): Boolean {
        return try {
            val template = EmailTemplate(subject, text, text)
            sendEmail(to, template)
            logger.info("Simple email sent successfully to: $to")
            true
        } catch (e: Exception) {
            logger.error("Failed to send simple email to: $to", e)
            false
        }
    }
    
    /**
     * Core method to send emails
     */
    private fun sendEmail(to: String, template: EmailTemplate) {
        if (!isValidEmail(to)) {
            throw IllegalArgumentException("Invalid email address: $to")
        }
        
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")
        
        // Set sender information
        helper.setFrom("$fromName <$fromEmail>")
        helper.setTo(to)
        helper.setSubject(template.subject)
        
        // Set email content (both HTML and text)
        helper.setText(template.textContent, template.htmlContent)
        
        // Send the email
        mailSender.send(message)
        
        logger.debug("Email sent successfully to: $to with subject: ${template.subject}")
    }
    
    /**
     * Validates email address format
     */
    private fun isValidEmail(email: String): Boolean {
        return StringUtils.hasText(email) && 
               email.matches(Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"))
    }
    
    /**
     * Sends bulk emails (useful for notifications)
     */
    fun sendBulkEmails(recipients: List<String>, template: EmailTemplate): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()
        
        recipients.forEach { email ->
            results[email] = try {
                sendEmail(email, template)
                true
            } catch (e: Exception) {
                logger.error("Failed to send bulk email to: $email", e)
                false
            }
        }
        
        val successCount = results.values.count { it }
        logger.info("Bulk email sent: $successCount/${recipients.size} successful")
        
        return results
    }
    
    /**
     * Tests email configuration
     */
    fun testEmailConfiguration(): Boolean {
        return try {
            val testTemplate = EmailTemplate(
                subject = "Test Email - Ecspedia",
                htmlContent = "<h1>Test Email</h1><p>This is a test email to verify email configuration.</p>",
                textContent = "Test Email\n\nThis is a test email to verify email configuration."
            )
            
            // Send to the configured from email as a test
            sendEmail(fromEmail, testTemplate)
            logger.info("Email configuration test successful")
            true
        } catch (e: Exception) {
            logger.error("Email configuration test failed", e)
            false
        }
    }
}
