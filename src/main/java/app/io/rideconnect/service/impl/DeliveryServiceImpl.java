package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final RideRepository rideRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    public DeliveryDetailsDTO createDelivery(String rideId, DeliveryDetailsDTO detailsDTO) {
        log.info("Creating delivery for ride ID: {}", rideId);

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        Delivery delivery = deliveryMapper.toEntity(detailsDTO);
        delivery.setRide(ride);

        Delivery saved = deliveryRepository.save(delivery);
        log.info("Delivery created with ID: {}", saved.getId());

        return deliveryMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDetailsDTO getDeliveryByRideId(String rideId) {
        Delivery delivery = deliveryRepository.findByRideId(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for ride"));
        return deliveryMapper.toDTO(delivery);
    }

    @Override
    public DeliveryDetailsDTO updateDeliveryProof(String deliveryId, DeliveryProofDTO proofDTO) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

        delivery.setProofOfDeliveryUrl(proofDTO.getProofOfDeliveryUrl());
        delivery.setRecipientSignatureUrl(proofDTO.getRecipientSignatureUrl());

        Delivery updated = deliveryRepository.save(delivery);
        log.info("Delivery proof updated for delivery ID: {}", deliveryId);

        return deliveryMapper.toDTO(updated);
    }

    @Override
    public DeliveryDetailsDTO completeDelivery(String deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

        delivery.setDeliveredAt(LocalDateTime.now());

        Delivery updated = deliveryRepository.save(delivery);
        log.info("Delivery completed: {}", deliveryId);

        return deliveryMapper.toDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDetailsDTO> getPendingDeliveries() {
        return deliveryRepository.findPendingDeliveries().stream()
                .map(deliveryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDetailsDTO> getUserDeliveries(String userId) {
        return deliveryRepository.findByUserId(userId).stream()
                .map(deliveryMapper::toDTO)
                .collect(Collectors.toList());
    }
}