package com.grupo3.dto

data class EmailTemplate(
    val subject: String,
    val htmlContent: String,
    val textContent: String
)

data class WelcomeEmailTemplate(
    val username: String,
    val email: String
) {
    fun toEmailTemplate(): EmailTemplate {
        val subject = "Welcome to Ecspedia! 🎉"
        
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Welcome to Ecspedia</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f4f4f4;
                    }
                    .container {
                        background-color: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 0 20px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .logo {
                        font-size: 28px;
                        font-weight: bold;
                        color: #2c3e50;
                        margin-bottom: 10px;
                    }
                    .welcome-title {
                        color: #27ae60;
                        font-size: 24px;
                        margin-bottom: 20px;
                    }
                    .content {
                        margin-bottom: 30px;
                    }
                    .highlight {
                        background-color: #e8f5e8;
                        padding: 15px;
                        border-left: 4px solid #27ae60;
                        margin: 20px 0;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #eee;
                        color: #666;
                        font-size: 14px;
                    }
                    .button {
                        display: inline-block;
                        background-color: #27ae60;
                        color: white;
                        padding: 12px 25px;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🏨 Ecspedia</div>
                        <h1 class="welcome-title">Welcome to Ecspedia!</h1>
                    </div>
                    
                    <div class="content">
                        <p>Hello <strong>$username</strong>,</p>
                        
                        <p>Welcome to Ecspedia! We're thrilled to have you join our community of travelers and hotel enthusiasts.</p>
                        
                        <div class="highlight">
                            <p><strong>Your account has been successfully created!</strong></p>
                            <p>Email: $email</p>
                        </div>
                        
                        <p>With Ecspedia, you can:</p>
                        <ul>
                            <li>🔍 Discover amazing hotels around the world</li>
                            <li>⭐ Read and write reviews</li>
                            <li>📍 Find hotels by location</li>
                            <li>💰 Compare prices and ratings</li>
                        </ul>
                        
                        <p>Ready to start exploring? Visit our platform and discover your next perfect stay!</p>
                        
                    </div>
                    
                    <div class="footer">
                        <p>This is an automated message. Please do not reply to this email.</p>
                        <p>© 2025 Ecspedia. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
        
        val textContent = """
            Welcome to Ecspedia!
            
            Hello $username,
            
            Welcome to Ecspedia! We're thrilled to have you join our community of travelers and hotel enthusiasts.
            
            Your account has been successfully created!
            Email: $email
            
            With Ecspedia, you can:
            - Discover amazing hotels around the world
            - Read and write reviews
            - Find hotels by location
            - Compare prices and ratings
            
            Ready to start exploring? Visit our platform and discover your next perfect stay!
            
            This is an automated message. Please do not reply to this email.
            © 2025 Ecspedia. All rights reserved.
        """.trimIndent()
        
        return EmailTemplate(subject, htmlContent, textContent)
    }
}

data class PasswordResetEmailTemplate(
    val username: String,
    val email: String,
    val resetToken: String,
    val resetUrl: String
) {
    fun toEmailTemplate(): EmailTemplate {
        val subject = "Password Reset Request - Ecspedia"
        
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset - Ecspedia</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f4f4f4;
                    }
                    .container {
                        background-color: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 0 20px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .logo {
                        font-size: 28px;
                        font-weight: bold;
                        color: #2c3e50;
                        margin-bottom: 10px;
                    }
                    .title {
                        color: #e74c3c;
                        font-size: 24px;
                        margin-bottom: 20px;
                    }
                    .content {
                        margin-bottom: 30px;
                    }
                    .warning {
                        background-color: #fdf2f2;
                        padding: 15px;
                        border-left: 4px solid #e74c3c;
                        margin: 20px 0;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #eee;
                        color: #666;
                        font-size: 14px;
                    }
                    .button {
                        display: inline-block;
                        background-color: #e74c3c;
                        color: white;
                        padding: 12px 25px;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🏨 Ecspedia</div>
                        <h1 class="title">Password Reset Request</h1>
                    </div>
                    
                    <div class="content">
                        <p>Hello <strong>$username</strong>,</p>
                        
                        <p>We received a request to reset your password for your Ecspedia account associated with <strong>$email</strong>.</p>
                        
                        <div class="warning">
                            <p><strong>Important:</strong> This link will expire in 24 hours for security reasons.</p>
                        </div>
                        
                        <p>To reset your password, click the button below:</p>
                        
                        <div style="text-align: center;">
                            <a href="$resetUrl" class="button">Reset My Password</a>
                        </div>
                        
                        <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; color: #666;">$resetUrl</p>
                        
                        <p>If you didn't request this password reset, please ignore this email. Your password will remain unchanged.</p>
                    </div>
                    
                    <div class="footer">
                        <p>This is an automated message. Please do not reply to this email.</p>
                        <p>© 2025 Ecspedia. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
        
        val textContent = """
            Password Reset Request - Ecspedia
            
            Hello $username,
            
            We received a request to reset your password for your Ecspedia account linked to $email.
            
            Important: This link will expire in 24 hours for security reasons.
            
            To reset your password, visit this link:
            $resetUrl
            
            If you didn't request this password reset, please ignore this email. Your password will remain unchanged.
            
            This is an automated message. Please do not reply to this email.
            © 2025 Ecspedia. All rights reserved.
        """.trimIndent()
        
        return EmailTemplate(subject, htmlContent, textContent)
    }
}

data class BookingEmailTemplate(
    val username: String,
    val hotelName: String,
    val checkInDate: String,
    val checkOutDate: String,
    val bookingId: String,
    val guestName: String,
    val priceSummary: String?
) {
    fun toEmailTemplate(): EmailTemplate {
        val subject = "Booking Confirmation - Ecspedia"

        val priceHtml = priceSummary?.let {
            """
                <div class="highlight">
                    <p><strong>Total:</strong> $it</p>
                </div>
            """.trimIndent()
        } ?: ""

        val priceText = priceSummary?.let { "\nTotal: $it\n" } ?: ""

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Booking Confirmation - Ecspedia</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f4f4f4;
                    }
                    .container {
                        background-color: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 0 20px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .logo {
                        font-size: 28px;
                        font-weight: bold;
                        color: #2c3e50;
                        margin-bottom: 10px;
                    }
                    .title {
                        color: #2c3e50;
                        font-size: 24px;
                        margin-bottom: 20px;
                    }
                    .content {
                        margin-bottom: 30px;
                    }
                    .details {
                        background-color: #f8fbff;
                        padding: 20px;
                        border-radius: 8px;
                        border: 1px solid #dfe9f5;
                    }
                    .details p {
                        margin: 8px 0;
                    }
                    .highlight {
                        background-color: #e8f5e8;
                        padding: 15px;
                        border-left: 4px solid #27ae60;
                        margin: 20px 0;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #eee;
                        color: #666;
                        font-size: 14px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🏨 Ecspedia</div>
                        <h1 class="title">Booking confirmed!</h1>
                    </div>
                    
                    <div class="content">
                        <p>Hello <strong>$username</strong>,</p>
                        <p>Thank you for booking with Ecspedia. Your stay is confirmed and we cannot wait to host you.</p>
                        
                        <div class="details">
                            <p><strong>Hotel:</strong> $hotelName</p>
                            <p><strong>Guest:</strong> $guestName</p>
                            <p><strong>Booking ID:</strong> $bookingId</p>
                            <p><strong>Check in:</strong> $checkInDate</p>
                            <p><strong>Check out:</strong> $checkOutDate</p>
                        </div>
                        
                        $priceHtml
                        
                        <p>If you need to make any changes or have questions, just reply to this email and our team will help you out.</p>
                    </div>
                    
                    <div class="footer">
                        <p>Safe travels,</p>
                        <p>The Ecspedia team</p>
                        <p>This is an automated message. Please do not reply to this email.</p>
                        <p>© 2025 Ecspedia. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        val textContent = """
            Booking confirmed!
            
            Hello $username,
            Thank you for booking with Ecspedia. Your stay is confirmed.
            
            Hotel: $hotelName
            Guest: $guestName
            Booking ID: $bookingId
            Check in: $checkInDate
            Check out: $checkOutDate
            $priceText
            If you need assistance with your booking, just reply to this email and our team will help you.
            
            Safe travels,
            The Ecspedia team
            © 2025 Ecspedia. All rights reserved.
        """.trimIndent()

        return EmailTemplate(subject, htmlContent, textContent)
    }
}
