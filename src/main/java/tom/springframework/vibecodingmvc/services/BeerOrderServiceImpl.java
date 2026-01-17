package tom.springframework.vibecodingmvc.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tom.springframework.vibecodingmvc.entities.Beer;
import tom.springframework.vibecodingmvc.entities.BeerOrder;
import tom.springframework.vibecodingmvc.entities.BeerOrderLine;
import tom.springframework.vibecodingmvc.mappers.BeerOrderMapper;
import tom.springframework.vibecodingmvc.models.BeerOrderResponse;
import tom.springframework.vibecodingmvc.models.BeerOrderSummaryResponse;
import tom.springframework.vibecodingmvc.models.CreateBeerOrderCommand;
import tom.springframework.vibecodingmvc.models.CreateBeerOrderItem;
import tom.springframework.vibecodingmvc.repositories.BeerOrderRepository;
import tom.springframework.vibecodingmvc.repositories.BeerRepository;

import java.util.List;
import java.util.Optional;

@Service
class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerRepository beerRepository;
    private final BeerOrderMapper beerOrderMapper;

    BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository,
                         BeerRepository beerRepository,
                         BeerOrderMapper beerOrderMapper) {
        this.beerOrderRepository = beerOrderRepository;
        this.beerRepository = beerRepository;
        this.beerOrderMapper = beerOrderMapper;
    }

    @Override
    @Transactional
    public int createOrder(CreateBeerOrderCommand cmd) {
        // Assume validated at controller boundary (@Validated)
        BeerOrder order = BeerOrder.builder()
                .customerRef(cmd.customerRef())
                .paymentAmount(cmd.paymentAmount())
                .status("NEW")
                .build();

        for (CreateBeerOrderItem item : cmd.items()) {
            Beer beerRef = beerRepository.getReferenceById(item.beerId());
            BeerOrderLine line = BeerOrderLine.builder()
                    .beer(beerRef)
                    .orderQuantity(item.quantity())
                    .quantityAllocated(0)
                    .status("NEW")
                    .build();
            order.addLine(line);
        }

        BeerOrder saved = beerOrderRepository.save(order);
        return saved.getId();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<BeerOrderResponse> getOrder(Integer id) {
        return beerOrderRepository.findWithLinesById(id)
                .map(beerOrderMapper::toResponse);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Page<BeerOrderSummaryResponse> listOrders(Pageable pageable) {
        Pageable effectivePageable = pageable;
        if (pageable.getSort().isSorted()) {
            List<Sort.Order> orders = pageable.getSort().stream()
                    .map(order -> {
                        if ("createdAt".equals(order.getProperty())) {
                            return new Sort.Order(order.getDirection(), "createdDate");
                        }
                        return order;
                    })
                    .toList();
            effectivePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
        }

        return beerOrderRepository.findAll(effectivePageable)
                .map(beerOrderMapper::toSummaryResponse);
    }
}
