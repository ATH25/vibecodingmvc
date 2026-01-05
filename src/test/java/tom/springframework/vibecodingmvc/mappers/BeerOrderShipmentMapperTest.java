package tom.springframework.vibecodingmvc.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import tom.springframework.vibecodingmvc.entities.BeerOrder;
import tom.springframework.vibecodingmvc.entities.BeerOrderShipment;
import tom.springframework.vibecodingmvc.entities.ShipmentStatus;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentCreateDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentUpdateDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BeerOrderShipmentMapperTest {

    private BeerOrderShipmentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BeerOrderShipmentMapper.class);
    }

    @Test
    void toDto_shouldMapAllFieldsAndConvertEnumToString() {
        BeerOrder order = BeerOrder.builder().id(42).build();
        LocalDateTime shipped = LocalDateTime.now().minusDays(1);

        BeerOrderShipment entity = BeerOrderShipment.builder()
                .id(7)
                .version(1)
                .beerOrder(order)
                .shipmentStatus(ShipmentStatus.IN_TRANSIT)
                .shippedDate(shipped)
                .trackingNumber("1Z999AA10123456784")
                .carrier("UPS")
                .notes("Handle with care")
                .build();

        BeerOrderShipmentDto dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(7);
        assertThat(dto.beerOrderId()).isEqualTo(42);
        assertThat(dto.shipmentStatus()).isEqualTo("IN_TRANSIT");
        assertThat(dto.shippedDate()).isEqualTo(shipped);
        assertThat(dto.trackingNumber()).isEqualTo("1Z999AA10123456784");
        assertThat(dto.carrier()).isEqualTo("UPS");
        assertThat(dto.notes()).isEqualTo("Handle with care");
    }

    @Test
    void toEntity_shouldMapFromCreateDtoAndConvertStatusString() {
        LocalDateTime shipped = LocalDateTime.now();
        BeerOrderShipmentCreateDto createDto = new BeerOrderShipmentCreateDto(
                101,
                ShipmentStatus.PACKED,
                shipped,
                "TRACK-123",
                "FedEx",
                "Leave at back door"
        );

        BeerOrderShipment entity = mapper.toEntity(createDto);

        assertThat(entity).isNotNull();
        // id, version, beerOrder, audit fields are ignored/set by persistence layer
        assertThat(entity.getId()).isNull();
        assertThat(entity.getVersion()).isNull();
        assertThat(entity.getBeerOrder()).isNull();
        assertThat(entity.getShipmentStatus()).isEqualTo(ShipmentStatus.PACKED);
        assertThat(entity.getShippedDate()).isEqualTo(shipped);
        assertThat(entity.getTrackingNumber()).isEqualTo("TRACK-123");
        assertThat(entity.getCarrier()).isEqualTo("FedEx");
        assertThat(entity.getNotes()).isEqualTo("Leave at back door");
    }

    @Test
    void updateEntity_shouldIgnoreNullsAndMapNonNullsWithEnumConversion() {
        BeerOrderShipment entity = BeerOrderShipment.builder()
                .id(5)
                .version(0)
                .shipmentStatus(ShipmentStatus.PENDING)
                .trackingNumber(null)
                .carrier(null)
                .notes("initial")
                .build();

        // Provide some nulls to ensure they are ignored, and some values to be updated
        LocalDateTime newShippedDate = LocalDateTime.now();
        BeerOrderShipmentUpdateDto updateDto = new BeerOrderShipmentUpdateDto(
                "IN_TRANSIT", // should update enum
                newShippedDate, // should set
                null, // should remain null on entity
                "DHL", // should update
                null // should keep existing notes
        );

        mapper.updateEntity(entity, updateDto);

        assertThat(entity.getShipmentStatus()).isEqualTo(ShipmentStatus.IN_TRANSIT);
        assertThat(entity.getShippedDate()).isEqualTo(newShippedDate);
        assertThat(entity.getTrackingNumber()).isNull(); // unchanged because null in DTO
        assertThat(entity.getCarrier()).isEqualTo("DHL");
        assertThat(entity.getNotes()).isEqualTo("initial"); // unchanged because null in DTO
    }
}
