package tom.springframework.vibecodingmvc.services.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tom.springframework.vibecodingmvc.entities.BeerOrder;
import tom.springframework.vibecodingmvc.entities.BeerOrderShipment;
import tom.springframework.vibecodingmvc.entities.ShipmentStatus;
import tom.springframework.vibecodingmvc.mappers.BeerOrderShipmentMapper;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentCreateDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentUpdateDto;
import tom.springframework.vibecodingmvc.repositories.BeerOrderRepository;
import tom.springframework.vibecodingmvc.repositories.BeerOrderShipmentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BeerOrderShipmentServiceImplTest {

    @Mock
    private BeerOrderShipmentRepository shipmentRepository;
    @Mock
    private BeerOrderRepository orderRepository;
    @Mock
    private BeerOrderShipmentMapper mapper;

    @InjectMocks
    private BeerOrderShipmentServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldValidateOrder_DefaultStatus_ApplyRules_AndReturnId() {
        BeerOrderShipmentCreateDto createDto = new BeerOrderShipmentCreateDto(10, null, null, "TN", "UPS", null);
        BeerOrder order = BeerOrder.builder().id(10).build();
        when(orderRepository.findById(10)).thenReturn(Optional.of(order));

        BeerOrderShipment mapped = new BeerOrderShipment();
        when(mapper.toEntity(createDto)).thenReturn(mapped);

        // After rules, repository saves and assigns id
        BeerOrderShipment saved = new BeerOrderShipment();
        saved.setId(123);
        saved.setBeerOrder(order);
        saved.setShipmentStatus(ShipmentStatus.IN_TRANSIT); // final state could be anything; not strictly checked here
        when(shipmentRepository.save(any(BeerOrderShipment.class))).thenReturn(saved);

        Integer id = service.create(createDto);

        assertThat(id).isEqualTo(123);
        // Ensure beerOrder was set and default status applied when null
        ArgumentCaptor<BeerOrderShipment> captor = ArgumentCaptor.forClass(BeerOrderShipment.class);
        verify(shipmentRepository).save(captor.capture());
        BeerOrderShipment toSave = captor.getValue();
        assertThat(toSave.getBeerOrder()).isEqualTo(order);
        assertThat(toSave.getShipmentStatus()).isNotNull();
    }

    @Test
    void create_shouldThrowWhenOrderNotFound() {
        BeerOrderShipmentCreateDto createDto = new BeerOrderShipmentCreateDto(99, null, null, null, null, null);
        when(orderRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(createDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("BeerOrder not found");

        verifyNoInteractions(mapper);
        verify(shipmentRepository, never()).save(any());
    }

    @Test
    void get_shouldReturnMappedDtoWhenFound() {
        BeerOrderShipment entity = new BeerOrderShipment();
        entity.setId(5);
        BeerOrderShipmentDto dto = new BeerOrderShipmentDto(5, 10, "PENDING", null, null, null, null);

        when(shipmentRepository.findById(5)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        Optional<BeerOrderShipmentDto> result = service.get(5);
        assertThat(result).contains(dto);
    }

    @Test
    void get_shouldReturnEmptyWhenNotFound() {
        when(shipmentRepository.findById(77)).thenReturn(Optional.empty());
        assertThat(service.get(77)).isEmpty();
    }

    @Test
    void listByBeerOrderId_shouldMapAll() {
        BeerOrderShipment e1 = new BeerOrderShipment(); e1.setId(1);
        BeerOrderShipment e2 = new BeerOrderShipment(); e2.setId(2);
        when(shipmentRepository.findByBeerOrder_Id(44)).thenReturn(List.of(e1, e2));
        BeerOrderShipmentDto d1 = new BeerOrderShipmentDto(1, 44, "PENDING", null, null, null, null);
        BeerOrderShipmentDto d2 = new BeerOrderShipmentDto(2, 44, "PACKED", null, null, null, null);
        when(mapper.toDto(e1)).thenReturn(d1);
        when(mapper.toDto(e2)).thenReturn(d2);

        List<BeerOrderShipmentDto> list = service.listByBeerOrderId(44);
        assertThat(list).containsExactly(d1, d2);
    }

    @Test
    void update_shouldApplyMapperAndBusinessRulesAndSave() {
        Integer id = 7;
        BeerOrderShipment existing = new BeerOrderShipment();
        existing.setId(id);
        existing.setShipmentStatus(ShipmentStatus.PENDING);
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(existing));

        BeerOrderShipmentUpdateDto updateDto = new BeerOrderShipmentUpdateDto("IN_TRANSIT", null, "TN-1", "DHL", "note");

        // Have mapper update the entity in-place (simulate MapStruct behavior)
        doAnswer(invocation -> {
            BeerOrderShipment target = invocation.getArgument(0);
            BeerOrderShipmentUpdateDto dto = invocation.getArgument(1);
            target.setShipmentStatus(ShipmentStatus.valueOf(dto.shipmentStatus()));
            target.setTrackingNumber(dto.trackingNumber());
            target.setCarrier(dto.carrier());
            target.setNotes(dto.notes());
            // shippedDate remains null to let service set it when moving to IN_TRANSIT
            return null;
        }).when(mapper).updateEntity(any(BeerOrderShipment.class), any(BeerOrderShipmentUpdateDto.class));

        when(shipmentRepository.save(existing)).thenReturn(existing);

        service.update(id, updateDto);

        // After IN_TRANSIT without shippedDate, service should set shippedDate automatically
        assertThat(existing.getShipmentStatus()).isEqualTo(ShipmentStatus.IN_TRANSIT);
        assertThat(existing.getShippedDate()).isNotNull();
        assertThat(existing.getTrackingNumber()).isEqualTo("TN-1");
        assertThat(existing.getCarrier()).isEqualTo("DHL");
        verify(shipmentRepository).save(existing);
    }

    @Test
    void update_shouldThrowWhenRulesViolated_InTransitWithoutTrackingOrCarrier() {
        Integer id = 8;
        BeerOrderShipment existing = new BeerOrderShipment();
        existing.setId(id);
        existing.setShipmentStatus(ShipmentStatus.PENDING);
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(existing));

        BeerOrderShipmentUpdateDto updateDto = new BeerOrderShipmentUpdateDto("IN_TRANSIT", null, null, null, null);

        doAnswer(invocation -> {
            BeerOrderShipment target = invocation.getArgument(0);
            BeerOrderShipmentUpdateDto dto = invocation.getArgument(1);
            target.setShipmentStatus(ShipmentStatus.valueOf(dto.shipmentStatus()));
            return null;
        }).when(mapper).updateEntity(any(BeerOrderShipment.class), any(BeerOrderShipmentUpdateDto.class));

        assertThatThrownBy(() -> service.update(id, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trackingNumber and carrier are required");

        verify(shipmentRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteWhenExists() {
        when(shipmentRepository.existsById(3)).thenReturn(true);
        service.delete(3);
        verify(shipmentRepository).deleteById(3);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(shipmentRepository.existsById(404)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(404))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Shipment not found");
        verify(shipmentRepository, never()).deleteById(any());
    }
}
