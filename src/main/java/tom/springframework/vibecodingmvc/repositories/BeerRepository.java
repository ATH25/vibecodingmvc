package tom.springframework.vibecodingmvc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tom.springframework.vibecodingmvc.entities.Beer;

public interface BeerRepository extends JpaRepository<Beer, Integer> {
    // Spring Data JPA will automatically implement basic CRUD operations
}