package tom.springframework.vibecodingmvc.mappers;

import org.mapstruct.*;
import tom.springframework.vibecodingmvc.entities.BeerOrderShipment;
import tom.springframework.vibecodingmvc.entities.ShipmentStatus;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentCreateDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentUpdateDto;

@Mapper(componentModel = "spring")
public interface BeerOrderShipmentMapper {

    // Entity -> DTO
    @Mapping(target = "beerOrderId", source = "beerOrder.id")
    @Mapping(target = "shipmentStatus", source = "shipmentStatus", qualifiedByName = "statusToString")
    BeerOrderShipmentDto toDto(BeerOrderShipment entity);

    // Create DTO -> Entity (beerOrder will be set in service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "beerOrder", ignore = true)
    @Mapping(target = "shipmentStatus", source = "shipmentStatus", qualifiedByName = "stringToStatus")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    BeerOrderShipment toEntity(BeerOrderShipmentCreateDto dto);

    // Update into existing entity; ignore nulls
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "beerOrder", ignore = true)
    @Mapping(target = "shipmentStatus", source = "shipmentStatus", qualifiedByName = "stringToStatus")
    void updateEntity(@MappingTarget BeerOrderShipment entity, BeerOrderShipmentUpdateDto dto);

    @Named("statusToString")
    default String statusToString(ShipmentStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    default ShipmentStatus stringToStatus(String value) {
        if (value == null || value.isBlank()) return null;
        return ShipmentStatus.valueOf(value);
    }
}
