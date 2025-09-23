package tom.springframework.vibecodingmvc.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "CustomerResponseDto", description = "Response payload representing a customer")
public record CustomerResponseDto(
        @Schema(description = "Unique identifier of the customer", example = "1")
        Integer id,

        @Schema(description = "Entity version for optimistic locking", example = "0", minimum = "0")
        Integer version,

        @Schema(description = "Full name of the customer", example = "Jane Doe")
        String name,

        @Schema(description = "Unique email address of the customer", example = "jane.doe@example.com")
        String email,

        @Schema(description = "Phone number", example = "+1-555-123-4567")
        String phone,

        @Schema(description = "Address line 1", example = "123 Main St")
        String addressLine1,

        @Schema(description = "Address line 2", example = "Apt 4B")
        String addressLine2,

        @Schema(description = "City", example = "Springfield")
        String city,

        @Schema(description = "State or province", example = "IL")
        String state,

        @Schema(description = "Postal/ZIP code", example = "62704")
        String postalCode,

        @Schema(description = "Creation timestamp", type = "string", format = "date-time", example = "2025-08-01T12:34:56")
        LocalDateTime createdDate,

        @Schema(description = "Last update timestamp", type = "string", format = "date-time", example = "2025-08-15T09:00:00")
        LocalDateTime updatedDate
) {}
