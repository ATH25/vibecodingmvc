# VibeCodingMVC 🎵🚀

A Spring Boot 3 (3.5.4) project on Java 21 to experiment with “vibe coding” — building modern Java apps with focus, flow, and fun.

Updated: 2025-11-24

## 🔧 Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Web, Spring Data JPA, Validation
- H2 (in-memory; auto-configured) for local/dev and tests
- Maven

## ✅ Prerequisites

- JDK 21
- Maven 3.9+

Verify locally:
- java -version
- mvn -version

## 📁 Project Structure

```
vibecodingmvc/
├── pom.xml
├── README.md
├── HELP.md
├── openapi-starter-main/
│   └── openapi/ (split OpenAPI spec: openapi.yaml, paths/, components/)
├── plan/
│   └── README-plan.md
├── prompts/
│   ├── add-customer/
│   │   ├── plan.md
│   │   ├── requirements.md
│   │   └── tasks.md
│   ├── applied-prompts/
│   │   └── add-beer-order-shipment/
│   │       ├── plan.md
│   │       └── tasks.md
│   └── refactor-nested-rest-paths/
│       └── prompt.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tom/springframework/vibecodingmvc/
│   │   │       ├── VibecodingmvcApplication.java
│   │   │       ├── controllers/
│   │   │       │   ├── BeerController.java
│   │   │       │   ├── CustomerController.java
│   │   │       │   ├── BeerOrderController.java
│   │   │       │   └── BeerOrderShipmentController.java
│   │   │       ├── entities/
│   │   │       │   ├── Beer.java
│   │   │       │   ├── Customer.java
│   │   │       │   ├── BeerOrder.java
│   │   │       │   ├── BeerOrderLine.java
│   │   │       │   ├── BeerOrderShipment.java
│   │   │       │   └── ShipmentStatus.java
│   │   │       ├── models/ (DTOs & records)
│   │   │       │   ├── BeerRequestDto.java
│   │   │       │   ├── BeerResponseDto.java
│   │   │       │   ├── CustomerRequestDto.java
│   │   │       │   ├── CustomerResponseDto.java
│   │   │       │   ├── CreateBeerOrderCommand.java
│   │   │       │   ├── CreateBeerOrderItem.java
│   │   │       │   ├── BeerOrderResponse.java
│   │   │       │   ├── BeerOrderLineResponse.java
│   │   │       │   ├── BeerOrderShipmentDto.java
│   │   │       │   ├── BeerOrderShipmentCreateDto.java
│   │   │       │   └── BeerOrderShipmentUpdateDto.java
│   │   │       ├── mappers/
│   │   │       │   ├── BeerMapper.java
│   │   │       │   ├── CustomerMapper.java
│   │   │       │   ├── BeerOrderMapper.java
│   │   │       │   └── BeerOrderShipmentMapper.java
│   │   │       ├── repositories/
│   │   │       │   ├── BeerRepository.java
│   │   │       │   ├── CustomerRepository.java
│   │   │       │   ├── BeerOrderRepository.java
│   │   │       │   ├── BeerOrderLineRepository.java
│   │   │       │   └── BeerOrderShipmentRepository.java
│   │   │       └── services/
│   │   │           ├── BeerService.java
│   │   │           ├── BeerServiceImpl.java
│   │   │           ├── CustomerService.java
│   │   │           ├── impl/CustomerServiceImpl.java
│   │   │           ├── BeerOrderService.java
│   │   │           ├── BeerOrderServiceImpl.java
│   │   │           ├── BeerOrderShipmentService.java
│   │   │           └── impl/BeerOrderShipmentServiceImpl.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   │           ├── V1__init.sql
│   │           ├── V2__customer.sql
│   │           └── V3__beer_order_shipment.sql
│   └── test/
│       ├── java/
│       │   └── tom/springframework/vibecodingmvc/
│       │       ├── VibecodingmvcApplicationTests.java
│       │       ├── controllers/BeerControllerTest.java
│       │       ├── controllers/BeerOrderShipmentControllerTest.java
│       │       ├── repositories/BeerRepositoryTest.java
│       │       ├── services/BeerServiceTest.java
│       │       ├── services/impl/BeerOrderShipmentServiceImplTest.java
│       │       └── mappers/BeerOrderShipmentMapperTest.java
│       └── resources/
│           └── application.properties
```

Layering: Controller → Service → Repository. Constructor injection is used across services and controllers.

## 🚀 Run & Build

- Dev run (auto-configured H2):
  - mvn spring-boot:run
- Package without tests:
  - mvn -DskipTests package
- Run packaged jar:
  - java -jar target/*-SNAPSHOT.jar

Default server port is 8080 (Spring Boot default). No external DB needed for local run; H2 in-memory will be auto-configured.

## 🧪 Testing

- All tests:
  - mvn test
- Single test class:
  - mvn -Dtest=tom.springframework.vibecodingmvc.controllers.BeerControllerTest test
- Single test method:
  - mvn -Dtest=BeerControllerTest#listBeers_returnsOk test
- Reports: target/surefire-reports

Tests use MockMvc for controller and H2 (create-drop) for repository/data interactions.

Additional coverage:
- BeerOrderShipmentControllerTest (MockMvc, nested paths)
- BeerOrderShipmentServiceImplTest (service rules and transitions)
- BeerOrderShipmentMapperTest (MapStruct mapping)

## 🍺 Beer API

Base URL: /api/v1/beers

Entity fields:
- id, version (managed)
- beerName, beerStyle, upc, quantityOnHand, price
- createdDate, updatedDate (timestamps)

Endpoints:
- GET /api/v1/beers
  - Description: List all beers
  - 200 OK -> JSON array of beers
  - curl: curl -s http://localhost:8080/api/v1/beers

- GET /api/v1/beers/{beerId}
  - Description: Get a beer by id
  - 200 OK -> Beer JSON, or 404 if not found
  - curl: curl -i http://localhost:8080/api/v1/beers/1

- POST /api/v1/beers
  - Description: Create a beer
  - 201 Created -> created Beer JSON
  - Body example:
    {
      "beerName": "Hoppy Trails",
      "beerStyle": "IPA",
      "upc": "123456789012",
      "quantityOnHand": 100,
      "price": 5.99
    }
  - curl:
    curl -i -H "Content-Type: application/json" \
      -d '{"beerName":"Hoppy Trails","beerStyle":"IPA","upc":"123456789012","quantityOnHand":100,"price":5.99}' \
      http://localhost:8080/api/v1/beers

- PUT /api/v1/beers/{beerId}
  - Description: Update an existing beer (full replace of updatable fields)
  - 200 OK with updated Beer JSON, or 404 if not found
  - curl:
    curl -i -X PUT -H "Content-Type: application/json" \
      -d '{"beerName":"Hoppy Trails 2","beerStyle":"IPA","upc":"123456789012","quantityOnHand":90,"price":6.49}' \
      http://localhost:8080/api/v1/beers/1

- DELETE /api/v1/beers/{beerId}
  - Description: Delete a beer
  - 204 No Content on success, 404 if not found
  - curl: curl -i -X DELETE http://localhost:8080/api/v1/beers/1

Notes:
- The API now uses DTOs: BeerRequestDto for inputs and BeerResponseDto for outputs. Validation is applied on inputs (@NotBlank, @PositiveOrZero, @DecimalMin>0). Mapping is done via MapStruct.
- Errors return standard HTTP status codes (404 on missing resources).

## 📦 Beer Order Shipments API

Base URL (nested): `/api/v1/beerorders/{beerOrderId}/shipments`

Endpoints:
- POST `/api/v1/beerorders/{beerOrderId}/shipments`
  - Create a shipment for a beer order. Returns `201 Created` with `Location` header of the new resource.
- GET `/api/v1/beerorders/{beerOrderId}/shipments`
  - List all shipments for a beer order. Returns `200 OK` with JSON array.
- GET `/api/v1/beerorders/{beerOrderId}/shipments/{id}`
  - Get a specific shipment. Returns `200 OK` or `404 Not Found`.
- PATCH `/api/v1/beerorders/{beerOrderId}/shipments/{id}`
  - Partial update of shipment status/metadata. Returns `204 No Content`.
- DELETE `/api/v1/beerorders/{beerOrderId}/shipments/{id}`
  - Delete a shipment. Returns `204 No Content`.

Business rules:
- If `beerOrderId` in the path does not exist, endpoints return `404 Not Found`.
- Default `shipmentStatus` is `PENDING` on create if not provided.
- When moving to `IN_TRANSIT` or later, `trackingNumber` and `carrier` are required; `shippedDate` will be set if missing.
- If status becomes `DELIVERED` and `shippedDate` is missing, it will be set.

DTOs:
- BeerOrderShipmentCreateDto
  - Fields: `beerOrderId` (Integer, required), `shipmentStatus` (ShipmentStatus, required), `shippedDate` (LocalDateTime, optional), `trackingNumber` (String), `carrier` (String), `notes` (String)
- BeerOrderShipmentDto (response)
  - Fields: `id` (Integer), `beerOrderId` (Integer), `shipmentStatus` (String), `shippedDate` (LocalDateTime), `trackingNumber` (String), `carrier` (String), `notes` (String)
- BeerOrderShipmentUpdateDto
  - Fields: `shipmentStatus` (String), `shippedDate` (LocalDateTime), `trackingNumber` (String), `carrier` (String), `notes` (String)

Example curl:
- Create
  ```bash
  curl -i -H "Content-Type: application/json" \
    -d '{
      "beerOrderId": 1,
      "shipmentStatus": "PENDING",
      "trackingNumber": null,
      "carrier": null,
      "notes": "Initial fulfillment"
    }' \
    http://localhost:8080/api/v1/beerorders/1/shipments
  ```

- List by order
  ```bash
  curl -s http://localhost:8080/api/v1/beerorders/1/shipments | jq
  ```

- Get by id
  ```bash
  curl -i http://localhost:8080/api/v1/beerorders/1/shipments/10
  ```

- Move to IN_TRANSIT
  ```bash
  curl -i -X PATCH -H "Content-Type: application/json" \
    -d '{
      "shipmentStatus": "IN_TRANSIT",
      "trackingNumber": "1Z999AA10123456784",
      "carrier": "UPS"
    }' \
    http://localhost:8080/api/v1/beerorders/1/shipments/10
  ```

- Delete
  ```bash
  curl -i -X DELETE http://localhost:8080/api/v1/beerorders/1/shipments/10
  ```

## 🧑‍🤝‍🧑 Customer API

Base URL: /api/v1/customers

Endpoints:
- GET /api/v1/customers
- GET /api/v1/customers/{id}
- POST /api/v1/customers
- PUT /api/v1/customers/{id}
- DELETE /api/v1/customers/{id}

Examples:
- List: curl -s http://localhost:8080/api/v1/customers
- Create:
  curl -i -H "Content-Type: application/json" \
    -d '{"name":"Jane Doe","email":"jane@example.com"}' \
    http://localhost:8080/api/v1/customers

Notes:
- Uses DTOs: CustomerRequestDto (input), CustomerResponseDto (output) with validation.
- See OpenAPI docs below for full request/response schemas and errors.

## ✨ Project Goals

- Explore Spring Boot MVC
- Practice Java 21 features
- Keep changes small, layered, and test-backed
- Build something fun and technically solid

## 🤝 Contributing

- Branch naming: feature/<short>, fix/<ticket>, chore/<task>
- Commits: small, imperative (e.g., "Add Beer update endpoint")
- Before pushing: mvn test
- Keep PRs focused; include tests where applicable

## 📖 OpenAPI documentation

- Location: openapi-starter-main/openapi/openapi.yaml (root) with split files under openapi-starter-main/openapi/paths and openapi-starter-main/openapi/components (schemas, responses, headers).
- Validate/lint the spec (run once per clone to install deps):
  - cd openapi-starter-main
  - npm install
  - npm test
- Preview locally (optional):
  - npm start
- Path file naming convention: mirror the URL with '/' replaced by '_' and keep path params in braces, e.g. /api/v1/beers/{beerId} -> paths/api_v1_beers_{beerId}.yaml

Shipment-specific OpenAPI files:
- Paths:
  - `openapi-starter-main/openapi/paths/api_v1_beerorders_{beerOrderId}_shipments.yaml`
  - `openapi-starter-main/openapi/paths/api_v1_beerorders_{beerOrderId}_shipments_{id}.yaml`
- Schemas:
  - `openapi-starter-main/openapi/components/schemas/BeerOrderShipmentCreateDto.yaml`
  - `openapi-starter-main/openapi/components/schemas/BeerOrderShipmentDto.yaml`

## 📌 Operational Notes

- DB: H2 in-memory for dev/tests.
- Flyway: enabled; migrations exist under src/main/resources/db/migration (e.g., V1__init.sql, V2__customer.sql, V3__beer_order_shipment.sql). JPA ddl-auto=validate.
- OSIV: disabled (spring.jpa.open-in-view=false).
- Exception handling: Plan to add a GlobalExceptionHandler to standardize error responses.

## 🆕 What's New

- Added Beer Order Shipments with nested RESTful paths under `/api/v1/beerorders/{beerOrderId}/shipments`.
- New DTOs: `BeerOrderShipmentCreateDto`, `BeerOrderShipmentUpdateDto`, `BeerOrderShipmentDto`.
- New controller/service/repository/entity: `BeerOrderShipmentController`, `BeerOrderShipmentService` + `BeerOrderShipmentServiceImpl`, `BeerOrderShipmentRepository`, `BeerOrderShipment` entity and `ShipmentStatus` enum.
- Business rules enforced at service layer (requires tracking/carrier when `IN_TRANSIT+`).
- MapStruct mapper `BeerOrderShipmentMapper` for DTO↔entity.
- Flyway migration `V3__beer_order_shipment.sql` to create tables and indexes.
- Tests added for controller, service, and mapper.
- OpenAPI spec updated with shipment paths and schemas.

## Using Junie

- When writing prompts for Junie:
  - Be explicit about files, packages, and tests to create/modify.
  - Keep changes minimal and scoped; follow Controller → Service → Repository layering.
  - After changes, run mvn test and review diffs before committing.
- Optional: To use Junie in IntelliJ IDEA, read the official guide: https://www.jetbrains.com/guide/ai/article/junie/intellij-idea/
