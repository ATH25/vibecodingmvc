package tom.springframework.vibecodingmvc.services;

import org.springframework.stereotype.Service;
import tom.springframework.vibecodingmvc.entities.Beer;
import tom.springframework.vibecodingmvc.mappers.BeerMapper;
import tom.springframework.vibecodingmvc.models.BeerRequestDto;
import tom.springframework.vibecodingmvc.models.BeerResponseDto;
import tom.springframework.vibecodingmvc.repositories.BeerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BeerServiceImpl implements BeerService {
    
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    public BeerServiceImpl(BeerRepository beerRepository, BeerMapper beerMapper) {
        this.beerRepository = beerRepository;
        this.beerMapper = beerMapper;
    }

    @Override
    public List<BeerResponseDto> listBeers() {
        return beerRepository.findAll().stream()
                .map(beerMapper::toResponseDto)
                .toList();
    }

    @Override
    public Optional<BeerResponseDto> getBeerById(Integer id) {
        return beerRepository.findById(id).map(beerMapper::toResponseDto);
    }

    @Override
    public BeerResponseDto saveBeer(BeerRequestDto dto) {
        Beer toSave = beerMapper.toEntity(dto);
        Beer saved = beerRepository.save(toSave);
        return beerMapper.toResponseDto(saved);
    }

    @Override
    public Optional<BeerResponseDto> updateBeer(Integer id, BeerRequestDto dto) {
        return beerRepository.findById(id)
                .map(existing -> {
                    beerMapper.updateEntityFromDto(dto, existing);
                    Beer saved = beerRepository.save(existing);
                    return beerMapper.toResponseDto(saved);
                });
    }

    @Override
    public void deleteBeer(Integer id) {
        beerRepository.deleteById(id);
    }
}