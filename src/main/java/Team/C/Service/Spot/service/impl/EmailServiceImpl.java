package Team.C.Service.Spot.service.impl;

import Team.C.Service.Spot.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email Service Implementation using Spring Boot Mail (Gmail SMTP).
 *
 * FREE & OPEN SOURCE - No API keys required, just Gmail account!
 *
 * Setup Instructions:
 * 1. Use your Gmail account
 * 2. Enable 2-Factor Authentication in Gmail
 * 3. Generate App Password: https://myaccount.google.com/apppasswords
 * 4. Set EMAIL_USERNAME and EMAIL_PASSWORD in environment variables
 *
 * @author Team C
 * @version 4.0
 * @since 2025-12-20
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.from.address}")
    private String fromEmail;

    @Value("${email.from.name}")
    private String fromName;

    /**
     * Send password reset email with 6-digit token.
     * Uses Gmail SMTP - FREE and reliable!
     */
    @Override
    public boolean sendPasswordResetEmail(String toEmail, String userName, String resetToken) {
        log.info("📧 Sending password reset email to: {}", toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Reset Your ServiceSpot Password");
            helper.setText(buildResetEmailTemplate(userName, resetToken), true);

            mailSender.send(message);

            log.info("✅ Password reset email sent successfully to: {}", toEmail);
            return true;

        } catch (MessagingException e) {
            log.error("❌ Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("❌ Unexpected error sending email to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

    /**
     * Send welcome email after registration.
     */
    @Override
    public boolean sendWelcomeEmail(String toEmail, String userName, String userRole) {
        log.info("📧 Sending welcome email to: {} (Role: {})", toEmail, userRole);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to ServiceSpot!");
            helper.setText(buildWelcomeEmailTemplate(userName, userRole), true);

            mailSender.send(message);

            log.info("✅ Welcome email sent successfully to: {}", toEmail);
            return true;

        } catch (Exception e) {
            log.error("❌ Failed to send welcome email: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Send booking confirmation email.
     */
    @Override
    public boolean sendBookingConfirmationEmail(String toEmail, String userName, String bookingDetails) {
        log.info("📧 Sending booking confirmation email to: {}", toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Booking Confirmed - ServiceSpot");
            helper.setText(buildBookingEmailTemplate(userName, bookingDetails), true);

            mailSender.send(message);

            log.info("✅ Booking confirmation email sent successfully to: {}", toEmail);
            return true;

        } catch (Exception e) {
            log.error("❌ Failed to send booking confirmation email: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Build HTML email template for password reset.
     */
    private String buildResetEmailTemplate(String userName, String resetToken) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    }
                    .container { 
                        max-width: 600px;
                        margin: 20px auto;
                        background: #ffffff;
                        border-radius: 12px;
                        overflow: hidden;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }
                    .header { 
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 40px 20px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 28px;
                        font-weight: 600;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .code-box { 
                        background: linear-gradient(135deg, #f3f4f6 0%%, #e5e7eb 100%%);
                        padding: 30px;
                        margin: 30px 0;
                        text-align: center;
                        border-radius: 10px;
                        border: 2px dashed #9ca3af;
                    }
                    .code { 
                        font-size: 42px;
                        font-weight: bold;
                        color: #4F46E5;
                        letter-spacing: 12px;
                        font-family: 'Courier New', monospace;
                    }
                    .info-box {
                        background: #fef3c7;
                        border-left: 4px solid #f59e0b;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .button {
                        display: inline-block;
                        padding: 12px 30px;
                        background: #4F46E5;
                        color: white;
                        text-decoration: none;
                        border-radius: 6px;
                        margin: 20px 0;
                        font-weight: 600;
                    }
                    .footer { 
                        background: #f9fafb;
                        color: #6b7280;
                        font-size: 13px;
                        padding: 20px 30px;
                        text-align: center;
                        border-top: 1px solid #e5e7eb;
                    }
                    .footer a {
                        color: #4F46E5;
                        text-decoration: none;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <p style="font-size: 16px;">Hi <strong>%s</strong>,</p>
                        <p>You requested to reset your password for your ServiceSpot account.</p>
                        <p>Enter this verification code in the app:</p>
                        
                        <div class="code-box">
                            <div class="code">%s</div>
                        </div>
                        
                        <div class="info-box">
                            <strong>⏱️ Important:</strong> This code will expire in <strong>15 minutes</strong>.
                        </div>
                        
                        <p>If you didn't request this password reset, please ignore this email. Your password will remain unchanged.</p>
                        
                        <p style="margin-top: 30px;">Need help? Contact us at <a href="mailto:support@servicespot.com">support@servicespot.com</a></p>
                    </div>
                    <div class="footer">
                        <p>© 2025 ServiceSpot. All rights reserved.</p>
                        <p>Localized Service Discovery & Booking Platform</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, resetToken);
    }

    /**
     * Build HTML email template for welcome message.
     */
    private String buildWelcomeEmailTemplate(String userName, String userRole) {
        String roleSpecific = userRole.equals("PROVIDER")
            ? "Start listing your services and connect with customers!"
            : "Discover and book local services in your area!";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 20px auto; background: #fff; border-radius: 10px; overflow: hidden; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 40px 20px; text-align: center; }
                    .content { padding: 30px; }
                    .footer { background: #f9fafb; padding: 20px; text-align: center; font-size: 12px; color: #6b7280; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Welcome to ServiceSpot!</h1>
                    </div>
                    <div class="content">
                        <p>Hi <strong>%s</strong>,</p>
                        <p>Welcome to ServiceSpot! We're excited to have you on board.</p>
                        <p>%s</p>
                        <p>If you have any questions, feel free to reach out to our support team.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 ServiceSpot. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, roleSpecific);
    }

    /**
     * Build HTML email template for booking confirmation.
     */
    private String buildBookingEmailTemplate(String userName, String bookingDetails) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 20px auto; background: #fff; border-radius: 10px; overflow: hidden; }
                    .header { background: linear-gradient(135deg, #10b981 0%%, #059669 100%%); color: white; padding: 40px 20px; text-align: center; }
                    .content { padding: 30px; }
                    .details-box { background: #f3f4f6; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .footer { background: #f9fafb; padding: 20px; text-align: center; font-size: 12px; color: #6b7280; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Booking Confirmed!</h1>
                    </div>
                    <div class="content">
                        <p>Hi <strong>%s</strong>,</p>
                        <p>Your booking has been confirmed!</p>
                        <div class="details-box">
                            <h3>Booking Details:</h3>
                            <p>%s</p>
                        </div>
                        <p>Thank you for using ServiceSpot!</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 ServiceSpot. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, bookingDetails);
    }
}


