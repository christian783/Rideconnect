package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.RideTrackingDTO;
import app.io.rideconnect.entity.RideTracking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RideTrackingMapper {

    @Mapping(target = "rideId", source = "ride.id")
    RideTrackingDTO toDTO(RideTracking entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ride", ignore = true)
    RideTracking toEntity(RideTrackingDTO dto);

    List<RideTrackingDTO> toDTOList(List<RideTracking> entities);

    List<RideTracking> toEntityList(List<RideTrackingDTO> dtos);
}
