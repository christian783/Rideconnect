package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.RideRatingRequestDTO;
import app.io.rideconnect.dto.RideRatingResponseDTO;
import app.io.rideconnect.entity.RideRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RideRatingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ride", ignore = true)
    @Mapping(target = "passengerRating", ignore = true)
    @Mapping(target = "driverRating", ignore = true)
    @Mapping(target = "passengerComment", ignore = true)
    @Mapping(target = "driverComment", ignore = true)
    RideRating toEntity(RideRatingRequestDTO dto);

    @Mapping(target = "rideId", source = "ride.id")
    RideRatingResponseDTO toResponseDTO(RideRating entity);
}