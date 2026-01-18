package tom.springframework.vibecodingmvc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tom.springframework.vibecodingmvc.entities.BeerOrder;
import tom.springframework.vibecodingmvc.entities.BeerOrderLine;
import tom.springframework.vibecodingmvc.models.BeerOrderLineResponse;
import tom.springframework.vibecodingmvc.models.BeerOrderResponse;
import tom.springframework.vibecodingmvc.models.BeerOrderSummaryResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BeerOrderMapper {

    @Mapping(target = "lines", source = "lines")
    BeerOrderResponse toResponse(BeerOrder order);

    BeerOrderSummaryResponse toSummaryResponse(BeerOrder order);

    @Mapping(target = "beerId", source = "beer.id")
    @Mapping(target = "beerName", source = "beer.beerName")
    BeerOrderLineResponse toLineResponse(BeerOrderLine line);

    List<BeerOrderLineResponse> toLineResponses(List<BeerOrderLine> lines);
}
