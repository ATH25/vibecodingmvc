package tom.springframework.vibecodingmvc.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tom.springframework.vibecodingmvc.models.BeerRequestDto;
import tom.springframework.vibecodingmvc.models.BeerResponseDto;
import tom.springframework.vibecodingmvc.services.BeerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequestMapping("/api/v1/beers")
@Tag(name = "Beers", description = "Operations for managing beers in the catalog")
class BeerController {

    private final BeerService beerService;

    BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping
    @Operation(
            summary = "List beers",
            description = "Returns beers available in the catalog, with optional filtering by beerName and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of beers returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "Spring Data Page of BeerResponseDto",
                                    implementation = Object.class
                            ),
                            examples = @ExampleObject(name = "BeersPageExample", value = "{\n  \"content\": [\n    {\n      \"id\": 1,\n      \"version\": 0,\n      \"beerName\": \"Galaxy Cat IPA\",\n      \"beerStyle\": \"IPA\",\n      \"upc\": \"0123456789012\",\n      \"quantityOnHand\": 120,\n      \"price\": 12.99,\n      \"createdDate\": \"2025-08-01T12:34:56\",\n      \"updatedDate\": \"2025-08-15T09:00:00\"\n    }\n  ],\n  \"pageable\": {\n    \"pageNumber\": 0,\n    \"pageSize\": 10\n  },\n  \"totalElements\": 1,\n  \"totalPages\": 1,\n  \"number\": 0,\n  \"size\": 10\n}"))
            )
    })
    ResponseEntity<Page<BeerResponseDto>> listBeers(
            @Parameter(description = "Optional filter by beer name", example = "Galaxy")
            @RequestParam(value = "beerName", required = false) String beerName,
            @Parameter(description = "Pagination parameters (page, size) provided by Spring Data")
            Pageable pageable
    ) {
        // Sanitize user input to avoid XSS issues when echoed back in any UI and pass along. Service handles null/blank
        String safeBeerName = beerName != null ? HtmlUtils.htmlEscape(beerName) : null;
        return ResponseEntity.ok(beerService.listBeers(safeBeerName, pageable));
    }

    @GetMapping("/{beerId}")
    @Operation(
            summary = "Get beer by id",
            description = "Returns the beer with the given id if it exists."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beer found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeerResponseDto.class),
                            examples = @ExampleObject(name = "BeerExample", value = "{\n  \"id\": 42,\n  \"version\": 1,\n  \"beerName\": \"Pale Rider\",\n  \"beerStyle\": \"PALE_ALE\",\n  \"upc\": \"0987654321098\",\n  \"quantityOnHand\": 64,\n  \"price\": 9.49,\n  \"createdDate\": \"2025-07-20T10:15:30\",\n  \"updatedDate\": \"2025-08-10T08:00:00\"\n}"))
            ),
            @ApiResponse(responseCode = "404", description = "Beer not found", content = @Content)
    })
    ResponseEntity<BeerResponseDto> getBeerById(
            @Parameter(description = "Unique identifier of the beer", example = "42")
            @PathVariable("beerId") Integer beerId) {
        return beerService.getBeerById(beerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @Operation(
            summary = "Create a beer",
            description = "Creates a new beer and returns the created resource."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Beer created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeerResponseDto.class))) ,
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    ResponseEntity<BeerResponseDto> createBeer(
            @Valid @RequestBody BeerRequestDto dto) {
        // Sanitize text fields in request body to mitigate XSS when values are echoed back in responses or views
        BeerRequestDto safeDto = sanitizeDto(dto);
        BeerResponseDto created = beerService.saveBeer(safeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{beerId}")
    @Operation(
            summary = "Update a beer",
            description = "Updates an existing beer by id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beer updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeerResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Beer not found", content = @Content)
    })
    ResponseEntity<BeerResponseDto> updateBeer(
            @Parameter(description = "Unique identifier of the beer", example = "1")
            @PathVariable("beerId") Integer beerId,
            @Valid @RequestBody BeerRequestDto dto) {
        BeerRequestDto safeDto = sanitizeDto(dto);
        return beerService.updateBeer(beerId, safeDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{beerId}")
    @Operation(
            summary = "Delete a beer",
            description = "Deletes a beer by id. Returns 204 if deleted, 404 if not found."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Beer deleted"),
        @ApiResponse(responseCode = "404", description = "Beer not found")
    })
    ResponseEntity<Void> deleteBeer(
            @Parameter(description = "Unique identifier of the beer", example = "1")
            @PathVariable("beerId") Integer beerId) {
        // Attempt deletion only if present
        return beerService.getBeerById(beerId)
                .map(b -> {
                    beerService.deleteBeer(beerId);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Package-private to aid potential reuse in tests; escapes only textual fields
    BeerRequestDto sanitizeDto(BeerRequestDto dto) {
        if (dto == null) return null;
        String safeName = dto.beerName() != null ? HtmlUtils.htmlEscape(dto.beerName()) : null;
        String safeStyle = dto.beerStyle() != null ? HtmlUtils.htmlEscape(dto.beerStyle()) : null;
        String safeUpc = dto.upc() != null ? HtmlUtils.htmlEscape(dto.upc()) : null;
        return new BeerRequestDto(safeName, safeStyle, safeUpc, dto.quantityOnHand(), dto.price());
    }
}