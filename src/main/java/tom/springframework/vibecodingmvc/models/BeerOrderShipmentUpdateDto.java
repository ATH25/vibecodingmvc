package tom.springframework.vibecodingmvc.models;

import java.time.LocalDateTime;

public record BeerOrderShipmentUpdateDto(
        String shipmentStatus,
        LocalDateTime shippedDate,
        String trackingNumber,
        String carrier,
        String notes
) {}
