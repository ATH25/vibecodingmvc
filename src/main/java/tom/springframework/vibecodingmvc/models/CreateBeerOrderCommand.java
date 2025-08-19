package tom.springframework.vibecodingmvc.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateBeerOrderCommand(
        String customerRef,
        @NotNull @PositiveOrZero BigDecimal paymentAmount,
        @NotNull @Size(min = 1) List<@Valid CreateBeerOrderItem> items
) {}
