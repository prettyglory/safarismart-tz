package tz.ac.dit.safarismart.dto.planning;

import tz.ac.dit.safarismart.dto.DestinationSummaryDto;

import java.math.BigDecimal;
import java.util.List;

public record TripFeasibilityResponse(
        String status,
        BigDecimal budget,
        BigDecimal estimatedCostMin,
        BigDecimal estimatedCostMax,
        BigDecimal shortfall,
        String message,
        List<DestinationSummaryDto> alternativeDestinations
) {
}
