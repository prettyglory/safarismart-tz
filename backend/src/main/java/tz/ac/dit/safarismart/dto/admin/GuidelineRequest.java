package tz.ac.dit.safarismart.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record GuidelineRequest(
        Long regionId,          // nullable
        Long destinationId,     // nullable -- both null = general guideline
        @NotBlank String category,
        @NotBlank String guidelineText
) {
}
