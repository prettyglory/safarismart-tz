package tz.ac.dit.safarismart.dto.history;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

import java.util.List;

public record SaveLegRequest(
        @NotNull Long destinationId,
        @Min(1) int sequenceOrder,
        @Min(1) int daysAllocated,
        @NotNull List<@Valid SaveDayRequest> days
) {
}
