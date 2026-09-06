package tz.ac.dit.safarismart.service.history;

import tz.ac.dit.safarismart.dto.history.*;
import tz.ac.dit.safarismart.dto.itinerary.*;
import tz.ac.dit.safarismart.entity.*;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.*;
import tz.ac.dit.safarismart.service.planning.model.ItemType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TripHistoryService {

    private final TripRepository tripRepository;
    private final AppUserRepository appUserRepository;
    private final DestinationRepository destinationRepository;
    private final AttractionRepository attractionRepository;
    private final AccommodationRepository accommodationRepository;
    private final TransportOptionRepository transportOptionRepository;
    private final InterDestinationRouteRepository routeRepository;

    public TripHistoryService(TripRepository tripRepository,
                               AppUserRepository appUserRepository,
                               DestinationRepository destinationRepository,
                               AttractionRepository attractionRepository,
                               AccommodationRepository accommodationRepository,
                               TransportOptionRepository transportOptionRepository,
                               InterDestinationRouteRepository routeRepository) {
        this.tripRepository = tripRepository;
        this.appUserRepository = appUserRepository;
        this.destinationRepository = destinationRepository;
        this.attractionRepository = attractionRepository;
        this.accommodationRepository = accommodationRepository;
        this.transportOptionRepository = transportOptionRepository;
        this.routeRepository = routeRepository;
    }

    public Long save(Long userId, SaveTripRequest request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: id=" + userId));

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setTotalDays(request.totalDays());
        trip.setBudget(request.budget());
        trip.setTravelers(request.travelers());
        trip.setInterests(request.interests() != null ? request.interests().toArray(new String[0]) : new String[0]);
        trip.setTravelStyle(request.travelStyle());
        trip.setLanguage(request.language() != null ? request.language() : AppLanguage.ENGLISH);

        for (SaveLegRequest legReq : request.legs()) {
            Destination destination = destinationRepository.findById(legReq.destinationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + legReq.destinationId()));

            TripDestination tripDestination = new TripDestination();
            tripDestination.setTrip(trip);
            tripDestination.setDestination(destination);
            tripDestination.setSequenceOrder(legReq.sequenceOrder());
            tripDestination.setDaysAllocated(legReq.daysAllocated());

            for (SaveDayRequest dayReq : legReq.days()) {
                ItineraryDay day = new ItineraryDay();
                day.setTripDestination(tripDestination);
                day.setDayNumber(dayReq.dayNumberInLeg());
                day.setNarrative(dayReq.narrative());

                for (SaveItemRequest itemReq : dayReq.items()) {
                    if (itemReq.type() == ItemType.NOTE) {
                        continue; // NOTE items are generation-time feedback, not persisted itinerary content
                    }
                    buildItineraryItem(day, itemReq).ifPresent(item -> day.getItems().add(item));
                }

                tripDestination.getItineraryDays().add(day);
            }

            trip.getTripDestinations().add(tripDestination);
        }

        Trip saved = tripRepository.save(trip);
        return saved.getId();
    }

    private Optional<ItineraryItem> buildItineraryItem(ItineraryDay day, SaveItemRequest req) {
        ItineraryItem item = new ItineraryItem();
        item.setItineraryDay(day);
        item.setEstimatedCostMin(req.costMin());
        item.setEstimatedCostMax(req.costMax());
        item.setNotes(req.notes());

        // If the referenced record was deleted or deactivated between generation
        // and save, skip it rather than failing the whole save -- the tourist's
        // other items are still worth keeping.
        switch (req.type()) {
            case ATTRACTION -> {
                var found = attractionRepository.findById(req.refId());
                if (found.isEmpty()) return Optional.empty();
                item.setAttraction(found.get());
            }
            case ACCOMMODATION -> {
                var found = accommodationRepository.findById(req.refId());
                if (found.isEmpty()) return Optional.empty();
                item.setAccommodation(found.get());
            }
            case TRANSPORT -> {
                var found = transportOptionRepository.findById(req.refId());
                if (found.isEmpty()) return Optional.empty();
                item.setTransport(found.get());
            }
            case ROUTE -> {
                var found = routeRepository.findById(req.refId());
                if (found.isEmpty()) return Optional.empty();
                item.setRoute(found.get());
            }
            default -> {
                return Optional.empty();
            }
        }
        return Optional.of(item);
    }

    @Transactional(readOnly = true)
    public List<TripSummaryDto> findAllForUser(Long userId) {
        return tripRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public TripDetailDto findOne(Long userId, Long tripId) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: id=" + tripId));
        return toDetail(trip);
    }

    public void delete(Long userId, Long tripId) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: id=" + tripId));
        tripRepository.delete(trip);
    }

    private TripSummaryDto toSummary(Trip trip) {
        List<String> destinationNames = trip.getTripDestinations().stream()
                .map(td -> td.getDestination().getName())
                .toList();
        return new TripSummaryDto(trip.getId(), trip.getCreatedAt(), trip.getTotalDays(),
                trip.getBudget(), trip.getTravelStyle().name(), destinationNames);
    }

    private TripDetailDto toDetail(Trip trip) {
        BigDecimal[] totals = { BigDecimal.ZERO, BigDecimal.ZERO };

        List<LegResponse> legs = trip.getTripDestinations().stream().map(td -> {
            List<DayResponse> days = td.getItineraryDays().stream().map(day -> {
                List<ItemResponse> items = day.getItems().stream().map(item -> {
                    totals[0] = totals[0].add(item.getEstimatedCostMin());
                    totals[1] = totals[1].add(item.getEstimatedCostMax());
                    return toItemResponse(item);
                }).toList();
                return new DayResponse(day.getDayNumber(), day.getNarrative(), items);
            }).toList();

            Destination destination = td.getDestination();
            return new LegResponse(destination.getId(), destination.getName(), td.getSequenceOrder(),
                    td.getDaysAllocated(), destination.getLatitude(), destination.getLongitude(), days);
        }).toList();

        ItineraryResponse itinerary = new ItineraryResponse(
                trip.getTotalDays(), trip.getBudget(), trip.getTravelers(),
                List.of(trip.getInterests()),
                totals[0], totals[1],
                totals[1].compareTo(trip.getBudget()) > 0, // overBudget, recomputed from current saved items
                null, // budgetNote -- not meaningful to recompute after the fact
                trip.getTravelStyle(), trip.getLanguage(),
                legs, true // aiNarrated -- not tracked historically; the narrative text itself is what matters now
        );

        return new TripDetailDto(trip.getId(), itinerary);
    }

    private ItemResponse toItemResponse(ItineraryItem item) {
        ItemType type;
        Long refId;
        String name;

        if (item.getAttraction() != null) {
            type = ItemType.ATTRACTION; refId = item.getAttraction().getId(); name = item.getAttraction().getName();
        } else if (item.getAccommodation() != null) {
            type = ItemType.ACCOMMODATION; refId = item.getAccommodation().getId(); name = item.getAccommodation().getName();
        } else if (item.getTransport() != null) {
            type = ItemType.TRANSPORT; refId = item.getTransport().getId(); name = item.getTransport().getType().name();
        } else if (item.getRoute() != null) {
            type = ItemType.ROUTE; refId = item.getRoute().getId();
            name = item.getRoute().getType() + ": " + item.getRoute().getFromDestination().getName()
                    + " -> " + item.getRoute().getToDestination().getName();
        } else {
            type = ItemType.NOTE; refId = null; name = "Notice"; // shouldn't occur, since NOTEs are never persisted
        }

        return new ItemResponse(type, refId, name, item.getEstimatedCostMin(), item.getEstimatedCostMax(), item.getNotes());
    }
}
