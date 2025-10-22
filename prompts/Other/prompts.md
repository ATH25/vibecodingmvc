


Inspect .junie/guidelines.md. Add a brief section about flyway migrations with Spring Boot. Include information about the default directory and version naming.

----------------

Branch 16:

Create flyway migration scripts for the JPA entities in this project. Use one script starting with the lowest version number. Inspect and update the application.properties to enable flyway migrations with Spring Boot.

---------------
Branch 17:

Inspect OpenAPI specification in openapi-starter-main/openapi/openapi.yaml. This is the API documentation for this project. Observe how file references are used. Note how the file names from path operations are determined from the path of the API, and how components such as headers and schemas are defined in file references. Update the file .junie/guidelines.md with a description of the API documentation, file naming conventions, and how components are defined. Provide instructions about testing the OpenAPI Specification using the command npm test. 

-------------

Branch 18:

Inspect the Spring MVC controller BeerController and comments on the DTOs accepted and returned. Update the OpenAPI specification for the operations in the BeerController. Provide constraint information, descriptions and examples in the schema object.


-------------

Branch 18:

Check pom.xml. If not present, add org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0. Do not change other dependencies. After the build, confirm that /v3/api-docs and /swagger-ui.html are available.

-------------

Branch 18:

Add Springdoc OpenAPI annotations to BeerController and Beer DTOs without changing logic or signatures.
- Controller: annotate class with @Tag. For each handler method add @Operation (summary, description) and @ApiResponses with proper status codes. Include request/response examples when helpful. Keep return types unchanged and do not alter routes or business logic.
- DTOs: add @Schema to each record field with description, example, and constraints that mirror Jakarta Validation annotations (@NotBlank, @PositiveOrZero, @DecimalMin, etc.). Use type string with format "date-time" for LocalDateTime fields.
- Use realistic examples consistent with fields: beerName, beerStyle, upc (13-digit), quantityOnHand, price, id/version, createdDate/updatedDate.

Note: Imports should come from io.swagger.v3.oas.annotations.*. Ensure the project still compiles and existing tests pass.


-------------

Inspect tom.springframework.vibecodingmvc.controllers.BeerController and the DTOs BeerRequestDto and BeerResponseDto.

A) Controller annotations (verify/complete only; do not duplicate or change logic/signatures):
	•	Ensure presence of @Tag, @Operation (summary/description), and @ApiResponses with correct status codes and examples.

B) DTO annotations (only where missing; do not modify fields):
	•	Add @Schema with field descriptions, examples, and constraints mapped from Jakarta Validation (e.g., @NotBlank, @Positive, etc.).

C) Examples in responses:
	•	Keep curated examples for 200 responses (single and list).
	•	No examples for 404/415 (empty content).

D) Non-goals:
	•	Do not change method signatures, path mappings, or service calls.
	•	Do not alter business logic.

E) When finished:
	•	Append this full prompt as a new entry at the bottom of /prompts/Other/prompts.md (do not modify prior entries).
	•	Report which files were updated.
</issue_description>
If you need to know the date and time for this issue, the current local date and time is: 2025-09-01 17:46.

-----------------------------
Inspect /prompts/Other/document-beerOrder-APIs.md file and use the instructions in it to build Beer Order documentation using
Springdoc annotations.

-----------------------------
We need to clean up our Redocly OpenAPI repo and switch it to split structure (“By split structure, I mean openapi.yaml is the root with only metadata and $refs, all operations live in paths/*.yaml, and all DTOs live in components/schemas/*.yaml.”)

Take the attached Springdoc export (`/v3/api-docs.yaml`) as the single source of truth.

Create (or overwrite if it exists) the file `/prompts/Other/cleanup-openapi-tasks.md`.  
In that file, write a detailed, step-by-step task list for cleaning up `openapi-starter-main/openapi/openapi.yaml` and related split files:
- Remove demo/example paths and schemas.
- Add only the real endpoints and DTOs from the Springdoc YAML.
- Organize them into split `paths/*.yaml` and `components/schemas/*.yaml` files.
- Ensure tags, examples, constraints, and 404/204 responses (no body) are preserved.
- Include acceptance checks (linting, Redocly preview, etc.).
- Document which files to delete, create, or update.

“Acceptance checks must include npx @redocly/cli lint openapi.yaml (no errors) and npx @redocly/cli preview-docs openapi.yaml (renders all endpoints).”

Do not modify any Java code. Only focus on documentation artifacts.
(Attach the file '/v3/api-docs.yaml')
-------------------------------

Use the checklist in /prompts/Other/cleanup-openapi-tasks.md to clean up and split the Redocly OpenAPI repo.

Scope:
- Docs only. Do NOT modify any Java code.


Do:
1) Apply all steps in cleanup-openapi-tasks.md (remove demo files, create split paths/*.yaml and components/schemas/*.yaml, refactor root openapi.yaml to metadata + $refs).
2) Preserve examples, constraints, enums; keep 404/204 without bodies; standardize 400 as description-only unless the Springdoc export includes a body.
3) Wire all $refs correctly.

Validate:
- Run `npx @redocly/cli lint openapi-starter-main/openapi/openapi.yaml` (must pass).
- Run `npx @redocly/cli preview-docs openapi-starter-main/openapi/openapi.yaml` (should render all endpoints).

Deliver:
- List files created/updated/deleted.
- Brief diff summary of key changes.
- Commit with message: "docs(openapi): clean split spec from Springdoc (Option B)".

-------------------------------
Analyze the class src/main/java/tom/springframework/vibecodingmvc/services/impl/CustomerServiceImpl.java and generate JUnit 5 tests using Mockito to cover all untested methods and branches. Create the new test class in src/test/java/tom/springframework/vibecodingmvc/services/impl/CustomerServiceImplTest.java. Use best practices like mocking dependencies, using @InjectMocks and @Mock annotations, and validating both success and failure scenarios. Include @Transactional method tests too.