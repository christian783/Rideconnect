package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.dto.security.*;
import app.io.rideconnect.entity.Role;
import app.io.rideconnect.entity.User;
import app.io.rideconnect.entity.enums.UserStatus;
import app.io.rideconnect.entity.security.CustomUserDetails;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.UserMapper;
import app.io.rideconnect.repository.UserRepository;
import app.io.rideconnect.service.AuthService;
import app.io.rideconnect.urtils.secutiry.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoleServiceImpl roleServiceImpl;
//    private final EmailService emailService; // Assume you have this service
//    private final TokenBlacklistService tokenBlacklistService; // Assume you have this service

    @Override
    @Transactional
    public AuthResponse login(LoginRequest requestDTO) {
        try {
            log.debug("Attempting to authenticate user: {}", requestDTO.getEmail());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDTO.getEmail(),
                            requestDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = userRepository.findByEmail(requestDTO.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + requestDTO.getEmail()));

            // Check if user is active
            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new AccountDisabledException("Account is not active. Current status: " + user.getStatus());
            }

//            Get user details form user
            CustomUserDetails user_ = (CustomUserDetails)  authentication.getPrincipal();
            // Generate tokens
            String accessToken = jwtTokenProvider.generateToken(user_);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user_);

            log.info("User logged in successfully: {}", user.getEmail());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .user(userMapper.toResponseDTO(user))
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for email: {}", requestDTO.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public UserResponseDTO register(UserRequestDTO requestDTO)  {
        try {
            log.debug("Starting user registration for email: {}", requestDTO.getEmail());

            // Check if user already exists
            if (userRepository.existsByEmail(requestDTO.getEmail())) {
                log.warn("Registration failed - user already exists with email: {}", requestDTO.getEmail());
                throw new UserAlreadyExistsException("User already exists with email: " + requestDTO.getEmail());
            }

//            Check if we already have user with that phone number
            // Validate phone uniqueness
            if (userRepository.existsByPhoneNumber(requestDTO.getPhoneNumber())) {
                throw new UserAlreadyExistsException(
                        "User already exists with phone: " + requestDTO.getPhoneNumber()
                );
            }
            // Validate password strength
            validatePasswordStrength(requestDTO.getPassword());

            // Map DTO to entity
            User user = userMapper.toEntity(requestDTO);
            System.out.println("The user returned is "+ user.getRoles()+" " + user.getEmail());

            // Encode password
            user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

//            Set the role
            Role role = roleServiceImpl.getRoleByName(requestDTO.getRole());
            user.setRoles(new HashSet<>(Collections.singletonList(role)));
            // Set timestamps
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // Save user
            User savedUser = userRepository.save(user);
            log.info("User registered successfully with ID: {}", savedUser.getId());

            // Send welcome email (async)
//            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());

            return userMapper.toResponseDTO(savedUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional
    public void logout(String token) {
        log.debug("Processing logout for token");
        // Clear security context
        SecurityContextHolder.clearContext();

        log.info("User logged out successfully");
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) throws UserNotFoundException {
        log.debug("Refreshing token");

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("Invalid refresh token provided");
            throw new InvalidTokenException("Invalid or expired refresh token");
        }



        // Get user from token
        String email = jwtTokenProvider.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Check if user is active
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountDisabledException("Account is not active");
        }

//        Get user details form the user
        CustomUserDetails user_ = new CustomUserDetails(user);

        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generateToken(user_);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user_);


        log.info("Token refreshed successfully for user: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getRefreshExpirationTime())
                .user(userMapper.toResponseDTO(user))
                .build();
    }

    @Override
    @Transactional
    public void resetPassword(String email) {
        log.debug("Processing password reset for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user != null && user.getStatus() == UserStatus.ACTIVE) {
            // Generate reset token
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(24)); // 24 hours expiry
            userRepository.save(user);

            // Send reset email
//            emailService.sendPasswordResetEmail(user.getEmail(), resetToken, user.getFirstName());
            log.info("Password reset token generated and email sent for user: {}", email);
        } else {
            log.info("Password reset not processed for inactive or non-existent user: {}", email);
        }
    }

    @Override
    @Transactional
    public void changePassword(String userId, String oldPassword, String newPassword) throws UserNotFoundException, InvalidPasswordException {
        log.debug("Changing password for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Password change failed - incorrect old password for user ID: {}", userId);
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Validate new password strength
        validatePasswordStrength(newPassword);

        // Check if new password is different from old password
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidPasswordException("New password must be different from current password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Send notification email
//        emailService.sendPasswordChangedNotification(user.getEmail(), user.getFirstName());

        log.info("Password changed successfully for user ID: {}", userId);
    }

    /**
     * Validate password strength
     */
    private void validatePasswordStrength(String password) throws InvalidPasswordException {
        if (password == null || password.length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters long");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidPasswordException("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new InvalidPasswordException("Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            throw new InvalidPasswordException("Password must contain at least one digit");
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new InvalidPasswordException("Password must contain at least one special character");
        }
    }
}