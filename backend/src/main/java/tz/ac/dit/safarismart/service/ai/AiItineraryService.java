package tz.ac.dit.safarismart.service.ai;

import tz.ac.dit.safarismart.dto.itinerary.*;
import tz.ac.dit.safarismart.service.planning.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AiItineraryService {

    private static final Logger log = LoggerFactory.getLogger(AiItineraryService.class);

    private final AiClient aiClient;
    private final PromptContextBuilder promptContextBuilder;
    private final AiResponseValidator responseValidator;

    public AiItineraryService(AiClient aiClient,
                               PromptContextBuilder promptContextBuilder,
                               AiResponseValidator responseValidator) {
        this.aiClient = aiClient;
        this.promptContextBuilder = promptContextBuilder;
        this.responseValidator = responseValidator;
    }

    public ItineraryResponse narrate(TripPlanDraft draft) {
        Map<String, String> narrativesByKey;
        boolean aiSucceeded;

        try {
            String systemPrompt = promptContextBuilder.buildSystemPrompt();
            String userPrompt = promptContextBuilder.buildUserPrompt(draft);
            String rawResponse = aiClient.complete(systemPrompt, userPrompt);
            narrativesByKey = responseValidator.validateAndExtract(rawResponse, draft);
            aiSucceeded = true;
        } catch (Exception ex) {
            log.warn("AI narration failed, falling back to template narrative: {}", ex.getMessage());
            narrativesByKey = Map.of(); // template builder below handles the empty map
            aiSucceeded = false;
        }

        return buildResponse(draft, narrativesByKey, aiSucceeded);
    }

    private ItineraryResponse buildResponse(TripPlanDraft draft, Map<String, String> narrativesByKey, boolean aiSucceeded) {
        List<LegResponse> legResponses = draft.getLegs().stream().map(leg -> {
            List<DayResponse> dayResponses = leg.getDays().stream().map(day -> {
                String key = leg.getDestinationName() + "::" + day.getDayNumberInLeg();
                String narrative = narrativesByKey.getOrDefault(key, buildTemplateNarrative(leg, day));

                List<ItemResponse> itemResponses = day.getItems().stream()
                        .map(item -> new ItemResponse(item.getType(), item.getRefId(), item.getName(),
                                item.getCostMin(), item.getCostMax(), item.getNotes()))
                        .toList();

                return new DayResponse(day.getDayNumberInLeg(), narrative, itemResponses);
            }).toList();

            return new LegResponse(leg.getDestinationId(), leg.getDestinationName(), leg.getSequenceOrder(),
                    leg.getDaysAllocated(), leg.getLatitude(), leg.getLongitude(), dayResponses);
        }).toList();

        return new ItineraryResponse(
                draft.getTotalDays(), draft.getBudget(), draft.getTravelers(), draft.getInterests(),
                draft.getTotalCostMin(), draft.getTotalCostMax(),
                draft.isOverBudget(), draft.getBudgetNote(),
                draft.getTravelStyle(), draft.getLanguage(),
                legResponses, aiSucceeded
        );
    }

    // Deterministic, always-correct fallback: a plain sentence listing the
    // day's verified items. Used whenever the AI response fails validation,
    // so the tourist always gets a usable itinerary.
    private String buildTemplateNarrative(LegDraft leg, ItineraryDayDraft day) {
        StringBuilder sb = new StringBuilder("Day ").append(day.getDayNumberInLeg())
                .append(" in ").append(leg.getDestinationName()).append(": ");

        List<String> names = day.getItems().stream()
                .filter(i -> i.getType() != ItemType.NOTE)
                .map(ItineraryItemDraft::getName)
                .toList();

        sb.append(names.isEmpty() ? "free time to explore at your own pace." : String.join(", ", names) + ".");
        return sb.toString();
    }
}
