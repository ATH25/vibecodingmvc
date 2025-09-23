package tom.springframework.vibecodingmvc.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tom.springframework.vibecodingmvc.entities.Customer;
import tom.springframework.vibecodingmvc.mappers.CustomerMapper;
import tom.springframework.vibecodingmvc.models.CustomerRequestDto;
import tom.springframework.vibecodingmvc.models.CustomerResponseDto;
import tom.springframework.vibecodingmvc.repositories.CustomerRepository;
import tom.springframework.vibecodingmvc.services.CustomerService;

import java.util.List;
import java.util.Optional;

@Service
class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> listCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerResponseDto> getCustomerById(Integer id) {
        return customerRepository.findById(id).map(customerMapper::toResponseDto);
    }

    @Override
    @Transactional
    public CustomerResponseDto createCustomer(CustomerRequestDto dto) {
        if (dto.email() != null && customerRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already exists: " + dto.email());
        }
        Customer entity = customerMapper.toEntity(dto);
        Customer saved = customerRepository.save(entity);
        return customerMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public Optional<CustomerResponseDto> updateCustomer(Integer id, CustomerRequestDto dto) {
        return customerRepository.findById(id).map(existing -> {
            // if email provided and changed, check uniqueness
            if (dto.email() != null && !dto.email().equalsIgnoreCase(existing.getEmail())) {
                if (customerRepository.existsByEmail(dto.email())) {
                    throw new IllegalArgumentException("Email already exists: " + dto.email());
                }
            }
            customerMapper.updateEntityFromDto(dto, existing);
            Customer saved = customerRepository.save(existing);
            return customerMapper.toResponseDto(saved);
        });
    }

    @Override
    @Transactional
    public boolean deleteCustomer(Integer id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
