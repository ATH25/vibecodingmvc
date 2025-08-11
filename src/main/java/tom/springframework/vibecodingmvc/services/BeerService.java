package tom.springframework.vibecodingmvc.services;

import tom.springframework.vibecodingmvc.models.BeerRequestDto;
import tom.springframework.vibecodingmvc.models.BeerResponseDto;

import java.util.List;
import java.util.Optional;

public interface BeerService {
    List<BeerResponseDto> listBeers();
    Optional<BeerResponseDto> getBeerById(Integer id);
    BeerResponseDto saveBeer(BeerRequestDto dto);
    Optional<BeerResponseDto> updateBeer(Integer id, BeerRequestDto dto);
    void deleteBeer(Integer id);
}