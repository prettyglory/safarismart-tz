package tz.ac.dit.safarismart.dto.itinerary;

import tz.ac.dit.safarismart.service.planning.model.ItemType;

import java.math.BigDecimal;

public record ItemResponse(
        ItemType type,
        Long refId,
        String name,
        BigDecimal costMin,
        BigDecimal costMax,
        String notes
) {
}
