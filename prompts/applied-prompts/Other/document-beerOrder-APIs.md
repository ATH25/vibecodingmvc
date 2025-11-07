
Inspect tom.springframework.vibecodingmvc.controllers.BeerOrderController and the DTOs:
•	tom.springframework.vibecodingmvc.models.CreateBeerOrderCommand
•	tom.springframework.vibecodingmvc.models.CreateBeerOrderItem
•	tom.springframework.vibecodingmvc.models.BeerOrderResponse
•	tom.springframework.vibecodingmvc.models.BeerOrderLineResponse

Goal: Add/complete Springdoc OpenAPI annotations so Beer Order endpoints are fully documented. Do not change method signatures, business logic, or routes.

A) Controller annotations (verify or add—no duplication)
•	Add @Tag(name = "Beer Orders", description = "Operations for creating and retrieving beer orders") on the class.
•	Add @Validated on the class (if missing).
•	Ensure request mappings declare media types:
•	produces = "application/json" on all endpoints
•	consumes = "application/json" on POST
•	Add @Operation(summary, description) on each method.
•	Add @Parameter(description, example) to the @PathVariable id parameter on the GET by id endpoint.
•	Add @ApiResponses per endpoint:
•	POST /api/v1/beer-orders
•	201 Created → body: BeerOrderResponse (application/json)
•	Add a response header: @Header(name = "Location", description = "URI of the created beer order")
•	400 Bad Request → no body (content = @Content)
•	415 Unsupported Media Type → no body (content = @Content)
•	GET /api/v1/beer-orders/{id}
•	200 OK → body: BeerOrderResponse (application/json)
•	404 Not Found → no body (content = @Content)

B) DTO annotations (add only where missing; don’t modify fields)

Add @Schema to types and fields with description, example, and constraints mapped from Jakarta Validation:
•	CreateBeerOrderCommand (request)
•	Type-level @Schema(description = "Command to create a beer order")
•	customerRef → description + example "PO-2025-0001"
•	paymentAmount → map @NotNull/@PositiveOrZero ⇒ required, minimum = "0", example "24.99"
•	items → map @NotNull + @Size(min=1) ⇒ required, minItems = 1
•	Use @ArraySchema(schema = @Schema(implementation = CreateBeerOrderItem.class))
•	Provide an example with 1–2 items
•	CreateBeerOrderItem (request line)
•	beerId → map @NotNull ⇒ required; example 1
•	quantity → map @Positive ⇒ minimum = "1"; example 2
•	BeerOrderResponse (response)
•	Type-level @Schema(description = "Beer order details")
•	id example 42
•	customerRef example "PO-2025-0001"
•	paymentAmount example "24.99"
•	status → if it’s a String with a known set, add allowableValues = {"PENDING","PAID","CANCELLED"} (adjust to actual values) and an example
•	lines → @ArraySchema(schema = @Schema(implementation = BeerOrderLineResponse.class)) with a small example list
•	createdDate, updatedDate → @Schema(type = "string", format = "date-time", example = "2025-08-20T14:13:00Z")
•	BeerOrderLineResponse (response line)
•	id example 10
•	beerId example 1
•	beerName example "Galaxy Cat IPA"
•	orderQuantity → map @Positive ⇒ minimum = "1"; example 2
•	quantityAllocated → map @PositiveOrZero ⇒ minimum = "0"; example 0
•	status → add allowableValues if finite; include example

C) Examples in responses
•	Include concise examples for 200/201 responses only.
•	For 400/404/415, specify no body (content = @Content) so Swagger UI shows no example.

D) Non-goals
•	Do not change method signatures, service calls, or business logic.
•	Do not add or remove endpoints.

E) When finished
•	Report which files were updated.
•	Append the prompt I gave you to /prompts/Other/prompts.md.