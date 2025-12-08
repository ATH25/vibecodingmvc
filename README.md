<h1>Vibecoding MVC</h1>

Simple Spring Boot MVC application to explore clean layering (Controller → Service → Repository), Java 21 features, and API-first development with OpenAPI.

This README focuses on high-level understanding, setup, usage, and contribution. For maintenance history and security notes, see `CHANGELOG.md` and `SECURITY.md`.

## Overview

The app exposes a small set of RESTful APIs for a beer catalog and customers. It uses Spring Data JPA with an in-memory H2 database for local development and tests. OpenAPI (Redocly) is used to document and validate the API.

## Tech stack

- Java 21, Maven
- Spring Boot 3 (Web, Validation, Data JPA)
- H2 Database (dev/test)
- Flyway (migrations)
- MapStruct (DTO mapping)
- JUnit 5, Spring Test (MockMvc)
- Redocly (OpenAPI lint/preview)

## Frontend Technology Stack

The project now includes a React-based frontend built using modern, production‑ready tooling.  
These technologies will be used as part of the Vibe Coding workflow for building the UI:

- **React** – Component-based UI library
- **Vite** – Fast dev server & build tool for modern frontend applications
- **Radix UI** – Accessible, unstyled UI primitives
- **shadcn/ui** – Reusable, customizable components built on Radix
- **Tailwind CSS** – Utility-first CSS framework used for styling shadcn/ui components

All frontend source code will live under:  
`src/main/frontend/`

A Maven plugin will build the frontend and copy its bundled assets into  
`src/main/resources/static`, which Spring Boot serves automatically.

## Frontend Requirements

Before running or building the UI, verify the following are installed:

- **npm 11 or higher**
- **Node.js 22 or higher**

## Prerequisites

- JDK 21
- Maven 3.9+
- Node.js 18+ (only for OpenAPI lint/preview)

## Run & Build

- Dev run: `mvn spring-boot:run`
- Package: `mvn -DskipTests package`
- Run jar: `java -jar target/*-SNAPSHOT.jar`

Default local URL: `http://localhost:8080`

## API usage examples

### Beer API

Base URL: `/api/v1/beers`

- List beers (paged):
  ```bash
  curl -s "http://localhost:8080/api/v1/beers?page=0&size=10"
  ```
- Filter by name:
  ```bash
  curl -s "http://localhost:8080/api/v1/beers?beerName=Galaxy"
  ```
- Get by id:
  ```bash
  curl -s "http://localhost:8080/api/v1/beers/1"
  ```
- Create:
  ```bash
  curl -i -H "Content-Type: application/json" \
    -d '{"beerName":"New Beer","beerStyle":"IPA","upc":"0123456789012","quantityOnHand":100,"price":12.99}' \
    http://localhost:8080/api/v1/beers
  ```
- Update:
  ```bash
  curl -i -X PUT -H "Content-Type: application/json" \
    -d '{"beerName":"Updated","beerStyle":"PALE_ALE","upc":"0123456789012","quantityOnHand":120,"price":10.50}' \
    http://localhost:8080/api/v1/beers/1
  ```
- Delete:
  ```bash
  curl -i -X DELETE http://localhost:8080/api/v1/beers/1
  ```

### Customer API

Base URL: `/api/v1/customers`

- List: `curl -s http://localhost:8080/api/v1/customers`
- Get by id: `curl -s http://localhost:8080/api/v1/customers/1`
- Create:
  ```bash
  curl -i -H "Content-Type: application/json" \
    -d '{"name":"Jane Doe","email":"jane@example.com"}' \
    http://localhost:8080/api/v1/customers
  ```
- Update:
  ```bash
  curl -i -X PUT -H "Content-Type: application/json" \
    -d '{"name":"Janet","email":"janet@example.com"}' \
    http://localhost:8080/api/v1/customers/1
  ```
- Delete: `curl -i -X DELETE http://localhost:8080/api/v1/customers/1`

> For additional endpoints (e.g., nested resources like shipments), see the OpenAPI specification.

## OpenAPI validation

Location: `openapi-starter-main/openapi/openapi.yaml` (split files under `openapi-starter-main/openapi/paths` and `openapi-starter-main/openapi/components`).

Validate/lint the spec:

```bash
cd openapi-starter-main
npm install
npm test
```

Optional preview:

```bash
npm start
```

## Testing

- Run all tests: `mvn test`
- One class: `mvn -Dtest=tom.springframework.vibecodingmvc.controllers.BeerControllerTest test`
- One method: `mvn -Dtest=BeerControllerTest#listBeers_returnsPaged_withFilter test`
- Reports: `target/surefire-reports`

## Contributing

- Workflow
  - Fork and create a topic branch: `feature/<short>`, `fix/<ticket>`, `chore/<task>`
  - Keep PRs small, focused, and include tests when applicable
  - Use imperative commit messages (e.g., `Add Beer update endpoint`)
- Required checks before pushing / opening a PR
  - Backend tests: `mvn test` (or `mvn clean verify`)
  - OpenAPI validation: `cd openapi-starter-main && npm install && npm test`
    - In short: run `mvn test` and `npm test` to validate code and OpenAPI specs before submitting a PR
- PR checklist
  - Tests added/updated as needed and all tests pass
  - OpenAPI updated for any API changes and lints clean (Redocly)
  - No breaking API changes unless documented and agreed in advance

## Configuration notes (local dev)

- DB: H2 in-memory
- Flyway migrations: `src/main/resources/db/migration`
- JPA: `ddl-auto=validate`
- OSIV disabled: `spring.jpa.open-in-view=false`

---

For maintenance history and security guidance, see:

- `CHANGELOG.md`
- `SECURITY.md`
