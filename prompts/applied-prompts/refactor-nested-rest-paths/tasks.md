
# Tasks for Refactoring BeerOrderShipmentController to Use Nested REST Paths

## Preparation
- [x] Review `BeerOrderShipmentController.java` to identify all existing endpoints using top-level paths.
- [x] Confirm parent-child relationship between `BeerOrder` and `BeerOrderShipment` in the data model.

## Refactor Path Design
- [x] Update controller method paths to follow the nested pattern:
    - Replace `/api/v1/beerorder-shipments` with `/api/v1/beerorders/{beerOrderId}/shipments`
    - Ensure all CRUD methods reflect the new nesting.
- [x] Adjust method parameters to accept `beerOrderId` from the path.

## DTO and Service Updates
- [x] Update service method usage in controller to pass `beerOrderId` where necessary; service signatures unchanged as DTO already contains `beerOrderId`.
- [x] Validate consistency between path `beerOrderId` and request body in create.

## Tests
- [x] Update `BeerOrderShipmentControllerTest.java` to reflect new endpoint paths.
- [x] Run and verify tests; ensure all assertions pass with the new path structure.
- [x] Add new tests for nested resource path validation (e.g., invalid `beerOrderId` scenarios).

## Documentation
- [x] Update OpenAPI split path files to reflect new nested paths, and update root `openapi.yaml` references.
- [ ] Regenerate OpenAPI spec if needed and confirm changes are reflected in Redocly.

## Guidelines
- [x] Confirm `guidelines.md` includes the nested resource path best practice.
