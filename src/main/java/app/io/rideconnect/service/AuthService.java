package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.dto.security.AuthResponse;
import app.io.rideconnect.exception.InvalidPasswordException;
import app.io.rideconnect.exception.UserNotFoundException;


public interface AuthService {

    AuthResponse login(LoginRequest requestDTO);

    UserResponseDTO register(UserRequestDTO requestDTO);

    void logout(String token);

    AuthResponse refreshToken(String refreshToken) throws UserNotFoundException;

    void resetPassword(String email);

    void changePassword(String userId, String oldPassword, String newPassword) throws UserNotFoundException, InvalidPasswordException;
}