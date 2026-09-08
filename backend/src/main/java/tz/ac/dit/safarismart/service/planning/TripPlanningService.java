package tz.ac.dit.safarismart.service.planning;

import tz.ac.dit.safarismart.dto.DestinationSummaryDto;
import tz.ac.dit.safarismart.dto.planning.TripFeasibilityResponse;
import tz.ac.dit.safarismart.dto.planning.TripGenerateRequest;
import tz.ac.dit.safarismart.service.planning.model.ItineraryItemDraft;
import tz.ac.dit.safarismart.service.planning.model.ItemType;
import tz.ac.dit.safarismart.service.planning.model.TripPlanDraft;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TripPlanningService {

    private final RecommendationEngine recommendationEngine;
    private final BudgetOptimizer budgetOptimizer;

    public TripPlanningService(RecommendationEngine recommendationEngine, BudgetOptimizer budgetOptimizer) {
        this.recommendationEngine = recommendationEngine;
        this.budgetOptimizer = budgetOptimizer;
    }

    @Transactional(readOnly = true)
    public TripPlanDraft generateDraft(TripGenerateRequest request) {
        TripPlanDraft draft = recommendationEngine.buildDraft(request);
        budgetOptimizer.optimize(draft);
        return draft;
    }

    @Transactional(readOnly = true)
    public TripFeasibilityResponse assessFeasibility(TripGenerateRequest request) {
        if (request.startingDestinationId() == null) {
            throw new IllegalArgumentException("startingDestinationId is required for feasibility checks");
        }

        TripPlanDraft draft = recommendationEngine.buildDraft(request);
        BigDecimal estimatedCostMin = estimateCost(draft, request.travelers(), false);
        BigDecimal estimatedCostMax = estimateCost(draft, request.travelers(), true);

        if (request.budget() == null) {
            return new TripFeasibilityResponse(
                    "NO_BUDGET",
                    null,
                    estimatedCostMin,
                    estimatedCostMax,
                    null,
                    "No budget limit was provided.",
                    List.of()
            );
        }

        if (estimatedCostMax.compareTo(request.budget()) <= 0) {
            return response("AFFORDABLE", request, estimatedCostMin, estimatedCostMax, null,
                    "This itinerary fits within your stated budget.", List.of());
        }

        if (estimatedCostMin.compareTo(request.budget()) <= 0) {
            return response("TIGHT_BUDGET", request, estimatedCostMin, estimatedCostMax, null,
                    "This itinerary may fit your budget, but final costs could exceed it.", List.of());
        }

        BigDecimal shortfall = estimatedCostMin.subtract(request.budget());
        List<DestinationSummaryDto> alternatives = recommendationEngine
                .findCheaperAlternatives(request, estimatedCostMin).stream()
                .map(destination -> new DestinationSummaryDto(
                        destination.getId(),
                        destination.getName(),
                        destination.getRegion().getName(),
                        destination.getDescription(),
                        destination.getLatitude(),
                        destination.getLongitude()
                ))
                .toList();

        return response("OVER_BUDGET", request, estimatedCostMin, estimatedCostMax, shortfall,
                "This itinerary exceeds your budget by approximately " + shortfall + ".", alternatives);
    }

    private TripFeasibilityResponse response(String status,
                                             TripGenerateRequest request,
                                             BigDecimal estimatedCostMin,
                                             BigDecimal estimatedCostMax,
                                             BigDecimal shortfall,
                                             String message,
                                             List<DestinationSummaryDto> alternatives) {
        return new TripFeasibilityResponse(status, request.budget(), estimatedCostMin, estimatedCostMax,
                shortfall, message, alternatives);
    }

    private BigDecimal estimateCost(TripPlanDraft draft, int travelers, boolean maximum) {
        BigDecimal total = BigDecimal.ZERO;
        for (var leg : draft.getLegs()) {
            for (var day : leg.getDays()) {
                for (ItineraryItemDraft item : day.getItems()) {
                    BigDecimal itemCost = maximum ? item.getCostMax() : item.getCostMin();
                    if (item.getType() == ItemType.ATTRACTION) {
                        itemCost = itemCost.multiply(BigDecimal.valueOf(travelers));
                    }
                    total = total.add(itemCost);
                }
            }
        }
        return total;
    }
}
