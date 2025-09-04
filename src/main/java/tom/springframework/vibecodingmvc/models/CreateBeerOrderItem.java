package tom.springframework.vibecodingmvc.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A single line item in a beer order")
public record CreateBeerOrderItem(
        @Schema(description = "Identifier of the beer to order",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "1")
        @NotNull Integer beerId,

        @Schema(description = "Quantity of the beer to order",
                minimum = "1",
                example = "2")
        @Positive int quantity
) {}
