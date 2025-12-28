package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.dto.security.AuthResponse;
import app.io.rideconnect.exception.InvalidPasswordException;
import app.io.rideconnect.exception.UserNotFoundException;
import app.io.rideconnect.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "User login",
            description = "Authenticate user with email and password, returns JWT tokens"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request format"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequest requestDTO) {

        log.info("Login attempt for email: {}", requestDTO.getEmail());
        AuthResponse response = authService.login(requestDTO);
        log.info("Login successful for email: {}", requestDTO.getEmail());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "User registration",
            description = "Register a new user account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or user already exists"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation errors in request data"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @Parameter(description = "User registration data", required = true)
            @Valid @RequestBody UserRequestDTO requestDTO) {

        log.info("Registration attempt for email: {}", requestDTO.getEmail());
        UserResponseDTO response = authService.register(requestDTO);
        log.info("User registered successfully with ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "User logout",
            description = "Invalidate user's JWT token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid token"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "JWT token to invalidate", required = true)
            @RequestHeader("Authorization") String token) {

        log.info("Logout request received");
        // Remove "Bearer " prefix if present
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        authService.logout(actualToken);
        log.info("Logout successful");

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Refresh access token",
            description = "Get new access token using refresh token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token"
            )
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            @Parameter(description = "Refresh token", required = true)
            @RequestHeader("Refresh-Token") String refreshToken) throws UserNotFoundException {

        log.info("Token refresh request");
        AuthResponse response = authService.refreshToken(refreshToken);
        log.info("Token refreshed successfully");

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Reset password",
            description = "Send password reset instructions to user's email"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Password reset email sent if account exists"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email format"
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "User's email address", required = true)
            @RequestParam String email) {

        log.info("Password reset requested for email: {}", email);
        authService.resetPassword(email);
        log.info("Password reset email sent to: {}", email);

        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Change password",
            description = "Change user's password (requires old password verification)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid old password or weak new password"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "User ID", required = true)
            @RequestParam String userId,
            @Parameter(description = "Current password", required = true)
            @RequestParam String oldPassword,
            @Parameter(description = "New password", required = true)
            @RequestParam String newPassword) throws UserNotFoundException, InvalidPasswordException {

        log.info("Password change requested for user ID: {}", userId);
        authService.changePassword(userId, oldPassword, newPassword);
        log.info("Password changed successfully for user ID: {}", userId);

        return ResponseEntity.ok().build();
    }
}