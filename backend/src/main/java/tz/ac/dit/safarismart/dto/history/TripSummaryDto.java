package tz.ac.dit.safarismart.dto.history;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record TripSummaryDto(
        Long id,
        OffsetDateTime createdAt,
        int totalDays,
        BigDecimal budget,
        String travelStyle,
        List<String> destinationNames
) {
}
