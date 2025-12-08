Your goal is to create a comprehensive, professional developer guide that teaches an experienced 
Java/Spring Boot engineer how to add a modern React frontend to this project. The final system 
must build as a single Spring Boot JAR using Maven, with the React application fully integrated 
into the build lifecycle.

The guide must be organized into clear, actionable steps that follow the Vibe Coding workflow:
requirements → plan → tasks → implementation. Your output will be written to:
`prompts/front-end/front-end-requirements.md`

# 1. Technologies Used (Group by Category)

## Core Frontend
- React v19.1.0  
- React DOM v19.1.1  
- TypeScript 5.8.3  
- React Router DOM 7.6.36  
- Node.js v22.16.0  
- npm v11.4.0  

## Type Definitions
- types/react 19.1.0  
- types/react-dom 19.1.0  
- types/node 24.0.1  
- types/jest 29.5.14  

## UI Component System
- Radix UI 3.2.1  
- Shadcn UI 2.6.3  
- lucide-react 0.515.0 (icons)  

## Styling
- TailwindCSS 4.1.10  
- tw-animate-css 1.3.4  
- tailwind-merge 3.3.1  
- clsx 2.1.1  
- class-variance-authority 0.7.1  
- PostCSS 8.5.5  
- Autoprefixer 10.4.20  

## Build System
- Vite 6.3.5  
- @vitejs/plugin-react 4.5.2  

## API & Utilities
- Axios 1.10.0  

## Testing
- Jest 30.0.0  
- testing-library/react 16.3.0  
- testing-library/jest-dom 6.6.3  

# 2. Project Constraints (Important)

Your guide must explicitly account for the following:
1. The frontend application resides in: `src/main/frontend`  
2. Spring Boot serves static assets from: `src/main/resources/static`  
3. A single `mvn clean package` must produce a runnable JAR containing the built React UI.  
4. Vite development server must proxy `/api` requests to `http://localhost:8080`.  
5. Production Vite builds must output to `src/main/resources/static`.  
6. Do NOT implement authentication. API is fully open.  
7. Use the existing OpenAPI spec at `openapi/openapi/openapi.yaml`.  
8. Java developers are your audience; explanations must be precise and structured.  

# 3. Vite Configuration Requirements

The guide must include:
- Step-by-step initialization using `npm create vite@latest`.  
- Update `vite.config.ts` to include:

**Development proxy:**  
```ts
server: {
  proxy: {
    '/api': 'http://localhost:8080'
  }
}
```

**Production output directory:**  
```ts
build: {
  outDir: '../resources/static',
  emptyOutDir: true
}
```

**Base path:**  
```ts
base: '/'
```

If `.env` files are used, explain how Vite injects variables via `import.meta.env`.

# 4. Maven Build Integration Requirements

Use the `frontend-maven-plugin`:

```xml
<plugin>
    <groupId>com.github.eirslett</groupId>
    <artifactId>frontend-maven-plugin</artifactId>
    <version>1.15.1</version>
    <executions>
        <execution>
            <id>install node and npm</id>
            <goals><goal>install-node-and-npm</goal></goals>
            <configuration>
                <nodeVersion>v22.16.0</nodeVersion>
                <npmVersion>11.4.0</npmVersion>
            </configuration>
        </execution>
        <execution>
            <id>npm install</id>
            <goals><goal>npm</goal></goals>
            <configuration><arguments>install</arguments></configuration>
        </execution>
        <execution>
            <id>build frontend</id>
            <phase>generate-resources</phase>
            <goals><goal>npm</goal></goals>
            <configuration><arguments>run build</arguments></configuration>
        </execution>
    </executions>
</plugin>
```

Also instruct Junie to:
- Update Maven clean plugin to delete `src/main/resources/static`.  
- Document how the plugin integrates with the lifecycle.  

# 5. React Application Requirements

Directives for the developer guide:

## API Consumption
- Use Axios with a reusable API client (configured base URL + interceptors).  
- Implement CRUD operations for each API defined in the OpenAPI spec.  

## Architecture
The guide must include:
- A standard layout + navigation.  
- A component folder structure.  
- A service layer for API calls.  
- Custom hooks for business logic (`useBeers`, `useCustomers`, etc.).  

## Error Handling
- Centralized Axios interceptor for error normalization.  
- React UI patterns for graceful failures.  

## Code Quality
- ESLint configuration with recommended + React + TS rules.  
- Prettier configuration.  
- Add `lint` and `format` scripts.  

## Type Safety (Important)
Use `openapi-typescript-codegen` to generate API types and clients:
- Add `generate-api-types` npm script.  
- Output to `src/main/frontend/src/api`.  

# 6. Testing Requirements

The guide must cover:
- Jest configuration.  
- A sample unit test.  
- How to mock Axios.  
- Component testing using React Testing Library.  

# 7. Guidelines Update

The guide must generate updated text for `.junie/guidelines.md` including:
- New project structure.  
- Commands for running the frontend + backend.  
- Commands for linting, formatting, testing.  
- End-to-end build instructions.  

# 8. Guide Outline (Required Structure)

Your generated guide must follow this format:

## **Part 1 — Foundation and Setup**
- Introduction & final goal.  
- Recommended project structure (include a tree diagram).  
- Initializing React + TypeScript with Vite.  
- Installing dependencies + configuring Tailwind, Shadcn, Radix.  

## **Part 2 — Build Integration**
- Maven + Vite integration.  
- Proxy + build output config.  
- How a single Spring Boot JAR is produced.  

## **Part 3 — Building the Application**
- API layer.  
- Custom hooks.  
- UI components.  
- Layout + routing.  
- Type-safe OpenAPI integration.  

## **Part 4 — Running & Testing**
- Running backend + frontend.  
- Jest unit tests.  
- Production build & running the final JAR.  
- Updating `.junie/guidelines.md`.