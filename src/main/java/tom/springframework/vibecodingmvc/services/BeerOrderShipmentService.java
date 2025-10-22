package tom.springframework.vibecodingmvc.services;

import tom.springframework.vibecodingmvc.models.BeerOrderShipmentCreateDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentUpdateDto;

import java.util.List;
import java.util.Optional;

public interface BeerOrderShipmentService {

    Integer create(BeerOrderShipmentCreateDto dto);

    Optional<BeerOrderShipmentDto> get(Integer id);

    List<BeerOrderShipmentDto> listByBeerOrderId(Integer beerOrderId);

    void update(Integer id, BeerOrderShipmentUpdateDto dto);

    void delete(Integer id);
}
