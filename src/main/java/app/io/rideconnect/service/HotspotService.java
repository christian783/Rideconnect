package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import java.util.List;

public interface HotspotService {

    HotspotResponseDTO createHotspot(HotspotResponseDTO hotspotDTO);

    HotspotResponseDTO getHotspotById(String id);

    List<HotspotResponseDTO> getNearbyHotspots(LocationDTO location, double radiusKm);

    List<HotspotResponseDTO> getTopHotspots(int limit);

    void updateHotspotDemand(String id, java.math.BigDecimal demandScore);

    void analyzeAndUpdateHotspots();
}