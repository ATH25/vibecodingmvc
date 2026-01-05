package tom.springframework.vibecodingmvc.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import tom.springframework.vibecodingmvc.entities.ShipmentStatus;

public record BeerOrderShipmentCreateDto(
        @NotNull @Positive Integer beerOrderId,
        @NotNull ShipmentStatus shipmentStatus,
        LocalDateTime shippedDate,
        String trackingNumber,
        String carrier,
        String notes
) {}
