package tz.ac.dit.safarismart.dto.history;

import tz.ac.dit.safarismart.service.planning.model.ItemType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaveItemRequest(
        @NotNull ItemType type,
        Long refId, // null only ever expected for NOTE, which is dropped before persistence anyway
        @NotNull BigDecimal costMin,
        @NotNull BigDecimal costMax,
        String notes
) {
}
