package tz.ac.dit.safarismart.dto.history;

import tz.ac.dit.safarismart.dto.itinerary.ItineraryResponse;

// Reuses the exact shape the frontend already knows how to render
// (ItinerarySummary / LegSection / DayCard / ItemRow all consume this).
public record TripDetailDto(
        Long tripId,
        ItineraryResponse itinerary
) {
}
