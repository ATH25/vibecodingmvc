package tom.springframework.vibecodingmvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tom.springframework.vibecodingmvc.entities.Beer;
import tom.springframework.vibecodingmvc.services.BeerService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/beers")
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping
    public List<Beer> listBeers() {
        return beerService.listBeers();
    }

    @GetMapping("/{beerId}")
    public ResponseEntity<Beer> getBeerById(@PathVariable("beerId") Integer beerId) {
        Optional<Beer> beerOptional = beerService.getBeerById(beerId);
        
        return beerOptional
                .map(beer -> new ResponseEntity<>(beer, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Beer createBeer(@RequestBody Beer beer) {
        return beerService.saveBeer(beer);
    }
    
    @PutMapping("/{beerId}")
    public ResponseEntity<Beer> updateBeer(@PathVariable("beerId") Integer beerId, @RequestBody Beer beer) {
        Beer updatedBeer = beerService.updateBeer(beerId, beer);
        
        if (updatedBeer != null) {
            return new ResponseEntity<>(updatedBeer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{beerId}")
    public ResponseEntity<Void> deleteBeer(@PathVariable("beerId") Integer beerId) {
        Optional<Beer> beer = beerService.getBeerById(beerId);
        
        if (beer.isPresent()) {
            beerService.deleteBeer(beerId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}