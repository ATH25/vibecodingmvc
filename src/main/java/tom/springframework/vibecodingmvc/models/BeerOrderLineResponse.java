package tom.springframework.vibecodingmvc.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Beer order line details")
public record BeerOrderLineResponse(
        @Schema(description = "Line identifier", example = "10")
        Integer id,

        @Schema(description = "Beer identifier", example = "1")
        Integer beerId,

        @Schema(description = "Name of the beer", example = "Galaxy Cat IPA")
        String beerName,

        @Schema(description = "Quantity ordered", minimum = "1", example = "2")
        Integer orderQuantity,

        @Schema(description = "Quantity allocated", minimum = "0", example = "0")
        Integer quantityAllocated,

        @Schema(description = "Line status",
                allowableValues = {"PENDING","ALLOCATED","BACKORDERED","SHIPPED"},
                example = "PENDING")
        String status
) {}
