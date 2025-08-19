package tom.springframework.vibecodingmvc.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tom.springframework.vibecodingmvc.models.BeerOrderResponse;
import tom.springframework.vibecodingmvc.models.CreateBeerOrderCommand;
import tom.springframework.vibecodingmvc.services.BeerOrderService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/beer-orders")
@Validated
class BeerOrderController {

    private final BeerOrderService beerOrderService;

    BeerOrderController(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @PostMapping
    ResponseEntity<BeerOrderResponse> create(@Valid @RequestBody CreateBeerOrderCommand cmd,
                                             UriComponentsBuilder uriBuilder) {
        int id = beerOrderService.createOrder(cmd);
        URI location = uriBuilder.path("/api/v1/beer-orders/{id}").buildAndExpand(id).toUri();
        return beerOrderService.getOrder(id)
                .map(resp -> ResponseEntity.created(location).body(resp))
                .orElseGet(() -> ResponseEntity.created(location).build());
    }

    @GetMapping("/{id}")
    ResponseEntity<BeerOrderResponse> get(@PathVariable Integer id) {
        return beerOrderService.getOrder(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
