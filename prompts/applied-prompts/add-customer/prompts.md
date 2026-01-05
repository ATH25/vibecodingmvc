
------------
Analyze the prompts /add-customer/requirements.md file and create a detailed plan for the improvements of this project. Write the plan to prompts/add-customer/plan.md file.
------------

Look at the prompts/add-customer/plan.md file and create a detailed, step-by-step task list in prompts/add-customer/tasks.md.

Each task should:
- Use checklist format: "- [ ]" for undone, "[x]" for done
- Include specific file paths and package names where applicable
- Cover all phases: migration, entity, DTOs, mapper, repository, service, controller, Springdoc annotations, tests, OpenAPI YAMLs, manual verification
- Include tasks for adding Springdoc OpenAPI annotations (e.g. @Operation, @ApiResponses) to controller methods
- Follow the existing project structure and naming conventions (Beer feature as reference)

Keep tasks atomic and actionable.

-------------------
Begin implementing the tasks listed in prompts/add-customer/tasks.md in sequential order.

- Complete all required code changes: Flyway migration, entity, DTOs, mapper, repository, service, controller, Springdoc annotations, tests, OpenAPI YAMLs, etc.
- Use the correct package structure and match the conventions used in the Beer feature.
- Mark each completed task with [x] in tasks.md as you proceed.
- If the Flyway migration filename/version is incorrect, I will manually rename it later — proceed as-is.
---------------------
Review prompts/add-customer/tasks.md and complete the remaining unchecked tasks only.

Focus on:
- Completing `Customer` entity's equals(), hashCode(), and toString() methods
- Creating service-level test: src/test/java/tom/springframework/vibecodingmvc/services/CustomerServiceImplTest.java
- Optionally add a repository test: CustomerRepositoryTest.java
- Verifying constructor injection across components
- Applying package-private visibility where appropriate
- Ensuring application.properties includes: spring.jpa.open-in-view=false
- Performing manual verification tasks if applicable
- Validating OpenAPI spec using: npm install && npm test in openapi-starter-main

Update tasks.md and mark all newly completed tasks as `[x]`. Do not modify previously completed tasks or re-implement any existing functionality.


