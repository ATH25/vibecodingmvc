# Cleanup tasks: migrate Redocly OpenAPI to split structure using Springdoc export as the source of truth

Goal: Remove demo/example artifacts and rebuild the OpenAPI in split form from the provided Springdoc export (/v3/api-docs.yaml). Do not modify any Java code.

Outcomes
- Root file openapi-starter-main/openapi/openapi.yaml contains only metadata and $refs to split files.
- Only real endpoints and DTOs from the Springdoc export are present.
- Demo/example paths and schemas are removed.
- Tags, examples, constraints, headers (Location), and empty 404/204 responses are preserved.
- Lint passes and docs preview renders all endpoints.
- OpenAPI version is locked to **3.0.1** (matches the Springdoc export).

Source of truth
- Use the attached Springdoc export file: /v3/api-docs.yaml (OpenAPI 3.0.1 style in the attachment you provided). Treat this as canonical; ignore any existing demo content in the repo.

Directory layout (split structure)
- openapi-starter-main/openapi/openapi.yaml (root)
- openapi-starter-main/openapi/paths/*.yaml (one file per path item)
- openapi-starter-main/openapi/components/schemas/*.yaml (one file per schema/DTO)
- openapi-starter-main/openapi/components/responses/*.yaml (optional, only if we centralize shared responses; not required for this task)
- openapi-starter-main/openapi/components/headers/*.yaml (only if reusing headers across operations; optional)

Conventions
- Path files: mirror the URL with '/' replaced by '_' and keep path params in braces.
  - Example: /api/v1/beers -> paths/api_v1_beers.yaml
  - Example: /api/v1/beers/{beerId} -> paths/api_v1_beers_{beerId}.yaml
  - Example: /api/v1/beer-orders -> paths/api_v1_beer-orders.yaml (hyphen kept)
  - Example: /api/v1/beer-orders/{id} -> paths/api_v1_beer-orders_{id}.yaml
- Schema files: one schema per file, file name matches schema key.
  - Example: components/schemas/BeerRequestDto.yaml

Step-by-step tasks
1) Backup and prepare
- Optional: copy openapi-starter-main/openapi to a backup folder.
- Ensure Node.js is installed; we will use Redocly CLI via npx.

2) Identify and list demo/example artifacts to remove
- In openapi-starter-main/openapi/openapi.yaml remove demo content:
  - paths: '/users/{username}', '/user', '/user/list', '/echo', '/pathItem', '/pathItemWithExamples'.
  - x-tagGroups referencing demo tags; remove or replace with real tags.
  - Demo tags: Echo, User, Admin, Info, Tag.
  - webhooks: userInfo (demo) — remove unless it exists in Springdoc export (it does not).
  - securitySchemes if they are demo-only and not referenced — may keep or remove; safe to remove if not used.
- In components/schemas remove demo/example schemas:
  - Admin.yaml, Basic.yaml, Email.yaml, ExampleObject.yaml, Problem.yaml (unless you use ProblemDetails), Schema.yaml, User.yaml, UserID.yaml.
- In components/headers: keep only those needed. ExpiresAfter.yaml is demo — remove if not used.
- In components/responses: Problem.yaml is demo — remove if not used.

3) Extract real endpoints and DTOs from Springdoc export (/v3/api-docs.yaml)
- Tags (keep):
  - Beers: "Operations for managing beers in the catalog"
  - Beer Orders: "Operations for creating and retrieving beer orders"
- Paths to include:
  - /api/v1/beers (GET list, POST create)
  - /api/v1/beers/{beerId} (GET, PUT, DELETE)
  - /api/v1/beer-orders (POST)
  - /api/v1/beer-orders/{id} (GET)
- Schemas to include:
  - BeerRequestDto
  - BeerResponseDto
  - CreateBeerOrderCommand
  - CreateBeerOrderItem
  - BeerOrderLineResponse
  - BeerOrderResponse

- Set `openapi` version to **3.0.1** (lock to this value to match the Springdoc export).
- Keep minimal info block (title and version). Optionally keep servers pointing to http://localhost:8080 to match Springdoc export.
- Define tags for "Beers" and "Beer Orders" with descriptions from export.
- Reference path files only using $ref. Example root:

  openapi: 3.0.1
  info:
    title: OpenAPI definition
    version: v0
  servers:
    - url: http://localhost:8080
      description: Generated server url
  tags:
    - name: Beers
      description: Operations for managing beers in the catalog
    - name: Beer Orders
      description: Operations for creating and retrieving beer orders
  paths:
    /api/v1/beers:
      $ref: paths/api_v1_beers.yaml
    /api/v1/beers/{beerId}:
      $ref: paths/api_v1_beers_{beerId}.yaml
    /api/v1/beer-orders:
      $ref: paths/api_v1_beer-orders.yaml
    /api/v1/beer-orders/{id}:
      $ref: paths/api_v1_beer-orders_{id}.yaml
  components:
    schemas:
      BeerRequestDto:
        $ref: components/schemas/BeerRequestDto.yaml
      BeerResponseDto:
        $ref: components/schemas/BeerResponseDto.yaml
      CreateBeerOrderCommand:
        $ref: components/schemas/CreateBeerOrderCommand.yaml
      CreateBeerOrderItem:
        $ref: components/schemas/CreateBeerOrderItem.yaml
      BeerOrderLineResponse:
        $ref: components/schemas/BeerOrderLineResponse.yaml
      BeerOrderResponse:
        $ref: components/schemas/BeerOrderResponse.yaml

5) Create split path files from the Springdoc export (preserve examples/headers/status codes)
- paths/api_v1_beers.yaml (GET list, POST create):
  get:
    tags: [Beers]
    summary: List beers
    description: Returns all beers available in the catalog.
    operationId: listBeers
    responses:
      '200':
        description: List of beers returned
        content:
          application/json:
            schema:
              type: array
              items:
                $ref: ../components/schemas/BeerResponseDto.yaml
            examples:
              BeersExample:
                description: BeersExample
                value: [ { id: 1, version: 0, beerName: Galaxy Cat IPA, beerStyle: IPA, upc: '0123456789012', quantityOnHand: 120, price: 12.99, createdDate: '2025-08-01T12:34:56', updatedDate: '2025-08-15T09:00:00' } ]
  post:
    tags: [Beers]
    summary: Create a beer
    description: Creates a new beer and returns the created resource.
    operationId: createBeer
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: ../components/schemas/BeerRequestDto.yaml
    responses:
      '400':
        description: Validation error
      '201':
        description: Beer created
        content:
          application/json:
            schema:
              $ref: ../components/schemas/BeerResponseDto.yaml

- paths/api_v1_beers_{beerId}.yaml (GET, PUT, DELETE):
  get:
    tags: [Beers]
    summary: Get beer by id
    description: Returns the beer with the given id if it exists.
    operationId: getBeerById
    parameters:
      - name: beerId
        in: path
        description: Unique identifier of the beer
        required: true
        schema: { type: integer, format: int32 }
        example: 42
    responses:
      '200':
        description: Beer found
        content:
          application/json:
            schema:
              $ref: ../components/schemas/BeerResponseDto.yaml
            examples:
              BeerExample:
                description: BeerExample
                value: { id: 42, version: 1, beerName: Pale Rider, beerStyle: PALE_ALE, upc: '0987654321098', quantityOnHand: 64, price: 9.49, createdDate: '2025-07-20T10:15:30', updatedDate: '2025-08-10T08:00:00' }
      '404':
        description: Beer not found
  put:
    tags: [Beers]
    summary: Update a beer
    description: Updates an existing beer by id.
    operationId: updateBeer
    parameters:
      - name: beerId
        in: path
        description: Unique identifier of the beer
        required: true
        schema: { type: integer, format: int32 }
        example: 1
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: ../components/schemas/BeerRequestDto.yaml
    responses:
      '400': { description: Validation error }
      '404': { description: Beer not found }
      '200':
        description: Beer updated
        content:
          application/json:
            schema:
              $ref: ../components/schemas/BeerResponseDto.yaml
  delete:
    tags: [Beers]
    summary: Delete a beer
    description: Deletes a beer by id. Returns 204 if deleted, 404 if not found.
    operationId: deleteBeer
    parameters:
      - name: beerId
        in: path
        description: Unique identifier of the beer
        required: true
        schema: { type: integer, format: int32 }
        example: 1
    responses:
      '404': { description: Beer not found }
      '204': { description: Beer deleted }

- paths/api_v1_beer-orders.yaml (POST):
  post:
    tags: [Beer Orders]
    summary: Create a beer order
    description: Creates a new beer order and returns the created resource. The Location header contains the URI of the created order.
    operationId: create
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: ../components/schemas/CreateBeerOrderCommand.yaml
    responses:
      '400': { description: Bad Request }
      '201':
        description: Created
        headers:
          Location:
            description: URI of the created beer order
            schema: { type: string }
        content:
          application/json:
            schema:
              $ref: ../components/schemas/BeerOrderResponse.yaml
            examples:
              created:
                description: created
                value: { id: 42, customerRef: PO-2025-0001, paymentAmount: 24.99, status: PENDING, lines: [ { id: 10, beerId: 1, beerName: Galaxy Cat IPA, orderQuantity: 2, quantityAllocated: 0, status: PENDING } ], createdDate: '2025-08-20T14:13:00Z', updatedDate: '2025-08-20T14:13:00Z' }
      '415': { description: Unsupported Media Type }

- paths/api_v1_beer-orders_{id}.yaml (GET):
  get:
    tags: [Beer Orders]
    summary: Get beer order by id
    description: Retrieves a beer order by its identifier.
    operationId: get
    parameters:
      - name: id
        in: path
        description: ID of the beer order
        required: true
        schema: { type: integer, format: int32 }
        example: 42
    responses:
      '200':
        description: OK
        content:
          application/json:
            schema:
              $ref: ../components/schemas/BeerOrderResponse.yaml
            examples:
              example:
                description: example
                value: { id: 42, customerRef: PO-2025-0001, paymentAmount: 24.99, status: PENDING, lines: [ { id: 10, beerId: 1, beerName: Galaxy Cat IPA, orderQuantity: 2, quantityAllocated: 0, status: PENDING } ], createdDate: '2025-08-20T14:13:00Z', updatedDate: '2025-08-20T14:13:00Z' }
      '404': { description: Not Found }

6) Create split schema files from Springdoc export (preserve constraints, examples)
- components/schemas/BeerRequestDto.yaml
  type: object
  description: Request payload to create or update a beer
  required: [beerName, beerStyle, upc]
  properties:
    beerName: { type: string, description: Name of the beer, example: Galaxy Cat IPA }
    beerStyle: { type: string, description: 'Style of the beer (e.g., ALE, PALE_ALE, IPA)', example: IPA }
    upc: { type: string, description: Universal Product Code (13-digit), example: '0123456789012' }
    quantityOnHand: { type: integer, format: int32, minimum: 0, description: Quantity on hand (must be zero or positive), example: 120 }
    price: { type: number, minimum: 0.0, exclusiveMinimum: true, description: Price per unit (must be greater than 0), example: 12.99 }

- components/schemas/BeerResponseDto.yaml
  type: object
  description: Response payload representing a beer
  properties:
    id: { type: integer, format: int32, description: Unique identifier of the beer, example: 1 }
    version: { type: integer, format: int32, minimum: 0, description: Entity version for optimistic locking, example: 0 }
    beerName: { type: string, description: Name of the beer, example: Galaxy Cat IPA }
    beerStyle: { type: string, description: 'Style of the beer (e.g., ALE, PALE_ALE, IPA)', example: IPA }
    upc: { type: string, description: Universal Product Code (13-digit), example: '0123456789012' }
    quantityOnHand: { type: integer, format: int32, minimum: 0, description: Quantity on hand, example: 120 }
    price: { type: number, minimum: 0.01, description: Price per unit, example: 12.99 }
    createdDate: { type: string, format: date-time, description: Creation timestamp }
    updatedDate: { type: string, format: date-time, description: Last update timestamp }

- components/schemas/CreateBeerOrderItem.yaml
  type: object
  description: A single line item in a beer order
  required: [beerId]
  properties:
    beerId: { type: integer, format: int32, description: Identifier of the beer to order, example: 1 }
    quantity: { type: integer, format: int32, minimum: 1, description: Quantity of the beer to order, example: 2 }

- components/schemas/CreateBeerOrderCommand.yaml
  type: object
  description: Command to create a beer order
  required: [items, paymentAmount]
  properties:
    customerRef: { type: string, description: Client-provided reference for the order, example: PO-2025-0001 }
    paymentAmount: { type: number, minimum: 0, description: Payment amount for the order, example: 24.99 }
    items:
      type: array
      description: Order items
      minItems: 1
      items:
        $ref: ./CreateBeerOrderItem.yaml

- components/schemas/BeerOrderLineResponse.yaml
  type: object
  description: Beer order line details
  properties:
    id: { type: integer, format: int32, description: Line identifier, example: 10 }
    beerId: { type: integer, format: int32, description: Beer identifier, example: 1 }
    beerName: { type: string, description: Name of the beer, example: Galaxy Cat IPA }
    orderQuantity: { type: integer, format: int32, minimum: 1, description: Quantity ordered, example: 2 }
    quantityAllocated: { type: integer, format: int32, minimum: 0, description: Quantity allocated, example: 0 }
    status:
      type: string
      description: Line status
      example: PENDING
      enum: [PENDING, ALLOCATED, BACKORDERED, SHIPPED]

- components/schemas/BeerOrderResponse.yaml
  type: object
  description: Beer order details
  properties:
    id: { type: integer, format: int32, description: Order identifier, example: 42 }
    customerRef: { type: string, description: Client-provided reference for the order, example: PO-2025-0001 }
    paymentAmount: { type: number, description: Payment amount, example: 24.99 }
    status:
      type: string
      description: Current status of the order
      example: PENDING
      enum: [PENDING, PAID, CANCELLED]
    lines:
      type: array
      items:
        $ref: ./BeerOrderLineResponse.yaml
      example: [ { id: 10, beerId: 1, beerName: Galaxy Cat IPA, orderQuantity: 2, quantityAllocated: 0, status: PENDING } ]
    createdDate: { type: string, format: date-time, example: '2025-08-20T14:13:00Z' }
    updatedDate: { type: string, format: date-time, example: '2025-08-20T14:13:00Z' }

7) Wire relative $ref paths correctly
- From a path file to a schema: $ref: ../components/schemas/SchemaName.yaml
- From CreateBeerOrderCommand.yaml to CreateBeerOrderItem.yaml: $ref: ./CreateBeerOrderItem.yaml
- From root to components: $ref: components/schemas/SchemaName.yaml

8) Preserve empty responses (no body)
- 404 and 204 responses must have description only and no content node.
- Keep 201 headers: include Location header with schema type string.
- Standardize `400` responses as **description-only** (no `content`) **unless** the Springdoc export includes a response body; if Springdoc provides a body, replicate it exactly.

9) Cleanup: delete demo files
- Delete the following files if present:
  - openapi-starter-main/openapi/paths/echo.yaml
  - openapi-starter-main/openapi/paths/user.yaml
  - openapi-starter-main/openapi/paths/user-status.yaml
  - openapi-starter-main/openapi/paths/users_{username}.yaml
  - openapi-starter-main/openapi/paths/pathItem.yaml
  - openapi-starter-main/openapi/paths/pathItemWithExamples.yaml
  - openapi-starter-main/openapi/components/schemas/Admin.yaml
  - openapi-starter-main/openapi/components/schemas/Basic.yaml
  - openapi-starter-main/openapi/components/schemas/Email.yaml
  - openapi-starter-main/openapi/components/schemas/ExampleObject.yaml
  - openapi-starter-main/openapi/components/schemas/Problem.yaml (if not using RFC9457 Problem here)
  - openapi-starter-main/openapi/components/schemas/Schema.yaml
  - openapi-starter-main/openapi/components/schemas/User.yaml
  - openapi-starter-main/openapi/components/schemas/UserID.yaml
  - openapi-starter-main/openapi/components/responses/Problem.yaml (if unused)
  - openapi-starter-main/openapi/components/headers/ExpiresAfter.yaml (if unused)

10) Update root openapi.yaml
- Remove demo tags, x-tagGroups, webhooks, and demo security schemes if not needed.
- Keep only real tags, paths, and components $refs as shown above.

11) Acceptance checks
- From openapi-starter-main/openapi directory, run:
  - npx @redocly/cli lint openapi.yaml
    - Expected: no errors (warnings acceptable if intentional; fix if actionable).
  - npx @redocly/cli preview-docs openapi.yaml
    - Expected: renders all endpoints; tags "Beers" and "Beer Orders" visible; examples render under responses; 404/204 responses show without body.
- Optional: add npm scripts in openapi-starter-main/package.json:
  - "test": "redocly lint openapi/openapi.yaml"
  - "start": "redocly preview-docs openapi/openapi.yaml"

12) Review checklist
- [ ] Root openapi.yaml has only metadata, tags, servers, $refs for paths and components schemas.
- [ ] All path files created and linked.
- [ ] All schema files created with constraints/examples from Springdoc export.
- [ ] 404/204 responses have no content bodies.
- [ ] 201 responses include Location header where applicable.
- [ ] No demo paths/schemas/responses/headers remain.
- [ ] Lint passes, preview renders.

Notes and pitfalls
- Keep relative $ref paths correct depending on the file location.
- Maintain numeric examples as numbers (unquoted) except UPC which is a string.
- Maintain date-time format for timestamps.
- If keeping ProblemDetails in the future, add explicit response components and reference them; not part of this task.
