package tz.ac.dit.safarismart.dto.admin;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AttractionRequest(
        @NotNull Long destinationId,
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 100) String category,
        String[] interestTags,
        String description,
        @NotNull @DecimalMin("0.0") BigDecimal entranceFeeMin,
        @NotNull @DecimalMin("0.0") BigDecimal entranceFeeMax,
        @NotNull @DecimalMin("0.1") BigDecimal avgDurationHours,
        boolean communityBased
) {
}
