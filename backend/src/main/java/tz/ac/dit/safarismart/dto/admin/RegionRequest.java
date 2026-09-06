package tz.ac.dit.safarismart.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegionRequest(
        @NotBlank @Size(max = 100) String name
) {
}
