package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.PromotionRequestDTO;
import app.io.rideconnect.dto.PromotionResponseDTO;
import app.io.rideconnect.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "usedCount", constant = "0")
    @Mapping(target = "isActive", constant = "true")
    Promotion toEntity(PromotionRequestDTO dto);

    @Mapping(target = "discountType", source = "discountType", qualifiedByName = "promotionEnumToString")
    PromotionResponseDTO toResponseDTO(Promotion entity);

    @Named("promotionEnumToString")
    default String enumToString(Enum<?> enumValue) {
        return enumValue != null ? enumValue.name() : null;
    }
}