package tom.springframework.vibecodingmvc.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record BeerOrderShipmentCreateDto(
        @NotNull @Positive Integer beerOrderId,
        String shipmentStatus,
        LocalDateTime shippedDate,
        String trackingNumber,
        String carrier,
        String notes
) {}
