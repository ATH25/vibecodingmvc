

## 🤖 Vibe Coding Prompts

### beer-controller-branch (branch name: 3-create-service-layer-and-controller)

```
In the package controllers, create a new Spring MVC controller for the Beer entity. Add operations for create, get by id, and list all. In the package services, create a service interface and implementation. Add methods as needed to support the controller operations using the Spring Data Repository. The controller should only use the service and the service will use the Spring Data JPA repository for persistence operations. 
Create Spring MockMVC tests for the controller operations. Verify tests are passing. 

Finally, add a section in readme.md file for the Junie prompts I provided. Have a sub section for the branch and add the prompt under the sub section
```

### controller-update-delete (branch name: 4-add-controller-endpoints)

```
Inspect the BeerController. Add API endpints for update and delete. Create new service methods. Create additional MockMVC tests for the new API operations. 
Create a new unit test to test all service operations. 
```

#### ✅ Files Added
- src/test/java/tom/springframework/vibecodingmvc/services/BeerServiceTest.java

#### ✅ Files Modified
- README.md
- BeerController.java
- BeerService.java
- BeerServiceImpl.java
- BeerControllerTest.java

### add-dtos (branch name: add-dtos)

```
Analyze the file `/prompts/add_dtos/requirements-draft.md` and inspect the project. Improve and rewrite the draft
requirements to a new file called `/prompts/add-dtos/requirements.md`.
```


## Add DTOs requirements and plan

**Branch:** feature/add-dtos

**Goal:** Rewrite the requirements from the draft and create a plan for DTO-related improvements

**Prompt**
Analyze the prompts/add-dtos/requirements.md file and create a detailed plan for the improvements of this project.
Write the plan to prompts/add-dtos/plan.md file.

**Files Added**
- prompts/add-dtos/plan.md

**Files Modified**
- prompts/add-dtos/prompts.md
- README.md

**Notes / Outcome**
- requirements.md created from the draft; plan.md created with a detailed phased plan
- No application code changes; tests unaffected

## Create tasks checklist

**Branch:** feature/10-create-task-list

**Goal:** Generate an enumerated checklist in tasks.md based on plan.md

**Prompt**
Create a detailed enumerated task list according to the suggested enhancements plan in prompts/add-dtos/plan.md 
Task items should have a placeholder [ ] for marking as done [x] upon task completion. 
Write the task list to prompts/add-dtos/tasks.md file.

**Files Added**
- prompts/add-dtos/tasks.md

**Files Modified**
- prompts/add-dtos/prompts.md

**Notes / Outcome**
- tasks.md created with a detailed enumerated checklist derived from plan.md, using [ ] placeholders for completion tracking.
- No application code changes; tests unaffected.

## Complete task list implementation

**Branch:** 11-complete-task-list

**Goal:** Implement tasks from the checklist and update progress

**Prompt**
Complete the task list prompts/add-dtos/tasks.md. Inspect the requirements.md and plan.md and task.md (task list). 
Implement the tasks in the task list. Focus on completing the tasks in order. Mark the task complete as it is done using [x]. 
As each step is completed, it is very important to update the task list mark and the task as done [x].

**Files Added**
- None

**Files Modified**
- prompts/add-dtos/tasks.md
- prompts/add-dtos/prompts.md

**Notes / Outcome**
- Began implementing tasks in tasks.md in sequential order.
- Updated tasks.md to mark completed tasks with [x].
- No other application code changes at this stage.
