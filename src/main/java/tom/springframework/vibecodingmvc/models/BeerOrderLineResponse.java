package tom.springframework.vibecodingmvc.models;

public record BeerOrderLineResponse(
        Integer id,
        Integer beerId,
        String beerName,
        Integer orderQuantity,
        Integer quantityAllocated,
        String status
) {}
