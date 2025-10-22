# BeerOrderShipment Implementation Tasks (Checklist)

Use this checklist to implement the BeerOrderShipment feature as outlined in plan.md. Mark tasks with [x] as you complete them.

## 1) Domain Model (Entity + Enum)
- [x] Create ShipmentStatus enum with values: PENDING, PACKED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, CANCELLED (src/main/java/tom/springframework/vibecodingmvc/entities/ShipmentStatus.java)
- [x] Create BeerOrderShipment entity with fields and mappings (src/main/java/tom/springframework/vibecodingmvc/entities/BeerOrderShipment.java)
  - [x] id: UUID (PK, @GeneratedValue)
  - [x] beerOrder: ManyToOne to BeerOrder (LAZY, @JoinColumn(name = "beer_order_id", nullable = false))
  - [x] shipmentStatus: @Enumerated(EnumType.STRING), default PENDING
  - [x] shippedDate: LocalDateTime (nullable)
  - [x] trackingNumber: String
  - [x] carrier: String
  - [x] notes: String
  - [x] createdDate/updatedDate auditing (if used by project)
  - [x] Add indexes on beer_order_id and shipment_status if applicable

## 2) Repository
- [x] Create BeerOrderShipmentRepository extends JpaRepository<BeerOrderShipment, UUID> (src/main/java/tom/springframework/vibecodingmvc/repositories/BeerOrderShipmentRepository.java)
- [x] Add method: List<BeerOrderShipment> findByBeerOrder_Id(Integer beerOrderId)
- [x] (Optional) Add method: Optional<BeerOrderShipment> findByTrackingNumber(String trackingNumber)
- [x] (Optional) Add pageable variant for listing by beerOrderId

## 3) DTOs (models)
- [x] Create BeerOrderShipmentDto (response) (src/main/java/tom/springframework/vibecodingmvc/models/BeerOrderShipmentDto.java)
- [x] Create BeerOrderShipmentCreateDto (request) with validation (src/main/java/tom/springframework/vibecodingmvc/models/BeerOrderShipmentCreateDto.java)
- [x] Create BeerOrderShipmentUpdateDto (request) (src/main/java/tom/springframework/vibecodingmvc/models/BeerOrderShipmentUpdateDto.java)

## 4) Mapper (MapStruct)
- [x] Create BeerOrderShipmentMapper interface (componentModel = "spring") (src/main/java/tom/springframework/vibecodingmvc/mappers/BeerOrderShipmentMapper.java)
  - [x] toDto(BeerOrderShipment entity)
  - [x] toEntity(BeerOrderShipmentCreateDto dto)
  - [x] updateEntity(@MappingTarget BeerOrderShipment entity, BeerOrderShipmentUpdateDto dto) with null-ignore
  - [x] Map beerOrder.id ↔ beerOrderId and enum ↔ String

## 5) Service Layer
- [x] Define BeerOrderShipmentService interface (src/main/java/tom/springframework/vibecodingmvc/services/BeerOrderShipmentService.java)
  - [x] UUID create(BeerOrderShipmentCreateDto dto)
  - [x] Optional<BeerOrderShipmentDto> get(UUID id)
  - [x] List<BeerOrderShipmentDto> listByBeerOrderId(Integer beerOrderId)
  - [x] void update(UUID id, BeerOrderShipmentUpdateDto dto)
  - [x] void delete(UUID id)
- [x] Implement BeerOrderShipmentServiceImpl (package-private) (src/main/java/tom/springframework/vibecodingmvc/services/impl/BeerOrderShipmentServiceImpl.java)
  - [x] Inject BeerOrderShipmentRepository, BeerOrderRepository, BeerOrderShipmentMapper via constructor
  - [x] @Transactional on create/update/delete; readOnly on get/list
  - [x] Validate beerOrderId exists before create (throw NotFound if not)
  - [x] Default shipmentStatus to PENDING if null
  - [x] Enforce rules: IN_TRANSIT or later requires trackingNumber and carrier; DELIVERED requires shippedDate (set if null)
  - [x] Set shippedDate when moving to IN_TRANSIT if null

## 6) Controller (REST)
- [x] Create BeerOrderShipmentController (package-private) (src/main/java/tom/springframework/vibecodingmvc/controllers/BeerOrderShipmentController.java)
  - [x] POST /api/v1/beerorder-shipments → 201 Created, Location header, return BeerOrderShipmentDto
  - [x] GET /api/v1/beerorder-shipments/{id} → 200/404
  - [x] GET /api/v1/beerorder-shipments?beerOrderId={id} → 200 list (validate positive id)
  - [x] PATCH /api/v1/beerorder-shipments/{id} → 204/400/404
  - [x] DELETE /api/v1/beerorder-shipments/{id} → 204/404

## 7) OpenAPI (Redocly split)
- [x] Add schemas:
  - [x] components/schemas/BeerOrderShipmentDto.yaml
  - [x] components/schemas/BeerOrderShipmentCreateDto.yaml
  - [x] components/schemas/BeerOrderShipmentUpdateDto.yaml
- [x] Add path file: paths/api_v1_beerorder-shipments.yaml (POST, GET by id, GET with beerOrderId, PATCH, DELETE)
- [x] Reference schemas from path operations and include ShipmentStatus enum
- [x] Run in openapi-starter-main: npm install (once) and npm test (redocly lint)

## 8) Tests
- [x] Mapper unit tests (entity ↔ DTO mapping, enum mapping, null-ignore update)
- [x] Service unit tests with mocks (create/update/delete/get/list, validation and business rules)
- [x] Controller integration tests with MockMvc (POST/GET/PATCH/DELETE, happy paths and error cases)

## 9) Error Handling & Logging
- [x] Ensure exceptions map to proper HTTP responses using GlobalExceptionHandler. Validate that all service-layer exceptions (e.g., NotFoundException, IllegalArgumentException) return correct HTTP status codes like 404 or 400. Confirm behavior via integration tests.
- [x] Add SLF4J logging for state transitions and validations in Service and Controller layers. Ensure logs are contextual, redact sensitive fields (e.g., trackingNumber), and follow structured logging best practices.

-## 10) Migration (optional)
- [x] Add agent configuration for Mockito inline mocking to support JDK 22+ compatibility. Use -javaagent JVM argument if dynamic attachment is blocked.
- [x] If using Flyway: add migration for beer_order_shipment table with UUID PK and FK to beer_order(id)

## 11) Final Review
- [x] Build and run tests: mvn clean verify
- [x] Manually verify endpoints with MockMvc tests or local run
- [x] Lint/format as per project (e.g., mvn spotless:apply if configured)
