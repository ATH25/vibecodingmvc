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
import tom.springframework.vibecodingmvc.models.CustomerRequestDto;
import tom.springframework.vibecodingmvc.models.CustomerResponseDto;
import tom.springframework.vibecodingmvc.services.CustomerService;

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
class CustomerControllerTest {

    MockMvc mockMvc;

    @Mock
    CustomerService customerService;

    @InjectMocks
    CustomerController customerController;

    ObjectMapper objectMapper = new ObjectMapper();

    List<CustomerResponseDto> customers;
    CustomerResponseDto testCustomer;

    @BeforeEach
    void setUp() {
        customers = new ArrayList<>();

        testCustomer = new CustomerResponseDto(
                1,
                0,
                "Jane Doe",
                "jane.doe@example.com",
                "+1-555-000-0000",
                "123 Main St",
                "Apt 4B",
                "Springfield",
                "IL",
                "62704",
                null,
                null
        );

        customers.add(testCustomer);

        mockMvc = MockMvcBuilders
                .standaloneSetup(customerController)
                .build();
    }

    @Test
    void testListCustomers() throws Exception {
        given(customerService.listCustomers()).willReturn(customers);

        mockMvc.perform(get("/api/v1/customers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Jane Doe")));
    }

    @Test
    void testGetCustomer() throws Exception {
        given(customerService.getCustomerById(1)).willReturn(Optional.of(testCustomer));

        mockMvc.perform(get("/api/v1/customers/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("jane.doe@example.com")));
    }

    @Test
    void testGetCustomerNotFound() throws Exception {
        given(customerService.getCustomerById(999)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/customers/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCustomer() throws Exception {
        CustomerRequestDto request = new CustomerRequestDto(
                "Jane Doe",
                "jane.doe@example.com",
                "+1-555-000-0000",
                "123 Main St",
                "Apt 4B",
                "Springfield",
                "IL",
                "62704"
        );

        CustomerResponseDto created = new CustomerResponseDto(
                10,
                0,
                request.name(),
                request.email(),
                request.phone(),
                request.addressLine1(),
                request.addressLine2(),
                request.city(),
                request.state(),
                request.postalCode(),
                null,
                null
        );

        given(customerService.createCustomer(any(CustomerRequestDto.class))).willReturn(created);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/customers/10"))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Jane Doe")));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        CustomerRequestDto request = new CustomerRequestDto(
                "Jane A. Doe",
                "jane.doe@example.com",
                "+1-555-000-0001",
                "456 Elm St",
                null,
                "Springfield",
                "IL",
                "62704"
        );

        CustomerResponseDto updated = new CustomerResponseDto(
                1,
                1,
                request.name(),
                request.email(),
                request.phone(),
                request.addressLine1(),
                request.addressLine2(),
                request.city(),
                request.state(),
                request.postalCode(),
                null,
                null
        );

        given(customerService.updateCustomer(eq(1), any(CustomerRequestDto.class))).willReturn(Optional.of(updated));

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.version", is(1)))
                .andExpect(jsonPath("$.name", is("Jane A. Doe")));
    }

    @Test
    void testUpdateCustomerNotFound() throws Exception {
        CustomerRequestDto request = new CustomerRequestDto(
                "Jane A. Doe",
                "jane.doe@example.com",
                "+1-555-000-0001",
                "456 Elm St",
                null,
                "Springfield",
                "IL",
                "62704"
        );

        given(customerService.updateCustomer(eq(999), any(CustomerRequestDto.class))).willReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/customers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCustomer() throws Exception {
        given(customerService.deleteCustomer(1)).willReturn(true);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCustomerNotFound() throws Exception {
        given(customerService.deleteCustomer(999)).willReturn(false);

        mockMvc.perform(delete("/api/v1/customers/999"))
                .andExpect(status().isNotFound());
    }
}
