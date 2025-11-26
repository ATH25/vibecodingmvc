package tom.springframework.vibecodingmvc.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tom.springframework.vibecodingmvc.entities.Beer;
import tom.springframework.vibecodingmvc.mappers.BeerMapper;
import tom.springframework.vibecodingmvc.models.BeerRequestDto;
import tom.springframework.vibecodingmvc.models.BeerResponseDto;
import tom.springframework.vibecodingmvc.repositories.BeerRepository;

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
    public Page<BeerResponseDto> listBeers(String beerName, Pageable pageable) {
        Page<Beer> page = (StringUtils.hasText(beerName))
                ? beerRepository.findAllByBeerNameContainingIgnoreCase(beerName, pageable)
                : beerRepository.findAll(pageable);
        return page.map(beerMapper::toResponseDto);
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