package tom.springframework.vibecodingmvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private String customerRef;

    private BigDecimal paymentAmount;

    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @Builder.Default
    @OneToMany(
            mappedBy = "beerOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<BeerOrderLine> lines = new ArrayList<>();

    public void addLine(BeerOrderLine line) {
        if (line == null) return;
        lines.add(line);
        line.setBeerOrder(this);
    }

    public void removeLine(BeerOrderLine line) {
        if (line == null) return;
        lines.remove(line);
        line.setBeerOrder(null);
    }
}
