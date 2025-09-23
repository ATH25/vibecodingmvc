package tom.springframework.vibecodingmvc.services;

import tom.springframework.vibecodingmvc.models.CustomerRequestDto;
import tom.springframework.vibecodingmvc.models.CustomerResponseDto;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerResponseDto> listCustomers();
    Optional<CustomerResponseDto> getCustomerById(Integer id);
    CustomerResponseDto createCustomer(CustomerRequestDto dto);
    Optional<CustomerResponseDto> updateCustomer(Integer id, CustomerRequestDto dto);
    boolean deleteCustomer(Integer id);
}
