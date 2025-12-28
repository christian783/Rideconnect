package app.io.rideconnect.service.impl;

import app.io.rideconnect.config.MapsConfig;
import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeocodingServiceImpl implements GeocodingService {

    private final MapsConfig mapsConfig;
    private final WebClient.Builder webClientBuilder;

    @Override
    public String getAddressFromCoordinates(LocationDTO location) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(mapsConfig.getBaseUrl()).build();

            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/geocoding/v5/mapbox.places/{longitude},{latitude}.json")
                            .queryParam("access_token", mapsConfig.getApiKey())
                            .build(location.getLongitude(), location.getLatitude()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse response and extract address
            // Simplified for demonstration
            return "Address at " + location.getLatitude() + ", " + location.getLongitude();

        } catch (Exception e) {
            log.error("Geocoding error: {}", e.getMessage());
            return "Unknown location";
        }
    }

    @Override
    public LocationDTO getCoordinatesFromAddress(String address) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(mapsConfig.getBaseUrl()).build();

            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/geocoding/v5/mapbox.places/{address}.json")
                            .queryParam("access_token", mapsConfig.getApiKey())
                            .build(address))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse response and extract coordinates
            // Simplified for demonstration
            return LocationDTO.builder()
                    .latitude(-1.9403)
                    .longitude(29.8739)
                    .build();

        } catch (Exception e) {
            log.error("Geocoding error: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public BigDecimal calculateDistance(LocationDTO from, LocationDTO to) {
        // Haversine formula for calculating distance between two coordinates
        double earthRadius = 6371; // kilometers

        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());
        double deltaLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double deltaLon = Math.toRadians(to.getLongitude() - from.getLongitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = earthRadius * c;

        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Integer estimateDuration(LocationDTO from, LocationDTO to) {
        // Simplified estimation: assume average speed of 40 km/h
        BigDecimal distance = calculateDistance(from, to);
        double hours = distance.doubleValue() / 40.0;
        return (int) (hours * 60); // Convert to minutes
    }
}