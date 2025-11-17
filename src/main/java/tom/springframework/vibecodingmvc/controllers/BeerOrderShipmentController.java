package tom.springframework.vibecodingmvc.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentCreateDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentUpdateDto;
import tom.springframework.vibecodingmvc.services.BeerOrderShipmentService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/beerorders/{beerOrderId}/shipments")
@Validated
class BeerOrderShipmentController {

    private final BeerOrderShipmentService service;

    BeerOrderShipmentController(BeerOrderShipmentService service) {
        this.service = service;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    ResponseEntity<BeerOrderShipmentDto> create(@PathVariable @Positive Integer beerOrderId,
                                                @Valid @RequestBody BeerOrderShipmentCreateDto dto,
                                                UriComponentsBuilder uriBuilder) {
        // Ensure BeerOrder exists
        if (!service.beerOrderExists(beerOrderId)) {
            return ResponseEntity.notFound().build();
        }
        // Ensure path variable and payload are consistent
        if (dto.beerOrderId() != null && !dto.beerOrderId().equals(beerOrderId)) {
            return ResponseEntity.badRequest().build();
        }
        Integer id = service.create(dto);
        URI location = uriBuilder
                .path("/api/v1/beerorders/{beerOrderId}/shipments/{id}")
                .buildAndExpand(beerOrderId, id)
                .toUri();
        Optional<BeerOrderShipmentDto> body = service.get(id);
        return body.map(b -> ResponseEntity.created(location).body(b))
                .orElseGet(() -> ResponseEntity.created(location).build());
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    ResponseEntity<BeerOrderShipmentDto> get(@PathVariable @Positive Integer beerOrderId,
                                             @PathVariable Integer id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(produces = "application/json")
    ResponseEntity<List<BeerOrderShipmentDto>> listByOrder(@PathVariable @Positive Integer beerOrderId) {
        // Return 404 if the BeerOrder does not exist
        if (!service.beerOrderExists(beerOrderId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.listByBeerOrderId(beerOrderId));
    }

    @PatchMapping(value = "/{id}", consumes = "application/json")
    ResponseEntity<Void> update(@PathVariable @Positive Integer beerOrderId,
                                @PathVariable Integer id,
                                @Valid @RequestBody BeerOrderShipmentUpdateDto dto) {
        service.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable @Positive Integer beerOrderId,
                                @PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
