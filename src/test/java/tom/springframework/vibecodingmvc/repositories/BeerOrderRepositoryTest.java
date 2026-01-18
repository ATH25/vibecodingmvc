package tom.springframework.vibecodingmvc.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import tom.springframework.vibecodingmvc.entities.BeerOrder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeerOrderRepositoryTest {

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Test
    void testFindAllWithCreatedDateSort() {
        // Given
        BeerOrder order1 = BeerOrder.builder()
                .customerRef("Order 1")
                .paymentAmount(new BigDecimal("10.00"))
                .status("NEW")
                .build();
        BeerOrder order2 = BeerOrder.builder()
                .customerRef("Order 2")
                .paymentAmount(new BigDecimal("20.00"))
                .status("NEW")
                .build();
        
        beerOrderRepository.saveAllAndFlush(List.of(order1, order2));

        // When
        Page<BeerOrder> page = beerOrderRepository.findAll(
                PageRequest.of(0, 10, Sort.by("createdDate").descending())
        );

        // Then
        assertThat(page.getContent()).hasSizeGreaterThanOrEqualTo(2);
        // We can't strictly assert order1 vs order2 because they might have same timestamp if saved too fast,
        // but the main point is it doesn't throw PropertyReferenceException.
    }
}
