### What the ERD shows
- BeerOrder 1 —* BeerOrderLine
- BeerOrderLine *—1 Beer
- Common columns: id (Integer PK), version, createdDate, updatedDate
- BeerOrder has customerRef, paymentAmount, status
- BeerOrderLine has orderQuantity, quantityAllocated, status
- Beer has beerName, beerStyle, upc, quantityOnHand, price

### Conventions to follow in this project
- Java 21, Spring Boot, Maven
- Use JPA + Hibernate annotations and Lombok for boilerplate
- Keep entities package-private or public as needed; prefer constructor or builder for services, but entities can use Lombok
- Enable optimistic locking with @Version
- Use @CreationTimestamp and @UpdateTimestamp for audit fields
- Prefer LAZY for associations; avoid exposing entities directly from controllers (use DTOs)

### 1) Beer entity (already present)
You already have Beer. No relationship fields are required on Beer for this ERD, but if you want a convenience back-reference to lines, see the Optional section below.

### 2) Create BeerOrder entity
Package: tom.springframework.vibecodingmvc.entities

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class BeerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private String customerRef;
    private BigDecimal paymentAmount;
    private String status; // consider enum later

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    // 1 —* to BeerOrderLine
    @OneToMany(
        mappedBy = "beerOrder",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<BeerOrderLine> lines = new ArrayList<>();

    // Helper methods to keep both sides in sync
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
```
Notes
- Cascade ALL + orphanRemoval allows persisting/removing lines via BeerOrder.
- Keep collection initialized to avoid NPEs.
- Use helper methods to maintain bidirectional association.

### 3) Create BeerOrderLine entity
Package: tom.springframework.vibecodingmvc.entities

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class BeerOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    // many-to-one to BeerOrder (owning side)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beer_order_id", nullable = false)
    private BeerOrder beerOrder;

    // many-to-one to Beer
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beer_id", nullable = false)
    private Beer beer;

    private Integer orderQuantity;
    private Integer quantityAllocated;
    private String status; // consider enum later

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
```
Notes
- ManyToOne is EAGER by default per JPA; explicitly set LAZY to avoid unnecessary loads.
- @JoinColumn names are explicit and create FK columns beer_order_id and beer_id.

### 4) Optional: back-reference on Beer
If you want to navigate from Beer to its order lines:

```java
@OneToMany(mappedBy = "beer", fetch = FetchType.LAZY)
private List<BeerOrderLine> orderLines = new ArrayList<>();
```
- Do not cascade from Beer to BeerOrderLine by default (business-wise, deleting a Beer should not delete historical order lines). Keep it read-only navigation.

### 5) Lombok and equals/hashCode
- Avoid Lombok’s default equals/hashCode on entities with relationships (can cause lazy-loading and recursion).
- If you need equals/hashCode (e.g., for Sets), restrict to identifier only once assigned:

```java
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class BeerOrder { /* … */
    @EqualsAndHashCode.Include
    private Integer id;
}
```

### 6) Repositories
Create simple Spring Data repositories:

```java
public interface BeerOrderRepository extends JpaRepository<BeerOrder, Integer> {
    @EntityGraph(attributePaths = {"lines", "lines.beer"})
    Optional<BeerOrder> findWithLinesById(Integer id);
}

public interface BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer> { }
public interface BeerRepository extends JpaRepository<Beer, Integer> { }
```
- The EntityGraph avoids N+1 when loading an order with its lines and beers (especially if you set spring.jpa.open-in-view=false).

### 7) Transaction and service usage
- Persist an order with lines in one transaction:

```java
@Transactional
public Integer createOrder(CreateBeerOrderCommand cmd) {
    BeerOrder order = BeerOrder.builder()
        .customerRef(cmd.customerRef())
        .paymentAmount(cmd.paymentAmount())
        .status("NEW")
        .build();

    for (var item : cmd.items()) {
        Beer beer = beerRepository.getReferenceById(item.beerId());
        BeerOrderLine line = BeerOrderLine.builder()
            .beer(beer)
            .orderQuantity(item.quantity())
            .quantityAllocated(0)
            .status("NEW")
            .build();
        order.addLine(line);
    }

    return beerOrderRepository.save(order).getId();
}
```

### 8) JSON serialization (if you ever serialize entities)
- Prefer DTOs in controllers. If you must serialize entities, avoid infinite recursion:
    - Option A: `@JsonManagedReference` on BeerOrder.lines and `@JsonBackReference` on BeerOrderLine.beerOrder
    - Option B: `@JsonIgnore` on BeerOrderLine.beerOrder
- Using DTOs is safer and aligns with your guidelines.

### 9) Schema and constraints
- The above will generate tables beer_order, beer_order_line, beer with FKs beer_order_line.beer_order_id -> beer_order.id and beer_order_line.beer_id -> beer.id.
- Consider DB indexes on beer_order_line(beer_order_id) and (beer_id). Hibernate will create FK indexes on many databases; add explicit indexes if needed:

```java
@Table(indexes = {
    @Index(name = "idx_bol_order", columnList = "beer_order_id"),
    @Index(name = "idx_bol_beer", columnList = "beer_id")
})
```

### 10) Testing pointers
- Write a repository slice test that saves a BeerOrder with two lines and verifies cascades and orphan removal.
- Use Testcontainers if you have integration tests hitting a real DB.

### 11) Quick checklist
- [ ] Add BeerOrder entity with @OneToMany lines (cascade ALL, orphanRemoval)
- [ ] Add BeerOrderLine entity with two @ManyToOne mappings (beerOrder, beer) set to LAZY
- [ ] Optionally add back-reference on Beer
- [ ] Add helper methods addLine/removeLine
- [ ] Create repositories and an EntityGraph method for fetching orders with lines
- [ ] Prefer DTOs in the web layer and keep transactions in services

This implements the ERD relationships in JPA with Lombok and follows your project’s Spring Boot guidelines.