# Create Beer Order – Requirements

Purpose
- Implement domain model and persistence for creating Beer Orders with lines referencing existing Beers.
- Provide repository and service-layer foundations to persist and fetch orders efficiently.
- Define web/API contracts using DTOs and validation (controller implementation may be a separate task).

Scope
- Add BeerOrder and BeerOrderLine entities and repositories.
- Implement a service method to create an order with lines in one transaction.
- Define request/response DTOs for create/read operations and basic validation rules.
- Outline controller endpoints and exception handling conventions to be used.

Tech assumptions
- Java 21, Spring Boot, Maven.
- Spring Data JPA (Hibernate) and Lombok.
- Package-by-feature under tom.springframework.vibecodingmvc.
- Existing Beer entity and BeerRepository already present.

1. ERD summary
- BeerOrder 1 —* BeerOrderLine
- BeerOrderLine *—1 Beer
- Shared columns: id (Integer PK), version, createdDate, updatedDate
- BeerOrder: customerRef, paymentAmount, status
- BeerOrderLine: orderQuantity, quantityAllocated, status

2. Entities (JPA + Lombok)
Package: tom.springframework.vibecodingmvc.entities

2.1 BeerOrder
- Annotations: @Entity, @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder
- Fields:
  - id: Integer, @Id, @GeneratedValue(strategy = IDENTITY)
  - version: Integer, @Version (optimistic locking)
  - customerRef: String (nullable allowed, validate in DTO if needed)
  - paymentAmount: BigDecimal (non-negative, validate in DTO)
  - status: String (start with string; consider enum later)
  - createdDate: LocalDateTime, @CreationTimestamp, @Column(updatable = false)
  - updatedDate: LocalDateTime, @UpdateTimestamp
  - lines: List<BeerOrderLine>
    - @OneToMany(mappedBy = "beerOrder", cascade = ALL, orphanRemoval = true, fetch = LAZY)
    - Initialize collection; use @Builder.Default new ArrayList<>()
- Helper methods to maintain bidirectional relation:
  - addLine(BeerOrderLine line): add to collection and set line.setBeerOrder(this)
  - removeLine(BeerOrderLine line): remove and set line.setBeerOrder(null)

2.2 BeerOrderLine
- Annotations: @Entity, @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder
- Fields:
  - id: Integer, @Id, @GeneratedValue(strategy = IDENTITY)
  - version: Integer, @Version
  - beerOrder: BeerOrder
    - @ManyToOne(fetch = LAZY, optional = false)
    - @JoinColumn(name = "beer_order_id", nullable = false)
  - beer: Beer
    - @ManyToOne(fetch = LAZY, optional = false)
    - @JoinColumn(name = "beer_id", nullable = false)
  - orderQuantity: Integer (>= 1, validate in DTO)
  - quantityAllocated: Integer (>= 0, validate/derive in service as needed)
  - status: String (start with string; consider enum later)
  - createdDate: LocalDateTime, @CreationTimestamp, @Column(updatable = false)
  - updatedDate: LocalDateTime, @UpdateTimestamp

2.3 Optional back-reference on Beer
- If needed for navigation:
  - @OneToMany(mappedBy = "beer", fetch = LAZY) private List<BeerOrderLine> orderLines = new ArrayList<>();
- Do NOT cascade from Beer to BeerOrderLine (avoid deleting historical lines when a Beer is removed).

2.4 Lombok equals/hashCode guidance
- Avoid default Lombok equals/hashCode on entities with relations to prevent recursion or unintended lazy loading.
- If needed (e.g., Sets), restrict to identifier only:
  - @EqualsAndHashCode(onlyExplicitIncluded = true)
  - @EqualsAndHashCode.Include private Integer id;

3. Repositories (Spring Data JPA)
Package: tom.springframework.vibecodingmvc.repositories

- BeerOrderRepository extends JpaRepository<BeerOrder, Integer>
  - Use an EntityGraph for eager graph fetch when needed to avoid N+1:
    - @EntityGraph(attributePaths = {"lines", "lines.beer"}) Optional<BeerOrder> findWithLinesById(Integer id)
- BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer>
- BeerRepository already exists and is reused.

4. Service layer and transactions
Package: tom.springframework.vibecodingmvc.services

- Create BeerOrderService with constructor injection (prefer package-private class visibility where possible):
  - int createOrder(CreateBeerOrderCommand cmd)
    - @Transactional on the method.
    - Build BeerOrder from command; set initial status (e.g., "NEW").
    - For each item in cmd.items():
      - Fetch Beer via beerRepository.getReferenceById(item.beerId()) (no select until needed).
      - Build BeerOrderLine with beer, orderQuantity, quantityAllocated=0, status="NEW".
      - order.addLine(line).
    - Save order via beerOrderRepository.save(order) and return id.
- Read-only fetch methods should be annotated with @Transactional(readOnly = true).
- Avoid repository access from controllers; use services.

5. DTOs and commands (web boundary)
Package: tom.springframework.vibecodingmvc.models (or a new dto/command subpackage)

5.1 Command for creation
- record CreateBeerOrderCommand(
  - String customerRef,
  - BigDecimal paymentAmount,
  - List<CreateBeerOrderItem> items)
- record CreateBeerOrderItem(Integer beerId, Integer quantity)
- Validation annotations:
  - @NotNull on paymentAmount and items
  - @PositiveOrZero on paymentAmount
  - @Size(min = 1) on items
  - @NotNull on beerId, @Positive on quantity

5.2 Response DTOs (for controller responses)
- record BeerOrderResponse(
  - Integer id,
  - String customerRef,
  - BigDecimal paymentAmount,
  - String status,
  - List<BeerOrderLineResponse> lines,
  - LocalDateTime createdDate,
  - LocalDateTime updatedDate)
- record BeerOrderLineResponse(
  - Integer id,
  - Integer beerId,
  - String beerName, // optional convenience
  - Integer orderQuantity,
  - Integer quantityAllocated,
  - String status)

6. Controller API design (outline)
Package: tom.springframework.vibecodingmvc.controllers
- Base path: /api/v1/beer-orders
- Endpoints:
  - POST /api/v1/beer-orders
    - Request: JSON object mapped to CreateBeerOrderCommand
    - Response: 201 Created with Location header /api/v1/beer-orders/{id} and body BeerOrderResponse
  - GET /api/v1/beer-orders/{id}
    - Response: 200 OK with BeerOrderResponse or 404 Not Found
- Use ResponseEntity<T> and explicit status codes.
- Apply @Validated at the controller class level.
- Do NOT expose entities directly; always convert entities to DTOs via a mapper (MapStruct recommended, already used in project).

7. JSON serialization
- Prefer DTOs; avoid serializing JPA entities directly.
- If entities must be serialized (not recommended), use @JsonManagedReference/@JsonBackReference or @JsonIgnore to break cycles between BeerOrder and BeerOrderLine.

8. Configuration and OSIV
- Ensure spring.jpa.open-in-view=false in application.properties to avoid OSIV pattern.
- Use LAZY associations and load required graphs via EntityGraph or explicit queries.

9. Schema and indexes
- Tables: beer_order, beer_order_line, beer.
- FKs: beer_order_line.beer_order_id -> beer_order.id; beer_order_line.beer_id -> beer.id.
- Index recommendations on beer_order_line:
  - @Table(indexes = {@Index(name = "idx_bol_order", columnList = "beer_order_id"), @Index(name = "idx_bol_beer", columnList = "beer_id")})

10. Exception handling
- Add a @RestControllerAdvice to map exceptions to ProblemDetails-like responses or a consistent error JSON.
  - Map javax/jakarta validation exceptions to 400 Bad Request.
  - Map EntityNotFound / order-not-found to 404 Not Found.
  - Map optimistic locking failures to 409 Conflict.

11. Testing strategy
- Unit tests for BeerOrderService (mock repositories) to verify:
  - Lines are added and cascade persists on save.
  - Validation of inputs at service boundary as needed.
- Repository slice tests (@DataJpaTest) to check:
  - Cascade persist and orphanRemoval on BeerOrder.lines.
  - EntityGraph fetching avoids N+1 where applicable.
- Integration tests (optional) with Testcontainers for a real DB; use @SpringBootTest(webEnvironment = RANDOM_PORT).
- Keep logs clean; avoid System.out—use SLF4J.

12. Acceptance criteria
- Entities BeerOrder and BeerOrderLine created with mappings as specified.
- Repositories implemented, including BeerOrderRepository#findWithLinesById using @EntityGraph.
- Service method createOrder(CreateBeerOrderCommand) persists order and lines in one transaction and returns generated id.
- DTOs defined for create and response with validation annotations.
- Controller contract outlined and aligns with REST guidelines (201 with Location on create, 200/404 on fetch).
- OSIV disabled; associations lazy; no infinite recursion risk in JSON because DTOs are used.
- Tests outlined; at least repository slice tests verifying cascade and orphan removal are planned/added in separate task if not in scope here.

13. Quick checklist
- [ ] BeerOrder entity with OneToMany lines (cascade ALL, orphanRemoval, LAZY) and helper methods
- [ ] BeerOrderLine entity with two LAZY ManyToOne relations to BeerOrder and Beer
- [ ] Optional back-reference list on Beer (no cascade) if needed
- [ ] Lombok usage without risky equals/hashCode over relations
- [ ] Repositories for BeerOrder, BeerOrderLine, and EntityGraph method
- [ ] Service method with @Transactional to create orders from command
- [ ] DTOs with validation for request/response and mapper usage
- [ ] Controller endpoints design and status codes
- [ ] OSIV disabled and fetch strategies considered
- [ ] Index annotations considered on beer_order_line
- [ ] Tests plan: service unit, repository slice, optional integration with Testcontainers
