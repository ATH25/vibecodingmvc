package tom.springframework.vibecodingmvc.services.impl;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tom.springframework.vibecodingmvc.entities.BeerOrder;
import tom.springframework.vibecodingmvc.entities.BeerOrderShipment;
import tom.springframework.vibecodingmvc.entities.ShipmentStatus;
import tom.springframework.vibecodingmvc.mappers.BeerOrderShipmentMapper;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentCreateDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentUpdateDto;
import tom.springframework.vibecodingmvc.repositories.BeerOrderRepository;
import tom.springframework.vibecodingmvc.repositories.BeerOrderShipmentRepository;
import tom.springframework.vibecodingmvc.services.BeerOrderShipmentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
class BeerOrderShipmentServiceImpl implements BeerOrderShipmentService {

    private static final Logger log = LoggerFactory.getLogger(BeerOrderShipmentServiceImpl.class);

    private final BeerOrderShipmentRepository shipmentRepository;
    private final BeerOrderRepository orderRepository;
    private final BeerOrderShipmentMapper mapper;

    BeerOrderShipmentServiceImpl(BeerOrderShipmentRepository shipmentRepository,
                                 BeerOrderRepository orderRepository,
                                 BeerOrderShipmentMapper mapper) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Integer create(BeerOrderShipmentCreateDto dto) {
        // Validate beer order exists
        BeerOrder order = orderRepository.findById(dto.beerOrderId())
                .orElseThrow(() -> new EntityNotFoundException("BeerOrder not found: " + dto.beerOrderId()));

        BeerOrderShipment entity = mapper.toEntity(dto);
        entity.setBeerOrder(order);

        // Default status
        if (entity.getShipmentStatus() == null) {
            entity.setShipmentStatus(ShipmentStatus.PENDING);
        }

        applyBusinessRulesOnCreateOrUpdate(entity);

        BeerOrderShipment saved = shipmentRepository.save(entity);
        log.info("Created shipment id={} for orderId={} with status={}", saved.getId(), order.getId(), saved.getShipmentStatus());
        return saved.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BeerOrderShipmentDto> get(Integer id) {
        return shipmentRepository.findById(id).map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeerOrderShipmentDto> listByBeerOrderId(Integer beerOrderId) {
        return shipmentRepository.findByBeerOrder_Id(beerOrderId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean beerOrderExists(Integer beerOrderId) {
        return orderRepository.existsById(beerOrderId);
    }

    @Override
    @Transactional
    public void update(Integer id, BeerOrderShipmentUpdateDto dto) {
        BeerOrderShipment entity = shipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found: " + id));

        mapper.updateEntity(entity, dto);

        applyBusinessRulesOnCreateOrUpdate(entity);

        shipmentRepository.save(entity);
        log.info("Updated shipment id={} to status={}", id, entity.getShipmentStatus());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!shipmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Shipment not found: " + id);
        }
        shipmentRepository.deleteById(id);
        log.info("Deleted shipment id={}", id);
    }

    private void applyBusinessRulesOnCreateOrUpdate(BeerOrderShipment entity) {
        ShipmentStatus status = entity.getShipmentStatus();
        if (status == null) {
            entity.setShipmentStatus(ShipmentStatus.PENDING);
            status = ShipmentStatus.PENDING;
        }

        // If moving to IN_TRANSIT or later, require tracking and carrier
        if (status.ordinal() >= ShipmentStatus.IN_TRANSIT.ordinal()) {
            if (isBlank(entity.getTrackingNumber()) || isBlank(entity.getCarrier())) {
                throw new IllegalArgumentException("trackingNumber and carrier are required when status is IN_TRANSIT or later");
            }
            // set shippedDate if missing
            if (entity.getShippedDate() == null) {
                entity.setShippedDate(LocalDateTime.now());
            }
        }

        // If DELIVERED, ensure shippedDate
        if (status == ShipmentStatus.DELIVERED && entity.getShippedDate() == null) {
            entity.setShippedDate(LocalDateTime.now());
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
