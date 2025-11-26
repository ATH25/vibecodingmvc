

## Refactor Plan: Nested REST Path for BeerOrderShipment

### Objective
Refactor the existing BeerOrderShipmentController to use a more RESTful, nested resource path structure under its parent BeerOrder.

### Motivation
Align API path structure with best practices documented in `guidelines.md`, improving clarity, REST semantics, and scoping. The current path structure uses a top-level path with query parameters, which obscures the resource hierarchy.

### Current State
Endpoints follow this structure:
- GET /api/v1/beerorder-shipments?beerOrderId=123
- POST /api/v1/beerorder-shipments
- PATCH /api/v1/beerorder-shipments/{id}
- DELETE /api/v1/beerorder-shipments/{id}

### Proposed Changes
Refactor all endpoints to be nested under the parent BeerOrder:
- GET /api/v1/beerorders/{beerOrderId}/shipments
- POST /api/v1/beerorders/{beerOrderId}/shipments
- PATCH /api/v1/beerorders/{beerOrderId}/shipments/{id}
- DELETE /api/v1/beerorders/{beerOrderId}/shipments/{id}

### Affected Components
- `BeerOrderShipmentController.java`
- Test files: `BeerOrderShipmentControllerTest.java`
- Possibly: service layer method signatures (depending on existing assumptions)

### Compatibility
Ensure backward compatibility is not required, as this change breaks existing consumers of the old path. If compatibility is needed, both routes should temporarily be supported.

### Prompting Guidance for Junie
> “Update the controller so that all endpoints for BeerOrderShipment are nested under their parent BeerOrder, using path parameters like /api/v1/beerorders/{beerOrderId}/shipments. Avoid using query params for lookup when the parent ID is known.”