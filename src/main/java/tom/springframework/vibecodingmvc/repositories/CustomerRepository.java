package tom.springframework.vibecodingmvc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tom.springframework.vibecodingmvc.entities.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByEmail(String email);
    Optional<Customer> findByEmail(String email);
}
