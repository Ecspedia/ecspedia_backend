# Email Service Documentation

## Overview
This document describes the no-reply email service implementation for the Ecspedia backend application. The service provides automated email functionality for user registration, password resets, and custom notifications.

## Features
- ✅ Welcome emails for new user registrations
- ✅ Password reset emails with secure tokens
- ✅ Custom email templates with HTML and text content
- ✅ Bulk email sending capabilities
- ✅ Email configuration testing
- ✅ No-reply email addresses
- ✅ Professional email templates with branding

## Configuration

### Environment Variables
Set these environment variables for production:

```bash
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
```

### Application Properties
The email configuration is already set up in `application.properties`:

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME:noreply@ecspedia.com}
spring.mail.password=${EMAIL_PASSWORD:your-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com

# Email Templates
app.email.from=noreply@ecspedia.com
app.email.from.name=Ecspedia Team
```

## API Endpoints

### Authentication Endpoints

#### Register User (with welcome email)
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

#### Forgot Password
```http
POST /api/auth/forgot-password?email=test@example.com&baseUrl=http://localhost:8080
```

#### Reset Password
```http
POST /api/auth/reset-password?email=test@example.com&token=reset-token&newPassword=newpassword123
```

### Email Management Endpoints

#### Test Email Configuration
```http
POST /api/email/test
```

#### Send Custom Email
```http
POST /api/email/send?to=test@example.com&subject=Test Subject&text=This is a test email
```

#### Send Bulk Emails
```http
POST /api/email/bulk
Content-Type: application/json

{
  "recipients": ["test1@example.com", "test2@example.com"],
  "subject": "Bulk Email Test",
  "htmlContent": "<h1>Bulk Email</h1><p>This is a bulk email test.</p>",
  "textContent": "Bulk Email\n\nThis is a bulk email test."
}
```

## Email Templates

### Welcome Email Template
- **Subject**: "Welcome to Ecspedia! 🎉"
- **Content**: Professional HTML template with:
  - Ecspedia branding
  - Welcome message with username
  - Account confirmation details
  - Feature highlights
  - Call-to-action button
  - Footer with no-reply notice

### Password Reset Email Template
- **Subject**: "Password Reset Request - Ecspedia"
- **Content**: Secure HTML template with:
  - Security warning
  - Reset link with token
  - 24-hour expiration notice
  - Fallback text link
  - Security recommendations

## Service Classes

### EmailService
Main service class handling all email operations:
- `sendWelcomeEmail(username, email)` - Sends welcome email
- `sendPasswordResetEmail(username, email, token, baseUrl)` - Sends password reset
- `sendCustomEmail(to, template)` - Sends custom template email
- `sendSimpleEmail(to, subject, text)` - Sends simple text email
- `sendBulkEmails(recipients, template)` - Sends bulk emails
- `testEmailConfiguration()` - Tests email setup

### UserService Integration
The UserService now automatically sends welcome emails when users register:
- Email sending is non-blocking (won't fail registration if email fails)
- Error logging for debugging
- Graceful error handling

## Security Features

### No-Reply Implementation
- All emails sent from `noreply@ecspedia.com`
- Clear "do not reply" notices in templates
- Professional sender name "Ecspedia Team"

### Password Reset Security
- UUID-based reset tokens
- Token expiration (24 hours recommended)
- Secure reset URLs
- Email validation

## Testing

### Manual Testing
Use the provided HTTP requests in `Request.http`:

1. **Test Configuration**:
   ```http
   POST http://localhost:8080/api/email/test
   ```

2. **Register User** (triggers welcome email):
   ```http
   POST http://localhost:8080/api/auth/register
   Content-Type: application/json
   {
     "username": "testuser",
     "email": "your-email@gmail.com",
     "password": "password123"
   }
   ```

3. **Password Reset**:
   ```http
   POST http://localhost:8080/api/auth/forgot-password?email=your-email@gmail.com
   ```

### Production Setup

1. **Gmail Setup** (recommended):
   - Enable 2-factor authentication
   - Generate app-specific password
   - Use your Gmail credentials

2. **Custom SMTP**:
   - Update `spring.mail.host` in properties
   - Configure appropriate port and security settings
   - Update authentication credentials

## Error Handling

The email service includes comprehensive error handling:
- Email validation
- SMTP connection errors
- Template rendering errors
- Graceful degradation (registration continues if email fails)
- Detailed logging for debugging

## Logging

Email operations are logged with appropriate levels:
- `INFO`: Successful email sends
- `ERROR`: Failed email operations
- `DEBUG`: Detailed email sending information

## Future Enhancements

Consider implementing:
- Email queue for high-volume sending
- Template engine (Thymeleaf) for dynamic content
- Email tracking and analytics
- Scheduled email campaigns
- Email preferences management
- Multi-language support

## Troubleshooting

### Common Issues

1. **Authentication Failed**:
   - Check email credentials
   - Verify app-specific password for Gmail
   - Ensure 2FA is enabled

2. **Connection Timeout**:
   - Check network connectivity
   - Verify SMTP host and port
   - Check firewall settings

3. **Emails Not Received**:
   - Check spam folder
   - Verify recipient email address
   - Test with different email providers

### Debug Steps

1. Test email configuration: `POST /api/email/test`
2. Check application logs for detailed error messages
3. Verify environment variables are set correctly
4. Test with a simple email first before complex templates

## Dependencies

The email service requires:
- `spring-boot-starter-mail` (already added)
- JavaMail API
- Spring Boot Mail Auto-configuration

All dependencies are automatically managed by Spring Boot.
