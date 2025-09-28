# Plan to Update README.md for VibeCodingMVC

Updated: 2025-09-28

Objective
- Refresh README.md to accurately reflect the current state of the project, improve developer onboarding, and align with the repository guidelines provided.

Summary of findings in current README.md
- Project structure is outdated: only Beer domain is shown; Customer domain (controller, DTOs, service) and additional entities (BeerOrder, BeerOrderLine, Customer) are missing.
- Flyway note is incorrect: README says “no migrations yet” but repo has migrations (V1__init.sql, V2__customer.sql) and Flyway is enabled in application.properties.
- OpenAPI docs present under openapi-starter-main but not documented in README.
- Using Junie section links to .junie/guidelines.md which does not exist in the repo.
- OSIV note says "consider setting" but application.properties already sets spring.jpa.open-in-view=false.

Goals for the updated README
- Keep it short, clear, and practical (per project guidelines).
- Provide correct structure tree and key modules.
- Document run/test commands and environment succinctly.
- Accurately reflect Flyway configuration and migrations.
- Add a brief Customer API overview consistent with existing Beer API style, or point to OpenAPI docs.
- Add OpenAPI documentation section with lint commands.
- Fix/adjust the Using Junie section to be self-contained.

Proposed changes (section-by-section)
1) Title and intro
- Keep the existing title and "vibe coding" description.
- Update the "Updated:" date.

2) Tech Stack and Prerequisites
- Confirm Java 21, Spring Boot 3.5.x, Spring Web, Spring Data JPA, Validation, H2, Maven.
- Keep quick verification commands (java -version, mvn -version).

3) Project Structure
- Replace the tree with an accurate snapshot, including at least:
  - controllers: BeerController, CustomerController
  - entities: Beer, BeerOrder, BeerOrderLine, Customer
  - models (DTOs): BeerRequestDto, BeerResponseDto, CustomerRequestDto, CustomerResponseDto
  - repositories: BeerRepository, CustomerRepository (if present)
  - services: BeerService/Impl, CustomerService/Impl
  - mappers: MapStruct mappers for Beer and Customer (if present)
  - resources: application.properties, db/migration (Flyway)
  - openapi-starter-main folder (docs tooling)
- Retain note about layering (Controller → Service → Repository) and constructor injection.

4) Run & Build
- Keep existing commands.
- Clarify that H2 in-memory is used; no external DB required.
- Mention default port 8080.

5) Testing
- Keep mvn test and single-class/method examples; update class names if needed.
- Note MockMvc for controllers and H2 for repository/data interactions.
- Keep pointer to surefire-reports.

6) APIs
- Beer API: keep as-is, but ensure DTO note matches current code (MapStruct + validation already in place).
- Customer API: add a concise section mirroring Beer API:
  - Base URL: /api/v1/customers
  - Endpoints: GET /, GET /{id}, POST, PUT /{id}, DELETE /{id}
  - Brief curl examples for GET list and POST (with simple JSON body).
  - Note DTOs (CustomerRequestDto, CustomerResponseDto) and validation.
- Alternatively (to keep README compact), replace detailed endpoint lists with a pointer to the OpenAPI docs section below. If space is a concern, prefer the pointer.

7) OpenAPI documentation
- Add a new section:
  - Location: openapi-starter-main/openapi/openapi.yaml (+ split files under paths and components)
  - Validate/lint: in openapi-starter-main run: npm install (once), npm test
  - Optional preview: npm start
  - Note file naming convention for split path files per repository guidelines

8) Operational Notes
- Flyway: update to reflect existing migrations and properties in application.properties:
  - spring.flyway.enabled=true, locations=classpath:db/migration, baseline-on-migrate=true
  - spring.jpa.hibernate.ddl-auto=validate
  - Migrations present: V1__init.sql, V2__customer.sql
- OSIV: already disabled (spring.jpa.open-in-view=false) – update wording.
- Exception handling: mention plan to add GlobalExceptionHandler if not yet present.

9) Using Junie
- Replace non-existent link with concise, local instructions:
  - “Use explicit prompts, specify files, packages, and tests; keep changes minimal; run mvn test after changes.”
  - Optionally reference JetBrains guide URL for Junie usage in IntelliJ IDEA.

10) Contributing
- Keep branch naming, commit message style, and pre-push mvn test reminder.

11) Changelog/Updated line
- Keep the "Updated:" line under the heading and set to current date when README is changed.

Acceptance criteria
- README accurately reflects:
  - Current modules (Beer + Customer), entities, DTOs, and layering
  - Flyway configuration and existing migrations
  - Presence and usage of OpenAPI docs
  - Correct OSIV state
  - Valid pointers/links only (remove or fix .junie link)
- README remains concise and practical; no superfluous details.

Out of scope (for this change)
- No code changes; documentation update only.
- No addition of new endpoints beyond documenting what exists.

Proposed concise diff examples (to implement manually)
- Change in "Operational Notes":
  - From: "Flyway is present but no migrations yet." → To: "Flyway is enabled; migrations exist under src/main/resources/db/migration (e.g., V1__init.sql, V2__customer.sql). JPA ddl-auto=validate."
  - From: "OSIV: Consider setting spring.jpa.open-in-view=false" → To: "OSIV disabled: spring.jpa.open-in-view=false."
- Remove or replace the .junie/guidelines.md link with a direct note and JetBrains guide URL.

Next steps
- Apply the above edits to README.md in a single commit titled: "Refresh README: structure, Flyway, OpenAPI, Customer API, OSIV"
- Verify links and command examples.
