### Overview
Below is a practical checklist of the files you should add for a new `BeerOrderShipment` feature, following the project’s layering and conventions (Java 21, Spring Boot, Maven, Spring Data JPA, MapStruct, DTOs, Controller → Service → Repository). The purpose of each file is explained so you can create them quickly and consistently.

---

### Domain layer (entities and enums)
- `src/main/java/tom/springframework/vibecodingmvc/entities/BeerOrderShipment.java`
  - JPA entity for shipments tied to a `BeerOrder`.
  - Typical fields: `id`, `version`, timestamps (`createdDate`, `updatedDate`), relationship to `BeerOrder` (e.g., `@ManyToOne`), `trackingNumber`, `carrier`, `status` (enum), `shippedAt`, `deliveredAt`, `notes`.
  - Add optimistic locking via `@Version`. Use package-private visibility where possible.

- `src/main/java/tom/springframework/vibecodingmvc/entities/ShipmentStatus.java`
  - Enum for shipment lifecycle: `CREATED`, `READY`, `SHIPPED`, `IN_TRANSIT`, `DELIVERED`, `RETURNED`, `CANCELLED`.
  - Keeps status values consistent across DTOs, persistence, and business logic.

---

### Persistence layer
- `src/main/java/tom/springframework/vibecodingmvc/repositories/BeerOrderShipmentRepository.java`
  - Extends `JpaRepository<BeerOrderShipment, Integer>`.
  - Add query methods as needed (e.g., `findByBeerOrderId(Integer orderId)`, paging variants) to support listing by order and status.

---

### DTOs (request/response)
- `src/main/java/tom/springframework/vibecodingmvc/models/BeerOrderShipmentRequestDto.java`
  - Request payload for create/update.
  - Fields likely allowed from clients: `trackingNumber`, `carrier`, `status` (optional on create), `shippedAt` (optional), `deliveredAt` (service-driven), `notes`.
  - Add Jakarta Validation, e.g., `@NotBlank` for `trackingNumber` and `carrier` as applicable.

- `src/main/java/tom/springframework/vibecodingmvc/models/BeerOrderShipmentResponseDto.java`
  - Response payload returned by API.
  - Fields: `id`, `version`, `orderId`, `trackingNumber`, `carrier`, `status`, `shippedAt`, `deliveredAt`, `createdDate`, `updatedDate`, `notes`.

- (Optional per use case) Command objects
  - `CreateShipmentCommand`, `UpdateShipmentCommand`, or small records to drive service methods. Useful if you want stronger separation between API and domain logic.

---

### Mapping (MapStruct)
- `src/main/java/tom/springframework/vibecodingmvc/mappers/BeerOrderShipmentMapper.java`
  - MapStruct interface to map between entity and DTOs.
  - Methods: `toEntity(BeerOrderShipmentRequestDto dto)`, `toResponseDto(BeerOrderShipment entity)`, `updateEntityFromDto(BeerOrderShipmentRequestDto dto, @MappingTarget BeerOrderShipment entity)`.
  - Follow the same pattern seen in existing mappers (e.g., `CustomerMapper`).

---

### Service layer
- `src/main/java/tom/springframework/vibecodingmvc/services/BeerOrderShipmentService.java`
  - Service interface defining use cases.
  - Common methods:
    - `BeerOrderShipmentResponseDto createShipment(Integer orderId, BeerOrderShipmentRequestDto dto)`
    - `BeerOrderShipmentResponseDto getShipment(Integer shipmentId)`
    - `Page<BeerOrderShipmentResponseDto> listShipmentsByOrder(Integer orderId, Pageable pageable)`
    - `BeerOrderShipmentResponseDto updateShipment(Integer shipmentId, BeerOrderShipmentRequestDto dto)`
    - `void deleteShipment(Integer shipmentId)`
    - Convenience methods if needed: `markShipped(...)`, `markDelivered(...)`, `updateTracking(...)`.
  - Annotate read operations with `@Transactional(readOnly = true)` and write operations with `@Transactional`.

- `src/main/java/tom/springframework/vibecodingmvc/services/impl/BeerOrderShipmentServiceImpl.java`
  - Implements the interface.
  - Inject `BeerOrderShipmentRepository`, `BeerOrderRepository` (to validate `orderId`), and `BeerOrderShipmentMapper` via constructor injection.
  - Enforce validations, status transitions, and set timestamps like `shippedAt`/`deliveredAt` appropriately.

---

### Web layer (REST controller)
- `src/main/java/tom/springframework/vibecodingmvc/controllers/BeerOrderShipmentController.java`
  - REST API endpoints. Prefer versioned, resource-oriented URLs.
  - If modeling as sub-resource of orders:
    - `POST /api/v1/beer-orders/{orderId}/shipments` → create
    - `GET /api/v1/beer-orders/{orderId}/shipments` → list by order (paged)
    - `GET /api/v1/shipments/{shipmentId}` → get by id
    - `PUT /api/v1/shipments/{shipmentId}` → update
    - `DELETE /api/v1/shipments/{shipmentId}` → delete
    - Optional actions: `POST /api/v1/shipments/{shipmentId}/deliver`, `POST /api/v1/shipments/{shipmentId}/ship`
  - Return `ResponseEntity` with proper status codes (201 on create, 200 on read/update, 204 on delete).
  - Validate request DTOs with `@Valid`.

- (Optional) Controller advice
  - If not already present globally, ensure `@RestControllerAdvice` maps domain exceptions (e.g., `NotFoundException`, `IllegalStateException`) to consistent error responses.

---

### Database migrations (Flyway)
- `src/main/resources/db/migration/V<next_version>__add_beer_order_shipment.sql`
  - Create table `beer_order_shipment` with columns: `id` (PK), `version`, audit timestamps, FK to `beer_order` (e.g., `beer_order_id`), `tracking_number`, `carrier`, `status`, `shipped_at`, `delivered_at`, `notes`.
  - Add indexes commonly queried: `beer_order_id`, `status`, maybe `tracking_number`.
  - Keep `spring.jpa.hibernate.ddl-auto=validate` and let Flyway own schema changes.

---

### OpenAPI (Redocly split spec)
- `openapi-starter-main/openapi/paths/api_v1_beer-orders_{orderId}_shipments.yaml`
  - Path items for collection under an order: `GET` (list), `POST` (create).

- `openapi-starter-main/openapi/paths/api_v1_shipments_{shipmentId}.yaml`
  - Path items for single shipment: `GET`, `PUT`, `DELETE` and optional action endpoints if you separate them.

- `openapi-starter-main/openapi/components/schemas/BeerOrderShipmentResponseDto.yaml`
  - Schema mirroring your response DTO.

- `openapi-starter-main/openapi/components/schemas/BeerOrderShipmentRequestDto.yaml`
  - Schema for request with validation hints and examples.

Update the root `openapi.yaml` to $ref these path files and schemas. Run `npm test` under `openapi-starter-main` to lint.

---

### Tests
- `src/test/java/.../controllers/BeerOrderShipmentControllerTest.java`
  - MockMvc tests for endpoints: create, get, list by order (paged), update, delete, and validation errors. Verify status codes and payload shape.

- `src/test/java/.../services/impl/BeerOrderShipmentServiceImplTest.java`
  - Unit tests with mocked repositories. Cover happy paths and edge cases (invalid orderId, invalid status transitions, missing tracking number, etc.).

- `src/test/java/.../repositories/BeerOrderShipmentRepositoryTest.java` (optional slice/integration)
  - Repository slice tests or Testcontainers-backed integration tests for queries and constraints.

---

### Wiring and conventions checklist
- Constructor injection for all components; keep classes/methods package-private where possible.
- Do not expose entities directly from controllers—always return DTOs.
- Transactions at service layer with `@Transactional` boundaries.
- Validation annotations on request DTOs; map to proper HTTP codes in controller.
- Logging via SLF4J; no sensitive data in logs.
- If OSIV is disabled (`spring.jpa.open-in-view=false`), fetch needed associations explicitly in service.

---

### Minimal file list (at a glance)
- entities: `BeerOrderShipment.java`, `ShipmentStatus.java`
- repositories: `BeerOrderShipmentRepository.java`
- models: `BeerOrderShipmentRequestDto.java`, `BeerOrderShipmentResponseDto.java`
- mappers: `BeerOrderShipmentMapper.java`
- services: `BeerOrderShipmentService.java`, `impl/BeerOrderShipmentServiceImpl.java`
- controllers: `BeerOrderShipmentController.java`
- db migration: `V<next>__add_beer_order_shipment.sql`
- openapi: path files + schemas (4 files) and root references
- tests: controller, service, and optional repository tests

This structure mirrors how your existing features are organized and will let you implement the shipment flow cleanly with clear boundaries and tests.