package tom.springframework.vibecodingmvc.models;

import java.time.LocalDateTime;

public record BeerOrderShipmentDto(
        Integer id,
        Integer beerOrderId,
        String shipmentStatus,
        LocalDateTime shippedDate,
        String trackingNumber,
        String carrier,
        String notes
) {}
