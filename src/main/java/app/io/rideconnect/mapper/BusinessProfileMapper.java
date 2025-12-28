package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BusinessProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "walletBalance", constant = "0")
    BusinessProfile toEntity(BusinessProfileRequestDTO dto);

    @Mapping(target = "userId", source = "user.id")
    BusinessProfileResponseDTO toResponseDTO(BusinessProfile entity);
}