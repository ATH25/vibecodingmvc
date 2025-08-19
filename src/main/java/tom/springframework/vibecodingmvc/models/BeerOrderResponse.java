package tom.springframework.vibecodingmvc.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BeerOrderResponse(
        Integer id,
        String customerRef,
        BigDecimal paymentAmount,
        String status,
        List<BeerOrderLineResponse> lines,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {}
