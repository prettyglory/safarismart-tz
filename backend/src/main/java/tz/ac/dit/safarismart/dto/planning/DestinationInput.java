package tz.ac.dit.safarismart.dto.planning;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record DestinationInput(
        @NotBlank String name,
        @Min(1) Integer days   // optional -- null means "auto-allocate"
) {
}
