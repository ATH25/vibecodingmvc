package tom.springframework.vibecodingmvc.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tom.springframework.vibecodingmvc.models.BeerOrderResponse;
import tom.springframework.vibecodingmvc.models.BeerOrderSummaryResponse;
import tom.springframework.vibecodingmvc.services.BeerOrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BeerOrderControllerTest {

    MockMvc mockMvc;

    @Mock
    BeerOrderService beerOrderService;

    @InjectMocks
    BeerOrderController beerOrderController;

    List<BeerOrderSummaryResponse> orderSummaries;
    BeerOrderResponse testOrder;

    @BeforeEach
    void setUp() {
        orderSummaries = new ArrayList<>();
        testOrder = new BeerOrderResponse(
                1,
                "PO-1",
                new BigDecimal("25.00"),
                "NEW",
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        orderSummaries.add(new BeerOrderSummaryResponse(
                1,
                "PO-1",
                new BigDecimal("25.00"),
                "NEW",
                LocalDateTime.now()
        ));

        mockMvc = MockMvcBuilders
                .standaloneSetup(beerOrderController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void listOrders_returnsPaged() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<BeerOrderSummaryResponse> page = new PageImpl<>(orderSummaries, pageRequest, orderSummaries.size());
        given(beerOrderService.listOrders(any())).willReturn(page);

        mockMvc.perform(get("/api/v1/beer-orders")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].customerRef", is("PO-1")));
    }

    @Test
    void listOrdersAlias_returnsPaged() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<BeerOrderSummaryResponse> page = new PageImpl<>(orderSummaries, pageRequest, orderSummaries.size());
        given(beerOrderService.listOrders(any())).willReturn(page);

        mockMvc.perform(get("/api/v1/beer-orders/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getOrderById() throws Exception {
        given(beerOrderService.getOrder(any())).willReturn(Optional.of(testOrder));

        mockMvc.perform(get("/api/v1/beer-orders/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));
    }
}
