

# Requirements: Beer Order Shipment Feature

## Objective
Implement a new BeerOrderShipment domain feature to manage and track the shipment status of beer orders. This feature will support CRUD operations and tie directly to existing BeerOrder entities.

## Entities to be Added
- `BeerOrderShipment`
  - `id`: UUID (auto-generated)
  - `beerOrderId`: UUID (foreign key reference to BeerOrder)
  - `shipmentStatus`: Enum with values representing the shipment lifecycle:
    - `PENDING`: Shipment is created but not yet processed
    - `PACKED`: Shipment is packed and ready for carrier pickup
    - `IN_TRANSIT`: Shipment has been picked up and is in transit
    - `OUT_FOR_DELIVERY`: Shipment is near destination and out for delivery
    - `DELIVERED`: Shipment has been successfully delivered
    - `CANCELLED`: Shipment has been canceled
  - `shippedDate`: LocalDateTime (nullable)
  - `trackingNumber`: String
  - `carrier`: String
  - `notes`: String (optional)

## Java Classes Required
- Entity: `BeerOrderShipment`
- Repository: `BeerOrderShipmentRepository`
- DTOs:
  - `BeerOrderShipmentDto`
  - `BeerOrderShipmentCreateDto`
  - `BeerOrderShipmentUpdateDto`
- Mapper: `BeerOrderShipmentMapper` (with MapStruct)
- Service Interface: `BeerOrderShipmentService`
- Service Implementation: `BeerOrderShipmentServiceImpl`
- Controller: `BeerOrderShipmentController`
- OpenAPI YAMLs:
  - Path: `/api/v1/beerorder-shipments`
  - Components: Request/Response schemas for Dtos

## Functionality
- **Create** a new shipment for an existing BeerOrder
- **Read** a single shipment or list of shipments by BeerOrder ID
- **Update** shipment status and tracking details
- **Delete** a shipment record (optional)
- Validate that `beerOrderId` exists before creating shipment
- Ensure transactional consistency between BeerOrder and shipment if updated together

## Testing
- Unit tests for Service and Mapper
- Integration tests for Controller
- OpenAPI test coverage with `redocly lint`

## Notes
- Align with existing project conventions (e.g., Lombok, JPA annotations, layered architecture)
- Add OpenAPI docs and bundle them via Redocly after implementation