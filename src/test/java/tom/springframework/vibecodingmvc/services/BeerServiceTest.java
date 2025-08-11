package tom.springframework.vibecodingmvc.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mapstruct.factory.Mappers;
import tom.springframework.vibecodingmvc.entities.Beer;
import tom.springframework.vibecodingmvc.mappers.BeerMapper;
import tom.springframework.vibecodingmvc.models.BeerRequestDto;
import tom.springframework.vibecodingmvc.models.BeerResponseDto;
import tom.springframework.vibecodingmvc.repositories.BeerRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class BeerServiceTest {

    @Mock
    BeerRepository beerRepository;

    BeerService beerService;

    BeerMapper beerMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        beerMapper = Mappers.getMapper(BeerMapper.class);
        beerService = new BeerServiceImpl(beerRepository, beerMapper);
    }

    @Test
    void testListBeers() {
        // Given
        Beer beer1 = Beer.builder()
                .id(1)
                .beerName("Beer 1")
                .beerStyle("IPA")
                .upc("111111")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(100)
                .build();
        Beer beer2 = Beer.builder()
                .id(2)
                .beerName("Beer 2")
                .beerStyle("Stout")
                .upc("222222")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(200)
                .build();
        
        when(beerRepository.findAll()).thenReturn(Arrays.asList(beer1, beer2));

        // When
        List<BeerResponseDto> beers = beerService.listBeers();

        // Then
        assertThat(beers).hasSize(2);
        assertThat(beers.get(0).id()).isEqualTo(1);
        verify(beerRepository, times(1)).findAll();
    }

    @Test
    void testGetBeerById() {
        // Given
        Beer beer = Beer.builder()
                .id(1)
                .beerName("Test Beer")
                .beerStyle("IPA")
                .upc("123456")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(100)
                .build();
        
        when(beerRepository.findById(1)).thenReturn(Optional.of(beer));

        // When
        Optional<BeerResponseDto> beerOptional = beerService.getBeerById(1);

        // Then
        assertThat(beerOptional).isPresent();
        assertThat(beerOptional.get().beerName()).isEqualTo("Test Beer");
        verify(beerRepository, times(1)).findById(1);
    }

    @Test
    void testGetBeerByIdNotFound() {
        // Given
        when(beerRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When
        Optional<BeerResponseDto> beerOptional = beerService.getBeerById(999);

        // Then
        assertThat(beerOptional).isEmpty();
        verify(beerRepository, times(1)).findById(999);
    }

    @Test
    void testSaveBeer() {
        // Given
        BeerRequestDto dto = new BeerRequestDto(
                "New Beer",
                "Lager",
                "654321",
                50,
                new BigDecimal("9.99")
        );
        
        Beer savedBeer = Beer.builder()
                .id(1)
                .beerName("New Beer")
                .beerStyle("Lager")
                .upc("654321")
                .price(new BigDecimal("9.99"))
                .quantityOnHand(50)
                .build();
        
        when(beerRepository.save(any(Beer.class))).thenReturn(savedBeer);

        // When
        BeerResponseDto result = beerService.saveBeer(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1);
        verify(beerRepository, times(1)).save(any(Beer.class));
    }

    @Test
    void testUpdateBeerFound() {
        // Given
        Integer beerId = 1;
        
        Beer existingBeer = Beer.builder()
                .id(beerId)
                .beerName("Original Beer")
                .beerStyle("IPA")
                .upc("123456")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(100)
                .build();
        
        BeerRequestDto updatedDto = new BeerRequestDto(
                "Updated Beer",
                "Stout",
                "654321",
                200,
                new BigDecimal("14.99")
        );
        
        Beer savedBeer = Beer.builder()
                .id(beerId)
                .beerName("Updated Beer")
                .beerStyle("Stout")
                .upc("654321")
                .price(new BigDecimal("14.99"))
                .quantityOnHand(200)
                .build();
        
        when(beerRepository.findById(beerId)).thenReturn(Optional.of(existingBeer));
        when(beerRepository.save(any(Beer.class))).thenReturn(savedBeer);

        // When
        Optional<BeerResponseDto> result = beerService.updateBeer(beerId, updatedDto);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(beerId);
        assertThat(result.get().beerName()).isEqualTo("Updated Beer");
        verify(beerRepository, times(1)).findById(beerId);
        verify(beerRepository, times(1)).save(any(Beer.class));
    }

    @Test
    void testUpdateBeerNotFound() {
        // Given
        Integer beerId = 999;
        BeerRequestDto updatedDto = new BeerRequestDto(
                "Updated Beer",
                "Stout",
                "654321",
                200,
                new BigDecimal("14.99")
        );
        
        when(beerRepository.findById(beerId)).thenReturn(Optional.empty());

        // When
        Optional<BeerResponseDto> result = beerService.updateBeer(beerId, updatedDto);

        // Then
        assertThat(result).isEmpty();
        verify(beerRepository, times(1)).findById(beerId);
        verify(beerRepository, never()).save(any(Beer.class));
    }

    @Test
    void testDeleteBeer() {
        // Given
        Integer beerId = 1;
        
        // When
        beerService.deleteBeer(beerId);

        // Then
        verify(beerRepository, times(1)).deleteById(beerId);
    }
}