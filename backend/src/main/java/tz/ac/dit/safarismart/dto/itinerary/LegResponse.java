package tz.ac.dit.safarismart.dto.itinerary;

import java.util.List;

public record LegResponse(
        Long destinationId,
        String destinationName,
        int sequenceOrder,
        int daysAllocated,
        Double latitude,
        Double longitude,
        List<DayResponse> days
) {
}
