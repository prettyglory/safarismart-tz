package tz.ac.dit.safarismart.service.planning;

import tz.ac.dit.safarismart.dto.planning.DestinationInput;
import tz.ac.dit.safarismart.dto.planning.TripGenerateRequest;
import tz.ac.dit.safarismart.entity.*;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.*;
import tz.ac.dit.safarismart.service.planning.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class RecommendationEngine {

    // Assumed active hours available per day for sightseeing (leaves room
    // for meals, rest, and local transport -- a simplifying MVP assumption).
    private static final BigDecimal ACTIVE_HOURS_PER_DAY = BigDecimal.valueOf(8);
    private static final int MAX_ATTRACTIONS_PER_DAY = 3;

    private final DestinationRepository destinationRepository;
    private final AttractionRepository attractionRepository;
    private final AccommodationRepository accommodationRepository;
    private final TransportOptionRepository transportOptionRepository;
    private final RestaurantRepository restaurantRepository;
    private final InterDestinationRouteRepository routeRepository;

    public RecommendationEngine(DestinationRepository destinationRepository,
                                 AttractionRepository attractionRepository,
                                 AccommodationRepository accommodationRepository,
                                 TransportOptionRepository transportOptionRepository,
                                 RestaurantRepository restaurantRepository,
                                 InterDestinationRouteRepository routeRepository) {
        this.destinationRepository = destinationRepository;
        this.attractionRepository = attractionRepository;
        this.accommodationRepository = accommodationRepository;
        this.transportOptionRepository = transportOptionRepository;
        this.restaurantRepository = restaurantRepository;
        this.routeRepository = routeRepository;
    }

    public TripPlanDraft buildDraft(TripGenerateRequest request) {
        List<Destination> destinations = resolveDestinations(request.destinations());
        int[] daysPerLeg = allocateDays(request, destinations.size());

        TripPlanDraft draft = new TripPlanDraft();
        draft.setBudget(request.budget());
        draft.setTravelers(request.travelers());
        draft.setInterests(request.interests() != null ? request.interests() : List.of());
        draft.setTravelStyle(request.travelStyle());
        draft.setLanguage(request.language() != null ? request.language() : AppLanguage.ENGLISH);
        draft.setTotalDays(Arrays.stream(daysPerLeg).sum());

        Destination previous = null;
        for (int i = 0; i < destinations.size(); i++) {
            Destination destination = destinations.get(i);
            LegDraft leg = buildLeg(destination, i + 1, daysPerLeg[i], draft.getInterests(), draft.getTravelStyle());

            if (previous != null) {
                attachInterDestinationTransfer(leg, previous, destination);
            }

            draft.getLegs().add(leg);
            previous = destination;
        }

        draft.recalculateTotals();
        return draft;
    }

    private List<Destination> resolveDestinations(List<DestinationInput> inputs) {
        List<Destination> resolved = new ArrayList<>();
        for (DestinationInput input : inputs) {
            Destination destination = destinationRepository.findByNameIgnoreCase(input.name())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination not found: " + input.name()));
            resolved.add(destination);
        }
        return resolved;
    }

    private int[] allocateDays(TripGenerateRequest request, int legCount) {
        int[] days = new int[legCount];
        int specifiedSum = 0;
        int unspecifiedCount = 0;

        for (int i = 0; i < legCount; i++) {
            Integer d = request.destinations().get(i).days();
            if (d != null) {
                days[i] = d;
                specifiedSum += d;
            } else {
                unspecifiedCount++;
            }
        }

        if (unspecifiedCount == 0) {
            return days; // every leg specified its own days
        }

        if (request.totalDays() == null) {
            throw new IllegalArgumentException(
                    "totalDays is required when not every destination specifies its own day count");
        }

        int remaining = request.totalDays() - specifiedSum;
        if (remaining < unspecifiedCount) {
            throw new IllegalArgumentException(
                    "totalDays is too small to allocate at least 1 day to each remaining destination");
        }

        int base = remaining / unspecifiedCount;
        int extra = remaining % unspecifiedCount;
        int assignedExtra = 0;

        for (int i = 0; i < legCount; i++) {
            if (request.destinations().get(i).days() == null) {
                days[i] = base + (assignedExtra < extra ? 1 : 0);
                assignedExtra++;
            }
        }

        return days;
    }

    private LegDraft buildLeg(Destination destination, int sequenceOrder, int daysAllocated,
                               List<String> interests, TravelStyle style) {
        LegDraft leg = new LegDraft();
        leg.setDestinationId(destination.getId());
        leg.setDestinationName(destination.getName());
        leg.setSequenceOrder(sequenceOrder);
        leg.setDaysAllocated(daysAllocated);
        leg.setLatitude(destination.getLatitude());
        leg.setLongitude(destination.getLongitude());

        List<Attraction> rankedAttractions = rankAttractions(destination.getId(), interests);
        List<Restaurant> restaurants = pickRestaurants(destination.getId(), style);

        int restaurantIndex = 0;

        for (int dayNum = 1; dayNum <= daysAllocated; dayNum++) {
            ItineraryDayDraft day = new ItineraryDayDraft();
            day.setDayNumberInLeg(dayNum);

            // Day 1 carries the leg's accommodation and local transport (one-time per leg).
            if (dayNum == 1) {
                day.getItems().add(buildAccommodationItem(destination.getId(), style, daysAllocated));
                buildTransportItem(destination.getId()).ifPresent(day.getItems()::add);
            }

            // Pack attractions into this day up to the active-hours budget.
            packAttractionsForDay(day, rankedAttractions);

            if (!restaurants.isEmpty()) {
                Restaurant r = restaurants.get(restaurantIndex % restaurants.size());
                day.getItems().add(buildRestaurantItem(r));
                restaurantIndex++;
            } else {
                day.getItems().add(ItineraryItemDraft.note(
                        "No verified restaurant data available for " + destination.getName() + " yet."));
            }

            leg.getDays().add(day);
        }

        return leg;
    }

    // Removes packed attractions from the shared ranked list as they're used across days,
    // so later days in the same leg don't repeat an attraction already scheduled.
    private void packAttractionsForDay(ItineraryDayDraft day, List<Attraction> remainingRanked) {
        BigDecimal hoursUsed = BigDecimal.ZERO;
        int count = 0;
        Iterator<Attraction> it = remainingRanked.iterator();
        while (it.hasNext() && count < MAX_ATTRACTIONS_PER_DAY) {
            Attraction a = it.next();
            BigDecimal projected = hoursUsed.add(a.getAvgDurationHours());
            if (projected.compareTo(ACTIVE_HOURS_PER_DAY) > 0 && count > 0) {
                continue; // doesn't fit today, but keep it in the list for a later day
            }
            day.getItems().add(new ItineraryItemDraft(
                    ItemType.ATTRACTION, a.getId(), a.getName(),
                    a.getEntranceFeeMin(), a.getEntranceFeeMax(),
                    a.getCategory() + (a.isCommunityBased() ? " (community-based)" : ""),
                    computeAttractionPriority(a)
            ));
            hoursUsed = projected;
            count++;
            it.remove();
        }
    }

    private List<Attraction> rankAttractions(Long destinationId, List<String> interests) {
        List<Attraction> candidates;
        if (interests != null && !interests.isEmpty()) {
            candidates = attractionRepository.findMatchingByDestinationAndInterests(
                    destinationId, interests.toArray(new String[0]));
            if (candidates.isEmpty()) {
                // No verified attraction matches the requested interests --
                // fall back to all active attractions rather than inventing one.
                candidates = attractionRepository.findByDestinationIdAndActiveTrue(destinationId);
            }
        } else {
            candidates = attractionRepository.findByDestinationIdAndActiveTrue(destinationId);
        }

        candidates.sort(Comparator
                .comparingInt((Attraction a) -> -tagOverlapCount(a, interests))
                .thenComparing(Attraction::getEntranceFeeMin));

        return new ArrayList<>(candidates); // mutable copy -- items get removed as they're scheduled
    }

    private int tagOverlapCount(Attraction attraction, List<String> interests) {
        if (interests == null || interests.isEmpty() || attraction.getInterestTags() == null) return 0;
        Set<String> tagSet = new HashSet<>(Arrays.asList(attraction.getInterestTags()));
        int count = 0;
        for (String interest : interests) {
            if (tagSet.contains(interest)) count++;
        }
        return count;
    }

    private int computeAttractionPriority(Attraction a) {
        // Higher score = more likely to be kept by the budget optimizer.
        return a.isCommunityBased() ? 2 : 1;
    }

    private ItineraryItemDraft buildAccommodationItem(Long destinationId, TravelStyle style, int nights) {
        List<Accommodation> options = accommodationRepository.findByDestinationIdAndStyleAndActiveTrue(destinationId, style);
        if (options.isEmpty()) {
            return ItineraryItemDraft.note(
                    "No verified " + style + " accommodation found for this destination -- please arrange lodging separately.");
        }
        Accommodation cheapest = options.stream()
                .min(Comparator.comparing(Accommodation::getPriceMin))
                .orElseThrow();
        return new ItineraryItemDraft(
                ItemType.ACCOMMODATION, cheapest.getId(), cheapest.getName(),
                cheapest.getPriceMin().multiply(BigDecimal.valueOf(nights)),
                cheapest.getPriceMax().multiply(BigDecimal.valueOf(nights)),
                nights + " night(s)", 3 // accommodation is high priority -- optimizer downgrades before removing
        );
    }

    private Optional<ItineraryItemDraft> buildTransportItem(Long destinationId) {
        List<TransportOption> options = transportOptionRepository.findByDestinationIdAndActiveTrue(destinationId);
        if (options.isEmpty()) return Optional.empty();

        TransportOption cheapest = options.stream()
                .min(Comparator.comparing(TransportOption::getPriceMin))
                .orElseThrow();
        return Optional.of(new ItineraryItemDraft(
                ItemType.TRANSPORT, cheapest.getId(), cheapest.getType() + " (local transport)",
                cheapest.getPriceMin(), cheapest.getPriceMax(),
                "For getting around during your stay", 2
        ));
    }

    private List<Restaurant> pickRestaurants(Long destinationId, TravelStyle style) {
        List<Restaurant> all = restaurantRepository.findByDestinationIdAndActiveTrue(destinationId);
        List<Restaurant> matchingStyle = all.stream().filter(r -> r.getPriceRange() == style).toList();
        return matchingStyle.isEmpty() ? all : matchingStyle;
    }

    private ItineraryItemDraft buildRestaurantItem(Restaurant r) {
        // Restaurants don't carry a fixed price in the schema (price_range only) --
        // cost is left at zero here and folded into the general "food" budget line
        // the budget estimator communicates separately, rather than inventing a figure.
        return new ItineraryItemDraft(
                ItemType.RESTAURANT, r.getId(), r.getName(),
                BigDecimal.ZERO, BigDecimal.ZERO,
                r.getCuisineType() != null ? r.getCuisineType() : "Local cuisine", 1
        );
    }

    private void attachInterDestinationTransfer(LegDraft leg, Destination from, Destination to) {
        List<InterDestinationRoute> routes =
                routeRepository.findByFromDestinationIdAndToDestinationIdAndActiveTrue(from.getId(), to.getId());

        ItineraryItemDraft transferItem;
        if (routes.isEmpty()) {
            transferItem = ItineraryItemDraft.note(
                    "No verified transport route found from " + from.getName() + " to " + to.getName()
                            + " -- please arrange this transfer separately.");
        } else {
            InterDestinationRoute cheapest = routes.stream()
                    .min(Comparator.comparing(InterDestinationRoute::getPriceMin))
                    .orElseThrow();
            transferItem = new ItineraryItemDraft(
                    ItemType.ROUTE, cheapest.getId(),
                    cheapest.getType() + ": " + from.getName() + " -> " + to.getName(),
                    cheapest.getPriceMin(), cheapest.getPriceMax(),
                    "Approx. " + cheapest.getEstimatedDurationHours() + " hours", 3
            );
        }

        if (!leg.getDays().isEmpty()) {
            leg.getDays().get(0).getItems().add(0, transferItem);
        }
    }
}
