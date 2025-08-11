Title: Introduce DTOs for Beer API and MapStruct-based Mapping

Objective
- Stop exposing JPA entities from the web layer.
- Introduce purpose-built DTOs for request and response payloads in the Beer feature.
- Use MapStruct for mapping between DTOs and the Beer entity.
- Adjust controller and service layers to use DTOs at the boundaries and keep repository interactions unchanged.

Scope
- Feature: Beer
- Affected packages: tom.springframework.vibecodingmvc.controllers, services, entities, repositories
- New packages to add: 
  - tom.springframework.vibecodingmvc.models (DTOs)
  - tom.springframework.vibecodingmvc.mappers (MapStruct mappers)

Background and current state
- BeerController currently exposes and accepts the JPA entity (Beer) directly in all endpoints.
- BeerService and BeerServiceImpl operate with Beer entities.
- Lombok and MapStruct are already configured in the project’s pom.xml with spring component model for MapStruct processors.

Requirements
1. Define Beer DTOs
   - Create two DTOs under tom.springframework.vibecodingmvc.models using Lombok (record or class with @Data/@Builder is acceptable; prefer records for immutability unless builder is required). If using classes, annotate with @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor.
     a) BeerRequestDto: used for create and update requests (input from clients)
        - Fields (no id/version/timestamps):
          - String beerName
          - String beerStyle
          - String upc
          - Integer quantityOnHand
          - BigDecimal price
        - Validation (Jakarta Validation):
          - @NotBlank on beerName, beerStyle, upc
          - @PositiveOrZero on quantityOnHand
          - @DecimalMin(value = "0.0", inclusive = false) on price
     b) BeerResponseDto: used for responses back to clients
        - Fields (includes server-managed fields):
          - Integer id
          - Integer version
          - String beerName
          - String beerStyle
          - String upc
          - Integer quantityOnHand
          - BigDecimal price
          - LocalDateTime createdDate
          - LocalDateTime updatedDate

2. Create a MapStruct mapper
   - Package: tom.springframework.vibecodingmvc.mappers
   - Interface name: BeerMapper
   - Component model: spring (already configured by compiler arg)
   - Mapping methods:
     - Beer toEntity(BeerRequestDto dto)
       - Ignore id, version, createdDate, updatedDate (they are generated/managed by DB/Hibernate)
     - BeerResponseDto toResponseDto(Beer beer)
     - void updateEntityFromDto(BeerRequestDto dto, @MappingTarget Beer beer)
       - Ignore id, version, createdDate, updatedDate
   - Add @Mapping annotations as needed to ignore server-managed fields on entity updates.

3. Update the service layer to operate with DTO boundaries
   - BeerService interface should expose DTO-based signatures to the web layer:
     - List<BeerResponseDto> listBeers()
     - Optional<BeerResponseDto> getBeerById(Integer id)
     - BeerResponseDto saveBeer(BeerRequestDto dto)
     - Optional<BeerResponseDto> updateBeer(Integer id, BeerRequestDto dto)
     - void deleteBeer(Integer id)
   - BeerServiceImpl should:
     - Inject BeerRepository and BeerMapper via constructor injection.
     - For create: map BeerRequestDto -> Beer, save, then map saved entity -> BeerResponseDto.
     - For update: load entity by id; if present, apply updateEntityFromDto(dto, entity), save, map to BeerResponseDto, return Optional.of(dto); else return Optional.empty().
     - For list and get: map entities to response DTOs.

4. Update the controller to use DTOs and proper status codes
   - Controller: tom.springframework.vibecodingmvc.controllers.BeerController
   - Keep the same base path: /api/v1/beers
   - Replace entity usage with DTOs:
     - GET /api/v1/beers -> List<BeerResponseDto>
     - GET /api/v1/beers/{beerId} -> ResponseEntity<BeerResponseDto> (200 or 404)
     - POST /api/v1/beers -> @Valid BeerRequestDto as request body; return ResponseEntity<BeerResponseDto> with 201 Created
     - PUT /api/v1/beers/{beerId} -> @Valid BeerRequestDto; return 200 with body when updated, 404 when not found
     - DELETE /api/v1/beers/{beerId} -> 204 when deleted; 404 when not found (may check existence or use repository delete semantics + translate exceptions)
   - Ensure controller methods do not expose or accept the entity type.
   - Use ResponseEntity to return explicit status codes consistently.

5. Validation and error handling
   - Add @Valid to controller method parameters for request DTOs.
   - If a bean validation error occurs, let Spring’s default MethodArgumentNotValidException handling return 400; a future improvement can add a @RestControllerAdvice for a consistent ProblemDetails response.

6. Logging and null-safety
   - Use constructor injection (already used) and keep fields final where possible.
   - Do not log request bodies or sensitive data.
   - Avoid returning nulls from service methods; prefer Optional for lookups and absent updates.

7. Tests to update/add
   - Update BeerControllerTest to assert against DTO JSON structures (id in responses; request payloads do not include id/version/timestamps).
   - For create/update tests, send BeerRequestDto-shaped JSON (no id/version/createdDate/updatedDate) and expect BeerResponseDto-shaped JSON in responses.
   - Adjust service mocks in controller tests to use DTO types.
   - Service unit tests: where present, adapt to new DTO signatures and verify mapping is invoked appropriately (use simple assertions; MapStruct can be tested indirectly by verifying output fields).

8. Dependency notes (pom.xml)
   - Lombok and MapStruct are already declared.
   - The maven-compiler-plugin is configured to set mapstruct.defaultComponentModel=spring. No POM changes are required.

9. Acceptance criteria
   - No controller method accepts or returns tom.springframework.vibecodingmvc.entities.Beer.
   - All controller endpoints compile and pass updated tests using DTOs.
   - MapStruct mapper handles create and update flows with id/version/createdDate/updatedDate ignored when mapping from request DTOs to entities.
   - Validation is enforced on incoming requests; invalid payloads result in 400 responses by default.
   - All existing tests are updated and passing: mvn test succeeds.

Implementation Hints
- DTO visibility can be public; controllers and methods can remain package-private if desired by project conventions.
- For JSON property naming, keep camelCase as per current entity fields to minimize client impact.
- If switching BeerService signatures is too invasive for now, a minimal alternative is to keep BeerService working with the entity and perform DTO mapping entirely within the controller. However, the preferred approach for this task is to expose DTOs from the service boundary as specified above.
