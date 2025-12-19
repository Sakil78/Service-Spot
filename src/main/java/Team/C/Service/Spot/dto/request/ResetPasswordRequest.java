package Team.C.Service.Spot.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for resetting password.
 * Contains email, reset token, and new password.
 *
 * @author Team C
 * @version 4.0
 * @since 2025-12-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    /**
     * User's registered email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Password reset token (6-digit code).
     */
    @NotBlank(message = "Reset token is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Reset token must be a 6-digit number")
    private String resetToken;

    /**
     * New password for the user account.
     */
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}

