package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import java.util.List;

public interface DeliveryService {

    DeliveryDetailsDTO createDelivery(String rideId, DeliveryDetailsDTO detailsDTO);

    DeliveryDetailsDTO getDeliveryByRideId(String rideId);

    DeliveryDetailsDTO updateDeliveryProof(String deliveryId, DeliveryProofDTO proofDTO);

    DeliveryDetailsDTO completeDelivery(String deliveryId);

    List<DeliveryDetailsDTO> getPendingDeliveries();

    List<DeliveryDetailsDTO> getUserDeliveries(String userId);
}