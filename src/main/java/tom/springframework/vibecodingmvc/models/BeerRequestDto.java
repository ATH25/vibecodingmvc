package tom.springframework.vibecodingmvc.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record BeerRequestDto(
        @NotBlank String beerName,
        // style of the beer, ALE, PALE ALE, IPA, etc
        @NotBlank String beerStyle,
        // Universal Product Code, a 13-digit number assigned to each unique beer product by the Federal Bar Association
        @NotBlank String upc,
        @PositiveOrZero Integer quantityOnHand,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal price
) {}
