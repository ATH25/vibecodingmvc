# Implementation Plan: BeerOrderShipment Feature

This plan translates the requirements for the BeerOrderShipment feature into concrete implementation steps, aligned with the project’s conventions and the Junie Developer Guidelines.

Tech context and conventions to follow
- Java 21, Spring Boot, Maven
- Layered architecture: Controller → Service → Repository; controllers must not directly use repositories
- Prefer constructor injection; make Spring components package‑private when possible
- DTOs as Java records under models package; validation with Jakarta Validation
- MapStruct for mappers under mappers package
- Transactions at the Service layer; read vs write transactional boundaries
- OpenAPI split spec under openapi-starter-main; validate with Redocly
- Tests: MockMvc for controller, unit tests for service/mapper; keep OSIV disabled


1. Domain model (Entity + Enum)
- Package: tom.springframework.vibecodingmvc.entities
- Enum: ShipmentStatus
  - Values: PENDING, PACKED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
  - Keep in package tom.springframework.vibecodingmvc.entities (or a subpackage like .enums if present). Use public enum.
- Entity: BeerOrderShipment
  - Fields
    - id: UUID (primary key, auto-generated via @GeneratedValue with UUID strategy)
    - beerOrder: BeerOrder (ManyToOne, required)
    - shipmentStatus: ShipmentStatus (EnumType.STRING, default PENDING)
    - shippedDate: LocalDateTime (nullable)
    - trackingNumber: String (non-null on certain statuses; validated at DTO/service level)
    - carrier: String (optional but recommended with trackingNumber)
    - notes: String (optional, @Column(length = X) if a cap is desired)
    - createdDate, updatedDate: auditing timestamps if project already uses them (mirror other entities)
  - JPA mapping details
    - @Entity, @Table(name = "beer_order_shipment")
    - @ManyToOne(fetch = LAZY) @JoinColumn(name = "beer_order_id", nullable = false)
    - @Enumerated(EnumType.STRING)
    - Indexes: add @Index on beer_order_id and shipment_status if commonly queried
  - Lombok
    - @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor (mirror existing entity style)
  - Validation/business notes
    - shippedDate can be null until status indicates shipment progressed
    - trackingNumber and carrier should be present when status ≥ IN_TRANSIT (enforce at service)


2. Repository
- Package: tom.springframework.vibecodingmvc.repositories
- Interface: BeerOrderShipmentRepository extends JpaRepository<BeerOrderShipment, UUID>
- Query methods
  - List<BeerOrderShipment> findByBeerOrder_Id(Integer beerOrderId)
  - Optional<BeerOrderShipment> findByTrackingNumber(String trackingNumber) (optional convenience)
  - Page<BeerOrderShipment> findByBeerOrder_Id(Integer beerOrderId, Pageable pageable) (optional for future pagination)


3. DTOs (records under models)
- Package: tom.springframework.vibecodingmvc.models
- BeerOrderShipmentDto (response)
  - UUID id
  - Integer beerOrderId
  - String shipmentStatus
  - LocalDateTime shippedDate
  - String trackingNumber
  - String carrier
  - String notes
- BeerOrderShipmentCreateDto (request)
  - Integer beerOrderId (required, positive)
  - String shipmentStatus (optional; default PENDING if null)
  - LocalDateTime shippedDate (optional)
  - String trackingNumber (optional; required depending on status)
  - String carrier (optional; required depending on status)
  - String notes (optional, max length if enforced)
  - Validation: @NotNull, @Positive for beerOrderId; use custom validation in service for status-dependent fields
- BeerOrderShipmentUpdateDto (request for partial or full updates)
  - String shipmentStatus (optional)
  - LocalDateTime shippedDate (optional)
  - String trackingNumber (optional)
  - String carrier (optional)
  - String notes (optional)
  - Note: Support PATCH semantics in service by applying only non-null fields
- Naming note
  - Keep DTO names as per requirements; place in models to align with project pattern of records.


4. Mapper (MapStruct)
- Package: tom.springframework.vibecodingmvc.mappers
- Interface: BeerOrderShipmentMapper
  - BeerOrderShipmentDto toDto(BeerOrderShipment entity)
  - BeerOrderShipment toEntity(BeerOrderShipmentCreateDto dto)
  - void updateEntity(@MappingTarget BeerOrderShipment entity, BeerOrderShipmentUpdateDto dto) — apply non-null fields only
- Mapping specifics
  - Map beerOrder.id ↔ beerOrderId
  - Convert enum to String for DTOs and back for entity
  - Null handling: ignore nulls in updateEntity; consider @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
- Component model: componentModel = "spring"


5. Service Layer
- Package: tom.springframework.vibecodingmvc.services
- Interface: BeerOrderShipmentService
  - UUID create(BeerOrderShipmentCreateDto dto)
  - Optional<BeerOrderShipmentDto> get(UUID id)
  - List<BeerOrderShipmentDto> listByBeerOrderId(Integer beerOrderId)
  - void update(UUID id, BeerOrderShipmentUpdateDto dto)
  - void delete(UUID id)
- Implementation: BeerOrderShipmentServiceImpl (package-private)
  - Dependencies (constructor-injected, final)
    - BeerOrderShipmentRepository
    - BeerOrderRepository (to validate beerOrder existence)
    - BeerOrderShipmentMapper
  - Transactions
    - @Transactional on create, update, delete
    - @Transactional(readOnly = true) on get and list
  - Business rules
    - Validate beerOrderId exists before create; throw NotFound or IllegalArgument with clear message
    - Default shipmentStatus to PENDING if not provided
    - Status transition checks (basic):
      - If setting to IN_TRANSIT or beyond, require trackingNumber and carrier
      - If setting to DELIVERED, ensure shippedDate is set (set now if null)
    - Set shippedDate when moving from PENDING/PACKED to IN_TRANSIT if not provided
  - Error handling
    - Map domain exceptions to appropriate HTTP in controller advice (reuse existing handler if present)


6. Controller (REST)
- Package: tom.springframework.vibecodingmvc.controllers
- Class: BeerOrderShipmentController (package‑private)
- Base path: /api/v1/beerorder-shipments
- Endpoints and behaviors
  - POST /api/v1/beerorder-shipments
    - Body: BeerOrderShipmentCreateDto
    - 201 Created with Location: /api/v1/beerorder-shipments/{id}; body BeerOrderShipmentDto
    - 400 on validation errors; 404 if beerOrderId does not exist
  - GET /api/v1/beerorder-shipments/{id}
    - 200 with BeerOrderShipmentDto, 404 if not found
  - GET /api/v1/beerorder-shipments?beerOrderId={id}
    - 200 with array of BeerOrderShipmentDto for the given order; validate beerOrderId is positive
  - PATCH /api/v1/beerorder-shipments/{id}
    - Body: BeerOrderShipmentUpdateDto; 204 No Content on success
    - 400 on invalid transitions; 404 if shipment not found
  - DELETE /api/v1/beerorder-shipments/{id}
    - 204 No Content; 404 if not found
- Controller details
  - Use ResponseEntity; constructor injection; @Validated
  - Add minimal OpenAPI annotations (optional because split OpenAPI will be authoritative)


7. OpenAPI (Redocly split)
- Paths
  - File: openapi-starter-main/openapi/paths/api_v1_beerorder-shipments.yaml
    - Define POST, GET (by id and by beerOrderId query param), PATCH, DELETE
    - Use $ref to component schemas for requests/responses
  - Optionally, add path for nested listing: /api/v1/beer-orders/{orderId}/shipments (alias) if desired later; for now stick to /beerorder-shipments with beerOrderId filter as per requirements
- Components (schemas)
  - File(s):
    - openapi-starter-main/openapi/components/schemas/BeerOrderShipmentDto.yaml
    - openapi-starter-main/openapi/components/schemas/BeerOrderShipmentCreateDto.yaml
    - openapi-starter-main/openapi/components/schemas/BeerOrderShipmentUpdateDto.yaml
  - Define properties, required fields, enums (ShipmentStatus)
  - Reuse common Problem response if present for errors
- Validation
  - Run in openapi-starter-main: npm install (once) and npm test (redocly lint)


8. Testing strategy
- Mapper unit tests
  - Verify entity ↔ DTO conversions; enum mapping; null-ignore update behavior
- Service unit tests
  - Mock BeerOrderShipmentRepository and BeerOrderRepository
  - Create: valid path, missing beerOrderId (404), status requiring tracking/carrier (400), default PENDING
  - Update: invalid id (404), invalid status transition (400), delivered without shippedDate (auto-set or 400 based on rule)
  - Delete: existing and non-existing
- Controller integration tests (MockMvc)
  - POST happy path → 201 with Location and body
  - GET by id → 200; not found → 404
  - GET by beerOrderId → 200 array
  - PATCH → 204; invalid payload → 400; not found → 404
  - DELETE → 204; not found → 404
  - Validate JSON structure matches DTO schemas; use ObjectMapper and JSONPath as in existing tests
- OpenAPI lint
  - Ensure added path and schemas pass redocly lint


9. Error handling and responses
- Reuse/extend existing GlobalExceptionHandler if present; otherwise include minimal mapping in controller for NotFound and Validation exceptions to standard ProblemDetails
- Do not expose internal details; return consistent error JSON


10. Logging and security
- Use SLF4J logging in service for notable state changes (status transitions); avoid sensitive data
- No authentication scopes specified; ensure actuator exposure remains controlled per guideline


11. Non-functional considerations
- OSIV disabled; ensure all needed data is fetched within transactional methods
- Keep JSON property casing consistent (camelCase)
- Keep classes package‑private where possible (controllers, service impl)


12. File checklist (summary)
- src/main/java/.../entities/ShipmentStatus.java
- src/main/java/.../entities/BeerOrderShipment.java
- src/main/java/.../repositories/BeerOrderShipmentRepository.java
- src/main/java/.../models/BeerOrderShipmentDto.java
- src/main/java/.../models/BeerOrderShipmentCreateDto.java
- src/main/java/.../models/BeerOrderShipmentUpdateDto.java
- src/main/java/.../mappers/BeerOrderShipmentMapper.java
- src/main/java/.../services/BeerOrderShipmentService.java
- src/main/java/.../services/BeerOrderShipmentServiceImpl.java
- src/main/java/.../controllers/BeerOrderShipmentController.java
- openapi-starter-main/openapi/components/schemas/BeerOrderShipmentDto.yaml
- openapi-starter-main/openapi/components/schemas/BeerOrderShipmentCreateDto.yaml
- openapi-starter-main/openapi/components/schemas/BeerOrderShipmentUpdateDto.yaml
- openapi-starter-main/openapi/paths/api_v1_beerorder-shipments.yaml
- Tests under src/test/java mirroring packages for mapper, service, controller


13. Migration and data (optional)
- If using Flyway in this module, add migration for beer_order_shipment table with UUID PK and FK to beer_order(id). Otherwise rely on JPA schema generation in dev.


14. Implementation order (suggested)
1) Entity + Enum + Repository
2) DTOs + Mapper
3) Service + Unit tests
4) Controller + MockMvc tests
5) OpenAPI files + lint
6) Review, format, and finalize
