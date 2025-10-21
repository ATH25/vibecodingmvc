package tom.springframework.vibecodingmvc.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tom.springframework.vibecodingmvc.entities.Customer;
import tom.springframework.vibecodingmvc.mappers.CustomerMapper;
import tom.springframework.vibecodingmvc.models.CustomerRequestDto;
import tom.springframework.vibecodingmvc.models.CustomerResponseDto;
import tom.springframework.vibecodingmvc.repositories.CustomerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CustomerMapper customerMapper;

    @InjectMocks
    CustomerServiceImpl service;

    private CustomerRequestDto sampleRequest;
    private Customer sampleEntity;
    private CustomerResponseDto sampleResponse;

    @BeforeEach
    void setUp() {
        sampleRequest = new CustomerRequestDto(
                "Jane Doe",
                "jane@example.com",
                "+1-555-1212",
                "123 Main St",
                null,
                "Springfield",
                "IL",
                "62704"
        );

        sampleEntity = Customer.builder()
                .id(1)
                .version(0)
                .name(sampleRequest.name())
                .email(sampleRequest.email())
                .phone(sampleRequest.phone())
                .addressLine1(sampleRequest.addressLine1())
                .addressLine2(sampleRequest.addressLine2())
                .city(sampleRequest.city())
                .state(sampleRequest.state())
                .postalCode(sampleRequest.postalCode())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        sampleResponse = new CustomerResponseDto(
                sampleEntity.getId(),
                sampleEntity.getVersion(),
                sampleEntity.getName(),
                sampleEntity.getEmail(),
                sampleEntity.getPhone(),
                sampleEntity.getAddressLine1(),
                sampleEntity.getAddressLine2(),
                sampleEntity.getCity(),
                sampleEntity.getState(),
                sampleEntity.getPostalCode(),
                sampleEntity.getCreatedDate(),
                sampleEntity.getUpdatedDate()
        );
    }

    @Test
    void listCustomers_returnsMappedDtos() {
        // given
        Customer other = Customer.builder().id(2).name("John").email("john@example.com").addressLine1("addr").build();
        CustomerResponseDto resp1 = sampleResponse;
        CustomerResponseDto resp2 = new CustomerResponseDto(2, null, "John", "john@example.com", null, "addr", null, null, null, null, null, null);

        when(customerRepository.findAll()).thenReturn(List.of(sampleEntity, other));
        when(customerMapper.toResponseDto(sampleEntity)).thenReturn(resp1);
        when(customerMapper.toResponseDto(other)).thenReturn(resp2);

        // when
        List<CustomerResponseDto> result = service.listCustomers();

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(resp1));
        assertTrue(result.contains(resp2));
        verify(customerRepository).findAll();
        verify(customerMapper, times(2)).toResponseDto(any());
    }

    @Test
    void getCustomerById_whenPresent_returnsDto() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(sampleEntity));
        when(customerMapper.toResponseDto(sampleEntity)).thenReturn(sampleResponse);

        Optional<CustomerResponseDto> result = service.getCustomerById(1);

        assertTrue(result.isPresent());
        assertEquals(sampleResponse, result.get());
        verify(customerRepository).findById(1);
        verify(customerMapper).toResponseDto(sampleEntity);
    }

    @Test
    void getCustomerById_whenMissing_returnsEmpty() {
        when(customerRepository.findById(99)).thenReturn(Optional.empty());

        Optional<CustomerResponseDto> result = service.getCustomerById(99);

        assertTrue(result.isEmpty());
        verify(customerRepository).findById(99);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void createCustomer_whenDuplicateEmail_throwsIllegalArgumentException() {
        when(customerRepository.existsByEmail(sampleRequest.email())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createCustomer(sampleRequest));

        assertTrue(ex.getMessage().contains("Email already exists"));
        verify(customerRepository).existsByEmail(sampleRequest.email());
        verify(customerRepository, never()).save(any());
        verifyNoInteractions(customerMapper);
    }

    @Test
    void createCustomer_success_savesAndReturnsDto() {
        when(customerRepository.existsByEmail(sampleRequest.email())).thenReturn(false);
        when(customerMapper.toEntity(sampleRequest)).thenReturn(sampleEntity);
        when(customerRepository.save(sampleEntity)).thenReturn(sampleEntity);
        when(customerMapper.toResponseDto(sampleEntity)).thenReturn(sampleResponse);

        CustomerResponseDto result = service.createCustomer(sampleRequest);

        assertNotNull(result);
        assertEquals(sampleResponse, result);
        verify(customerRepository).existsByEmail(sampleRequest.email());
        verify(customerMapper).toEntity(sampleRequest);
        verify(customerRepository).save(sampleEntity);
        verify(customerMapper).toResponseDto(sampleEntity);
    }

    @Test
    void updateCustomer_whenIdNotFound_returnsEmpty() {
        when(customerRepository.findById(123)).thenReturn(Optional.empty());

        Optional<CustomerResponseDto> result = service.updateCustomer(123, sampleRequest);

        assertTrue(result.isEmpty());
        verify(customerRepository).findById(123);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer_whenEmailUnchanged_updatesAndReturnsDto() {
        Customer existing = Customer.builder()
                .id(1)
                .email(sampleRequest.email()) // unchanged
                .name("Old Name")
                .addressLine1("Old Addr")
                .build();

        when(customerRepository.findById(1)).thenReturn(Optional.of(existing));
        // Because email unchanged, existsByEmail must not be checked
        // Update mapping
        doAnswer(invocation -> {
            CustomerRequestDto dto = invocation.getArgument(0);
            Customer entity = invocation.getArgument(1);
            entity.setName(dto.name());
            entity.setAddressLine1(dto.addressLine1());
            return null;
        }).when(customerMapper).updateEntityFromDto(any(CustomerRequestDto.class), any(Customer.class));

        when(customerRepository.save(existing)).thenReturn(existing);
        when(customerMapper.toResponseDto(existing)).thenReturn(sampleResponse);

        Optional<CustomerResponseDto> result = service.updateCustomer(1, sampleRequest);

        assertTrue(result.isPresent());
        assertEquals(sampleResponse, result.get());
        verify(customerRepository).findById(1);
        verify(customerRepository, never()).existsByEmail(anyString());
        verify(customerMapper).updateEntityFromDto(eq(sampleRequest), same(existing));
        verify(customerRepository).save(existing);
        verify(customerMapper).toResponseDto(existing);
    }

    @Test
    void updateCustomer_whenEmailChangedAndConflicts_throwsIllegalArgumentException() {
        Customer existing = Customer.builder().id(1).email("old@example.com").build();
        CustomerRequestDto changed = new CustomerRequestDto(
                sampleRequest.name(),
                "new@example.com",
                sampleRequest.phone(),
                sampleRequest.addressLine1(),
                sampleRequest.addressLine2(),
                sampleRequest.city(),
                sampleRequest.state(),
                sampleRequest.postalCode()
        );

        when(customerRepository.findById(1)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByEmail("new@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateCustomer(1, changed).orElse(null));

        assertTrue(ex.getMessage().contains("Email already exists"));
        verify(customerRepository).findById(1);
        verify(customerRepository).existsByEmail("new@example.com");
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).updateEntityFromDto(any(), any());
    }

    @Test
    void updateCustomer_whenEmailChangedAndNoConflict_updatesAndReturnsDto() {
        Customer existing = Customer.builder().id(1).email("old@example.com").name("Old").build();
        CustomerRequestDto changed = new CustomerRequestDto(
                "New Name",
                "new@example.com",
                sampleRequest.phone(),
                sampleRequest.addressLine1(),
                sampleRequest.addressLine2(),
                sampleRequest.city(),
                sampleRequest.state(),
                sampleRequest.postalCode()
        );

        when(customerRepository.findById(1)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByEmail("new@example.com")).thenReturn(false);

        doAnswer(invocation -> {
            CustomerRequestDto dto = invocation.getArgument(0);
            Customer entity = invocation.getArgument(1);
            entity.setName(dto.name());
            entity.setEmail(dto.email());
            return null;
        }).when(customerMapper).updateEntityFromDto(any(CustomerRequestDto.class), any(Customer.class));

        when(customerRepository.save(existing)).thenReturn(existing);
        when(customerMapper.toResponseDto(existing)).thenReturn(sampleResponse);

        Optional<CustomerResponseDto> result = service.updateCustomer(1, changed);

        assertTrue(result.isPresent());
        assertEquals(sampleResponse, result.get());
        verify(customerRepository).findById(1);
        verify(customerRepository).existsByEmail("new@example.com");
        verify(customerMapper).updateEntityFromDto(eq(changed), same(existing));
        verify(customerRepository).save(existing);
        verify(customerMapper).toResponseDto(existing);
    }

    @Test
    void updateCustomer_whenEmailNull_treatedAsUnchanged_updates() {
        Customer existing = Customer.builder().id(1).email("keep@example.com").name("Old").build();
        CustomerRequestDto changed = new CustomerRequestDto(
                "New Name",
                null, // email null => treated as unchanged
                sampleRequest.phone(),
                sampleRequest.addressLine1(),
                sampleRequest.addressLine2(),
                sampleRequest.city(),
                sampleRequest.state(),
                sampleRequest.postalCode()
        );

        when(customerRepository.findById(1)).thenReturn(Optional.of(existing));

        doNothing().when(customerMapper).updateEntityFromDto(any(), any());
        when(customerRepository.save(existing)).thenReturn(existing);
        when(customerMapper.toResponseDto(existing)).thenReturn(sampleResponse);

        Optional<CustomerResponseDto> result = service.updateCustomer(1, changed);

        assertTrue(result.isPresent());
        verify(customerRepository, never()).existsByEmail(anyString());
        verify(customerRepository).save(existing);
    }

    @Test
    void deleteCustomer_whenExists_deletesAndReturnsTrue() {
        when(customerRepository.existsById(1)).thenReturn(true);

        boolean deleted = service.deleteCustomer(1);

        assertTrue(deleted);
        verify(customerRepository).existsById(1);
        verify(customerRepository).deleteById(1);
    }

    @Test
    void deleteCustomer_whenNotExists_returnsFalse() {
        when(customerRepository.existsById(42)).thenReturn(false);

        boolean deleted = service.deleteCustomer(42);

        assertFalse(deleted);
        verify(customerRepository).existsById(42);
        verify(customerRepository, never()).deleteById(anyInt());
    }
}
