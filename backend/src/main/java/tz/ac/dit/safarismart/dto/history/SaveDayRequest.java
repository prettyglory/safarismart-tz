package tz.ac.dit.safarismart.dto.history;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

import java.util.List;

public record SaveDayRequest(
        @Min(1) int dayNumberInLeg,
        String narrative,
        @NotNull List<@Valid SaveItemRequest> items
) {
}
