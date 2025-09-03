package tom.springframework.vibecodingmvc.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BeerRequestDto", description = "Request payload to create or update a beer")
public record BeerRequestDto(
        @NotBlank
        @Schema(description = "Name of the beer", example = "Galaxy Cat IPA")
        String beerName,

        // style of the beer, ALE, PALE ALE, IPA, etc
        @NotBlank
        @Schema(description = "Style of the beer (e.g., ALE, PALE_ALE, IPA)", example = "IPA")
        String beerStyle,

        // Universal Product Code, a 13-digit number assigned to each unique beer product by the Federal Bar Association
        @NotBlank
        @Schema(description = "Universal Product Code (13-digit)", example = "0123456789012")
        String upc,

        @PositiveOrZero
        @Schema(description = "Quantity on hand (must be zero or positive)", example = "120", minimum = "0")
        Integer quantityOnHand,

        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(description = "Price per unit (must be greater than 0)", example = "12.99", minimum = "0.01")
        BigDecimal price
) {}
