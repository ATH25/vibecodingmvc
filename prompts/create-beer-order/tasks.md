# Create Beer Order – Task List

Purpose: Execute the enhancements from prompts/create-beer-order/plan.md. Mark each task with [x] when complete.

1. Foundations and Architecture
1.1 [x] Confirm package-by-feature structure under tom.springframework.vibecodingmvc; keep new components package-private where possible. (Entities public for JPA access across packages; controllers/services kept package-private.)
1.2 [x] Enforce Controller → Service → Repository layering; ensure no controller directly uses repositories.
1.3 [x] Ensure constructor injection for all new beans (no field/setter injection).
1.4 [x] Verify OSIV disabled: spring.jpa.open-in-view=false remains in application.properties.

2. Data Model (Entities)
2.1 [x] Create BeerOrder entity (id, version, customerRef, paymentAmount, status, createdDate, updatedDate, lines).
2.2 [x] Configure BeerOrder ↔ BeerOrderLine relation: @OneToMany(mappedBy = "beerOrder", cascade = ALL, orphanRemoval = true, fetch = LAZY).
2.3 [x] Add helper methods BeerOrder.addLine(line) and BeerOrder.removeLine(line) to maintain both sides; initialize lines with @Builder.Default.
2.4 [x] Add @CreationTimestamp and @UpdateTimestamp to BeerOrder; add @Version for optimistic locking.
2.5 [x] Create BeerOrderLine entity (id, version, beerOrder, beer, orderQuantity, quantityAllocated, status, createdDate, updatedDate).
2.6 [x] Configure BeerOrderLine relations: @ManyToOne(fetch = LAZY) BeerOrder not null; @ManyToOne(fetch = LAZY) Beer not null.
2.7 [x] Add @Table(indexes = …) on BeerOrderLine for beer_order_id and beer_id.
2.8 [x] Review Lombok equals/hashCode to avoid cycles; restrict to identifier if needed (not required now—no Sets used).
2.9 [x] Decide on Beer back-reference List<BeerOrderLine> orderLines mappedBy = "beer" (skipped by default as not needed now).

3. Repositories
3.1 [x] Create BeerOrderRepository extends JpaRepository<BeerOrder, Integer>.
3.2 [x] Add method with graph: @EntityGraph(attributePaths = {"lines", "lines.beer"}) Optional<BeerOrder> findWithLinesById(Integer id).
3.3 [x] Create BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer>.
3.4 [x] Reuse BeerRepository; no changes required.

4. Commands, DTOs, and Mapping
4.1 [x] Define command records: CreateBeerOrderCommand(customerRef, paymentAmount, List<CreateBeerOrderItem> items).
4.2 [x] Define CreateBeerOrderItem(beerId, quantity).
4.3 [x] Add Bean Validation: @NotNull, @PositiveOrZero for paymentAmount; @Size(min = 1) on items; @NotNull beerId; @Positive quantity.
4.4 [x] Define response DTOs: BeerOrderResponse(id, customerRef, paymentAmount, status, List<BeerOrderLineResponse>, createdDate, updatedDate).
4.5 [x] Define BeerOrderLineResponse(id, beerId, beerName [optional], orderQuantity, quantityAllocated, status).
4.6 [x] Create MapStruct mapper interface BeerOrderMapper: BeerOrder → BeerOrderResponse (map nested lines and beer info).

5. Service Layer and Transactions
5.1 [x] Create BeerOrderService interface with createOrder(CreateBeerOrderCommand cmd) returning int and getOrder(Integer id) returning Optional<BeerOrderResponse> (read-only).
5.2 [x] Implement BeerOrderServiceImpl with @Transactional on createOrder.
5.3 [x] In createOrder: build BeerOrder with status = "NEW"; validate/assume validated cmd; set paymentAmount.
5.4 [x] For each item, obtain Beer via beerRepository.getReferenceById(item.beerId).
5.5 [x] Create BeerOrderLine with beer, orderQuantity = item.quantity, quantityAllocated = 0, status = "NEW"; add to order via addLine.
5.6 [x] Save BeerOrder via beerOrderRepository.save(order) and return generated id.
5.7 [x] Implement getOrder(Integer id) with @Transactional(readOnly = true) using findWithLinesById + mapper.

6. Controller API (if in scope for this iteration)
6.1 [x] Create BeerOrderController at /api/v1/beer-orders.
6.2 [x] Implement POST /api/v1/beer-orders consuming CreateBeerOrderCommand; return 201 Created with Location and BeerOrderResponse.
6.3 [x] Implement GET /api/v1/beer-orders/{id} returning 200 with BeerOrderResponse or 404 if not found.
6.4 [x] Add @Validated to the controller and ensure validation errors map to 400.

7. Validation and Exception Handling
7.1 [x] Add @RestControllerAdvice GlobalExceptionHandler.
7.2 [x] Handle MethodArgumentNotValidException and ConstraintViolationException → 400 with ProblemDetails-like body.
7.3 [x] Handle EntityNotFoundException / order not found → 404.
7.4 [x] Handle OptimisticLockingFailureException / ObjectOptimisticLockingFailureException → 409.
7.5 [x] Standardize error JSON fields: type, title, status, detail, instance.

8. Persistence and Performance
8.1 [x] Confirm all associations are LAZY by default where appropriate.
8.2 [x] Use @EntityGraph on read method to fetch lines and beers to avoid N+1.
8.3 [x] Ensure DB indexes exist on beer_order_line(beer_order_id) and (beer_id).

9. Testing
9.1 [ ] Service unit tests: verify createOrder builds BeerOrder with correct lines and persists once.
9.2 [ ] Service unit tests: verify beerRepository.getReferenceById called per line; save called once; cascade works.
9.3 [ ] Service unit tests: edge cases—reject empty items; quantity must be positive; negative payment amount handled at validation.
9.4 [ ] Repository slice tests (@DataJpaTest): cascade persist from BeerOrder to lines.
9.5 [ ] Repository slice tests: orphanRemoval removes lines when removed and order saved.
9.6 [ ] Repository slice tests: findWithLinesById loads graph (no N+1 where feasible).
9.7 [ ] Mapper unit tests: BeerOrderMapper maps entity to BeerOrderResponse including nested lines and beer fields.
9.8 [ ] Controller tests (if controller implemented): MockMvc for POST (201/Location, 400 on validation) and GET (200/404).

10. Performance & Clean-up
10.1 [x] Verify SLF4J logging only; remove System.out usages if any.
10.2 [x] Reconfirm spring.jpa.open-in-view=false in application.properties.
10.3 [x] Review LAZY loading and EntityGraph usage in code review.

11. Documentation & Acceptance
11.1 [x] Ensure this tasks.md reflects implemented items with [x] as work progresses.
11.2 [ ] Confirm acceptance criteria:
     - [x] Entities and mappings implemented per plan.
     - [x] Repositories with EntityGraph method present.
     - [x] Service createOrder persists order + lines in one transaction.
     - [x] DTOs and mapper defined; getOrder returns mapped response.
     - [ ] Tests for repository and service passing.
     - [x] OSIV disabled; lazy associations; only DTOs exposed.

12. Next Steps
12.1 [x] Proceed with Phase 1 (Entities & Repositories), then Phase 2 (Commands, Mapper, Service), Phase 3 (Controller if in scope), Phase 4 (Exception Handling), and Phase 5 (Performance & Clean-up), keeping commits small and scoped.

---

New files created in this iteration:
- src/main/java/tom/springframework/vibecodingmvc/entities/BeerOrder.java
- src/main/java/tom/springframework/vibecodingmvc/entities/BeerOrderLine.java
- src/main/java/tom/springframework/vibecodingmvc/repositories/BeerOrderRepository.java
- src/main/java/tom/springframework/vibecodingmvc/repositories/BeerOrderLineRepository.java
- src/main/java/tom/springframework/vibecodingmvc/models/CreateBeerOrderCommand.java
- src/main/java/tom/springframework/vibecodingmvc/models/CreateBeerOrderItem.java
- src/main/java/tom/springframework/vibecodingmvc/models/BeerOrderResponse.java
- src/main/java/tom/springframework/vibecodingmvc/models/BeerOrderLineResponse.java
- src/main/java/tom/springframework/vibecodingmvc/mappers/BeerOrderMapper.java
- src/main/java/tom/springframework/vibecodingmvc/services/BeerOrderService.java
- src/main/java/tom/springframework/vibecodingmvc/services/BeerOrderServiceImpl.java
- src/main/java/tom/springframework/vibecodingmvc/controllers/BeerOrderController.java
- src/main/java/tom/springframework/vibecodingmvc/controllers/GlobalExceptionHandler.java

Configuration updated:
- src/main/resources/application.properties (spring.jpa.open-in-view=false)

Classes with Lombok-injected methods (getters/setters/builders/constructors):
- tom.springframework.vibecodingmvc.entities.BeerOrder (@Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor)
- tom.springframework.vibecodingmvc.entities.BeerOrderLine (@Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor)
