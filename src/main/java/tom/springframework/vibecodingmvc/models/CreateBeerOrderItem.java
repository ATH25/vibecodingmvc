package tom.springframework.vibecodingmvc.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBeerOrderItem(
        @NotNull Integer beerId,
        @Positive int quantity
) {}
