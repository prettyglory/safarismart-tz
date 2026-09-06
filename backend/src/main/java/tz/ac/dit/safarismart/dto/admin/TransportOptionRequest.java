package tz.ac.dit.safarismart.dto.admin;

import tz.ac.dit.safarismart.entity.TransportType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransportOptionRequest(
        @NotNull Long destinationId,
        @NotNull TransportType type,
        String description,
        @NotNull @DecimalMin("0.0") BigDecimal priceMin,
        @NotNull @DecimalMin("0.0") BigDecimal priceMax
) {
}
