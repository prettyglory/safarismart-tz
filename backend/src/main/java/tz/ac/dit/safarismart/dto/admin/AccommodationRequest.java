package tz.ac.dit.safarismart.dto.admin;

import tz.ac.dit.safarismart.entity.TravelStyle;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AccommodationRequest(
        @NotNull Long destinationId,
        @NotBlank @Size(max = 200) String name,
        @NotNull TravelStyle style,
        @NotNull @DecimalMin("0.0") BigDecimal priceMin,
        @NotNull @DecimalMin("0.0") BigDecimal priceMax,
        String description
) {
}
