# Add Customer Feature — Implementation Plan

This plan outlines the minimal-yet-complete steps to add a fully functional Customer feature to the project, mirroring the Beer feature patterns and adhering to the repository’s guidelines (Java 21, Spring Boot, Maven, DTOs, MapStruct, Springdoc, Redocly split OpenAPI, Flyway, tests).

## 1. Database and Migrations
- Next Flyway version is `V2` (since `V1__init.sql` already exists).
- Create `src/main/resources/db/migration/V2__create_customer_table.sql` with:
  - `customer` table (H2/Postgres-friendly SQL) and a unique index on `email`.
  - Columns: id (identity, PK), version (@Version), name, email, phone, address_line1, address_line2, city, state, postal_code, created_date, updated_date with defaults.
- Verify app starts and Flyway applies migration; keep `spring.jpa.hibernate.ddl-auto=validate` (or project’s default) to validate schema.

## 2. Domain Model (Entity)
- Package: `tom.springframework.vibecodingmvc.entities`.
- Add `Customer` entity:
  - Fields: id (Integer, @Id @GeneratedValue), version (@Version), name, email, phone, addressLine1, addressLine2, city, state, postalCode, createdDate, updatedDate.
  - Map to table/columns: address_line1, address_line2, postal_code via `@Column(name="...")`.
  - Timestamps: `@CreationTimestamp` and `@UpdateTimestamp` with `OffsetDateTime` or `LocalDateTime` consistent with Beer entity conventions.
  - Equals/hashCode by id only; toString without sensitive data.

## 3. DTOs and Validation
- Package: `tom.springframework.vibecodingmvc.models`.
- Add request record `CustomerRequestDto` with Jakarta Validation:
  - name: @NotBlank @Size(max=120)
  - email: @NotBlank @Email @Size(max=255)
  - phone: @Size(max=40)
  - addressLine1: @NotBlank @Size(max=200)
  - addressLine2: @Size(max=200)
  - city: @Size(max=120)
  - state: @Size(max=80)
  - postalCode: @Size(max=20)
- Add response record `CustomerResponseDto` with all readable fields (id, version, name, email, phone, address*, city, state, postalCode, createdDate, updatedDate).
- Add Springdoc @Schema annotations with descriptions/examples matching realistic data.

## 4. MapStruct Mapper
- Package: `tom.springframework.vibecodingmvc.mappers`.
- Add `CustomerMapper` interface:
  - `Customer toEntity(CustomerRequestDto dto)`
  - `CustomerResponseDto toResponse(Customer entity)`
  - `void update(@MappingTarget Customer entity, CustomerRequestDto dto)` ignoring nulls:
    - Use `@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)`.
  - Ensure field name mappings for snake_case columns handled at entity level (mapper maps object fields).

## 5. Repository
- Package: `tom.springframework.vibecodingmvc.repositories`.
- Create `CustomerRepository extends JpaRepository<Customer, Integer>`.
- Add `boolean existsByEmail(String email)` and `Optional<Customer> findByEmail(String email)` if helpful.

## 6. Service Layer
- Package: `tom.springframework.vibecodingmvc.services`.
- Define `CustomerService` interface with methods:
  - `List<CustomerResponseDto> list()`
  - `Optional<CustomerResponseDto> get(Integer id)`
  - `CustomerResponseDto create(CustomerRequestDto dto)`
  - `Optional<CustomerResponseDto> update(Integer id, CustomerRequestDto dto)`
  - `boolean delete(Integer id)`
- Implementation package: `tom.springframework.vibecodingmvc.services.impl`.
- `CustomerServiceImpl` (constructor injection):
  - Enforce unique email on create (use `existsByEmail`).
  - On update: allow same email if unchanged; if changed, verify uniqueness.
  - Map DTO↔Entity via mapper; persist via repository.
  - Transactional boundaries: readOnly for reads; @Transactional for create/update/delete.

## 7. Controller (REST API)
- Package: `tom.springframework.vibecodingmvc.controllers`.
- `CustomerController` with base path `/api/v1/customers` using `@RequestMapping("/api/v1/customers")`.
- Endpoints and status codes:
  - GET `/` → 200: list of CustomerResponseDto.
  - GET `/{id}` → 200 with body or 404 no body.
  - POST `/` → 201 with body; include `Location` header `/api/v1/customers/{id}`.
  - PUT `/{id}` → 200 with body or 404 no body.
  - DELETE `/{id}` → 204 no body or 404 no body.
- Use DTOs; validate request with `@Valid`.
- Springdoc:
  - `@Tag(name = "Customers", description = "Operations for managing customers")`
  - `@Operation` and `@ApiResponses` for 200/201 bodies; 204/404/400 as described (no body unless global ProblemDetails is used elsewhere).
- Use ResponseEntity to control statuses; do not expose entities.

## 8. Global Exception Handling (if missing)
- Reuse existing global handlers (ProblemDetails) if present; otherwise keep controller returning 400 on validation via Spring’s default and 404 by empty body ResponseEntity.

## 9. OpenAPI (Redocly split structure)
- Files under `openapi-starter-main/openapi`:
  - Paths:
    - `paths/api_v1_customers.yaml` (GET list, POST create)
    - `paths/api_v1_customers_{id}.yaml` (GET, PUT, DELETE)
  - Schemas:
    - `components/schemas/CustomerRequestDto.yaml`
    - `components/schemas/CustomerResponseDto.yaml`
- Reference these in `openapi/openapi.yaml` under `paths` and `components.schemas`.
- Keep 400/404/204 as description-only responses unless standardized Problem is already used.
- Run `npm install` once in `openapi-starter-main` and `npm test` (Redocly lint) to validate.

## 10. Tests
- Controller tests (MockMvc):
  - Validate status codes, JSON payloads, and validation errors (400 for bad input).
- Service unit tests:
  - Mock repository; test create/update unique email logic and basic CRUD transformations.
- Repository slice tests (optional):
  - Verify `existsByEmail` works with embedded DB.
- Ensure app starts and migration runs in test context; prefer Testcontainers for integration tests if the project already uses it; otherwise rely on H2 defaults.

## 11. Configuration & Conventions
- Follow constructor injection; package-private visibility where possible for controllers and configuration.
- Keep logging via SLF4J; avoid sensitive data.
- Ensure `spring.jpa.open-in-view=false` (project guideline) and adapt fetch strategies accordingly.

## 12. Manual Verification Checklist
- Start app: `mvn spring-boot:run`; confirm Flyway applies customer migration.
- Exercise endpoints with curl/Postman:
  - POST create → 201 and Location header; GET by id → 200; list → 200; PUT → 200; DELETE → 204; non-existent → 404.
- Swagger UI shows Customers tag and operations.
- Redocly: `npm test` and optionally `npm start` in `openapi-starter-main`.

## 13. Delivery Scope Minimization
- Implement minimal code to satisfy acceptance checks; defer pagination, filtering, auth, and PATCH to later iterations.
- Reuse existing project patterns (Beer feature) for consistency and speed.
