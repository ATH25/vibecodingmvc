<h1>Vibecoding MVC</h1>

[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)](#cicd)

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

## Running the app

This application can be run in two primary ways depending on whether you are developing the frontend or validating the production setup.

### Quick start (production-style)

Use this mode to run the application exactly as it would behave in production, with Spring Boot serving the compiled React frontend.

```bash
cd src/main/frontend
npm run build

# from project root
./mvnw spring-boot:run
```

Open the application at:
- http://localhost:8080

### Development mode (recommended for UI work)

Use this mode when actively developing the frontend. Vite provides hot module replacement (HMR) and fast feedback while proxying API calls to Spring Boot.

```bash
# terminal 1 (backend)
./mvnw spring-boot:run

# terminal 2 (frontend)
cd src/main/frontend
npm run dev
```

Open the application at:
- Frontend (Vite dev server): http://localhost:5173
- Backend API (Spring Boot): http://localhost:8080

In development mode, frontend requests to `/api/*` are proxied by Vite to the Spring Boot backend, avoiding CORS issues.

### Running the application (Dev vs Production)

This project supports two ways to run the frontend. Both render the same UI and use the same APIs — the difference is how the frontend is served.

#### Frontend development mode (Vite)

Use this mode when actively developing or debugging the UI. It provides fast startup and hot module replacement (HMR).

1. Start the backend:
   ```bash
   mvn spring-boot:run
   ```

2. In a separate terminal, start the frontend dev server:
   ```bash
   cd src/main/frontend
   npm run dev
   ```

- Frontend URL: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- API requests are proxied from Vite to Spring Boot (`/api` → `http://localhost:8080`)

Changes to frontend code are reflected immediately without rebuilding.

---

#### Production-style mode (Spring Boot serving React)

Use this mode to test the application as it will run in production.

1. Build the frontend:
   ```bash
   cd src/main/frontend
   npm run build
   ```

2. Start the backend:
   ```bash
   mvn spring-boot:run
   ```

- Application URL: `http://localhost:8080`
- React assets are served from `src/main/resources/static`

In this mode, Spring Boot serves the compiled React application directly.

---

**Note:**  
Both modes display the same UI. If both servers are running, the pages may look identical — this is expected. Use port `5173` for frontend development and port `8080` for production-style validation.

## CI/CD

This project’s CI should validate and build both backend and frontend.

Recommended steps (GitHub Actions or similar):

1. Set up JDK 21 and cache the Maven repository (`~/.m2`).
2. Setup Node.js 22.16.0 and cache `src/main/frontend/node_modules` using `package-lock.json`.
3. Frontend quality checks:
   - `npm --prefix src/main/frontend ci`
   - `npm --prefix src/main/frontend run lint` (Linting)
   - `npm --prefix src/main/frontend test` (Unit tests)
   - `npm --prefix src/main/frontend run build` (Production build)
4. Backend build:
   - `mvn -DskipTests package` (or `mvn verify` for full test run)

Integration via Maven (frontend-maven-plugin):

- The Maven build is configured to run the frontend steps during `mvn package` using `frontend-maven-plugin` with `workingDirectory=src/main/frontend`.
- Executions include: install Node/NPM, `npm install`, `npm test`, and `npm run build`.
- This ensures the final JAR contains the compiled frontend assets under `src/main/resources/static`.

Badge note:

- Replace the badge link at the top with your repository’s live workflow badge once CI is set up, e.g.:
  `https://github.com/<owner>/<repo>/actions/workflows/<workflow>.yml/badge.svg?branch=main`.

OpenAPI TypeScript types:

- Generate types from the OpenAPI spec into `src/main/frontend/src/types/generated`:
  ```bash
  cd src/main/frontend
  npm run api:generate
  ```
- When to regenerate:
  - Whenever `openapi-starter-main/openapi/openapi.yaml` (or its split files) change.
  - Commit the generated files to keep builds reproducible.

Git hooks (Husky):

- On commit, Husky runs frontend lint and format to enforce code quality:
  - Hook file: `.husky/pre-commit`
  - Commands executed: `npm --prefix src/main/frontend run lint` and `npm --prefix src/main/frontend run format`
  - If hooks don’t run, ensure you’ve installed deps and Husky is enabled: `cd src/main/frontend && npm install` (this triggers `prepare` script)

## Frontend Requirements

Before running or building the UI, verify the following are installed:

- **Node.js 22.16.0** (via `.nvmrc`)
- **npm 11 or higher**

Using nvm (recommended):

```bash
# use the project's Node version
nvm use

# if not installed yet, install it once
nvm install 22.16.0
```

This repository includes an `.nvmrc` pinned to `22.16.0` to avoid version drift across environments.

Environment variables (frontend):

- Location: `src/main/frontend/.env*`
- Priority: Vite loads mode-specific files first (e.g., `.env.development`) and falls back to `.env`.
- Defaults in this repo:
  - `.env`: `VITE_API_BASE_URL=/api` (works with dev proxy and when the app is served by Spring Boot)
  - `.env.development`: `VITE_API_BASE_URL=http://localhost:8080/api` (direct calls to backend during vite dev)

Usage in code:

```ts
// any frontend TS/TSX file
const apiBase = import.meta.env.VITE_API_BASE_URL
```

## Prerequisites

- JDK 21
- Maven 3.9+
- Node.js 22.16.0 (only for OpenAPI lint/preview)

## Run & Build

- Dev run: `mvn spring-boot:run`
- Package: `mvn -DskipTests package`
- Run jar: `java -jar target/*-SNAPSHOT.jar`

Default local URL: `http://localhost:8080`

### Maven frontend integration

This project uses `frontend-maven-plugin` to build the React app during the standard Maven lifecycle:

- Plugin: com.github.eirslett:frontend-maven-plugin
- Node/NPM pinned: Node v22.16.0, npm 11.4.0 (installed locally per build)
- Working directory: `src/main/frontend`
- Executions wired into lifecycle:
  - `install-node-and-npm` (prepares toolchain)
  - `npm ci` (runs during generate-resources)
  - `npm test` (runs during test phase)
  - `npm run build` (runs during prepare-package; outputs to `src/main/resources/static`)

Clean removes old UI assets:

- `mvn clean` is configured to delete `src/main/resources/static/**` to ensure reproducible builds.

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

## CI/CD guidance

Recommended CI steps for the combined backend + frontend build:

1. Cache npm directory for `src/main/frontend` (e.g., `~/.npm`), and Maven repository (`~/.m2/repository`).
2. Use Node 22.16.0 (e.g., via setup-node) and JDK 21.
3. Frontend install: `npm ci` in `src/main/frontend`.
4. Quality checks (lint, format, test):
   - `npm run lint`, `npm run format`, and `npm test` in `src/main/frontend`.
5. Build everything: `mvn -DskipTests package` (the Maven build will run the frontend plugin to build the UI and emit assets under `src/main/resources/static`).
   - Alternatively, run full verification: `mvn clean verify`.

Notes:
- If the OpenAPI spec changes, run `npm run api:generate` in `src/main/frontend` and commit the generated types.
- The `vite` build emits hashed assets to `src/main/resources/static`, which Spring Boot serves in production.

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
