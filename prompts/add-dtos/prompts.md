# Prompt: Add DTOs to the Beer feature

Goal
- Introduce request/response DTOs for the Beer API and stop exposing JPA entities directly from controllers.

Context
- Tech: Java 21, Spring Boot, Maven, Spring Data JPA
- Follow project guidelines: controller -> service -> repository; constructor injection; clear validation at boundaries; map exceptions to proper HTTP status codes.

Scope
- Create BeerRequest and BeerResponse records (DTOs) under package: tom.springframework.vibecodingmvc.beer.dto (or similar feature package).
- Controller must consume BeerRequest and return ResponseEntity<BeerResponse>.
- Service should accept command objects (e.g., CreateBeerCommand, UpdateBeerCommand) and return BeerResponse or an internal model.
- Do not expose JPA entities in controllers.
- Add Jakarta Bean Validation annotations to BeerRequest.
- Add mappers (manual or MapStruct if already present). If MapStruct is not present, implement manual mapping for now.

Acceptance Criteria
- All BeerController endpoints use DTOs (no entity classes in method signatures or JSON payloads).
- Validation errors return HTTP 400 with a JSON error object.
- Success responses return appropriate status codes (201 for create, 200 for read/update, 204 for delete) with BeerResponse payload when applicable.
- All existing tests are updated or extended to reflect DTO usage and still pass with `mvn test`.

Notes
- Keep classes small and focused; prefer package-private visibility for controllers where possible.
- Disable OSIV is already recommended; ensure no lazy-init issues leak to web layer.
- Logging via SLF4J only; avoid sensitive data in logs.
