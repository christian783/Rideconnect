package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.DriverFinanceRequestDTO;
import app.io.rideconnect.dto.DriverFinanceResponseDTO;
import app.io.rideconnect.entity.DriverFinance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {GeometryMapper.class})
public interface DriverFinanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionDate", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "ride", ignore = true)
    @Mapping(target = "balance", ignore = true)
    DriverFinance toEntity(DriverFinanceRequestDTO dto);

    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "rideId", source = "ride.id")
    @Mapping(target = "transactionType", source = "transactionType")
    DriverFinanceResponseDTO toResponseDTO(DriverFinance entity);

}