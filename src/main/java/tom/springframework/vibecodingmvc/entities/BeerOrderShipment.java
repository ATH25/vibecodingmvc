package tom.springframework.vibecodingmvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "beer_order_shipment", indexes = {
        @Index(name = "idx_bos_order", columnList = "beer_order_id"),
        @Index(name = "idx_bos_status", columnList = "shipmentStatus")
})
public class BeerOrderShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beer_order_id", nullable = false)
    private BeerOrder beerOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus shipmentStatus;

    private LocalDateTime shippedDate;

    private String trackingNumber;

    private String carrier;

    @Column(length = 1000)
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
