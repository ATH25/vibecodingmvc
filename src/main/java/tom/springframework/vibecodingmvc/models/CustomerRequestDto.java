package tom.springframework.vibecodingmvc.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CustomerRequestDto", description = "Request payload to create or update a customer")
public record CustomerRequestDto(
        @NotBlank
        @Size(max = 120)
        @Schema(description = "Full name of the customer", example = "Jane Doe")
        String name,

        @NotBlank
        @Email
        @Size(max = 255)
        @Schema(description = "Unique email address of the customer", example = "jane.doe@example.com")
        String email,

        @Size(max = 40)
        @Schema(description = "Phone number", example = "+1-555-123-4567")
        String phone,

        @NotBlank
        @Size(max = 200)
        @Schema(description = "Address line 1", example = "123 Main St")
        String addressLine1,

        @Size(max = 200)
        @Schema(description = "Address line 2", example = "Apt 4B")
        String addressLine2,

        @Size(max = 120)
        @Schema(description = "City", example = "Springfield")
        String city,

        @Size(max = 80)
        @Schema(description = "State or province", example = "IL")
        String state,

        @Size(max = 20)
        @Schema(description = "Postal/ZIP code", example = "62704")
        String postalCode
) {}
