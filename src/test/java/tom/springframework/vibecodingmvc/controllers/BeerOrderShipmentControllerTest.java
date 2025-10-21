package tom.springframework.vibecodingmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentCreateDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentUpdateDto;
import tom.springframework.vibecodingmvc.services.BeerOrderShipmentService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BeerOrderShipmentController.class)
class BeerOrderShipmentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerOrderShipmentService service;

    @Test
    @DisplayName("POST create returns 201 with Location and body")
    void create_ok() throws Exception {
        BeerOrderShipmentCreateDto create = new BeerOrderShipmentCreateDto(11, "PENDING", null, null, null, null);
        String json = objectMapper.writeValueAsString(create);

        given(service.create(any(BeerOrderShipmentCreateDto.class))).willReturn(100);
        BeerOrderShipmentDto dto = new BeerOrderShipmentDto(100, 11, "PENDING", null, null, null, null);
        given(service.get(100)).willReturn(Optional.of(dto));

        mockMvc.perform(post("/api/v1/beerorder-shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/beerorder-shipments/100")))
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.beerOrderId", is(11)))
                .andExpect(jsonPath("$.shipmentStatus", is("PENDING")));
    }

    @Test
    @DisplayName("POST create with negative beerOrderId fails validation 400")
    void create_validationError() throws Exception {
        // negative beerOrderId should fail @Positive
        BeerOrderShipmentCreateDto create = new BeerOrderShipmentCreateDto(-1, null, null, null, null, null);
        String json = objectMapper.writeValueAsString(create);

        mockMvc.perform(post("/api/v1/beerorder-shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_found() throws Exception {
        BeerOrderShipmentDto dto = new BeerOrderShipmentDto(5, 11, "PENDING", null, null, null, null);
        given(service.get(5)).willReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/beerorder-shipments/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.beerOrderId", is(11)))
                .andExpect(jsonPath("$.shipmentStatus", is("PENDING")));
    }

    @Test
    void get_notFound() throws Exception {
        given(service.get(55)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/beerorder-shipments/{id}", 55))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_ok_and_validation() throws Exception {
        List<BeerOrderShipmentDto> list = List.of(
                new BeerOrderShipmentDto(1, 9, "PENDING", null, null, null, null),
                new BeerOrderShipmentDto(2, 9, "PACKED", null, null, null, null)
        );
        given(service.listByBeerOrderId(9)).willReturn(list);

        mockMvc.perform(get("/api/v1/beerorder-shipments").param("beerOrderId", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].shipmentStatus", is("PACKED")));

        mockMvc.perform(get("/api/v1/beerorder-shipments").param("beerOrderId", "-2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patch_update_noContent_and_errorCases() throws Exception {
        BeerOrderShipmentUpdateDto updateDto = new BeerOrderShipmentUpdateDto("IN_TRANSIT", null, "TN", "DHL", null);
        String json = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(patch("/api/v1/beerorder-shipments/{id}", 77)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        // 404 case
        doThrow(new EntityNotFoundException("Shipment not found: 404")).when(service).update(eq(404), any());
        mockMvc.perform(patch("/api/v1/beerorder-shipments/{id}", 404)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_noContent_and_notFound() throws Exception {
        mockMvc.perform(delete("/api/v1/beerorder-shipments/{id}", 12))
                .andExpect(status().isNoContent());

        doThrow(new EntityNotFoundException("Shipment not found: 999")).when(service).delete(999);
        mockMvc.perform(delete("/api/v1/beerorder-shipments/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
