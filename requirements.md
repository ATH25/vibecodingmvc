# Master Technical Requirements

## 1. Purpose and Scope
This document serves as the master technical requirements specification for the VibeCodingMVC application. it provides a high-level overview of the system's capabilities, architectural design, and the technologies used to implement them. It acts as a stable reference for developers and architects to understand the technical intent and current state of the application.

This document covers the backend architecture, frontend capabilities, integration model, build and execution requirements, and the overall testing strategy.

## 2. System Overview
VibeCodingMVC is a modern web application for managing a beer brewery's operations, including product catalog (beers), customer management, and order fulfillment.

- **High-Level Architecture**: The system follows a decoupled architecture with a Spring Boot backend providing a RESTful API and a React-based frontend.
- **Backend Role**: Handles business logic, data persistence, validation, and provides a versioned REST API.
- **Frontend Role**: Provides a responsive user interface for interacting with the beer catalog, managing customers, and processing orders.
- **Deployment Model**: The application is packaged as a single executable JAR file. The React frontend is built into static assets and served by the Spring Boot application, simplifying deployment and ensuring version consistency between layers.

## 3. Backend Technical Requirements

### API Design Principles
The backend implements a resource-oriented REST API under the `/api/v1` namespace.
- **Resource-Oriented**: Endpoints are structured around resources (beers, customers, orders).
- **Nested Resources**: Sub-resources are accessed through nested paths (e.g., `/api/v1/beer-orders/{id}/shipments`) to reflect ownership and lifecycle dependency.
- **Status Codes**: Uses standard HTTP status codes via `ResponseEntity` to communicate the outcome of requests (e.g., 201 Created, 204 No Content, 404 Not Found).
- **JSON Standard**: Consistently uses JSON for request and response payloads with camelCase property naming.

### DTO Usage and Boundaries
The system strictly separates the internal persistence model from the external API layer.
- **Request/Response DTOs**: Dedicated records (e.g., `BeerRequestDto`, `BeerResponseDto`) are used to define the API contract.
- **Command Objects**: Use-case specific command objects (e.g., `CreateBeerOrderCommand`) encapsulate input data for business operations in the service layer.
- **Encapsulation**: Entities are never exposed directly to the web layer, preventing accidental leaking of internal state or database schema details.

### Mapping Strategy
- **MapStruct**: The application uses MapStruct for compile-time generation of type-safe bean mappers. This ensures high-performance mapping between Entities and DTOs without the overhead of reflection or manual boiler-plate code.

### Persistence Model
- **Spring Data JPA / Hibernate**: The persistence layer is built on Spring Data JPA using Hibernate as the JPA provider.
- **Transaction Management**: Transactions are managed at the service layer. Read-only operations are optimized with `@Transactional(readOnly = true)`.
- **Flyway**: Database schema migrations are managed using Flyway, ensuring consistent and versioned database updates across environments.
- **Validation**: Jakarta Validation (Bean Validation) is applied to DTOs and entities to enforce data integrity at the boundaries.

### Feature Breakdown

#### Beer
- Manages the product catalog including beer names, styles, UPCs, and pricing.
- Supports filtering and searching (e.g., by name or style).

#### Customer
- Manages customer profiles and contact information.
- Provides standard CRUD operations.

#### Beer Orders
- Handles the lifecycle of beer orders from creation to fulfillment.
- **List vs. Detail**: Order summaries are provided in list views, while full order details including line items are retrieved via specific detail endpoints.
- **Beer Order Shipments**: Manages shipment tracking for orders. Implemented as a nested resource of Beer Orders, supporting status updates (Pending, In Transit, Delivered, etc.) and tracking information.

## 4. Frontend Technical Requirements

### Framework and Tooling
- **React**: Built with React and Vite for a modern, fast development experience.
- **Tailwind CSS**: Uses utility-first CSS for styling, combined with `shadcn/ui` components for a consistent and accessible UI.
- **TypeScript**: The entire frontend is written in TypeScript to provide type safety and improved developer productivity.

### Page and Feature Responsibilities
- **Product Catalog**: Browsing and managing beers.
- **Customer Management**: Interfaces for creating and editing customer data.
- **Order Management**: Dashboards for viewing order lists and drilling down into order details and shipment status.

### API Consumption Model
- **Axios**: A centralized Axios instance handles HTTP communication with the backend.
- **Generated Types**: TypeScript models are generated from the OpenAPI specification to ensure strict type alignment with the backend API.
- **Custom Hooks**: Data fetching and state management are encapsulated in custom React hooks (e.g., `useBeers`), separating UI components from data logic.

### Data Handling Expectations
- **List vs. Detail**: The frontend implements separate views and data fetching strategies for list and detail views to optimize performance and data usage.
- **Global Error Handling**: Interceptors are used to provide consistent error feedback to the user based on backend responses.

## 5. Frontend–Backend Integration

### API Contract Usage
- **OpenAPI/Swagger**: The system uses an OpenAPI 3.0 specification as the single source of truth for the API contract. The spec is modularized into split files for maintainability.
- **Redocly**: Used for linting, validating, and rendering the OpenAPI documentation.

### Proxying and Static Asset Serving
- **Development**: During development, the Vite dev server proxies `/api` requests to the Spring Boot backend (running on localhost:8080).
- **Production**: In production, the Spring Boot application serves the pre-built React static assets from `src/main/resources/static`.

## 6. Build and Execution Requirements

### Backend Build Expectations
- **Java 21 / Maven**: The project requires JDK 21 and is managed by Maven.
- **Spring Boot**: Utilizes Spring Boot's build plugins for packaging and execution.

### Frontend Build Integration
- **Maven Integration**: The `frontend-maven-plugin` orchestrates the frontend build process (npm install, npm run build) during the Maven `package` phase.
- **Single Artifact**: The final build output is a single, self-contained executable JAR.

## 7. Testing Requirements

### Backend Tests
- **JUnit 5 / Mockito**: Standard unit tests for services and mappers.
- **MockMvc**: Controller-level integration tests to verify API endpoints and validation.
- **Testcontainers**: Used for integration tests that require real database instances, ensuring tests run against a production-like environment.

### Frontend Tests
- **Vitest / React Testing Library**: Used for component testing and verifying UI logic.
- **API Mocking**: Mocking strategies are used to test components in isolation from the actual backend.

### Integration Expectations
- Integration tests verify the contract between the web layer and the service layer, as well as the persistence layer's interaction with the database.

## 8. Explicit Non-Goals
- **Authentication/Authorization**: Not implemented in the current scope; the system assumes a trusted environment or external security wrapper.
- **Distributed Systems**: The application is designed as a modular monolith; distributed tracing and microservices-specific patterns are intentionally excluded.
- **Real-time Notifications**: WebSockets or other real-time communication channels are not currently part of the technical requirements.
