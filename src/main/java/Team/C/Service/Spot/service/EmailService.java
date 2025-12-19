package Team.C.Service.Spot.service;

/**
 * Email Service Interface for sending emails.
 * Implement this interface with your preferred email provider:
 * - SendGrid
 * - AWS SES
 * - Mailgun
 * - Gmail SMTP
 *
 * @author Team C
 * @version 4.0
 * @since 2025-12-20
 */
public interface EmailService {

    /**
     * Send password reset email with 6-digit token.
     *
     * @param toEmail recipient email address
     * @param userName recipient name
     * @param resetToken 6-digit reset code
     * @return true if email sent successfully, false otherwise
     */
    boolean sendPasswordResetEmail(String toEmail, String userName, String resetToken);

    /**
     * Send welcome email after successful registration.
     *
     * @param toEmail recipient email address
     * @param userName recipient name
     * @param userRole user role (CUSTOMER or PROVIDER)
     * @return true if email sent successfully, false otherwise
     */
    boolean sendWelcomeEmail(String toEmail, String userName, String userRole);

    /**
     * Send booking confirmation email.
     *
     * @param toEmail recipient email address
     * @param userName recipient name
     * @param bookingDetails booking information
     * @return true if email sent successfully, false otherwise
     */
    boolean sendBookingConfirmationEmail(String toEmail, String userName, String bookingDetails);
}

