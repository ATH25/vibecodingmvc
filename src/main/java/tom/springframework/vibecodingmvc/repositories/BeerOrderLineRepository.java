package tom.springframework.vibecodingmvc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tom.springframework.vibecodingmvc.entities.BeerOrderLine;

public interface BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer> {
}
