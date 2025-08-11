# Tasks — Add DTOs for Beer API and MapStruct Mapping

Date: 2025-08-11 14:58 local
Branch: feature/add-dtos
Source plan: prompts/add-dtos/plan.md

1. Phase 1 — Model and Mapper (no externally visible behavior change)
   1.1. Models package setup
   - [ ] Create package `tom.springframework.vibecodingmvc.models`.
   - [ ] Add BeerRequestDto (input only) with fields: beerName, beerStyle, upc, quantityOnHand, price.
   - [ ] Add validation on BeerRequestDto: @NotBlank (beerName, beerStyle, upc), @PositiveOrZero (quantityOnHand), @DecimalMin(value = "0.0", inclusive = false) (price).
   - [ ] Add BeerResponseDto (output) with fields: id, version, beerName, beerStyle, upc, quantityOnHand, price, createdDate, updatedDate.

   1.2. Mapper package and interface
   - [ ] Create package `tom.springframework.vibecodingmvc.mappers`.
   - [ ] Define `BeerMapper` (MapStruct, componentModel = "spring").
   - [ ] Add `Beer toEntity(BeerRequestDto dto)`; ignore id, version, createdDate, updatedDate.
   - [ ] Add `BeerResponseDto toResponseDto(Beer beer)`.
   - [ ] Add `void updateEntityFromDto(BeerRequestDto dto, @MappingTarget Beer beer)`; ignore id, version, createdDate, updatedDate.

   1.3. Build verification
   - [ ] Ensure MapStruct annotation processing is configured (via Maven/Boot parent) and perform a build to generate mapper implementations.

2. Phase 2 — Service Layer DTO Boundary
   2.1. API surface changes
   - [ ] Update `BeerService` signatures to use DTOs:
     - [ ] `List<BeerResponseDto> listBeers()`
     - [ ] `Optional<BeerResponseDto> getBeerById(Integer id)`
     - [ ] `BeerResponseDto saveBeer(BeerRequestDto dto)`
     - [ ] `Optional<BeerResponseDto> updateBeer(Integer id, BeerRequestDto dto)`
     - [ ] `void deleteBeer(Integer id)`

   2.2. Implementation updates
   - [ ] Inject `BeerRepository` and `BeerMapper` via constructor in `BeerServiceImpl`.
   - [ ] Implement create: map request DTO -> entity, save, map saved entity -> response DTO.
   - [ ] Implement update: find by id; if present apply `updateEntityFromDto`, save, map -> response DTO; else return `Optional.empty()`.
   - [ ] Implement list/get: map entities to `BeerResponseDto`.
   - [ ] Avoid returning null; use `Optional` as specified.

3. Phase 3 — Controller Adaptation and Validation
   - [ ] Update `BeerController` to use DTOs exclusively and return explicit `ResponseEntity` statuses:
     - [ ] `GET /api/v1/beers` → 200 OK with `List<BeerResponseDto>`.
     - [ ] `GET /api/v1/beers/{beerId}` → 200 OK with `BeerResponseDto` or 404 Not Found.
     - [ ] `POST /api/v1/beers` → accepts `@Valid BeerRequestDto`; returns 201 Created with `BeerResponseDto`.
     - [ ] `PUT /api/v1/beers/{beerId}` → accepts `@Valid BeerRequestDto`; returns 200 OK with `BeerResponseDto` if updated or 404 Not Found.
     - [ ] `DELETE /api/v1/beers/{beerId}` → 204 No Content (404 if not found).
   - [ ] Apply `@Valid` to request DTO parameters.
   - [ ] Ensure controller signatures do not expose entity `tom.springframework.vibecodingmvc.entities.Beer`.

4. Phase 4 — Tests Update
   4.1. Controller tests (MockMvc)
   - [ ] Adjust request payloads to match BeerRequestDto shape (no id/version/timestamps).
   - [ ] Assert responses match BeerResponseDto JSON (id present; timestamps where applicable).
   - [ ] Mock service to return DTOs instead of entities.

   4.2. Service tests
   - [ ] Update tests to call service methods with DTO inputs and verify DTO outputs.
   - [ ] Verify mapping outcomes by field assertions; repository interactions remain unchanged.

   4.3. Repository tests
   - [ ] Leave repository tests unchanged; re-run to ensure they still pass.

   4.4. Build & fix
   - [ ] Run `mvn test`; fix any failing assertions and adjust stubs/mocks accordingly.

5. Phase 5 — Documentation and Clean-up
   - [ ] Update README with a brief note about DTO introduction for Beer endpoints and MapStruct usage.
   - [ ] Leave GlobalExceptionHandler and ProblemDetails for a future task (not in scope now).

6. Acceptance Criteria Verification
   - [ ] Confirm no controller method accepts or returns `tom.springframework.vibecodingmvc.entities.Beer`.
   - [ ] Confirm service layer exposes only DTOs to the web layer; repositories still use entities.
   - [ ] Confirm MapStruct ignores id, version, createdDate, updatedDate when mapping from BeerRequestDto.
   - [ ] Confirm validation: invalid payloads yield 400 via default handling.
   - [ ] Confirm all updated tests pass (`mvn test`).
