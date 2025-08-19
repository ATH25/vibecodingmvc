package tom.springframework.vibecodingmvc.services;

import tom.springframework.vibecodingmvc.models.BeerOrderResponse;
import tom.springframework.vibecodingmvc.models.CreateBeerOrderCommand;

import java.util.Optional;

public interface BeerOrderService {

    int createOrder(CreateBeerOrderCommand cmd);

    Optional<BeerOrderResponse> getOrder(Integer id);
}
