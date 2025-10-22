package tom.springframework.vibecodingmvc.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tom.springframework.vibecodingmvc.entities.BeerOrderShipment;

import java.util.List;
import java.util.Optional;

public interface BeerOrderShipmentRepository extends JpaRepository<BeerOrderShipment, Integer> {

    List<BeerOrderShipment> findByBeerOrder_Id(Integer beerOrderId);

    Page<BeerOrderShipment> findByBeerOrder_Id(Integer beerOrderId, Pageable pageable);

    Optional<BeerOrderShipment> findByTrackingNumber(String trackingNumber);
}
