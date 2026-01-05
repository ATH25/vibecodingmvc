package tom.springframework.vibecodingmvc.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tom.springframework.vibecodingmvc.models.BeerRequestDto;
import tom.springframework.vibecodingmvc.models.BeerResponseDto;

import java.util.Optional;

public interface BeerService {
    Page<BeerResponseDto> listBeers(String beerName, Pageable pageable);
    Optional<BeerResponseDto> getBeerById(Integer id);
    BeerResponseDto saveBeer(BeerRequestDto dto);
    Optional<BeerResponseDto> updateBeer(Integer id, BeerRequestDto dto);
    void deleteBeer(Integer id);
}