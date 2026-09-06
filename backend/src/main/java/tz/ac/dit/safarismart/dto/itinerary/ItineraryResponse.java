package tz.ac.dit.safarismart.dto.itinerary;

import tz.ac.dit.safarismart.entity.AppLanguage;
import tz.ac.dit.safarismart.entity.TravelStyle;

import java.math.BigDecimal;
import java.util.List;

public record ItineraryResponse(
        int totalDays,
        BigDecimal budget,
        int travelers,
        List<String> interests,
        BigDecimal estimatedCostMin,
        BigDecimal estimatedCostMax,
        boolean overBudget,
        String budgetNote,
        TravelStyle travelStyle,
        AppLanguage language,
        List<LegResponse> legs,
        boolean aiNarrated
) {
}
