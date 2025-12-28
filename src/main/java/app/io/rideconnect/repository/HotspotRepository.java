package app.io.rideconnect.repository;

import app.io.rideconnect.entity.Hotspot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotspotRepository extends JpaRepository<Hotspot, String> {

    @Query("SELECT h FROM Hotspot h " +
            "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(h.latitude)) * " +
            "cos(radians(h.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(h.latitude)))) <= :radiusKm " +
            "ORDER BY h.demandScore DESC")
    List<Hotspot> findNearbyHotspots(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusKm") double radiusKm);

    @Query("SELECT h FROM Hotspot h ORDER BY h.demandScore DESC")
    List<Hotspot> findTopHotspotsByDemand(Pageable pageable);

    @Query("SELECT h FROM Hotspot h " +
            "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(h.latitude)) * " +
            "cos(radians(h.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(h.latitude)))) ASC")
    List<Hotspot> findClosestHotspots(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            Pageable pageable);

    @Query("SELECT h FROM Hotspot h " +
            "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(h.latitude)) * " +
            "cos(radians(h.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(h.latitude)))) <= :radiusKm " +
            "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(h.latitude)) * " +
            "cos(radians(h.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(h.latitude)))) ASC")
    Page<Hotspot> findNearLocation(

            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusKm") double radiusKm,
            Pageable pageable);
}