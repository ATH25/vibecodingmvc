package tom.springframework.vibecodingmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentCreateDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentDto;
import tom.springframework.vibecodingmvc.models.BeerOrderShipmentUpdateDto;
import tom.springframework.vibecodingmvc.services.BeerOrderShipmentService;

import tom.springframework.vibecodingmvc.entities.ShipmentStatus;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BeerOrderShipmentController.class)
class BeerOrderShipmentControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        BeerOrderShipmentService beerOrderShipmentService() {
            return org.mockito.Mockito.mock(BeerOrderShipmentService.class);
        }
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BeerOrderShipmentService service;

    @Test
    @DisplayName("POST create returns 201 with Location")
    void create_ok() throws Exception {
        int beerOrderId = 11;
        BeerOrderShipmentCreateDto create = new BeerOrderShipmentCreateDto(beerOrderId, ShipmentStatus.PENDING, null, null, null, null);
        String json = objectMapper.writeValueAsString(create);
        given(service.beerOrderExists(beerOrderId)).willReturn(true);
        given(service.create(any(BeerOrderShipmentCreateDto.class))).willReturn(100);

        mockMvc.perform(post("/api/v1/beerorders/{beerOrderId}/shipments", beerOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/beerorders/" + beerOrderId + "/shipments/100")));
    }

    @Test
    @DisplayName("GET list returns items")
    void list_ok() throws Exception {
        int beerOrderId = 9;
        List<BeerOrderShipmentDto> list = List.of(
                new BeerOrderShipmentDto(1, beerOrderId, "PENDING", null, null, null, null),
                new BeerOrderShipmentDto(2, beerOrderId, "PACKED", null, null, null, null)
        );
        given(service.beerOrderExists(beerOrderId)).willReturn(true);
        given(service.listByBeerOrderId(beerOrderId)).willReturn(list);

        mockMvc.perform(get("/api/v1/beerorders/{beerOrderId}/shipments", beerOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].shipmentStatus", is("PACKED")));
    }

    @Test
    @DisplayName("GET list returns 404 when beer order does not exist")
    void list_notFound_whenOrderMissing() throws Exception {
        int beerOrderId = 1234;
        given(service.beerOrderExists(beerOrderId)).willReturn(false);

        mockMvc.perform(get("/api/v1/beerorders/{beerOrderId}/shipments", beerOrderId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST create returns 404 when beer order does not exist")
    void create_notFound_whenOrderMissing() throws Exception {
        int beerOrderId = 55;
        BeerOrderShipmentCreateDto create = new BeerOrderShipmentCreateDto(beerOrderId, ShipmentStatus.PENDING, null, null, null, null);
        String json = objectMapper.writeValueAsString(create);
        given(service.beerOrderExists(beerOrderId)).willReturn(false);

        mockMvc.perform(post("/api/v1/beerorders/{beerOrderId}/shipments", beerOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET list with negative beerOrderId fails validation 400")
    void list_validationError() throws Exception {
        mockMvc.perform(get("/api/v1/beerorders/{beerOrderId}/shipments", -2))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patch_update_noContent_and_errorCases() throws Exception {
        int beerOrderId = 22;
        BeerOrderShipmentUpdateDto updateDto = new BeerOrderShipmentUpdateDto("IN_TRANSIT", null, "TN", "DHL", null);
        String json = objectMapper.writeValueAsString(updateDto);
        // Beer order exists for success case
        given(service.beerOrderExists(beerOrderId)).willReturn(true);

        mockMvc.perform(patch("/api/v1/beerorders/{beerOrderId}/shipments/{id}", beerOrderId, 77)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        // 404 shipment not found case (but order exists)
        given(service.beerOrderExists(beerOrderId)).willReturn(true);
        doThrow(new EntityNotFoundException("Shipment not found: 404")).when(service).update(eq(404), any(BeerOrderShipmentUpdateDto.class));
        mockMvc.perform(patch("/api/v1/beerorders/{beerOrderId}/shipments/{id}", beerOrderId, 404)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());

        // 404 when beer order does not exist
        given(service.beerOrderExists(9999)).willReturn(false);
        mockMvc.perform(patch("/api/v1/beerorders/{beerOrderId}/shipments/{id}", 9999, 77)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_noContent_and_notFound() throws Exception {
        int beerOrderId = 33;
        given(service.beerOrderExists(beerOrderId)).willReturn(true);
        mockMvc.perform(delete("/api/v1/beerorders/{beerOrderId}/shipments/{id}", beerOrderId, 12))
                .andExpect(status().isNoContent());

        doThrow(new EntityNotFoundException("Shipment not found: 999")).when(service).delete(999);
        given(service.beerOrderExists(beerOrderId)).willReturn(true);
        mockMvc.perform(delete("/api/v1/beerorders/{beerOrderId}/shipments/{id}", beerOrderId, 999))
                .andExpect(status().isNotFound());

        // 404 when beer order does not exist
        given(service.beerOrderExists(4444)).willReturn(false);
        mockMvc.perform(delete("/api/v1/beerorders/{beerOrderId}/shipments/{id}", 4444, 12))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET by id returns item and 404 when order missing")
    void getById_ok_and_notFoundWhenOrderMissing() throws Exception {
        int beerOrderId = 77;
        int shipmentId = 888;
        BeerOrderShipmentDto dto = new BeerOrderShipmentDto(shipmentId, beerOrderId, "PENDING", null, null, null, null);

        // success
        given(service.beerOrderExists(beerOrderId)).willReturn(true);
        given(service.get(shipmentId)).willReturn(java.util.Optional.of(dto));
        mockMvc.perform(get("/api/v1/beerorders/{beerOrderId}/shipments/{id}", beerOrderId, shipmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(shipmentId)))
                .andExpect(jsonPath("$.beerOrderId", is(beerOrderId)))
                .andExpect(jsonPath("$.shipmentStatus", is("PENDING")));

        // order missing
        given(service.beerOrderExists(9999)).willReturn(false);
        mockMvc.perform(get("/api/v1/beerorders/{beerOrderId}/shipments/{id}", 9999, shipmentId))
                .andExpect(status().isNotFound());
    }
}
