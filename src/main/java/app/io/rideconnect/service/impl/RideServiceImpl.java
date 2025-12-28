package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.entity.enums.RideStatus;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RideServiceImpl implements RideService {

        private final RideRepository rideRepository;
        private final UserRepository userRepository;
        private final RideMapper rideMapper;
        private final FareCalculationService fareCalculationService;
        private final GeocodingService geocodingService;
        private final MatchingService matchingService;
        private final NotificationService notificationService;

        @Override
        public RideResponseDTO createRide(RideRequestDTO requestDTO) {
                log.info("Creating ride for passenger ID: {}", requestDTO.getPassengerId());

                User passenger = userRepository.findById(requestDTO.getPassengerId())
                                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

                Ride ride = rideMapper.toEntity(requestDTO);
                ride.setPassenger(passenger);
                ride.setStatus(RideStatus.REQUESTED);
                ride.setRequestedAt(LocalDateTime.now());

                // Calculate fare
                BigDecimal distance = geocodingService.calculateDistance(
                                requestDTO.getPickupLocation(), requestDTO.getDropoffLocation());
                BigDecimal fare = fareCalculationService.calculateTotalFare(
                                requestDTO.getRideType(), distance, requestDTO.getPickupLocation(),
                                LocalDateTime.now());

                ride.setEstimatedFare(fare);
                ride.setDistanceKm(distance);

                Ride savedRide = rideRepository.save(ride);

                // Notify nearby drivers
                matchingService.notifyNearbyDrivers(savedRide.getId(), requestDTO.getPickupLocation(), 5.0);

                log.info("Ride created with ID: {}", savedRide.getId());
                return rideMapper.toResponseDTO(savedRide);
        }

        @Override
        @Transactional(readOnly = true)
        public RideResponseDTO getRideById(String id) {
                Ride ride = rideRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
                return rideMapper.toResponseDTO(ride);
        }

        @Override
        public RideResponseDTO acceptRide(String rideId, String driverId) {
                Ride ride = rideRepository.findById(rideId)
                                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

                if (ride.getStatus() != RideStatus.REQUESTED) {
                        throw new InvalidOperationException("Ride is not available for acceptance");
                }

                User driver = userRepository.findById(driverId)
                                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

                ride.setDriver(driver);
                ride.setStatus(RideStatus.ACCEPTED);
                ride.setAcceptedAt(LocalDateTime.now());

                Ride updated = rideRepository.save(ride);

                // Notify passenger
                notificationService.sendRideAcceptedNotification(ride.getPassenger().getId(), rideId);

                log.info("Ride {} accepted by driver {}", rideId, driverId);
                return rideMapper.toResponseDTO(updated);
        }

        @Override
        public RideResponseDTO startRide(String rideId) {
                Ride ride = rideRepository.findById(rideId)
                                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

                if (ride.getStatus() != RideStatus.ACCEPTED) {
                        throw new InvalidOperationException("Ride must be accepted before starting");
                }

                ride.setStatus(RideStatus.IN_PROGRESS);
                ride.setStartedAt(LocalDateTime.now());

                Ride updated = rideRepository.save(ride);

                notificationService.sendRideStartedNotification(ride.getPassenger().getId(), rideId);

                return rideMapper.toResponseDTO(updated);
        }

        @Override
        public RideResponseDTO completeRide(String rideId) {
                Ride ride = rideRepository.findById(rideId)
                                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

                if (ride.getStatus() != RideStatus.IN_PROGRESS) {
                        throw new InvalidOperationException("Ride must be in progress to complete");
                }

                ride.setStatus(RideStatus.COMPLETED);
                ride.setCompletedAt(LocalDateTime.now());
                ride.setActualFare(ride.getEstimatedFare()); // Can be adjusted

                Ride updated = rideRepository.save(ride);

                notificationService.sendRideCompletedNotification(ride.getPassenger().getId(), rideId);

                log.info("Ride {} completed", rideId);
                return rideMapper.toResponseDTO(updated);
        }

        @Override
        public RideResponseDTO cancelRide(String rideId, String reason) {
                Ride ride = rideRepository.findById(rideId)
                                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

                ride.setStatus(RideStatus.CANCELLED);
                ride.setCancelledAt(LocalDateTime.now());

                Ride updated = rideRepository.save(ride);

                log.info("Ride {} cancelled. Reason: {}", rideId, reason);
                return rideMapper.toResponseDTO(updated);
        }

        @Override
        public RideResponseDTO updateRideStatus(String rideId, String status) {
                Ride ride = rideRepository.findById(rideId)
                                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

                ride.setStatus(RideStatus.valueOf(status.toUpperCase()));
                Ride updated = rideRepository.save(ride);

                return rideMapper.toResponseDTO(updated);
        }

        @Override
        @Transactional(readOnly = true)
        public PageResponseDTO<RideResponseDTO> getPassengerRides(String passengerId, Pageable pageable) {
                Page<Ride> ridePage = rideRepository.findByPassengerId(passengerId, pageable);

                List<RideResponseDTO> content = ridePage.getContent().stream()
                                .map(rideMapper::toResponseDTO)
                                .collect(Collectors.toList());

                return PageResponseDTO.<RideResponseDTO>builder()
                                .content(content)
                                .pageNumber(ridePage.getNumber())
                                .pageSize(ridePage.getSize())
                                .totalElements(ridePage.getTotalElements())
                                .totalPages(ridePage.getTotalPages())
                                .last(ridePage.isLast())
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public PageResponseDTO<RideResponseDTO> getDriverRides(String driverId, Pageable pageable) {
                Page<Ride> ridePage = rideRepository.findByDriverId(driverId, pageable);

                List<RideResponseDTO> content = ridePage.getContent().stream()
                                .map(rideMapper::toResponseDTO)
                                .collect(Collectors.toList());

                return PageResponseDTO.<RideResponseDTO>builder()
                                .content(content)
                                .pageNumber(ridePage.getNumber())
                                .pageSize(ridePage.getSize())
                                .totalElements(ridePage.getTotalElements())
                                .totalPages(ridePage.getTotalPages())
                                .last(ridePage.isLast())
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public List<RideResponseDTO> getActiveRides() {
                List<RideStatus> activeStatuses = List.of(
                                RideStatus.REQUESTED, RideStatus.ACCEPTED, RideStatus.IN_PROGRESS);

                return rideRepository.findByStatusIn(activeStatuses).stream()
                                .map(rideMapper::toResponseDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<RideResponseDTO> getRidesByStatus(String status) {
                RideStatus rideStatus = RideStatus.valueOf(status.toUpperCase());
                return rideRepository.findByStatus(rideStatus).stream()
                                .map(rideMapper::toResponseDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public RideResponseDTO calculateFare(RideRequestDTO requestDTO) {
                BigDecimal distance = geocodingService.calculateDistance(
                                requestDTO.getPickupLocation(), requestDTO.getDropoffLocation());
                BigDecimal fare = fareCalculationService.calculateTotalFare(
                                requestDTO.getRideType(), distance, requestDTO.getPickupLocation(),
                                LocalDateTime.now());

                RideResponseDTO response = new RideResponseDTO();
                response.setEstimatedFare(fare);
                response.setDistanceKm(distance);

                return response;
        }

        @Override
        public RideResponseDTO activateSOS(String rideId, String notes) {
                log.debug("Activating SOS for ride: {}", rideId);

                Ride ride = rideRepository.findById(rideId)
                                .orElseThrow(() -> {
                                        log.error("Ride not found with id: {}", rideId);
                                        return new ResourceNotFoundException("Ride not found with id: " + rideId);
                                });

                if (ride.getStatus() != RideStatus.IN_PROGRESS) {
                        throw new InvalidOperationException("SOS can only be activated for rides in progress");
                }

                ride.setSosActivated(true);
                ride.setSosNotes(notes);

                Ride updated = rideRepository.save(ride);

                // Send SOS alert with current pickup location
                LocationDTO pickupLocation = LocationDTO.builder()
                                .latitude(ride.getPickupLatitude())
                                .longitude(ride.getPickupLongitude())
                                .address(ride.getPickupAddress())
                                .build();

                notificationService.sendSOSAlert(rideId, pickupLocation);

                log.warn("SOS activated for ride: {} with notes: {}", rideId, notes);
                return rideMapper.toResponseDTO(updated);
        }

        @Override
        @Transactional(readOnly = true)
        public RideAnalyticsDTO getRideAnalytics() {
                Long totalRides = rideRepository.count();
                Long completedRides = rideRepository.countByStatus(RideStatus.COMPLETED);
                Long cancelledRides = rideRepository.countByStatus(RideStatus.CANCELLED);

                return RideAnalyticsDTO.builder()
                                .totalRides(totalRides)
                                .completedRides(completedRides)
                                .cancelledRides(cancelledRides)
                                .totalRevenue(rideRepository.getTotalRevenueBetweenDates(
                                                LocalDateTime.now().minusMonths(1), LocalDateTime.now()))
                                .averageFare(rideRepository.getAverageFare())
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public RideAnalyticsDTO getRideAnalyticsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
                return RideAnalyticsDTO.builder()
                                .totalRevenue(rideRepository.getTotalRevenueBetweenDates(startDate, endDate))
                                .build();
        }
}
