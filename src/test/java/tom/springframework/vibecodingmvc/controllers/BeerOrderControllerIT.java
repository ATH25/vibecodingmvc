package tom.springframework.vibecodingmvc.controllers;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tom.springframework.vibecodingmvc.entities.Beer;
import tom.springframework.vibecodingmvc.entities.BeerOrder;
import tom.springframework.vibecodingmvc.entities.BeerOrderLine;
import tom.springframework.vibecodingmvc.repositories.BeerOrderRepository;
import tom.springframework.vibecodingmvc.repositories.BeerRepository;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BeerOrderControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @Transactional
    void testListOrders_WithLines_ShouldNotThrowLazyInitializationException() throws Exception {
        // Given
        Beer beer = Beer.builder()
                .beerName("Test Beer")
                .beerStyle("IPA")
                .price(new BigDecimal("5.00"))
                .upc("123456")
                .build();
        Beer savedBeer = beerRepository.saveAndFlush(beer);

        BeerOrder order = BeerOrder.builder()
                .customerRef("Test Order with Lines")
                .paymentAmount(new BigDecimal("10.00"))
                .status("NEW")
                .build();

        BeerOrderLine line = BeerOrderLine.builder()
                .beer(savedBeer)
                .orderQuantity(2)
                .quantityAllocated(0)
                .status("NEW")
                .build();
        order.addLine(line);

        beerOrderRepository.saveAndFlush(order);
        
        // Detach the order and its lines from the persistence context
        entityManager.clear();

        // When/Then
        mockMvc.perform(get("/api/v1/beer-orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customerRef").value("Test Order with Lines"));
    }

    @Test
    @Transactional
    void testListOrdersWithCreatedDateSort() throws Exception {
        // Given
        BeerOrder order = BeerOrder.builder()
                .customerRef("Test Order")
                .paymentAmount(new BigDecimal("10.00"))
                .status("NEW")
                .build();
        beerOrderRepository.saveAndFlush(order);

        // When/Then
        mockMvc.perform(get("/api/v1/beer-orders")
                        .param("sort", "createdDate,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testListOrdersWithCreatedAtSort_ShouldBeMappedToCreatedDate() throws Exception {
        // This test confirms that createdAt is safely mapped to createdDate in the service layer
        mockMvc.perform(get("/api/v1/beer-orders")
                        .param("sort", "createdAt,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
