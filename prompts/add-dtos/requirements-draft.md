# Requirements Draft: Introduce DTOs for Beer API

Objective
- Replace direct exposure of the Beer JPA entity in the web layer with DTOs and command objects.

Functional Requirements
- Create DTOs:
  - BeerRequest (input): name, style, upc, price, quantityOnHand
  - BeerResponse (output): id, name, style, upc, price, quantityOnHand, createdDate, lastModifiedDate
- Validation:
  - Apply Jakarta Validation on BeerRequest (e.g., @NotBlank name, @NotNull price, limits on lengths and numeric ranges).
  - On validation failure, return HTTP 400 with a JSON error object (ProblemDetails or a simple error structure).
- Controller:
  - Accept BeerRequest for create/update endpoints.
  - Return ResponseEntity<BeerResponse> on success with correct HTTP status codes.
- Service:
  - Introduce CreateBeerCommand and UpdateBeerCommand records mirroring needed inputs.
  - Service methods accept commands and return BeerResponse or a domain model mapped back to BeerResponse.
- Mapping:
  - Provide mapping between Entity ↔ DTO (manual or MapStruct if available).

Non-Functional Requirements
- Follow constructor injection.
- Keep controller/service methods package-private where feasible.
- Ensure no entity classes are exposed in controller method signatures or JSON payloads.
- Update or add tests to reflect DTO usage (MockMvc for controller tests).

Acceptance Criteria
- All Beer endpoints exclusively use DTOs (requests/responses).
- Validation errors produce 400 responses with structured JSON errors.
- `mvn test` passes.
- README updated if public API shapes changed.
