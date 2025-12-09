Epic: Project Setup & Configuration
  User Story: As a developer, I want a React app scaffolded with Vite and TypeScript under src/main/frontend so that I can build the UI alongside the Spring Boot backend.
    Tasks:
      - Create directory src/main/frontend
      - Initialize Vite React + TypeScript project in src/main/frontend
      - Initialize npm with lockfile committed
      - Add .nvmrc with Node.js 22.16.0 and document usage in README

  User Story: As a developer, I want Vite configured for dev and production so that the app runs locally and emits assets into Spring Boot’s static folder.
    Tasks:
      - Configure Vite base path to '/'
      - Configure Vite dev server to run on port 5173
      - Configure Vite proxy to forward '/api' to 'http://localhost:8080'
      - Configure Vite build output directory to 'src/main/resources/static'
      - Configure Vite plugins with @vitejs/plugin-react and React refresh

  User Story: As a developer, I want environment variables set up to control API URLs per environment.
    Tasks:
      - Create .env with VITE_API_BASE_URL set to '/api'
      - Create .env.development with VITE_API_BASE_URL set to 'http://localhost:8080/api'
      - Add typing support for import.meta.env in vite-env.d.ts
      - Document env usage in README (priority and fallback rules)

  User Story: As a developer, I want styling and UI libraries configured so that I can build consistent, accessible components.
    Tasks:
      - Install Tailwind CSS, PostCSS, Autoprefixer
      - Initialize Tailwind config and PostCSS config
      - Create tailwind.css and import in main.tsx
      - Configure Tailwind content globs to include src/**/* and shadcn paths
      - Install Radix UI primitives, shadcn/ui, lucide-react, tailwind-merge, clsx, class-variance-authority, tw-animate-css
      - Initialize shadcn/ui and set components directory to src/components/ui
      - Extend Tailwind theme tokens (colors, radius, spacing, shadows) aligned with shadcn defaults
      - Add dark mode 'class' strategy and root theme variables

  User Story: As a developer, I want code quality tooling configured for a consistent codebase.
    Tasks:
      - Install ESLint with TypeScript, React, React Hooks, and import plugins
      - Install Prettier and eslint-config-prettier, eslint-plugin-prettier
      - Add .eslintrc with project rules (no any, no unused vars, hooks rules, import order)
      - Add .prettierrc with formatting rules (printWidth, semi, singleQuote, trailingComma)
      - Add lint and format npm scripts
      - Configure husky pre-commit to run lint and format (optional; document if added)

  User Story: As a developer, I want a clear folder structure so that contributors can navigate the project easily.
    Tasks:
      - Create folders: src/components, src/components/ui, src/pages, src/hooks, src/services, src/lib, src/routes, src/styles, src/types
      - Add index.ts barrels for components, hooks, services where helpful
      - Create src/lib/config.ts to centralize app config (e.g., API base URL)
      - Create src/styles/globals.css and import Tailwind layers

Epic: API Integration
  User Story: As a developer, I want generated TypeScript models from OpenAPI so that API usage is type-safe.
    Tasks:
      - Add openapi-typescript-codegen as a dev dependency
      - Add npm script 'api:generate' to generate types from openapi-starter-main/openapi/openapi.yaml into src/types/generated
      - Run generation and commit generated files
      - Add README note to re-generate types when the spec changes

  User Story: As a developer, I want a reusable Axios client with interceptors so that requests and errors are handled consistently.
    Tasks:
      - Create src/services/httpClient.ts with Axios instance using VITE_API_BASE_URL
      - Add request interceptor to set default headers (Accept, Content-Type)
      - Add response interceptor to normalize errors and map to a standard AppError
      - Export helper methods for get/post/put/delete with generics
      - Add retry policy (1–2 retries) for idempotent GETs (optional, configurable)

  User Story: As a developer, I want domain service modules so that features can call the backend via cohesive functions.
    Tasks:
      - Create src/services/beerService.ts with CRUD and list (with filters, pagination)
      - Create src/services/customerService.ts with CRUD and list
      - Create src/services/beerOrderService.ts with CRUD, list, and shipment actions
      - Define request/response DTO types using generated models where applicable
      - Standardize error handling by throwing AppError with code/message

  User Story: As a developer, I want a shared error handling strategy so that users receive clear messages.
    Tasks:
      - Create src/lib/errors.ts defining AppError shape and mappers from AxiosError and ProblemDetails
      - Implement mapHttpStatusToMessage and fallback messages
      - Integrate toasts for user-visible errors in pages and critical actions

Epic: Build Process Integration
  User Story: As a release engineer, I want Maven to build the UI during mvn clean package so that the final JAR includes the static assets.
    Tasks:
      - Add frontend-maven-plugin to pom.xml with executions: install-node-and-npm, npm install, npm run build, npm test
      - Configure plugin workingDirectory to src/main/frontend
      - Pin Node/NPM versions to 22.16.0 / 11.x in plugin config
      - Ensure Vite build outputs to src/main/resources/static

  User Story: As a release engineer, I want Maven clean to remove prior UI assets so that builds are reproducible.
    Tasks:
      - Configure maven-clean-plugin to delete src/main/resources/static/**
      - Verify clean removes hashed assets and manifest files

  User Story: As a team, I want CI/CD expectations documented so that pipelines are predictable.
    Tasks:
      - Document CI steps: cache npm, run npm ci, run lint, run tests, run build, then mvn -DskipTests package or full verify
      - Document how frontend-maven-plugin integrates with mvn package
      - Add status badge and instructions in README (optional)

Epic: Component Development & Routing
  User Story: As a user, I want a consistent app layout so that navigation is intuitive across modules.
    Tasks:
      - Install and configure theme provider (class-based dark mode)
      - Create AppLayout with Navbar, Sidebar (optional), main container, and Footer
      - Add Toaster using shadcn/ui and wire to global error notifications
      - Add Loading and Empty states components

  User Story: As a user, I want navigable pages via a route hierarchy so that I can access lists and details for each module.
    Tasks:
      - Install React Router DOM and set up BrowserRouter in main.tsx
      - Define routes under /beers, /customers, /orders with index (list), /create, /:id, /:id/edit
      - Create route guards for not found and error elements
      - Add breadcrumb component and integrate per route

  User Story: As a developer, I want reusable UI primitives so that I can compose features quickly.
    Tasks:
      - Scaffold shadcn/ui components: Button, Input, Select, Textarea, Label, Badge, Dialog, DropdownMenu, Sheet, Tabs, Alert, Toast
      - Create DataTable component with sorting, pagination, and row selection
      - Create ConfirmDialog for destructive actions
      - Create Form components (Form, FormField, FieldError) using shadcn patterns
      - Create Pagination and TableToolbar components

  User Story: As a developer, I want global styles and tokens set so that the design is cohesive.
    Tasks:
      - Define CSS variables for color palette, borders, radii, and spacing
      - Configure Tailwind theme to read from CSS variables
      - Add motion/animation utilities via tw-animate-css classes

  User Story: As a developer, I want shared hooks to encapsulate cross-cutting logic.
    Tasks:
      - Create useToast hook wrapper around shadcn toaster
      - Create useQueryParams for reading/updating URL search params
      - Create usePagination to manage page/index/pageSize
      - Create useDebounce for filter inputs
      - Create useAsync for wrapping async calls with loading/error

Epic: Feature Implementation — Beers
  User Story: As a user, I want to view a paginated, filterable list of beers so that I can find items quickly.
    Tasks:
      - Create BeerListPage at /beers with table, pagination, and filter controls (name, style)
      - Wire data loading via beerService.list with query params (page, size, name, style)
      - Persist filters in URL query string using useQueryParams
      - Add loading, empty, and error states with retry
      - Add actions column with View, Edit, Delete

  User Story: As a user, I want to view details of a beer so that I can see its attributes.
    Tasks:
      - Create BeerDetailPage at /beers/:id displaying core fields
      - Load item via beerService.getById
      - Add back navigation and actions to Edit/Delete

  User Story: As an admin, I want to create a beer so that I can add new inventory.
    Tasks:
      - Create BeerCreatePage at /beers/create with form fields (name, style, upc, price, quantity)
      - Add client-side validation and inline errors
      - Submit via beerService.create and navigate to detail on success
      - Show success/error toasts

  User Story: As an admin, I want to edit a beer so that I can correct or update details.
    Tasks:
      - Create BeerEditPage at /beers/:id/edit prefilled with existing values
      - Submit via beerService.update and navigate back to detail
      - Handle concurrent modification with latest data fetch before submit

  User Story: As an admin, I want to delete a beer so that I can remove obsolete items.
    Tasks:
      - Add Delete action with ConfirmDialog on list and detail pages
      - Call beerService.delete and optimistically update the list
      - Handle 409/400 cases with specific error messages

Epic: Feature Implementation — Customers
  User Story: As a user, I want to view a list of customers so that I can locate accounts.
    Tasks:
      - Create CustomerListPage at /customers with table and basic filters (name)
      - Implement pagination and sorting in the table
      - Load via customerService.list and handle states

  User Story: As a user, I want to view details of a customer so that I can see contact information.
    Tasks:
      - Create CustomerDetailPage at /customers/:id
      - Load via customerService.getById and display fields

  User Story: As an admin, I want to create a customer so that I can onboard new accounts.
    Tasks:
      - Create CustomerCreatePage at /customers/create with form (name, email, address, phone)
      - Submit via customerService.create and navigate to detail
      - Show success/error toasts

  User Story: As an admin, I want to edit a customer so that I can keep records up to date.
    Tasks:
      - Create CustomerEditPage at /customers/:id/edit prefilled with data
      - Submit via customerService.update and handle navigation

  User Story: As an admin, I want to delete a customer so that I can remove inactive records.
    Tasks:
      - Add Delete action with ConfirmDialog on list and detail pages
      - Call customerService.delete and refresh list

Epic: Feature Implementation — Beer Orders
  User Story: As a user, I want to view beer orders with filters so that I can manage fulfillment.
    Tasks:
      - Create BeerOrderListPage at /orders with table, filters (status, customer, date range)
      - Load via beerOrderService.list with pagination
      - Add actions column for View, Edit, Shipment

  User Story: As a user, I want to view details of a beer order so that I can track its items and status.
    Tasks:
      - Create BeerOrderDetailPage at /orders/:id showing order items, totals, status history
      - Load via beerOrderService.getById

  User Story: As an admin, I want to create a beer order so that I can place new orders for customers.
    Tasks:
      - Create BeerOrderCreatePage at /orders/create with form (customer, items with beer + qty)
      - Provide item add/remove controls and inline validation
      - Submit via beerOrderService.create and navigate to detail

  User Story: As an admin, I want to edit a beer order so that I can adjust items before shipment.
    Tasks:
      - Create BeerOrderEditPage at /orders/:id/edit
      - Allow updating items and quantities subject to business rules
      - Submit via beerOrderService.update and navigate to detail

  User Story: As an admin, I want to manage shipment workflow so that orders can be shipped and tracked.
    Tasks:
      - Add ShipmentDialog to capture shipment details (carrier, tracking number, shippedAt)
      - Integrate shipment action on list and detail pages
      - Call beerOrderService.createShipment and update order status
      - Display shipment info and status badges in detail page

Epic: Testing
  User Story: As a developer, I want Jest and React Testing Library configured so that I can write component and unit tests.
    Tasks:
      - Install Jest, ts-jest, @testing-library/react, @testing-library/jest-dom
      - Add jest.config.ts with TS transform and jsdom environment
      - Add setupTests.ts to extend expect and configure RTL
      - Add npm scripts: test, test:watch, coverage

  User Story: As a developer, I want tests for core UI components so that regressions are caught early.
    Tasks:
      - Write tests for Button, Input, Dialog, ConfirmDialog interactions
      - Write tests for DataTable sorting, pagination, empty state

  User Story: As a developer, I want tests for services so that API interactions are verified.
    Tasks:
      - Mock Axios with jest.mock and verify request URLs, params, and payloads
      - Test success and error paths for beerService, customerService, beerOrderService
      - Test error mapping to AppError

  User Story: As a developer, I want tests for hooks so that async behavior works as expected.
    Tasks:
      - Test usePagination state transitions
      - Test useQueryParams read/write behavior
      - Test useAsync loading and error propagation

  User Story: As a developer, I want testing strategies for forms, async logic, routing, and API interactions so that critical flows are reliable.
    Tasks:
      - Test BeerCreatePage form submission success and validation errors
      - Test navigation between list → detail → edit routes
      - Use RTL to assert toasts appear on success/error
      - Add smoke tests for each page to ensure render without crashing
