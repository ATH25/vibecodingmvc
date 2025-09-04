package tom.springframework.vibecodingmvc.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Command to create a beer order")
public record CreateBeerOrderCommand(
        @Schema(description = "Client-provided reference for the order",
                example = "PO-2025-0001")
        String customerRef,

        @Schema(description = "Payment amount for the order",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "0",
                example = "24.99")
        @NotNull @PositiveOrZero BigDecimal paymentAmount,

        @ArraySchema(
                minItems = 1,
                schema = @Schema(implementation = CreateBeerOrderItem.class),
                arraySchema = @Schema(example = "[{\"beerId\":1,\"quantity\":2}]")
        )
        @Schema(description = "Order items",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Size(min = 1) List<@Valid CreateBeerOrderItem> items
) {}
