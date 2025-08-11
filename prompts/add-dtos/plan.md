# Plan: Introduce DTOs for Beer API and MapStruct-based Mapping

Date: 2025-08-11 12:18 local
Branch: feature/add-dtos

Objective
- Stop exposing JPA entities in the web layer for the Beer feature.
- Introduce request/response DTOs and MapStruct mapper.
- Update service and controller boundaries to use DTOs, keep repository unchanged.
- Update tests accordingly.

Guiding Principles
- Follow Controller → Service → Repository layering; no direct repository calls in controllers.
- Constructor injection, package-private controllers/methods where appropriate.
- Validation at controller boundary with Jakarta Validation (@Valid).
- Centralize mapping via MapStruct (component model: spring).
- Do not log sensitive request body data.

Phase 1 — Model and Mapper (no behavior change visible externally)
1. Create DTO package: tom.springframework.vibecodingmvc.models
   - BeerRequestDto (input only)
     - Fields: beerName, beerStyle, upc, quantityOnHand, price
     - Validation: @NotBlank (beerName, beerStyle, upc), @PositiveOrZero (quantityOnHand), @DecimalMin(value = "0.0", inclusive = false) (price)
   - BeerResponseDto (output)
     - Fields: id, version, beerName, beerStyle, upc, quantityOnHand, price, createdDate, updatedDate
2. Create Mapper package: tom.springframework.vibecodingmvc.mappers
   - BeerMapper (interface)
     - Beer toEntity(BeerRequestDto dto)
       - Ignore id, version, createdDate, updatedDate
     - BeerResponseDto toResponseDto(Beer beer)
     - void updateEntityFromDto(BeerRequestDto dto, @MappingTarget Beer beer)
       - Ignore id, version, createdDate, updatedDate
3. Build to ensure MapStruct generates code (no tests need change yet).

Phase 2 — Service Layer DTO Boundary
1. Update BeerService signatures to use DTOs:
   - List<BeerResponseDto> listBeers()
   - Optional<BeerResponseDto> getBeerById(Integer id)
   - BeerResponseDto saveBeer(BeerRequestDto dto)
   - Optional<BeerResponseDto> updateBeer(Integer id, BeerRequestDto dto)
   - void deleteBeer(Integer id)
2. Update BeerServiceImpl:
   - Inject BeerRepository and BeerMapper via constructor.
   - Implement create: map request -> entity, save, map saved -> response.
   - Implement update: find by id; if present apply updateEntityFromDto, save, map -> response; else Optional.empty().
   - Implement list/get: map entities to response DTOs.
   - Avoid returning null; use Optional.

Phase 3 — Controller Adaptation and Validation
1. Update BeerController to use DTOs and explicit ResponseEntity statuses:
   - GET /api/v1/beers → List<BeerResponseDto>
   - GET /api/v1/beers/{beerId} → 200 with BeerResponseDto or 404
   - POST /api/v1/beers → @Valid BeerRequestDto; return 201 Created with BeerResponseDto
   - PUT /api/v1/beers/{beerId} → @Valid BeerRequestDto; return 200 with BeerResponseDto if updated or 404 if not found
   - DELETE /api/v1/beers/{beerId} → 204 No Content (404 if not found)
2. Apply @Valid to request DTO parameters.
3. Ensure controller does not mention the entity type in method signatures.

Phase 4 — Tests Update
1. Controller tests (MockMvc):
   - Adjust request payloads to BeerRequestDto shape (no id/version/timestamps).
   - Assert responses match BeerResponseDto JSON (id present for responses; timestamps included where applicable).
   - Mock service to return DTOs.
2. Service tests:
   - Update to use DTO inputs/outputs.
   - Verify mapping outcomes by field assertions; repository interactions remain the same.
3. Repository tests: unchanged.

Phase 5 — Documentation and Clean-up
1. README: briefly note DTO introduction for Beer endpoints and MapStruct usage.
2. Consider adding a GlobalExceptionHandler with ProblemDetails in a future task (out of scope here per requirements, default validation handling is acceptable).

Acceptance Criteria
- No controller method accepts or returns tom.springframework.vibecodingmvc.entities.Beer.
- Service layer exposes only DTOs to the web layer; repositories still use entities.
- MapStruct correctly ignores id, version, createdDate, updatedDate when mapping from BeerRequestDto.
- Validation applied; invalid payloads yield 400 via default handling.
- All tests updated to new DTO contracts; mvn test passes.

Impact & Risk Assessment
- Moderate refactor in service and controller; repository untouched.
- Backward compatibility: request bodies for POST/PUT must exclude id/version/timestamps; response bodies change to include metadata consistently.
- Mitigation: update tests and README; ensure status codes are explicit.

Task Breakdown (checklist)
- [ ] Add BeerRequestDto and BeerResponseDto under models with validation.
- [ ] Add BeerMapper with mappings and @MappingTarget update method.
- [ ] Refactor BeerService signatures and implementations to DTOs.
- [ ] Refactor BeerController to DTOs and ResponseEntity statuses.
- [ ] Update controller tests to DTO JSONs.
- [ ] Update service tests to DTO signatures.
- [ ] Run mvn test; fix any failing assertions.
- [ ] Update README with a short note about DTOs and mapper.

Out-of-Scope / Future Enhancements
- Global exception handling with ProblemDetails.
- Pagination for list endpoint.
- MapStruct mappers for other features.

Execution Notes
- Keep classes small and focused; prefer constructor injection.
- Use package-private visibility for controllers/handlers where possible.
- Maintain camelCase JSON to align with current naming.
