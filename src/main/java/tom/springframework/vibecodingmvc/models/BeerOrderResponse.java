package tom.springframework.vibecodingmvc.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Beer order details")
public record BeerOrderResponse(
        @Schema(description = "Order identifier", example = "42")
        Integer id,

        @Schema(description = "Client-provided reference for the order", example = "PO-2025-0001")
        String customerRef,

        @Schema(description = "Payment amount", example = "24.99")
        BigDecimal paymentAmount,

        @Schema(description = "Current status of the order",
                allowableValues = {"PENDING","PAID","CANCELLED"},
                example = "PENDING")
        String status,

        @ArraySchema(
                schema = @Schema(implementation = BeerOrderLineResponse.class),
                arraySchema = @Schema(example = "[{\"id\":10,\"beerId\":1,\"beerName\":\"Galaxy Cat IPA\",\"orderQuantity\":2,\"quantityAllocated\":0,\"status\":\"PENDING\"}]")
        )
        List<BeerOrderLineResponse> lines,

        @Schema(type = "string", format = "date-time", example = "2025-08-20T14:13:00Z")
        LocalDateTime createdDate,

        @Schema(type = "string", format = "date-time", example = "2025-08-20T14:13:00Z")
        LocalDateTime updatedDate
) {}
