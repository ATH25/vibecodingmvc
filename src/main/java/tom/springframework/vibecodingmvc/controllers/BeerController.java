package tom.springframework.vibecodingmvc.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tom.springframework.vibecodingmvc.models.BeerRequestDto;
import tom.springframework.vibecodingmvc.models.BeerResponseDto;
import tom.springframework.vibecodingmvc.services.BeerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beers")
class BeerController {

    private final BeerService beerService;

    BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping
    ResponseEntity<List<BeerResponseDto>> listBeers() {
        return ResponseEntity.ok(beerService.listBeers());
    }

    @GetMapping("/{beerId}")
    ResponseEntity<BeerResponseDto> getBeerById(@PathVariable("beerId") Integer beerId) {
        return beerService.getBeerById(beerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    ResponseEntity<BeerResponseDto> createBeer(@Valid @RequestBody BeerRequestDto dto) {
        BeerResponseDto created = beerService.saveBeer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{beerId}")
    ResponseEntity<BeerResponseDto> updateBeer(@PathVariable("beerId") Integer beerId,
                                               @Valid @RequestBody BeerRequestDto dto) {
        return beerService.updateBeer(beerId, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{beerId}")
    ResponseEntity<Void> deleteBeer(@PathVariable("beerId") Integer beerId) {
        // Attempt deletion only if present
        return beerService.getBeerById(beerId)
                .map(b -> {
                    beerService.deleteBeer(beerId);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}