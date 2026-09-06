package tz.ac.dit.safarismart.dto.itinerary;

import java.util.List;

public record DayResponse(
        int dayNumberInLeg,
        String narrative,
        List<ItemResponse> items
) {
}
