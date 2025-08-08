package tom.springframework.vibecodingmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tom.springframework.vibecodingmvc.entities.Beer;
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

    List<Beer> beers;
    Beer testBeer;

    @BeforeEach
    void setUp() {
        beers = new ArrayList<>();
        
        testBeer = Beer.builder()
                .id(1)
                .version(1)
                .beerName("Test Beer")
                .beerStyle("IPA")
                .upc("123456")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(123)
                .build();
        
        beers.add(testBeer);
        
        Beer testBeer2 = Beer.builder()
                .id(2)
                .version(1)
                .beerName("Test Beer 2")
                .beerStyle("Lager")
                .upc("654321")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(65)
                .build();
        
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
        Beer newBeer = Beer.builder()
                .beerName("New Beer")
                .beerStyle("Pale Ale")
                .upc("987654")
                .price(new BigDecimal("10.99"))
                .quantityOnHand(100)
                .build();
        
        Beer savedBeer = Beer.builder()
                .id(3)
                .version(1)
                .beerName("New Beer")
                .beerStyle("Pale Ale")
                .upc("987654")
                .price(new BigDecimal("10.99"))
                .quantityOnHand(100)
                .build();

        given(beerService.saveBeer(any(Beer.class))).willReturn(savedBeer);

        mockMvc.perform(post("/api/v1/beers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBeer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.beerName", is("New Beer")));
    }

    @Test
    void testUpdateBeer() throws Exception {
        Beer updatedBeer = Beer.builder()
                .beerName("Updated Beer")
                .beerStyle("Stout")
                .upc("654321")
                .price(new BigDecimal("14.99"))
                .quantityOnHand(200)
                .build();
        
        Beer savedBeer = Beer.builder()
                .id(1)
                .version(2)
                .beerName("Updated Beer")
                .beerStyle("Stout")
                .upc("654321")
                .price(new BigDecimal("14.99"))
                .quantityOnHand(200)
                .build();

        given(beerService.updateBeer(eq(1), any(Beer.class))).willReturn(savedBeer);

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
        Beer updatedBeer = Beer.builder()
                .beerName("Updated Beer")
                .beerStyle("Stout")
                .upc("654321")
                .price(new BigDecimal("14.99"))
                .quantityOnHand(200)
                .build();

        given(beerService.updateBeer(eq(999), any(Beer.class))).willReturn(null);

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