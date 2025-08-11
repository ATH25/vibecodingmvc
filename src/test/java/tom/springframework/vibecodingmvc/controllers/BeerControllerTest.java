package tom.springframework.vibecodingmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tom.springframework.vibecodingmvc.models.BeerRequestDto;
import tom.springframework.vibecodingmvc.models.BeerResponseDto;
import tom.springframework.vibecodingmvc.services.BeerService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BeerControllerTest {

    MockMvc mockMvc;

    @Mock
    BeerService beerService;

    @InjectMocks
    BeerController beerController;

    ObjectMapper objectMapper = new ObjectMapper();

    List<BeerResponseDto> beers;
    BeerResponseDto testBeer;

    @BeforeEach
    void setUp() {
        beers = new ArrayList<>();
        
        testBeer = new BeerResponseDto(
                1,
                1,
                "Test Beer",
                "IPA",
                "123456",
                123,
                new BigDecimal("12.99"),
                null,
                null
        );
        
        beers.add(testBeer);
        
        BeerResponseDto testBeer2 = new BeerResponseDto(
                2,
                1,
                "Test Beer 2",
                "Lager",
                "654321",
                65,
                new BigDecimal("11.99"),
                null,
                null
        );
        
        beers.add(testBeer2);
        
        // Setup MockMvc
        mockMvc = MockMvcBuilders
                .standaloneSetup(beerController)
                .build();
    }

    @Test
    void testListBeers() throws Exception {
        given(beerService.listBeers()).willReturn(beers);

        mockMvc.perform(get("/api/v1/beers")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].beerName", is("Test Beer")));
    }

    @Test
    void testGetBeerById() throws Exception {
        given(beerService.getBeerById(1)).willReturn(Optional.of(testBeer));

        mockMvc.perform(get("/api/v1/beers/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.beerName", is("Test Beer")));
    }

    @Test
    void testGetBeerByIdNotFound() throws Exception {
        given(beerService.getBeerById(999)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/beers/999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateBeer() throws Exception {
        BeerRequestDto newBeer = new BeerRequestDto(
                "New Beer",
                "Pale Ale",
                "987654",
                100,
                new BigDecimal("10.99")
        );
        
        BeerResponseDto savedBeer = new BeerResponseDto(
                3,
                1,
                "New Beer",
                "Pale Ale",
                "987654",
                100,
                new BigDecimal("10.99"),
                null,
                null
        );

        given(beerService.saveBeer(any(BeerRequestDto.class))).willReturn(savedBeer);

        mockMvc.perform(post("/api/v1/beers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBeer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.beerName", is("New Beer")));
    }

    @Test
    void testUpdateBeer() throws Exception {
        BeerRequestDto updatedBeer = new BeerRequestDto(
                "Updated Beer",
                "Stout",
                "654321",
                200,
                new BigDecimal("14.99")
        );
        
        BeerResponseDto savedBeer = new BeerResponseDto(
                1,
                2,
                "Updated Beer",
                "Stout",
                "654321",
                200,
                new BigDecimal("14.99"),
                null,
                null
        );

        given(beerService.updateBeer(eq(1), any(BeerRequestDto.class))).willReturn(Optional.of(savedBeer));

        mockMvc.perform(put("/api/v1/beers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBeer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.beerName", is("Updated Beer")))
                .andExpect(jsonPath("$.beerStyle", is("Stout")));
    }

    @Test
    void testUpdateBeerNotFound() throws Exception {
        BeerRequestDto updatedBeer = new BeerRequestDto(
                "Updated Beer",
                "Stout",
                "654321",
                200,
                new BigDecimal("14.99")
        );

        given(beerService.updateBeer(eq(999), any(BeerRequestDto.class))).willReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/beers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBeer)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBeer() throws Exception {
        given(beerService.getBeerById(1)).willReturn(Optional.of(testBeer));

        mockMvc.perform(delete("/api/v1/beers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteBeerNotFound() throws Exception {
        given(beerService.getBeerById(999)).willReturn(Optional.empty());

        mockMvc.perform(delete("/api/v1/beers/999"))
                .andExpect(status().isNotFound());
    }
}