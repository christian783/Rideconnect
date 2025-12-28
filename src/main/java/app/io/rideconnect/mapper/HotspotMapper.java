package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { GeometryMapper.class })
public interface HotspotMapper {

    // @Mapping(target = "location", source = "location")
    HotspotResponseDTO toResponseDTO(Hotspot entity);
}
