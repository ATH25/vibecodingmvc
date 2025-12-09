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