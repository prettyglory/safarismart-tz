package tz.ac.dit.safarismart.dto.history;

import tz.ac.dit.safarismart.entity.AppLanguage;
import tz.ac.dit.safarismart.entity.TravelStyle;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

public record SaveTripRequest(
        @Min(1) @Max(30) int totalDays,
        @DecimalMin(value = "0.01", message = "budget must be at least 0.01 when provided") BigDecimal budget,
        @Min(1) int travelers,
        List<String> interests,
        @NotNull TravelStyle travelStyle,
        AppLanguage language,
        @NotEmpty List<@Valid SaveLegRequest> legs
) {
}
