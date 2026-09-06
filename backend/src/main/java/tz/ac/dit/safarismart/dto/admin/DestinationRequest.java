package tz.ac.dit.safarismart.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DestinationRequest(
        @NotNull Long regionId,
        @NotBlank @Size(max = 150) String name,
        String description,
        Double latitude,
        Double longitude
) {
}
