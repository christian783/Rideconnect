package app.io.rideconnect.service;

import app.io.rideconnect.dto.UserUpdateDTO;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.enums.UserRole;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO requestDTO);

    UserResponseDTO getUserById(String  id);

    UserResponseDTO getUserByEmail(String email);

    UserResponseDTO getUserByPhoneNumber(String phoneNumber);

    UserResponseDTO updateUser(String  id, UserUpdateDTO updateDTO);

    void deleteUser(String  id);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

//    UserResponseDTO updateUserLocation(String  id, LocationDTO location);

    PageResponseDTO<UserResponseDTO> getAllUsers(Pageable pageable);

    List<UserResponseDTO> getUsersByRole(UserRole role);

//    List<DriverFinanceResponseDTO> getDriverTransactionsBetweenDates(
//            String  driverId, LocalDateTime startDate, LocalDateTime endDate);
//
//    DriverFinanceSummaryDTO getDriverFinanceSummary(String  driverId);
//
//    DriverFinanceSummaryDTO getDriverFinanceSummaryForPeriod(
//            String  driverId, LocalDateTime startDate, LocalDateTime endDate);

//    void processRidePayment(String  rideId);
}