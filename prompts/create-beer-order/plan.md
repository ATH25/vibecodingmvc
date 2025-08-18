# Project Improvements Plan – Create Beer Order

Purpose: Turn the requirements in prompts/create-beer-order/requirements.md into a pragmatic, phased implementation plan for the vibecodingmvc project (Java 21, Spring Boot, Maven) that adds Beer Order capabilities following best practices.

1. Foundations and Architecture
- Confirm package-by-feature structure under tom.springframework.vibecodingmvc. Keep new components package-private where possible. 
- Layers enforced: Controller → Service → Repository; no direct repo access from controllers.
- Use constructor injection for all beans. Avoid field injection.
- Keep OSIV disabled (spring.jpa.open-in-view=false) to encourage explicit fetch strategies.

2. Data Model (Entities)
- Add BeerOrder entity in entities package
  - Fields: id, version, customerRef, paymentAmount, status, createdDate, updatedDate, lines.
  - Relations: OneToMany lines mappedBy beerOrder; cascade ALL, orphanRemoval true, LAZY.
  - Helper methods addLine/removeLine to maintain bidirectional integrity; initialize lines with @Builder.Default.
  - Hibernate timestamps: @CreationTimestamp, @UpdateTimestamp; optimistic @Version.
- Add BeerOrderLine entity in entities package
  - Fields: id, version, beerOrder, beer, orderQuantity, quantityAllocated, status, createdDate, updatedDate.
  - Relations: ManyToOne(beerOrder) LAZY, not null; ManyToOne(beer) LAZY, not null.
  - Indexes via @Table(indexes = …) on beer_order_id and beer_id to support lookups.
- Optional on Beer entity: add back-reference List<BeerOrderLine> orderLines with mappedBy="beer" (no cascade). Only if navigation needed; default to skip now to keep Beer lean.
- Lombok: Avoid default equals/hashCode across relations. If needed, restrict to identifier only.

3. Repositories
- BeerOrderRepository extends JpaRepository<BeerOrder, Integer>.
  - Add @EntityGraph(attributePaths = {"lines", "lines.beer"}) Optional<BeerOrder> findWithLinesById(Integer id) for graph fetching.
- BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer>.
- Reuse existing BeerRepository.

4. Commands, DTOs, and Mapping
- Commands (service input)
  - CreateBeerOrderCommand: customerRef, paymentAmount, List<CreateBeerOrderItem> items.
  - CreateBeerOrderItem: beerId, quantity.
  - Bean Validation: @NotNull paymentAmount/items, @PositiveOrZero paymentAmount, @Size(min=1) items, @NotNull beerId, @Positive quantity.
- Response DTOs (controller output)
  - BeerOrderResponse: id, customerRef, paymentAmount, status, List<BeerOrderLineResponse>, createdDate, updatedDate.
  - BeerOrderLineResponse: id, beerId, beerName (optional), orderQuantity, quantityAllocated, status.
- Mapping strategy
  - Prefer MapStruct for DTO <-> entity mapping for consistency with existing Beer mapping.
  - Create a BeerOrderMapper interface to map entity to BeerOrderResponse.
  - For creation, the service will assemble entities from the command (no direct MapStruct needed for command → entity to keep business rules explicit).

5. Service Layer and Transactions
- Create BeerOrderService interface and BeerOrderServiceImpl.
- Method: int createOrder(CreateBeerOrderCommand cmd)
  - @Transactional (write).
  - Build BeerOrder with status "NEW" and validated paymentAmount.
  - For each item: 
    - Obtain Beer via beerRepository.getReferenceById(beerId) to avoid immediate select.
    - Create BeerOrderLine with beer, orderQuantity=item.quantity, quantityAllocated=0, status="NEW".
    - order.addLine(line).
  - Save via beerOrderRepository.save(order) and return generated id.
- Read methods (optional initial scope):
  - Optional<BeerOrderResponse> getOrder(Integer id) using findWithLinesById + mapper; @Transactional(readOnly=true).

6. Controller API (Outline Now, Implement Later if Out of Scope)
- Base path: /api/v1/beer-orders.
- POST /api/v1/beer-orders – body CreateBeerOrderCommand; return 201 with Location header and BeerOrderResponse body.
- GET /api/v1/beer-orders/{id} – return 200 with BeerOrderResponse or 404 if not found.
- Use @Validated on controller class; map validation errors to 400.

7. Validation and Exception Handling
- Centralize via @RestControllerAdvice:
  - Handle MethodArgumentNotValidException / ConstraintViolationException → 400.
  - EntityNotFoundException / order not found → 404.
  - OptimisticLockingFailureException / ObjectOptimisticLockingFailureException → 409.
  - Return consistent error JSON (ProblemDetails style) with type, title, status, detail, instance.

8. Persistence and Performance Considerations
- OSIV already disabled; keep LAZY on associations.
- Use EntityGraph on read endpoints that require lines + beers to avoid N+1.
- Consider pagination for future list endpoints.
- Add DB indexes on beer_order_line(beer_order_id) and (beer_id).

9. Testing Strategy
- Service unit tests (JUnit + Mockito)
  - Verify createOrder builds BeerOrder with correct lines and persists once.
  - Ensure repository interactions: getReferenceById called per line; save called with cascade.
  - Validate edge cases: empty items rejected; negative payment amount rejected by validation at controller boundary; quantity must be positive.
- Repository slice tests (@DataJpaTest)
  - Cascade persist from BeerOrder to lines.
  - orphanRemoval removes lines when dropped from collection then saved.
  - findWithLinesById loads graph without extra N+1 (use TestEntityManager + SQL logging if enabled).
- Integration tests (optional / future) with Testcontainers to cover POST/GET flows via MockMvc/WebTestClient on RANDOM_PORT.

10. Incremental Implementation Plan (Phased)
- Phase 1: Entities & Repositories
  - Implement BeerOrder and BeerOrderLine entities with mappings and indexes.
  - Create repositories with EntityGraph method.
  - Add simple repository tests for cascade and orphanRemoval.
- Phase 2: Commands, Mapper, and Service
  - Define CreateBeerOrderCommand and CreateBeerOrderItem.
  - Implement BeerOrderService and createOrder logic.
  - Implement BeerOrderMapper (entity → response) and unit tests.
- Phase 3: Controller (if in scope)
  - Add BeerOrderController with POST and GET endpoints, using DTOs and mapper.
  - Add MockMvc tests validating 201/Location, 200/404, and validation errors 400.
- Phase 4: Exception Handling
  - Add GlobalExceptionHandler returning ProblemDetails-like responses.
  - Tests for common error scenarios.
- Phase 5: Performance & Clean-up
  - Review LAZY loading and EntityGraph usage.
  - Verify logs via SLF4J; no System.out.
  - Ensure OSIV remains false.

11. Risks and Mitigations
- Risk: Infinite recursion during JSON serialization if entities leaked. Mitigation: Use DTOs exclusively; never return entities.
- Risk: N+1 on fetch. Mitigation: Use EntityGraph for reads; consider fetch joins if necessary.
- Risk: Cascade deletes lines unintentionally. Mitigation: Use orphanRemoval only within BeerOrder; do not cascade from Beer to lines.
- Risk: Validation gaps. Mitigation: Place validation on DTOs; service assumes validated inputs.

12. Acceptance Criteria (Traceable)
- Entities BeerOrder and BeerOrderLine created with specified mappings and helper methods.
- Repositories added, including BeerOrderRepository#findWithLinesById with @EntityGraph.
- Service createOrder(CreateBeerOrderCommand) persists order + lines in one transaction and returns id.
- DTOs with validation and mapper for response defined.
- Controller contract outlined; if implemented, returns 201 with Location on create and 200/404 on get.
- OSIV disabled; lazy associations; JSON safe through DTOs.
- Tests in place for repository behavior; service unit tests passing.

13. Deliverables for This Iteration
- Source: new entities, repositories, service, commands, response DTOs, mapper interface.
- Tests: service unit; repository slice tests (at minimum for cascade/orphanRemoval).
- Documentation: this plan committed at prompts/create-beer-order/plan.md.

14. Out of Scope (if time-boxed)
- Payment processing or allocation logic beyond setting quantityAllocated=0.
- Complex status transitions (use simple "NEW" string status initial value).
- Full integration tests with Testcontainers (can be a follow-up iteration).

15. Next Steps
- Implement Phase 1 changes, then proceed sequentially through phases, keeping commits small and scoped.
