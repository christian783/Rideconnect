package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.RideTracking;

import java.util.List;

public interface RideTrackingService {

    void addTrackingPoint(String rideId, LocationDTO location, Double speed, Double heading);

    List<LocationDTO> getRideTrackingHistory(String rideId);

    List<RideTracking> getLatestTrackingPoint(String rideId);
}