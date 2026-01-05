# Front-End Tasks Checklist

Note: Only task items have checkboxes. Use [x] to mark completed tasks.

## Epic: Project Setup & Configuration

### User Story: React app scaffolded with Vite + TypeScript under src/main/frontend
- [x] Create directory `src/main/frontend`
- [x] Initialize Vite React + TypeScript project in `src/main/frontend`
- [x] Initialize npm with lockfile committed
- [x] Add `.nvmrc` with Node.js `22.16.0` and document usage in README

### User Story: Vite configured for dev and production
- [x] Configure Vite base path to `/`
- [x] Configure Vite dev server to run on port `5173`
- [x] Configure Vite proxy to forward `/api` to `http://localhost:8080`
- [x] Configure Vite build output directory to `src/main/resources/static`
- [x] Configure Vite plugins with `@vitejs/plugin-react` and React Refresh

### User Story: Environment variables for API URLs per environment
- [x] Create `.env` with `VITE_API_BASE_URL` set to `/api`
- [x] Create `.env.development` with `VITE_API_BASE_URL` set to `http://localhost:8080/api`
- [x] Add typing support for `import.meta.env` in `vite-env.d.ts`
- [x] Document env usage in README (priority and fallback rules)

### User Story: Styling and UI libraries configured
- [x] Install Tailwind CSS, PostCSS, Autoprefixer
- [x] Initialize Tailwind config and PostCSS config
- [x] Create `tailwind.css` and import in `main.tsx`
- [x] Configure Tailwind content globs to include `src/**/*` and shadcn paths
- [x] Install Radix UI, shadcn/ui, lucide-react, tailwind-merge, clsx, class-variance-authority, tw-animate-css
- [x] Initialize shadcn/ui and set components directory to `src/components/ui`
- [x] Extend Tailwind theme tokens (colors, radius, spacing, shadows) aligned with shadcn defaults
- [x] Add dark mode `class` strategy and root theme variables

### User Story: Code quality tooling configured
- [x] Install ESLint with TypeScript, React, React Hooks, and import plugins
- [x] Install Prettier and `eslint-config-prettier`, `eslint-plugin-prettier`
- [x] Add `.eslintrc` with project rules (no any, no unused vars, hooks rules, import order)
- [x] Add `.prettierrc` with formatting rules (printWidth, semi, singleQuote, trailingComma)
- [x] Add `lint` and `format` npm scripts
- [x] Configure Husky pre-commit to run lint and format (optional; document if added)

### User Story: Clear folder structure
- [x] Create folders: `src/components`, `src/components/ui`, `src/pages`, `src/hooks`, `src/services`, `src/lib`, `src/routes`, `src/styles`, `src/types`
- [x] Add `index.ts` barrels for components, hooks, services where helpful
- [x] Create `src/lib/config.ts` to centralize app config (e.g., API base URL)
- [x] Create `src/styles/globals.css` and import Tailwind layers

## Epic: API Integration

### User Story: Generated TypeScript models from OpenAPI
- [x] Add `openapi-typescript-codegen` as a dev dependency
- [x] Add npm script `api:generate` to generate types from `openapi-starter-main/openapi/openapi.yaml` into `src/types/generated`
- [x] Run generation and commit generated files
- [x] Add README note to re-generate types when the spec changes

### User Story: Reusable Axios client with interceptors
- [x] Create `src/services/httpClient.ts` with Axios instance using `VITE_API_BASE_URL`
- [x] Add request interceptor to set default headers (Accept, Content-Type)
- [x] Add response interceptor to normalize errors and map to a standard `AppError`
- [x] Export helper methods for `get/post/put/delete` with generics
- [x] Add retry policy (1–2 retries) for idempotent GETs (optional, configurable)

### User Story: Domain service modules
- [x] Create `src/services/beerService.ts` with CRUD and list (with filters, pagination)
- [x] Create `src/services/customerService.ts` with CRUD and list
- [x] Create `src/services/beerOrderService.ts` with CRUD, list, and shipment actions
- [x] Define request/response DTO types using generated models where applicable
- [x] Standardize error handling by throwing `AppError` with code/message

### User Story: Shared error handling strategy
- [x] Create `src/lib/errors.ts` defining `AppError` shape and mappers from `AxiosError` and `ProblemDetails`
- [x] Implement `mapHttpStatusToMessage` and fallback messages
- [x] Integrate toasts for user-visible errors in pages and critical actions

## Epic: Build Process Integration

### User Story: Maven builds UI during `mvn clean package`
- [x] Add `frontend-maven-plugin` to `pom.xml` with executions: install-node-and-npm, npm install, npm run build, npm test
- [x] Configure plugin `workingDirectory` to `src/main/frontend`
- [x] Pin Node/NPM versions to `22.16.0` / `11.x` in plugin config
- [x] Ensure Vite build outputs to `src/main/resources/static`

### User Story: Maven clean removes prior UI assets
- [x] Configure `maven-clean-plugin` to delete `src/main/resources/static/**`
- [x] Verify clean removes hashed assets and manifest files

### User Story: CI/CD expectations documented
- [x] Document CI steps: cache npm, run `npm ci`, run lint, run tests, run build, then `mvn -DskipTests package` or full verify
- [x] Document how `frontend-maven-plugin` integrates with `mvn package`
- [x] Add status badge and instructions in README (optional)

## Epic: Component Development & Routing

### User Story: Consistent app layout
- [x] Install and configure theme provider (class-based dark mode)
- [x] Create `AppLayout` with Navbar, optional Sidebar, main container, and Footer
- [x] Add Toaster using shadcn/ui and wire to global error notifications
- [x] Add Loading and Empty states components

### User Story: Navigable pages via a route hierarchy
- [x] Install React Router DOM and set up `BrowserRouter` in `main.tsx`
- [x] Define routes under `/beers`, `/customers`, `/orders` with index (list), `/create`, `/:id`, `/:id/edit`
- [x] Create route guards for not found and error elements
- [x] Add breadcrumb component and integrate per route

### User Story: Reusable UI primitives
- [x] Scaffold shadcn/ui components: Button, Input, Select, Textarea, Label, Badge, Dialog, DropdownMenu, Sheet, Tabs, Alert, Toast
- [x] Create `DataTable` component with sorting, pagination, and row selection
- [x] Create `ConfirmDialog` for destructive actions
- [x] Create Form components (`Form`, `FormField`, `FieldError`) using shadcn patterns
- [x] Create `Pagination` and `TableToolbar` components

### User Story: Global styles and tokens
- [x] Define CSS variables for color palette, borders, radii, and spacing
- [x] Configure Tailwind theme to read from CSS variables
- [x] Add motion/animation utilities via `tw-animate-css` classes

### User Story: Shared hooks
- [x] Create `useToast` hook wrapper around shadcn toaster
- [x] Create `useQueryParams` for reading/updating URL search params
- [x] Create `usePagination` to manage page/index/pageSize
- [x] Create `useDebounce` for filter inputs
- [x] Create `useAsync` for wrapping async calls with loading/error

## Epic: Feature Implementation — Beers

### User Story: Paginated, filterable list of beers
- [x] Create `BeerListPage` at `/beers` with table, pagination, and filter controls (name, style)
- [x] Wire data loading via `beerService.list` with query params (page, size, name, style)
- [x] Persist filters in URL query string using `useQueryParams`
- [x] Add loading, empty, and error states with retry
- [x] Add actions column with View, Edit, Delete

### User Story: Beer detail view
- [x] Create `BeerDetailPage` at `/beers/:id` displaying core fields
- [x] Load item via `beerService.getById`
- [x] Add back navigation and actions to Edit/Delete

### User Story: Create beer
- [x] Create `BeerCreatePage` at `/beers/create` with form fields (name, style, upc, price, quantity)
- [x] Add client-side validation and inline errors
- [x] Submit via `beerService.create` and navigate to detail on success
- [x] Show success/error toasts

### User Story: Edit beer
- [x] Create `BeerEditPage` at `/beers/:id/edit` prefilled with existing values
- [x] Submit via `beerService.update` and navigate back to detail
- [x] Handle concurrent modification with latest data fetch before submit

### User Story: Delete beer
- [x] Add Delete action with `ConfirmDialog` on list and detail pages
- [x] Call `beerService.delete` and optimistically update the list
- [x] Handle 409/400 cases with specific error messages

## Epic: Feature Implementation — Customers

### User Story: Customer list
- [x] Create `CustomerListPage` at `/customers` with table and basic filters (name)
- [x] Implement pagination and sorting in the table
- [x] Load via `customerService.list` and handle states

### User Story: Customer detail
- [x] Create `CustomerDetailPage` at `/customers/:id`
- [x] Load via `customerService.getById` and display fields

### User Story: Create customer
- [x] Create `CustomerCreatePage` at `/customers/create` with form (name, email, address, phone)
- [x] Submit via `customerService.create` and navigate to detail
- [x] Show success/error toasts

### User Story: Edit customer
- [x] Create `CustomerEditPage` at `/customers/:id/edit` prefilled with data
- [x] Submit via `customerService.update` and handle navigation

### User Story: Delete customer
- [x] Add Delete action with `ConfirmDialog` on list and detail pages
- [x] Call `customerService.delete` and refresh list

## Epic: Feature Implementation — Beer Orders

### User Story: Beer orders list with filters
- [x] Create `BeerOrderListPage` at `/orders` with table, filters (status, customer, date range)
- [x] Load via `beerOrderService.list` with pagination
- [x] Add actions column for View, Edit, Shipment

### User Story: Beer order detail
- [x] Create `BeerOrderDetailPage` at `/orders/:id` showing order items, totals, status history
- [x] Load via `beerOrderService.getById`

### User Story: Create beer order
- [x] Create `BeerOrderCreatePage` at `/orders/create` with form (customer, items with beer + qty)
- [x] Provide item add/remove controls and inline validation
- [x] Submit via `beerOrderService.create` and navigate to detail

### User Story: Edit beer order
- [x] Create `BeerOrderEditPage` at `/orders/:id/edit`
- [x] Allow updating items and quantities subject to business rules
- [x] Submit via `beerOrderService.update` and navigate to detail

### User Story: Shipment management workflow
- [x] Add `ShipmentDialog` to capture shipment details (carrier, tracking number, shippedAt)
- [x] Integrate shipment action on list and detail pages
- [x] Call `beerOrderService.createShipment` and update order status
- [x] Display shipment info and status badges in detail page

## Epic: Testing

### User Story: Jest + React Testing Library configured
- [x] Install Jest, ts-jest, @testing-library/react, @testing-library/jest-dom
- [x] Add `jest.config.ts` with TS transform and jsdom environment
- [x] Add `setupTests.ts` to extend expect and configure RTL
- [x] Add npm scripts: `test`, `test:watch`, `coverage`

### User Story: Tests for core UI components
- [ ] Write tests for Button, Input, Dialog, ConfirmDialog interactions
- [ ] Write tests for DataTable sorting, pagination, empty state

### User Story: Tests for service modules
- [ ] Mock Axios with `jest.mock` and verify request URLs, params, and payloads
- [ ] Test success and error paths for `beerService`, `customerService`, `beerOrderService`
- [ ] Test error mapping to `AppError`

### User Story: Tests for shared hooks
- [ ] Test `usePagination` state transitions
- [ ] Test `useQueryParams` read/write behavior
- [ ] Test `useAsync` loading and error propagation

### User Story: Testing strategies for critical flows
- [x] Test `BeerCreatePage` form submission success and validation errors
- [ ] Test navigation between list → detail → edit routes
- [ ] Use RTL to assert toasts appear on success/error
- [x] Add smoke tests for each page to ensure render without crashing
