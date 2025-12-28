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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HotspotServiceImpl implements HotspotService {

    private final HotspotRepository hotspotRepository;
    private final RideRepository rideRepository;
    private final HotspotMapper hotspotMapper;

    @Override
    public HotspotResponseDTO createHotspot(HotspotResponseDTO hotspotDTO) {
        log.info("Creating hotspot: {}", hotspotDTO.getName());

        Hotspot hotspot = new Hotspot();
        hotspot.setName(hotspotDTO.getName());
        hotspot.setRadiusMeters(hotspotDTO.getRadiusMeters());
        hotspot.setDemandScore(hotspotDTO.getDemandScore());
        hotspot.setAverageRidesPerHour(hotspotDTO.getAverageRidesPerHour());
        hotspot.setTimeOfDayPattern(hotspotDTO.getTimeOfDayPattern());

        Hotspot saved = hotspotRepository.save(hotspot);
        log.info("Hotspot created with ID: {}", saved.getId());

        return hotspotMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public HotspotResponseDTO getHotspotById(String id) {
        Hotspot hotspot = hotspotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotspot not found"));
        return hotspotMapper.toResponseDTO(hotspot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotspotResponseDTO> getNearbyHotspots(LocationDTO location, double radiusKm) {
        double radiusMeters = radiusKm * 1000;

        List<Hotspot> hotspots = hotspotRepository.findNearbyHotspots(
                location.getLatitude(),
                location.getLongitude(),
                radiusMeters);

        return hotspots.stream()
                .map(hotspotMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotspotResponseDTO> getTopHotspots(int limit) {
        return hotspotRepository.findTopHotspotsByDemand(
                org.springframework.data.domain.PageRequest.of(0, limit)).stream()
                .map(hotspotMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateHotspotDemand(String id, BigDecimal demandScore) {
        Hotspot hotspot = hotspotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotspot not found"));

        hotspot.setDemandScore(demandScore);
        hotspotRepository.save(hotspot);

        log.info("Hotspot {} demand updated to {}", id, demandScore);
    }

    @Override
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void analyzeAndUpdateHotspots() {
        log.info("Starting hotspot analysis...");

        LocalDateTime hourAgo = LocalDateTime.now().minusHours(1);

        // Fetch recent rides to analyze demand patterns
        List<Ride> recentRides = rideRepository.findByStatusAndDateRange(
                List.of(RideStatus.COMPLETED), hourAgo, LocalDateTime.now());

        if (recentRides.isEmpty()) {
            log.info("No recent rides found. Skipping hotspot update.");
            return;
        }

        // Group rides by proximity to existing hotspots
        Map<String, List<Ride>> hotspotClusters = new HashMap<>();
        List<Hotspot> existingHotspots = hotspotRepository.findAll();

        for (Ride ride : recentRides) {
            LocationDTO pickupLocation = LocationDTO.builder()
                    .latitude(ride.getPickupLatitude())
                    .longitude(ride.getPickupLongitude())
                    .build();

            boolean foundNearby = false;
            for (Hotspot hotspot : existingHotspots) {
                LocationDTO hotspotLocation = LocationDTO.builder()
                        .latitude(hotspot.getLatitude())
                        .longitude(hotspot.getLongitude())
                        .build();

                double distanceKm = calculateDistanceBetweenCoordinates(
                        pickupLocation.getLatitude(), pickupLocation.getLongitude(),
                        hotspot.getLatitude(), hotspot.getLongitude());

                if (distanceKm <= 0.05) { // 50m tolerance
                    hotspotClusters.computeIfAbsent(hotspot.getId(), k -> new ArrayList<>()).add(ride);
                    foundNearby = true;
                    break;
                }
            }
        }

        // Update demand scores
        for (Map.Entry<String, List<Ride>> entry : hotspotClusters.entrySet()) {
            Hotspot hotspot = hotspotRepository.findById(entry.getKey()).orElse(null);
            if (hotspot != null) {
                long rideCount = entry.getValue().size();
                double demandScore = Math.min(rideCount * 10, 100);
                hotspot.setDemandScore(BigDecimal.valueOf(demandScore));
                hotspot.setUpdatedAt(LocalDateTime.now());
                hotspotRepository.save(hotspot);
                log.info("Updated hotspot {} with score {} ({} rides)",
                        hotspot.getId(), demandScore, rideCount);
            }
        }

        log.info("Hotspot analysis completed. {} hotspots processed.", hotspotClusters.size());
    }

    private double calculateDistanceBetweenCoordinates(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}