package tom.springframework.vibecodingmvc.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tom.springframework.vibecodingmvc.entities.BeerOrder;

import java.util.Optional;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, Integer> {

    @EntityGraph(attributePaths = {"lines", "lines.beer"})
    Optional<BeerOrder> findWithLinesById(Integer id);
}
