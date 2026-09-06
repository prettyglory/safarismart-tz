package tz.ac.dit.safarismart.dto.admin;

import tz.ac.dit.safarismart.entity.TransportType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record InterDestinationRouteRequest(
        @NotNull Long fromDestinationId,
        @NotNull Long toDestinationId,
        @NotNull TransportType type,
        @NotNull @DecimalMin("0.1") BigDecimal estimatedDurationHours,
        @NotNull @DecimalMin("0.0") BigDecimal priceMin,
        @NotNull @DecimalMin("0.0") BigDecimal priceMax
) {
}
