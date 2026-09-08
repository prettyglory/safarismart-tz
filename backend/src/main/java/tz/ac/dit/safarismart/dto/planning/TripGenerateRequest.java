package tz.ac.dit.safarismart.dto.planning;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import tz.ac.dit.safarismart.entity.AppLanguage;
import tz.ac.dit.safarismart.entity.TravelStyle;

import java.math.BigDecimal;
import java.util.List;

public record TripGenerateRequest(
        @NotEmpty List<DestinationInput> destinations,

        // Optional if every destination already specifies its own day count.
        @Min(1) @Max(30) Integer totalDays,

        Long startingDestinationId,

        @DecimalMin(value = "1.0", message = "budget must be at least 1 when provided") BigDecimal budget,

        @NotNull @Min(1) Integer travelers,

        List<String> interests,

        @NotNull TravelStyle travelStyle,

        AppLanguage language
) {
}