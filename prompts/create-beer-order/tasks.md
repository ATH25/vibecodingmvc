# Create Beer Order – Task List

Purpose: Execute the enhancements from prompts/create-beer-order/plan.md. Mark each task with [x] when complete.

1. Foundations and Architecture
1.1 [ ] Confirm package-by-feature structure under tom.springframework.vibecodingmvc; keep new components package-private where possible.
1.2 [ ] Enforce Controller → Service → Repository layering; ensure no controller directly uses repositories.
1.3 [ ] Ensure constructor injection for all new beans (no field/setter injection).
1.4 [ ] Verify OSIV disabled: spring.jpa.open-in-view=false remains in application.properties.

2. Data Model (Entities)
2.1 [ ] Create BeerOrder entity (id, version, customerRef, paymentAmount, status, createdDate, updatedDate, lines).
2.2 [ ] Configure BeerOrder ↔ BeerOrderLine relation: @OneToMany(mappedBy = "beerOrder", cascade = ALL, orphanRemoval = true, fetch = LAZY).
2.3 [ ] Add helper methods BeerOrder.addLine(line) and BeerOrder.removeLine(line) to maintain both sides; initialize lines with @Builder.Default.
2.4 [ ] Add @CreationTimestamp and @UpdateTimestamp to BeerOrder; add @Version for optimistic locking.
2.5 [ ] Create BeerOrderLine entity (id, version, beerOrder, beer, orderQuantity, quantityAllocated, status, createdDate, updatedDate).
2.6 [ ] Configure BeerOrderLine relations: @ManyToOne(fetch = LAZY) BeerOrder not null; @ManyToOne(fetch = LAZY) Beer not null.
2.7 [ ] Add @Table(indexes = …) on BeerOrderLine for beer_order_id and beer_id.
2.8 [ ] Review Lombok equals/hashCode to avoid cycles; restrict to identifier if needed.
2.9 [ ] Decide on Beer back-reference List<BeerOrderLine> orderLines mappedBy = "beer" (skip by default unless needed).

3. Repositories
3.1 [ ] Create BeerOrderRepository extends JpaRepository<BeerOrder, Integer>.
3.2 [ ] Add method with graph: @EntityGraph(attributePaths = {"lines", "lines.beer"}) Optional<BeerOrder> findWithLinesById(Integer id).
3.3 [ ] Create BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer>.
3.4 [ ] Reuse BeerRepository; no changes required.

4. Commands, DTOs, and Mapping
4.1 [ ] Define command records: CreateBeerOrderCommand(customerRef, paymentAmount, List<CreateBeerOrderItem> items).
4.2 [ ] Define CreateBeerOrderItem(beerId, quantity).
4.3 [ ] Add Bean Validation: @NotNull, @PositiveOrZero for paymentAmount; @Size(min = 1) on items; @NotNull beerId; @Positive quantity.
4.4 [ ] Define response DTOs: BeerOrderResponse(id, customerRef, paymentAmount, status, List<BeerOrderLineResponse>, createdDate, updatedDate).
4.5 [ ] Define BeerOrderLineResponse(id, beerId, beerName [optional], orderQuantity, quantityAllocated, status).
4.6 [ ] Create MapStruct mapper interface BeerOrderMapper: BeerOrder → BeerOrderResponse (map nested lines and beer info).

5. Service Layer and Transactions
5.1 [ ] Create BeerOrderService interface with createOrder(CreateBeerOrderCommand cmd) returning int and getOrder(Integer id) returning Optional<BeerOrderResponse> (read-only).
5.2 [ ] Implement BeerOrderServiceImpl with @Transactional on createOrder.
5.3 [ ] In createOrder: build BeerOrder with status = "NEW"; validate/assume validated cmd; set paymentAmount.
5.4 [ ] For each item, obtain Beer via beerRepository.getReferenceById(item.beerId).
5.5 [ ] Create BeerOrderLine with beer, orderQuantity = item.quantity, quantityAllocated = 0, status = "NEW"; add to order via addLine.
5.6 [ ] Save BeerOrder via beerOrderRepository.save(order) and return generated id.
5.7 [ ] Implement getOrder(Integer id) with @Transactional(readOnly = true) using findWithLinesById + mapper.

6. Controller API (if in scope for this iteration)
6.1 [ ] Create BeerOrderController at /api/v1/beer-orders.
6.2 [ ] Implement POST /api/v1/beer-orders consuming CreateBeerOrderCommand; return 201 Created with Location and BeerOrderResponse.
6.3 [ ] Implement GET /api/v1/beer-orders/{id} returning 200 with BeerOrderResponse or 404 if not found.
6.4 [ ] Add @Validated to the controller and ensure validation errors map to 400.

7. Validation and Exception Handling
7.1 [ ] Add @RestControllerAdvice GlobalExceptionHandler.
7.2 [ ] Handle MethodArgumentNotValidException and ConstraintViolationException → 400 with ProblemDetails-like body.
7.3 [ ] Handle EntityNotFoundException / order not found → 404.
7.4 [ ] Handle OptimisticLockingFailureException / ObjectOptimisticLockingFailureException → 409.
7.5 [ ] Standardize error JSON fields: type, title, status, detail, instance.

8. Persistence and Performance
8.1 [ ] Confirm all associations are LAZY by default where appropriate.
8.2 [ ] Use @EntityGraph on read method to fetch lines and beers to avoid N+1.
8.3 [ ] Ensure DB indexes exist on beer_order_line(beer_order_id) and (beer_id).

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
10.1 [ ] Verify SLF4J logging only; remove System.out usages if any.
10.2 [ ] Reconfirm spring.jpa.open-in-view=false in application.properties.
10.3 [ ] Review LAZY loading and EntityGraph usage in code review.

11. Documentation & Acceptance
11.1 [ ] Ensure this tasks.md reflects implemented items with [x] as work progresses.
11.2 [ ] Confirm acceptance criteria:
      - [ ] Entities and mappings implemented per plan.
      - [ ] Repositories with EntityGraph method present.
      - [ ] Service createOrder persists order + lines in one transaction.
      - [ ] DTOs and mapper defined; getOrder returns mapped response.
      - [ ] Tests for repository and service passing.
      - [ ] OSIV disabled; lazy associations; only DTOs exposed.

12. Next Steps
12.1 [ ] Proceed with Phase 1 (Entities & Repositories), then Phase 2 (Commands, Mapper, Service), Phase 3 (Controller if in scope), Phase 4 (Exception Handling), and Phase 5 (Performance & Clean-up), keeping commits small and scoped.
