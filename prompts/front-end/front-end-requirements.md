# Front-End Requirements Guide
Integrate a modern React frontend into this Spring Boot app and produce a single runnable JAR via Maven.

Audience: Java/Spring Boot engineers. Workflow: requirements → plan → tasks → implementation.

What you will have when done:
- React app in `src/main/frontend`
- Vite dev server with proxy to Spring Boot
- Tailwind + Radix + shadcn/ui UI stack
- Production assets emitted to `src/main/resources/static`
- One-command build: `mvn clean package` produces a JAR with the UI bundled
- Type-safe API clients generated from `openapi-starter-main/openapi/openapi.yaml`

—

# 1) Technologies (by category)

- Core: React 19.1.0, React DOM 19.1.1, TypeScript 5.8.3, React Router DOM 7.6.36
- Runtime: Node.js 22.16.0, npm 11.4.0
- UI: Radix UI 3.2.x, shadcn/ui 2.6.x, lucide-react 0.515.x
- Styling: Tailwind CSS 4.1.x, tailwind-merge 3.3.x, clsx 2.1.x, class-variance-authority 0.7.x, PostCSS 8.5.x, Autoprefixer 10.4.x
- Build: Vite 6.3.x, @vitejs/plugin-react 4.5.x
- API: Axios 1.10.x
- Testing: Jest 30.x, @testing-library/react 16.3.x, @testing-library/jest-dom 6.6.x, @types/jest 29.5.x

—

# 2) Project constraints (must-haves)

1. Frontend sources live in: `src/main/frontend`
2. Spring Boot serves static assets from: `src/main/resources/static`
3. `mvn clean package` must build the React app and include assets in the final JAR
4. Dev proxy: Vite must proxy `/api` to `http://localhost:8080`
5. Prod build output directory for Vite: `src/main/resources/static`
6. No authentication (open API)
7. OpenAPI spec path: `openapi-starter-main/openapi/openapi.yaml`

—

# 3) Essential requirements (addendum)

These requirements clarify foundational expectations that the implementation must satisfy. They are high-level and do not prescribe specific code.

1. UI component libraries
   - Use shadcn/ui and Radix UI components for accessible, composable primitives and application-ready components.
   - Use Tailwind CSS for styling, with extended theme variables to support the design system and shadcn/ui defaults.
   - Use utility helpers consistently: `clsx`, `tailwind-merge`, and `class-variance-authority` for conditional classes, merge safety, and variant APIs.

2. Tailwind CSS extended configuration
   - Extend theme tokens for colors, border radius, spacing, and support for dark mode. The design tokens should align with shadcn/ui expectations.
   - Ensure shadcn/ui components derive styling from the extended tokens so that global theme changes cascade predictably.

3. Environment variables
   - Define `VITE_API_BASE_URL` in both `.env` and `.env.development` to control the backend API base URL.
   - All API clients must read and use `VITE_API_BASE_URL`; do not hard-code hostnames or ports in services or components.

4. API client layer
   - Provide a reusable Axios instance shared across modules.
   - Configure global error handling using Axios interceptors (e.g., request/response normalization, user-friendly error mapping).
   - Organize resource-specific API modules at minimum for Beer, Customer, and Order domains.

5. Routing structure
   - Use React Router for client-side routing.
   - Provide top-level routes for Beer, Customer, and BeerOrder modules with list/detail/create/edit views as appropriate under a common app layout.

6. Component folder structure
   - Keep a clear separation by responsibility:
     - `pages/` for route-level screens
     - `components/` for reusable presentational building blocks
     - `hooks/` for stateful logic and data fetching
     - `services/` for API clients and domain-specific service functions

7. Custom hooks
   - For Beer, Customer, and Order, provide dedicated hooks that cover loading, filtering, pagination, and CRUD workflows.
   - Hooks should encapsulate remote state (loading/error) and expose a simple, typed interface to pages/components.

8. Optional OpenAPI type generation
   - You may use openapi-typescript-codegen to generate TypeScript models and API clients from the provided OpenAPI spec.
   - This is optional but recommended to improve type safety and consistency; generated types should integrate with the API client layer.

9. Testing requirements
   - Use Jest with React Testing Library.
   - Include basic tests for components (rendering/interaction), hooks (state transitions, data loading), and services (API call behavior and error handling).

10. Maven build integration (optional)
   - If Maven integration is adopted for CI/CD convenience, the frontend should build automatically via `frontend-maven-plugin` during `mvn clean package`.
   - This is optional; when used, ensure the plugin’s goals are wired to the standard Maven lifecycle without custom commands.

—

# Part 1 — Foundation & Setup

Goal: Create the React app under `src/main/frontend` with the chosen UI stack.

Step 1. Create the Vite app
- From project root:
  - `mkdir -p src/main/frontend && cd src/main/frontend`
  - `npm create vite@latest .`
  - Choose: Framework = React, Variant = TypeScript

Step 2. Install dependencies
```
# Runtime + UI + styling utilities
npm i react-router-dom axios clsx tailwindcss tailwind-merge class-variance-authority lucide-react tw-animate-css

# Build tools + TypeScript + PostCSS pipeline
npm i -D vite @vitejs/plugin-react typescript @types/node postcss autoprefixer

# Testing stack
npm i -D jest @types/jest ts-jest @testing-library/react @testing-library/jest-dom
```

Step 3. Tailwind CSS 4 setup
- Tailwind 4 uses config-in-CSS. Create `src/main/frontend/src/index.css` with:
```
@import "tailwindcss";
```
- Ensure PostCSS and Autoprefixer are installed (done above). No tailwind config file is required.

Step 4. shadcn/ui and Radix
- Initialize shadcn/ui:
  - `npx shadcn@latest init` (accept defaults; tailwind v4 works without config file)
- Add a component example (e.g., Button):
  - `npx shadcn@latest add button`
- Radix UI primitives are installed per-component by shadcn; you can also add directly from Radix as needed.

Step 5. Project structure (frontend)
```
src/main/frontend/
  package.json
  vite.config.ts
  tsconfig.json
  index.html
  src/
    index.css
    main.tsx
    app/
      App.tsx
      routes/
        index.tsx
        beers/
          BeersPage.tsx
          BeerForm.tsx
        customers/
          CustomersPage.tsx
          CustomerForm.tsx
      components/
        layout/
          AppLayout.tsx
          NavBar.tsx
        ui/ (shadcn components if customized)
      hooks/
        useBeers.ts
        useCustomers.ts
      services/
        http.ts
        beers.ts
        customers.ts
      api/ (OpenAPI generated)
```

—

# Part 2 — Build Integration

Step 6. Configure Vite (vite.config.ts)
Create or update `src/main/frontend/vite.config.ts`:
```
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'node:path';

export default defineConfig(({ mode }) => ({
  plugins: [react()],
  root: '.',
  base: '/',
  server: {
    port: 5173,
    strictPort: true,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  },
  build: {
    outDir: path.resolve(__dirname, '../resources/static'),
    emptyOutDir: true,
    sourcemap: mode !== 'production'
  }
}));
```

Step 7. package.json scripts (frontend)
Update `src/main/frontend/package.json`:
```
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview --port 4173",
    "lint": "echo 'Use your IDE ESLint plugin or add eslint config'",
    "format": "prettier -w .",
    "test": "jest --passWithNoTests",
    "generate-api-types": "openapi -i ../openapi-starter-main/openapi/openapi.yaml -o src/api --client axios"
  }
}
```
Notes:
- `generate-api-types` uses `openapi-typescript-codegen` CLI (`openapi`); install with `npm i -D openapi-typescript-codegen`.
- The input path is relative to `src/main/frontend`.

Step 8. Maven integration
Add to `pom.xml` (top-level project) the frontend plugin in the `<build><plugins>` section:
```
<plugin>
  <groupId>com.github.eirslett</groupId>
  <artifactId>frontend-maven-plugin</artifactId>
  <version>1.15.1</version>
  <configuration>
    <workingDirectory>src/main/frontend</workingDirectory>
  </configuration>
  <executions>
    <execution>
      <id>install node and npm</id>
      <goals>
        <goal>install-node-and-npm</goal>
      </goals>
      <configuration>
        <nodeVersion>v22.16.0</nodeVersion>
        <npmVersion>11.4.0</npmVersion>
      </configuration>
    </execution>
    <execution>
      <id>npm install</id>
      <goals>
        <goal>npm</goal>
      </goals>
      <configuration>
        <arguments>ci</arguments>
      </configuration>
    </execution>
    <execution>
      <id>build frontend</id>
      <phase>generate-resources</phase>
      <goals>
        <goal>npm</goal>
      </goals>
      <configuration>
        <arguments>run build</arguments>
      </configuration>
    </execution>
  </executions>
  </plugin>
```

Also ensure `maven-clean-plugin` clears the static output to avoid stale assets:
```
<plugin>
  <artifactId>maven-clean-plugin</artifactId>
  <version>3.3.2</version>
  <configuration>
    <filesets>
      <fileset>
        <directory>src/main/resources/static</directory>
      </fileset>
    </filesets>
  </configuration>
  </plugin>
```

How the one-command build works
- `generate-resources` runs before packaging; the frontend plugin builds Vite and writes to `/static`
- Spring Boot Maven plugin then packages everything (including `/static`) into the JAR

—

# Part 3 — Implementing the Application

Step 9. Routing and layout
- `src/main/frontend/src/main.tsx` mounts `<App />`
- `App.tsx` configures React Router with routes: `/`, `/beers`, `/customers`
- Create `AppLayout` with a top `NavBar` and an `<Outlet/>`

Example `App.tsx`:
```
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import AppLayout from './components/layout/AppLayout';
import IndexPage from './routes/index';
import BeersPage from './routes/beers/BeersPage';
import CustomersPage from './routes/customers/CustomersPage';

const router = createBrowserRouter([
  {
    element: <AppLayout />,
    children: [
      { path: '/', element: <IndexPage /> },
      { path: '/beers', element: <BeersPage /> },
      { path: '/customers', element: <CustomersPage /> }
    ]
  }
]);

export default function App() {
  return <RouterProvider router={router} />;
}
```

Step 10. API layer
- `services/http.ts` creates a configured Axios instance:
```
import axios from 'axios';

export const http = axios.create({ baseURL: '/api' });

http.interceptors.response.use(
  (r) => r,
  (err) => {
    const status = err?.response?.status ?? 0;
    const message = err?.response?.data?.message || err.message || 'Request failed';
    return Promise.reject({ status, message });
  }
);
```

- Generate OpenAPI clients: `npm run generate-api-types` (in `src/main/frontend`)
- Use generated clients in feature services, e.g., `services/beers.ts`

Step 11. Hooks
- `hooks/useBeers.ts` and `hooks/useCustomers.ts` wrap service calls and expose state `{data, loading, error}`

Step 12. UI components
- Build on shadcn/ui components (e.g., Button, Input, Card)
- Keep domain-specific UI in `app/components/` and generic in `app/components/ui/`

Step 13. Coding standards
- Add Prettier: `npm i -D prettier` then run `npm run format`
- For ESLint, either use your IDE ESLint plugin or add eslint config later. Keep files small and typed.

—

# Part 4 — Running & Testing

Step 14. Local development
- Terminal A (backend): `mvn spring-boot:run`
- Terminal B (frontend):
  - `cd src/main/frontend`
  - `npm install`
  - `npm run dev` → open http://localhost:5173 (API proxied to 8080)

Step 15. Generate clients and run tests
- `cd src/main/frontend`
- `npm run generate-api-types`
- `npm test`

Step 16. Production build and run
- From project root: `mvn clean package`
- Run: `java -jar target/*-SNAPSHOT.jar`
- Open: http://localhost:8080 (assets served from `/static`)

Step 17. Testing examples
- Mock Axios in tests:
```
jest.mock('axios');
```
- Component test example (outline):
```
import { render, screen } from '@testing-library/react';
import BeersPage from './BeersPage';

test('renders Beers header', () => {
  render(<BeersPage />);
  expect(screen.getByRole('heading', { name: /beers/i })).toBeInTheDocument();
});
```

—

# Notes for Junie (automation prompts)

- Update `.junie/guidelines.md` to include:
  - New frontend directory structure
  - Dev commands: `mvn spring-boot:run`, `npm run dev`
  - Build steps: `mvn clean package`
  - Testing: `npm test` (frontend), `mvn test` (backend)
  - OpenAPI codegen command

- Controller → Service → Repository layering remains unchanged on the backend; do not call repositories from the frontend.

- REST API follows `/api/v1/...` with nested resources where applicable (see OpenAPI). Use Axios baseURL `/api` and keep path versions in service methods.
