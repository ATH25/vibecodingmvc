package tom.springframework.vibecodingmvc.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tom.springframework.vibecodingmvc.models.BeerOrderResponse;
import tom.springframework.vibecodingmvc.models.BeerOrderSummaryResponse;
import tom.springframework.vibecodingmvc.models.CreateBeerOrderCommand;
import tom.springframework.vibecodingmvc.services.BeerOrderService;

import java.net.URI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v1/beer-orders")
@Validated
@Tag(name = "Beer Orders", description = "Operations for creating and retrieving beer orders")
class BeerOrderController {

    private final BeerOrderService beerOrderService;

    BeerOrderController(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @GetMapping(produces = "application/json")
    @Operation(
            summary = "List beer orders",
            description = "Returns a paged list of beer orders."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(name = "paged",
                                    value = "{\n  \"content\": [\n    {\n      \"id\": 42,\n      \"customerRef\": \"PO-2025-0001\",\n      \"paymentAmount\": 24.99,\n      \"status\": \"PENDING\",\n      \"createdDate\": \"2025-08-20T14:13:00Z\"\n    }\n  ],\n  \"pageable\": {\n    \"pageNumber\": 0,\n    \"pageSize\": 10\n  },\n  \"totalElements\": 1,\n  \"totalPages\": 1,\n  \"number\": 0,\n  \"size\": 10\n}"))
            )
    })
    public ResponseEntity<Page<BeerOrderSummaryResponse>> list(
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(beerOrderService.listOrders(pageable));
    }

    @GetMapping(value = "/list", produces = "application/json")
    @Operation(summary = "Alias for list beer orders", description = "Same as GET /api/v1/beer-orders")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Page<BeerOrderSummaryResponse>> listAlias(
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return list(pageable);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Operation(
            summary = "Create a beer order",
            description = "Creates a new beer order and returns the created resource. The Location header contains the URI of the created order.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    headers = {@Header(name = "Location", description = "URI of the created beer order")},
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeerOrderResponse.class),
                            examples = @ExampleObject(name = "created",
                                    value = "{\n  \"id\": 42,\n  \"customerRef\": \"PO-2025-0001\",\n  \"paymentAmount\": 24.99,\n  \"status\": \"PENDING\",\n  \"lines\": [\n    {\n      \"id\": 10,\n      \"beerId\": 1,\n      \"beerName\": \"Galaxy Cat IPA\",\n      \"orderQuantity\": 2,\n      \"quantityAllocated\": 0,\n      \"status\": \"PENDING\"\n    }\n  ],\n  \"createdDate\": \"2025-08-20T14:13:00Z\",\n  \"updatedDate\": \"2025-08-20T14:13:00Z\"\n}"))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "415", description = "Unsupported Media Type", content = @Content)
    })
    ResponseEntity<BeerOrderResponse> create(@Valid @RequestBody CreateBeerOrderCommand cmd,
                                             UriComponentsBuilder uriBuilder) {
        int id = beerOrderService.createOrder(cmd);
        URI location = uriBuilder.path("/api/v1/beer-orders/{id}").buildAndExpand(id).toUri();
        return beerOrderService.getOrder(id)
                .map(resp -> ResponseEntity.created(location).body(resp))
                .orElseGet(() -> ResponseEntity.created(location).build());
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Get beer order by id", description = "Retrieves a beer order by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeerOrderResponse.class),
                            examples = @ExampleObject(name = "example",
                                    value = "{\n  \"id\": 42,\n  \"customerRef\": \"PO-2025-0001\",\n  \"paymentAmount\": 24.99,\n  \"status\": \"PENDING\",\n  \"lines\": [\n    {\n      \"id\": 10,\n      \"beerId\": 1,\n      \"beerName\": \"Galaxy Cat IPA\",\n      \"orderQuantity\": 2,\n      \"quantityAllocated\": 0,\n      \"status\": \"PENDING\"\n    }\n  ],\n  \"createdDate\": \"2025-08-20T14:13:00Z\",\n  \"updatedDate\": \"2025-08-20T14:13:00Z\"\n}"))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    })
    ResponseEntity<BeerOrderResponse> get(
            @Parameter(description = "ID of the beer order", example = "42")
            @PathVariable Integer id) {
        return beerOrderService.getOrder(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
