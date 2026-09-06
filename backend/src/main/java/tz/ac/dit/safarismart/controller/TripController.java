package tz.ac.dit.safarismart.controller;

import tz.ac.dit.safarismart.dto.history.SaveTripRequest;
import tz.ac.dit.safarismart.dto.history.TripDetailDto;
import tz.ac.dit.safarismart.dto.history.TripSummaryDto;
import tz.ac.dit.safarismart.dto.itinerary.ItineraryResponse;
import tz.ac.dit.safarismart.dto.planning.TripGenerateRequest;
import tz.ac.dit.safarismart.security.CustomUserDetails;
import tz.ac.dit.safarismart.service.ai.AiItineraryService;
import tz.ac.dit.safarismart.service.history.TripHistoryService;
import tz.ac.dit.safarismart.service.planning.TripPlanningService;
import tz.ac.dit.safarismart.service.planning.model.TripPlanDraft;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripPlanningService tripPlanningService;
    private final AiItineraryService aiItineraryService;
    private final TripHistoryService tripHistoryService;

    public TripController(TripPlanningService tripPlanningService,
                           AiItineraryService aiItineraryService,
                           TripHistoryService tripHistoryService) {
        this.tripPlanningService = tripPlanningService;
        this.aiItineraryService = aiItineraryService;
        this.tripHistoryService = tripHistoryService;
    }

    // Public per Phase 5's SecurityConfig -- guests can generate an itinerary
    // without an account. Saving it is what requires authentication.
    @PostMapping("/generate")
    public ItineraryResponse generate(@Valid @RequestBody TripGenerateRequest request) {
        TripPlanDraft draft = tripPlanningService.generateDraft(request);
        return aiItineraryService.narrate(draft);
    }

    // Authenticated (falls under SecurityConfig's anyRequest().authenticated() rule).
    @PostMapping
    public ResponseEntity<Map<String, Long>> save(@AuthenticationPrincipal CustomUserDetails principal,
                                                    @Valid @RequestBody SaveTripRequest request) {
        Long tripId = tripHistoryService.save(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", tripId));
    }

    @GetMapping
    public List<TripSummaryDto> listMine(@AuthenticationPrincipal CustomUserDetails principal) {
        return tripHistoryService.findAllForUser(principal.getId());
    }

    @GetMapping("/{id}")
    public TripDetailDto getOne(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
        return tripHistoryService.findOne(principal.getId(), id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
        tripHistoryService.delete(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
