# Add Customer Feature — Task Checklist

Follow these atomic tasks to implement the Customer feature, mirroring the Beer feature conventions. Use the specified packages and paths. Mark done items with [x].

## 1) Flyway Migration
- [x] Determine next Flyway version N by checking existing files under: src/main/resources/db/migration (e.g., V1__init.sql → next is V2__customer.sql)
- [x] Create migration: src/main/resources/db/migration/V2__customer.sql
- [x] Add table SQL (H2/Postgres-friendly):
  - [x] CREATE TABLE customer with columns: id (IDENTITY, PK), version (NOT NULL DEFAULT 0), name, email, phone, address_line1, address_line2, city, state, postal_code, created_date (DEFAULT CURRENT_TIMESTAMP), updated_date (DEFAULT CURRENT_TIMESTAMP)
  - [x] Add unique index on email: CREATE UNIQUE INDEX uq_customer_email ON customer(email)
- [ ] Start app (mvn spring-boot:run) and verify Flyway applies the migration successfully

## 2) Entity
- [x] Add entity class: src/main/java/tom/springframework/vibecodingmvc/entities/Customer.java (package: tom.springframework.vibecodingmvc.entities)
- [x] Map table and columns:
  - [x] @Entity, @Table(name = "customer")
  - [x] Fields: Integer id (@Id @GeneratedValue), Integer version (@Version), String name, String email, String phone, String addressLine1 (@Column(name = "address_line1")), String addressLine2 (@Column(name = "address_line2")), String city, String state, String postalCode (@Column(name = "postal_code"))
  - [x] createdDate, updatedDate with @CreationTimestamp and @UpdateTimestamp using LocalDateTime or OffsetDateTime consistent with Beer entity
- [ ] Implement equals/hashCode by id only; toString without sensitive data

## 3) DTOs (records) with Validation and Schema
- [x] Create request DTO: src/main/java/tom/springframework/vibecodingmvc/models/CustomerRequestDto.java (package: tom.springframework.vibecodingmvc.models)
  - [x] Use record with fields: name, email, phone, addressLine1, addressLine2, city, state, postalCode
  - [x] Add Jakarta Validation: @NotBlank/@Size on name and addressLine1; @NotBlank @Email @Size on email; @Size max constraints on others
  - [x] Add Springdoc @Schema for each field with description and example values
- [x] Create response DTO: src/main/java/tom/springframework/vibecodingmvc/models/CustomerResponseDto.java (package: tom.springframework.vibecodingmvc.models)
  - [x] Use record with fields: id, version, name, email, phone, addressLine1, addressLine2, city, state, postalCode, createdDate, updatedDate
  - [x] Add Springdoc @Schema for each field with description and example values

## 4) MapStruct Mapper
- [x] Create mapper interface: src/main/java/tom/springframework/vibecodingmvc/mappers/CustomerMapper.java (package: tom.springframework.vibecodingmvc.mappers)
  - [x] Annotate with @Mapper(componentModel = "spring")
  - [x] Define: Customer toEntity(CustomerRequestDto dto)
  - [x] Define: CustomerResponseDto toResponse(Customer entity)
  - [x] Define: void update(@MappingTarget Customer entity, CustomerRequestDto dto)
  - [x] Use @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) on update to ignore nulls

## 5) Repository
- [x] Add repository interface: src/main/java/tom/springframework/vibecodingmvc/repositories/CustomerRepository.java (package: tom.springframework.vibecodingmvc.repositories)
  - [x] extend JpaRepository<Customer, Integer>
  - [x] Add boolean existsByEmail(String email)
  - [x] (Optional) Add Optional<Customer> findByEmail(String email)

## 6) Service Layer
- [x] Define service interface: src/main/java/tom/springframework/vibecodingmvc/services/CustomerService.java (package: tom.springframework.vibecodingmvc.services)
  - [x] Methods: List<CustomerResponseDto> list(); Optional<CustomerResponseDto> get(Integer id); CustomerResponseDto create(CustomerRequestDto dto); Optional<CustomerResponseDto> update(Integer id, CustomerRequestDto dto); boolean delete(Integer id)
- [x] Implement service: src/main/java/tom/springframework/vibecodingmvc/services/impl/CustomerServiceImpl.java (package: tom.springframework.vibecodingmvc.services.impl)
  - [x] Constructor inject CustomerRepository and CustomerMapper
  - [x] @Transactional(readOnly = true) on list() and get()
  - [x] @Transactional on create(), update(), delete()
  - [x] create(): enforce unique email via repository.existsByEmail(email); map dto→entity; save; return mapper.toResponse
  - [x] update(id,dto): load entity; if not found return Optional.empty(); if email changed, enforce uniqueness; mapper.update(entity,dto); save; return Optional.of(mapper.toResponse)
  - [x] delete(id): if exists then delete and return true; else return false

## 7) Controller (REST)
- [x] Create controller: src/main/java/tom/springframework/vibecodingmvc/controllers/CustomerController.java (package: tom.springframework.vibecodingmvc.controllers)
  - [x] Class-level annotations: @RestController, @RequestMapping("/api/v1/customers"), @Tag(name = "Customers", description = "Operations for managing customers")
  - [x] GET /api/v1/customers → 200 List<CustomerResponseDto>
    - [x] Method: list(); return ResponseEntity.ok(service.list())
    - [x] Add @Operation(summary = "List customers") and @ApiResponses(200)
  - [x] GET /api/v1/customers/{id} → 200 or 404 (no body)
    - [x] Method: get(@PathVariable Integer id); return ok or notFound()
    - [x] Add @Operation(summary = "Get customer by id") and @ApiResponses(200, 404)
  - [x] POST /api/v1/customers → 201 with body and Location header
    - [x] Method: create(@Valid @RequestBody CustomerRequestDto dto)
    - [x] Build Location: /api/v1/customers/{id}
    - [x] Add @Operation(summary = "Create customer") and @ApiResponses(201, 400)
  - [x] PUT /api/v1/customers/{id} → 200 or 404
    - [x] Method: update(@PathVariable Integer id, @Valid @RequestBody CustomerRequestDto dto)
    - [x] Add @Operation(summary = "Update customer") and @ApiResponses(200, 400, 404)
  - [x] DELETE /api/v1/customers/{id} → 204 or 404
    - [x] Method: delete(@PathVariable Integer id); return noContent() or notFound()
    - [x] Add @Operation(summary = "Delete customer") and @ApiResponses(204, 404)

## 8) Springdoc/OpenAPI Annotations (Controller)
- [x] Ensure each controller method has @Operation(summary, description) explaining behavior
- [x] Add @ApiResponses with @ApiResponse codes: 200/201 (with content = CustomerResponseDto), 204 (no content), 400 (validation error description), 404 (not found, no body)
- [x] Use @Parameter for path variables if additional description is helpful

## 9) OpenAPI (Redocly split)
- [x] Add schemas:
  - [x] openapi-starter-main/openapi/components/schemas/CustomerRequestDto.yaml
  - [x] openapi-starter-main/openapi/components/schemas/CustomerResponseDto.yaml
- [x] Add paths:
  - [x] openapi-starter-main/openapi/paths/api_v1_customers.yaml (GET list, POST create)
  - [x] openapi-starter-main/openapi/paths/api_v1_customers_{id}.yaml (GET, PUT, DELETE)
- [x] Wire into root spec: openapi-starter-main/openapi/openapi.yaml (add $ref entries under paths and components.schemas)
- [ ] Validate Redocly: in openapi-starter-main run npm install (once) and npm test

## 10) Tests
- [x] Controller tests (MockMvc): src/test/java/tom/springframework/vibecodingmvc/controllers/CustomerControllerTest.java
  - [x] Test list → 200 and expected JSON shape
  - [x] Test get existing → 200; get missing → 404
  - [x] Test create valid → 201 with Location; create invalid → 400
  - [x] Test update existing valid → 200; update missing → 404; invalid → 400
  - [x] Test delete existing → 204; delete missing → 404
- [ ] Service tests: src/test/java/tom/springframework/vibecodingmvc/services/CustomerServiceImplTest.java
  - [ ] Mock repository; verify unique email on create/update; mapping and results
- [ ] Repository slice/integration test (optional): src/test/java/tom/springframework/vibecodingmvc/repositories/CustomerRepositoryTest.java verifying existsByEmail

## 11) Configuration & Conventions
- [ ] Ensure constructor injection usage across components
- [ ] Prefer package-private visibility where feasible (controllers/methods/config)
- [ ] Confirm spring.jpa.open-in-view=false in application.properties (or add if missing)

## 12) Manual Verification
- [ ] Run: mvn spring-boot:run — observe Flyway migration applied for customer table
- [ ] Verify endpoints with curl/Postman:
  - [ ] POST /api/v1/customers (valid body) → 201 + Location header
  - [ ] GET /api/v1/customers/{id} → 200
  - [ ] GET /api/v1/customers → 200
  - [ ] PUT /api/v1/customers/{id} → 200
  - [ ] DELETE /api/v1/customers/{id} → 204
  - [ ] Missing id → 404; invalid request → 400
- [ ] Check Swagger UI shows Customers tag and operations
- [ ] In openapi-starter-main: npm test passes; optionally npm start to preview on a port (e.g., 8089)
