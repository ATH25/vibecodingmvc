package tom.springframework.vibecodingmvc.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record BeerRequestDto(
        @NotBlank String beerName,
        @NotBlank String beerStyle,
        @NotBlank String upc,
        @PositiveOrZero Integer quantityOnHand,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal price
) {}
