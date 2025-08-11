package tom.springframework.vibecodingmvc.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BeerResponseDto(
        Integer id,
        Integer version,
        String beerName,
        String beerStyle,
        String upc,
        Integer quantityOnHand,
        BigDecimal price,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {}
