package tom.springframework.vibecodingmvc.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BeerResponseDto", description = "Response payload representing a beer")
public record BeerResponseDto(
        @Schema(description = "Unique identifier of the beer", example = "1")
        Integer id,

        @Schema(description = "Entity version for optimistic locking", example = "0", minimum = "0")
        Integer version,

        @Schema(description = "Name of the beer", example = "Galaxy Cat IPA")
        String beerName,

        @Schema(description = "Style of the beer (e.g., ALE, PALE_ALE, IPA)", example = "IPA")
        String beerStyle,

        @Schema(description = "Universal Product Code (13-digit)", example = "0123456789012")
        String upc,

        @Schema(description = "Quantity on hand", example = "120", minimum = "0")
        Integer quantityOnHand,

        @Schema(description = "Price per unit", example = "12.99", minimum = "0.01")
        BigDecimal price,

        @Schema(description = "Optional human-readable description of the beer", example = "A bright, citrus-forward IPA brewed with Galaxy hops")
        String description,

        @Schema(description = "Creation timestamp", type = "string", format = "date-time", example = "2025-08-01T12:34:56")
        LocalDateTime createdDate,

        @Schema(description = "Last update timestamp", type = "string", format = "date-time", example = "2025-08-15T09:00:00")
        LocalDateTime updatedDate
) {}
