package Team.C.Service.Spot.controller;

import Team.C.Service.Spot.dto.request.AdminRegistrationRequest;
import Team.C.Service.Spot.dto.request.CustomerRegistrationRequest;
import Team.C.Service.Spot.dto.request.LoginRequest;
import Team.C.Service.Spot.dto.request.ProviderRegistrationRequest;
import Team.C.Service.Spot.dto.request.ForgotPasswordRequest;
import Team.C.Service.Spot.dto.request.ResetPasswordRequest;
import Team.C.Service.Spot.dto.response.ApiResponse;
import Team.C.Service.Spot.dto.response.AuthResponse;
import Team.C.Service.Spot.dto.response.UserResponse;
import Team.C.Service.Spot.model.User;
import Team.C.Service.Spot.security.JwtTokenProvider;
import Team.C.Service.Spot.service.UserService;
import Team.C.Service.Spot.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

/**
 * REST Controller for Authentication operations.
 * Handles user registration (admin, customer and provider), login, and password reset.
 *
 * @author Team C
 * @version 4.0
 * @since 2025-11-29
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    /**
     * Register a new admin.
     *
     * @param request admin registration details
     * @return registered admin information
     */
    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<UserResponse>> registerAdmin(
            @Valid @RequestBody AdminRegistrationRequest request) {
        UserResponse user = userService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin registered successfully", user));
    }

    /**
     * Register a new customer.
     *
     * @param request customer registration details
     * @return registered user information
     */
    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponse<UserResponse>> registerCustomer(
            @Valid @RequestBody CustomerRegistrationRequest request) {
        UserResponse user = userService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer registered successfully", user));
    }

    /**
     * Register a new service provider.
     *
     * @param request provider registration details
     * @return registered provider information
     */
    @PostMapping("/register/provider")
    public ResponseEntity<ApiResponse<UserResponse>> registerProvider(
            @Valid @RequestBody ProviderRegistrationRequest request) {
        UserResponse user = userService.registerProvider(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service provider registered successfully", user));
    }

    /**
     * User login with JWT token generation.
     *
     * @param request login credentials
     * @return authentication response with user details and JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        User user = userService.authenticateUser(request);

        // Generate real JWT token
        String accessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Map User to UserResponse with ALL fields including provider-specific ones
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .doorNo(user.getDoorNo())
                .addressLine(user.getAddressLine())
                .city(user.getCity())
                .state(user.getState())
                .pincode(user.getPincode())
                .active(user.getActive())
                // Provider-specific fields
                .serviceType(user.getServiceType())
                .approxPrice(user.getApproxPrice())
                .yearsExperience(user.getYearsExperience())
                .description(user.getDescription())
                .verified(user.getVerified())
                .averageRating(user.getAverageRating())
                .reviewCount(user.getReviewCount())
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L) // 24 hours from config
                .user(userResponse)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    /**
     * Logout endpoint.
     * JWT is stateless, so logout is client-side (remove token).
     * In production, consider adding token to blacklist for extra security.
     *
     * @return success response
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        // Client should remove token from localStorage
        return ResponseEntity.ok(ApiResponse.success("Logout successful", "Token should be removed from client"));
    }

    /**
     * Verify JWT token validity.
     *
     * @param token JWT token (without "Bearer " prefix)
     * @return token validation response
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyToken(@RequestParam String token) {
        boolean isValid = jwtTokenProvider.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success("Token verification completed", isValid));
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param refreshToken refresh token
     * @return new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestParam String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid refresh token", HttpStatus.UNAUTHORIZED.value()));
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userService.getUserEntityById(userId);

        String newAccessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        AuthResponse authResponse = AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
    }

    /**
     * Initiate password reset process.
     * Generates a reset token and sends it via email (Gmail SMTP).
     *
     * @param request forgot password request with email
     * @return success message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        try {
            // Generate reset token
            String resetToken = userService.initiatePasswordReset(request.getEmail());

            // Get user details for email
            User user = userService.getUserByEmail(request.getEmail());

            // Send email with reset token
            boolean emailSent = emailService.sendPasswordResetEmail(
                request.getEmail(),
                user.getName(),
                resetToken
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset token generated successfully");
            response.put("email", request.getEmail());
            response.put("emailSent", String.valueOf(emailSent));

            // DEVELOPMENT ONLY: Show token in response for testing
            // Remove in production or when email is working properly
            String isDevelopment = System.getProperty("spring.profiles.active", "dev");
            if ("dev".equals(isDevelopment) || isDevelopment.contains("dev")) {
                response.put("resetToken", resetToken);
                response.put("warning", "Token visible for development - check your email in production");
            }

            String message = emailSent
                ? "Password reset code has been sent to your email. Please check your inbox."
                : "Password reset initiated. If email delivery fails, contact support.";

            return ResponseEntity.ok(ApiResponse.success(message, response));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process password reset request",
                           HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Verify password reset token.
     *
     * @param email user email
     * @param token reset token to verify
     * @return verification result
     */
    @GetMapping("/verify-reset-token")
    public ResponseEntity<ApiResponse<Boolean>> verifyResetToken(
            @RequestParam String email,
            @RequestParam String token) {
        boolean isValid = userService.verifyResetToken(email, token);

        if (isValid) {
            return ResponseEntity.ok(
                ApiResponse.success("Token is valid", isValid)
            );
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid or expired token", HttpStatus.BAD_REQUEST.value()));
        }
    }

    /**
     * Reset password using reset token.
     *
     * @param request reset password request
     * @return success message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getEmail(), request.getResetToken(), request.getNewPassword());
            return ResponseEntity.ok(
                ApiResponse.success("Password reset successfully", "You can now login with your new password")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }
}
