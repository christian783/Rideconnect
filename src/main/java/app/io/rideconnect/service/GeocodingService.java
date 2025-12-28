package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;

public interface GeocodingService {

    String getAddressFromCoordinates(LocationDTO location);

    LocationDTO getCoordinatesFromAddress(String address);

    java.math.BigDecimal calculateDistance(LocationDTO from, LocationDTO to);

    Integer estimateDuration(LocationDTO from, LocationDTO to);
}