# VibeCodingMVC 🎵🚀

A Spring Boot 3 (3.5.4) project on Java 21 to experiment with “vibe coding” — building modern Java apps with focus, flow, and fun.

Updated: 2025-08-11

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
├── prompts/
│   └── add-dtos/
│       ├── prompts.md
│       ├── requirements.md
│       └── requirements-draft.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tom/springframework/vibecodingmvc/
│   │   │       ├── VibecodingmvcApplication.java
│   │   │       ├── controllers/
│   │   │       │   └── BeerController.java
│   │   │       ├── entities/
│   │   │       │   └── Beer.java
│   │   │       ├── repositories/
│   │   │       │   └── BeerRepository.java
│   │   │       └── services/
│   │   │           ├── BeerService.java
│   │   │           └── BeerServiceImpl.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       │   └── tom/springframework/vibecodingmvc/
│       │       ├── VibecodingmvcApplicationTests.java
│       │       ├── controllers/BeerControllerTest.java
│       │       ├── repositories/BeerRepositoryTest.java
│       │       └── services/BeerServiceTest.java
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

## 📌 Operational Notes

- DB: H2 in-memory for dev/tests; Flyway is present but no migrations yet.
- OSIV: Consider setting spring.jpa.open-in-view=false in future.
- Exception handling: Add a GlobalExceptionHandler when introducing DTOs/validation.

## Using Junie

- New contributors: start with the project guidelines at [.junie/guidelines.md](.junie/guidelines.md) for structure, commands, and best practices.
- To use Junie in IntelliJ IDEA, read the official guide: https://www.jetbrains.com/guide/ai/article/junie/intellij-idea/
- When writing prompts for Junie:
  - Be explicit about files, packages, and tests to create/modify.
  - Keep changes minimal and scoped; follow Controller → Service → Repository layering.
  - After changes, run mvn test and review diffs before committing.
