package tz.ac.dit.safarismart.service.planning;

import tz.ac.dit.safarismart.entity.Accommodation;
import tz.ac.dit.safarismart.entity.Restaurant;
import tz.ac.dit.safarismart.entity.TravelStyle;
import tz.ac.dit.safarismart.repository.AccommodationRepository;
import tz.ac.dit.safarismart.repository.RestaurantRepository;
import tz.ac.dit.safarismart.service.planning.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetOptimizer {

    private final AccommodationRepository accommodationRepository;
    private final RestaurantRepository restaurantRepository;

    public BudgetOptimizer(AccommodationRepository accommodationRepository,
                            RestaurantRepository restaurantRepository) {
        this.accommodationRepository = accommodationRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void optimize(TripPlanDraft draft) {
        draft.recalculateTotals();

        if (draft.getBudget() == null) {
            draft.setOverBudget(false);
            draft.setBudgetNote("No budget limit was provided.");
            return;
        }

        if (draft.getTotalCostMax().compareTo(draft.getBudget()) <= 0) {
            draft.setOverBudget(false);
            draft.setBudgetNote("This itinerary fits within your stated budget.");
            return;
        }

        downgradeAccommodations(draft);
        draft.recalculateTotals();
        if (isNowWithinBudget(draft)) return;

        downgradeRestaurants(draft);
        draft.recalculateTotals();
        if (isNowWithinBudget(draft)) return;

        trimLowestPriorityAttractions(draft);
        draft.recalculateTotals();

        if (isNowWithinBudget(draft)) {
            draft.setOverBudget(false);
            draft.setBudgetNote("Some lower-priority activities or upgrades were adjusted to fit your budget.");
        } else {
            BigDecimal shortfall = draft.getTotalCostMin().subtract(draft.getBudget());
            draft.setOverBudget(true);
            draft.setBudgetNote(
                    "This itinerary still exceeds your budget by approximately " + shortfall
                            + ". Consider increasing your budget, reducing the number of days or travelers, "
                            + "or choosing fewer/different destinations.");
        }
    }

    private boolean isNowWithinBudget(TripPlanDraft draft) {
        return draft.getTotalCostMax().compareTo(draft.getBudget()) <= 0;
    }

    private void downgradeAccommodations(TripPlanDraft draft) {
        if (draft.getTravelStyle() == TravelStyle.BUDGET) return; // already cheapest tier

        for (LegDraft leg : draft.getLegs()) {
            for (ItineraryDayDraft day : leg.getDays()) {
                for (ItineraryItemDraft item : day.getItems()) {
                    if (item.getType() != ItemType.ACCOMMODATION) continue;

                    List<Accommodation> budgetOptions = accommodationRepository
                            .findByDestinationIdAndStyleAndActiveTrue(leg.getDestinationId(), TravelStyle.BUDGET);
                    if (budgetOptions.isEmpty()) continue;

                    Accommodation cheapest = budgetOptions.stream()
                            .min(Comparator.comparing(Accommodation::getPriceMin))
                            .orElseThrow();

                    int nights = leg.getDaysAllocated();
                    item.setRefId(cheapest.getId());
                    item.setName(cheapest.getName());
                    item.setCostMin(cheapest.getPriceMin().multiply(BigDecimal.valueOf(nights)));
                    item.setCostMax(cheapest.getPriceMax().multiply(BigDecimal.valueOf(nights)));
                    item.setNotes(nights + " night(s) -- switched to a budget option to help fit your budget");
                }
            }
        }
    }

    private void downgradeRestaurants(TripPlanDraft draft) {
        if (draft.getTravelStyle() == TravelStyle.BUDGET) return;

        for (LegDraft leg : draft.getLegs()) {
            List<Restaurant> budgetOptions = restaurantRepository
                    .findByDestinationIdAndActiveTrue(leg.getDestinationId()).stream()
                    .filter(r -> r.getPriceRange() == TravelStyle.BUDGET)
                    .toList();
            if (budgetOptions.isEmpty()) continue;

            int index = 0;
            for (ItineraryDayDraft day : leg.getDays()) {
                for (ItineraryItemDraft item : day.getItems()) {
                    if (item.getType() != ItemType.RESTAURANT) continue;
                    Restaurant r = budgetOptions.get(index % budgetOptions.size());
                    item.setRefId(r.getId());
                    item.setName(r.getName());
                    item.setNotes((r.getCuisineType() != null ? r.getCuisineType() : "Local cuisine")
                            + " -- switched to a more affordable option");
                    index++;
                }
            }
        }
    }

    private void trimLowestPriorityAttractions(TripPlanDraft draft) {
        // Repeatedly remove the single lowest-priority, highest-cost attraction
        // across the whole trip until the budget is met or none remain.
        while (draft.getTotalCostMax().compareTo(draft.getBudget()) > 0) {
            Optional<ItineraryItemDraft> victim = findRemovableAttraction(draft);
            if (victim.isEmpty()) break;

            removeItem(draft, victim.get());
            draft.recalculateTotals();
        }
    }

    private Optional<ItineraryItemDraft> findRemovableAttraction(TripPlanDraft draft) {
        return draft.getLegs().stream()
                .flatMap(leg -> leg.getDays().stream())
                .flatMap(day -> day.getItems().stream())
                .filter(item -> item.getType() == ItemType.ATTRACTION)
                .min(Comparator.comparingInt(ItineraryItemDraft::getPriorityScore)
                        .thenComparing(Comparator.comparing(ItineraryItemDraft::getCostMax).reversed()));
    }

    private void removeItem(TripPlanDraft draft, ItineraryItemDraft target) {
        for (LegDraft leg : draft.getLegs()) {
            for (ItineraryDayDraft day : leg.getDays()) {
                day.getItems().remove(target);
            }
        }
    }
}
