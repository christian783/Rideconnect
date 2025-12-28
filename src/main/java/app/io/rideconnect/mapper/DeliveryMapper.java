package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.DeliveryDetailsDTO;
import app.io.rideconnect.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ride", ignore = true)
    @Mapping(target = "proofOfDeliveryUrl", ignore = true)
    @Mapping(target = "recipientSignatureUrl", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    Delivery toEntity(DeliveryDetailsDTO dto);

    DeliveryDetailsDTO toDTO(Delivery entity);
}