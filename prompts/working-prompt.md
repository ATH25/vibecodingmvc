# Jetbrains AI Prompts

Inspect the requirements document. Analyze for accuracy and completeness. Make recommendations for
how we can improve this document. Implement the improvements in a revised version.

------------------

For a React project using Shadcn, inspect the technologies used. Are there any missing dependencies?

--------------------
What information is needed for the vite configuration?

--------------------
suggest further improvements for the guide?

--------------------
Can the Guide outline be improved?

# Junie Prompts
Inspect the file `prompts/front-end/front-end-requirements-prompt-draft.md`. Use this file to create a developer guide to
implement a React front end for this project. Update and improve this developer guide using the context
of this project. The guide should be organized into clear actionable steps.

Write the improved guide to `prompts/front-end/front-end-requirements.md`.

---------------------

---------------------
# Junie Prompt — Generate Front-End Implementation Plan

**Task:**  
Using the file `prompts/front-end/front-end-requirements.md` as the source of truth, generate a comprehensive implementation plan for building the React frontend for the Spring Boot Beer Service.

**Output:**  
Write the completed plan to `prompts/front-end/plan.md`.

---

### **Instructions:**

Analyze the requirements and produce a structured implementation plan that:

- Breaks work into **Epics → User Stories → Tasks**
- Covers all phases of the front-end build
- Provides clear, actionable steps a developer can follow
- Maintains alignment with the architecture, tooling, and workflows described in the requirements document

---

### **Plan Scope (must include):**

---

### **1. Project Setup & Configuration**
Epics and tasks should cover:

- Creating the initial React project using Vite + TypeScript  
- Vite configuration for dev/prod, proxy setup, build output path  
- Environment variable setup for API base URLs  
- Installing and configuring:
  - TailwindCSS  
  - Shadcn UI  
  - Radix UI  
  - Lucide React  
  - tw-animate-css  
  - class-variance-authority  
  - clsx  
- ESLint + Prettier setup and project-wide code quality rules  
- Folder structure conventions (`components/`, `pages/`, `services/`, `hooks/`, etc.)

---

### **2. API Integration**
Include epics for:

- Generating TypeScript API models + client using OpenAPI  
- Creating a reusable Axios API client (base URL, headers, interceptors)  
- Creating service modules for:  
  - Beers  
  - Customer  
  - Beer Orders  
- Establishing shared error-handling patterns

---

### **3. Build Process Integration**
Epics related to Maven integration:

- Add and configure `frontend-maven-plugin` for:
  - install-node-and-npm  
  - npm install  
  - npm build  
  - npm test  
- Configure `maven-clean-plugin` to clean `src/main/resources/static`  
- Document integration expectations for CI/CD

---

### **4. Component Development & Routing**
Epics should include:

- Setting up React Router with route hierarchy  
- Creating global layout (Navbar, container, toaster, theme config)  
- Creating reusable UI components using Shadcn + Radix (buttons, forms, tables, dialogs)  
- Setting up global styles & Tailwind theme tokens  
- Establishing reusable hooks for shared state management

---

### **5. Feature Implementation (per resource)**  
Break into epics for:

#### **Beers**
- Beer List with filtering + pagination  
- Beer Detail  
- Beer Create  
- Beer Edit  
- Delete flows  

#### **Customers**
- Customer List  
- Customer Detail  
- Customer Create  
- Customer Edit  
- Delete flows  

#### **Beer Orders**
- Beer Order List  
- Beer Order Detail  
- Beer Order Create  
- Beer Order Edit  
- Shipment management flow  

Each epic must break down into user stories and concrete development tasks.

---

### **6. Testing**
Epics must include:

- Jest + RTL setup  
- Tests for components  
- Tests for hooks  
- Tests for service modules  
- Testing strategy for forms, routing, async state, and API calls  

---

### **General Requirements**
- Ensure plan is **chronological** and **incremental**  
- Use a consistent format throughout  
- Avoid implementation code — keep the plan focused on deliverables  
- Provide enough detail for a team to execute without needing clarification  

---------------------

Create a detailed enumerated task list according to the suggested enhancements plan in
`prompts/front-end/plan.md` Task items should have a placeholder [ ] for marking as done [x] upon task completion.
Write the task list to `prompts/front-end/tasks.md` file.

---------------------
# Junie Prompt — Execute Front-End Tasks Sequentially

## Checkbox Rules (Very Important)
- Every task in `prompts/front-end/tasks.md` uses a checkbox placeholder `[ ]`.
- After completing a task, immediately update the same file and mark it as `[x]`.
- Never skip this step — it is required for continuing work across multiple runs.
- This makes the workflow resumable even when tokens or context resets occur.

## Task Progress Rules (Critical)
- Only work on tasks that are still unchecked `[ ]`.
- Skip all tasks already marked `[x]` — do NOT modify, regenerate, or repeat them.
- Continue execution from the **first unchecked task** in `prompts/front-end/tasks.md`.
- The task list is the authoritative source for what remains to be done.
---


Using the task list in `prompts/front-end/tasks.md`, complete the tasks in strict sequential order.

Use `prompts/front-end/front-end-requirements.md` and `prompts/front-end/plan.md` as supporting context to guide accurate implementation.

## Execution Rules

- Complete **one task at a time**, starting from the top of the list.
- After completing a task, **immediately update `prompts/front-end/tasks.md`** by marking that task as done with `[x]`.
- Do NOT skip tasks unless explicitly instructed.
- When implementing a task, place all created or modified files in the correct frontend directory, typically:
  `src/main/frontend/`
- Ensure code is:
  - TypeScript-correct
  - Vite-compatible
  - Follows the project’s folder structure and conventions
  - Uses Tailwind, Shadcn, Radix, and the established architecture

## Output Requirements

- For each task:
  1. Implement the required change(s)
  2. Update the task list (`prompts/front-end/tasks.md`) marking the completed item with `[x]`
  3. Show only the diff or created files necessary for the current task—no unrelated changes

## Important Notes

- Maintain consistency with the design system and component conventions established in the requirements.
- When generating components, pages, hooks, or services, use the exact names specified in `plan.md` and `tasks.md`.
- When a task refers to updating documentation, update the relevant markdown files in `prompts/front-end/`.

Begin execution with the first unchecked task in the file and continue sequentially.

----------------------------------------------
You are working on the React frontend for this project.

## Step 1: Load Context (Required)
Before executing any tasks, read and understand the following files in this order:

1. `prompts/front-end/front-end-requirements.md`
  - This defines WHAT must be built and the required technologies, structure, and constraints.

2. `prompts/front-end/plan.md`
  - This defines the overall implementation approach, epics, and sequencing.

Do not skip this step.

---

## Step 2: Task Execution Rules (Critical)

- The file `prompts/front-end/tasks.md` is the single source of truth for execution state.
- Tasks marked `[x]` are already complete and MUST NOT be repeated, regenerated, or modified.
- Only tasks marked `[ ]` are eligible for execution.
- Tasks must be executed in the order they appear in the file.

---

## Step 3: Execute Tasks

- Starting from the top of `prompts/front-end/tasks.md`, execute unchecked tasks `[ ]` sequentially.
- After completing EACH task:
  - Immediately update `prompts/front-end/tasks.md` and mark that task as `[x]`.
- Continue executing tasks in order until:
  - You reach a natural stopping point, OR
  - You are approaching token/context limits.

This workflow must support being run multiple times without repeating completed work.

---

## Implementation Constraints

- Place all frontend code under `src/main/frontend/` unless a task explicitly says otherwise.
- Follow the architecture, naming, and conventions defined in the requirements and plan.
- Use the specified stack: Vite, TypeScript, Tailwind CSS, shadcn/ui, Radix UI, Axios.
- Keep changes scoped strictly to tasks being executed.

---

## Output Expectations

- Update `prompts/front-end/tasks.md` incrementally as tasks are completed.
- Show code and file changes relevant to the tasks completed in this run.
- Do not re-list or re-explain completed tasks.
- Do not speculate about future work.

If no unchecked tasks remain, respond with:
"All tasks in prompts/front-end/tasks.md are complete."

-----------------------------------
**RUN THIS IN ASK MODE, then copy and paste the output into the guidelines.md file**
Inspect the files `prompts/requirements.md` and `prompts/plan.md`. These changes have been implemented in the project.
Review the project as needed. Plan additional sections in the /.junie/guidelines.md file for the changes which have been
implemented in the project. Include instructions for the project structure, and for building and testing the frontend project.
Also identify any best practices used for the front end code.
-----------------------------------

**CHANGE BACK TO CODE MODE**
The frontend project has build errors. Fix errors, verify tests are passing.

-----------------------------------

The command `npm test` is failing, fix test errors, verify all tests are passing

-----------------------------------

The command `npm line` is shows lint errors, inspect the lint errors and fix, verify there are no lint errors

-----------------------------------
Update eslint configuration to disable the warning for `Unused eslint-disable directive`

-----------------------------------

⸻

Junie Prompt: Update README with Frontend Build/Test/Lint Instructions

You are updating the existing README.md in this repository.

Context (Read First – Required)

Before making any changes, read and understand:
1.	The current README.md
2.	src/main/frontend/package.json (especially the scripts section)
3.	vite.config.ts (for build output behavior)

Do not skip this step.

⸻

Goal

Improve the README so that a new developer can clearly understand how to build, run, test, lint, and format the React frontend, without rewriting or duplicating existing content.

⸻

Scope & Rules (Very Important)
•	Do NOT rewrite the README from scratch.
•	Do NOT remove or significantly restructure existing sections.
•	Do NOT duplicate information already present.
•	Only refine, clarify, or extend frontend-related documentation.
•	Keep changes minimal, precise, and consistent with the existing style.

⸻

Required Updates

1. Frontend Commands

Ensure the README clearly documents the following frontend commands (using the actual npm scripts), run from:

cd src/main/frontend

Include brief descriptions for each:

npm install
npm run dev
npm run build
npm test
npm run lint
npm run format
npm run api:generate

If any of these commands are missing or only partially documented, add them once in the most appropriate frontend section.

⸻

2. Build Output Clarification

Clearly state (once) that:
•	npm run build emits frontend assets to src/main/resources/static
•	These assets are served automatically by Spring Boot
•	This behavior is controlled by vite.config.ts

Avoid repeating this information in multiple places.

⸻

3. Node & npm Versions

Ensure the README consistently reflects:
•	Node.js 22.16.0 (via .nvmrc)
•	npm 11 or higher
•	Use of nvm use for local setup

Do not introduce conflicting version guidance.

⸻

4. CI / Quality Gates (Minor Refinement)

Where frontend CI steps are mentioned:
•	Clearly identify npm test and npm run lint as frontend quality checks
•	Do not duplicate backend Maven steps

⸻

Formatting & Style
•	Follow existing Markdown conventions
•	Prefer short bullet lists and concise code blocks
•	Do not add verbose explanations
•	Keep the README cohesive and readable

⸻

Completion Instruction

When finished:
•	Output only the updated README.md
•	Do not include explanations, commentary, or summaries

⸻
