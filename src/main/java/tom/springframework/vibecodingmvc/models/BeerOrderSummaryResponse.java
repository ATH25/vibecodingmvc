package tom.springframework.vibecodingmvc.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Summary of a beer order")
public record BeerOrderSummaryResponse(
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

        @Schema(type = "string", format = "date-time", example = "2025-08-20T14:13:00Z")
        LocalDateTime createdDate
) {}
