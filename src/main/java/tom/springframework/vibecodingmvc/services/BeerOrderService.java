package tom.springframework.vibecodingmvc.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tom.springframework.vibecodingmvc.models.BeerOrderResponse;
import tom.springframework.vibecodingmvc.models.BeerOrderSummaryResponse;
import tom.springframework.vibecodingmvc.models.CreateBeerOrderCommand;

import java.util.Optional;

public interface BeerOrderService {

    int createOrder(CreateBeerOrderCommand cmd);

    Optional<BeerOrderResponse> getOrder(Integer id);

    Page<BeerOrderSummaryResponse> listOrders(Pageable pageable);
}
