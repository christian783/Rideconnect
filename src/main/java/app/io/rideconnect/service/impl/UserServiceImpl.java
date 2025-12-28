package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.entity.enums.UserRole;
import app.io.rideconnect.entity.enums.UserStatus;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        log.info("Creating user with email: {}", requestDTO.getEmail());

        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("User with email already exists");
        }

        if (userRepository.existsByPhoneNumber(requestDTO.getPhoneNumber())) {
            throw new ResourceAlreadyExistsException("User with phone number already exists");
        }

//        validate roles
        Role user_role = roleRepository.findByName(requestDTO.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));


        User user = userMapper.toEntity(requestDTO);
        user.setRoles(Set.of(user_role));


        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + phoneNumber));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(String id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        userMapper.updateEntityFromDTO(updateDTO, user);
        User updatedUser = userRepository.save(user);

        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<UserResponseDTO> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponseDTO> content = userPage.getContent().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<UserResponseDTO>builder()
                .content(content)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

//    @Override
//    public List<DriverFinanceResponseDTO> getDriverTransactionsBetweenDates(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {
//        return List.of();
//    }
//
//    @Override
//    public DriverFinanceSummaryDTO getDriverFinanceSummary(Long driverId) {
//        return null;
//    }
//
//    @Override
//    public DriverFinanceSummaryDTO getDriverFinanceSummaryForPeriod(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {
//        return null;
//    }

//    @Override
//    public void processRidePayment(String rideId) {
//
//    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(UserRole role) {
          List<User> users_ = userRepository.findAll();

          List<User> filtered_ =  users_.stream().filter(user -> user.getRoles().contains(new Role(role))).collect(Collectors.toList());
          return filtered_.stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }


    private UserLocation createUserLocation(LocationDTO locationDTO) {
        return UserLocation.builder()
                .placeName(locationDTO.getPlaceName())
                .latitude(locationDTO.getLatitude())
                .longitude(locationDTO.getLongitude())
                .address(locationDTO.getAddress())
                .city(locationDTO.getCity())
                .country(locationDTO.getCountry())
                .postalCode(locationDTO.getPostalCode())
                .isPrimary(locationDTO.getIsPrimary() != null ? locationDTO.getIsPrimary() : true)
                .build();
    }

    // Additional methods for location management
    public UserResponseDTO addLocation(String userId, LocationDTO locationDTO) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        UserLocation location = createUserLocation(locationDTO);
        user.addLocation(location);

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    public UserResponseDTO setPrimaryLocation(String userId, String locationId) throws UserNotFoundException, LocationNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        UserLocation location = user.getLocations().stream()
                .filter(loc -> loc.getId().equals(locationId))
                .findFirst()
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));

        user.setPrimaryLocation(location);

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
}