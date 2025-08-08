package tom.springframework.vibecodingmvc.services;

import org.springframework.stereotype.Service;
import tom.springframework.vibecodingmvc.entities.Beer;
import tom.springframework.vibecodingmvc.repositories.BeerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BeerServiceImpl implements BeerService {
    
    private final BeerRepository beerRepository;

    public BeerServiceImpl(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    @Override
    public List<Beer> listBeers() {
        return beerRepository.findAll();
    }

    @Override
    public Optional<Beer> getBeerById(Integer id) {
        return beerRepository.findById(id);
    }

    @Override
    public Beer saveBeer(Beer beer) {
        return beerRepository.save(beer);
    }

    @Override
    public Beer updateBeer(Integer id, Beer beer) {
        Optional<Beer> existingBeer = beerRepository.findById(id);
        
        if (existingBeer.isPresent()) {
            beer.setId(id);
            return beerRepository.save(beer);
        }
        
        return null;
    }

    @Override
    public void deleteBeer(Integer id) {
        beerRepository.deleteById(id);
    }
}