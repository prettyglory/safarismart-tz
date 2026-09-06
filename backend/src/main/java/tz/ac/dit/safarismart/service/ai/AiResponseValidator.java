package tz.ac.dit.safarismart.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tz.ac.dit.safarismart.service.planning.model.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AiResponseValidator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parses the AI's raw JSON text and validates that every day's narrative
     * mentions every non-NOTE item name it was given. Returns a map of
     * "destinationName::dayNumberInLeg" -> narrative on success.
     * Throws IllegalArgumentException on any structural or content failure --
     * callers must fall back to the template narrative in that case.
     */
    public Map<String, String> validateAndExtract(String rawResponse, TripPlanDraft draft) {
        JsonNode root;
        try {
            String cleaned = stripCodeFences(rawResponse);
            root = objectMapper.readTree(cleaned);
        } catch (Exception ex) {
            throw new IllegalArgumentException("AI response was not valid JSON", ex);
        }

        JsonNode legsNode = root.path("legs");
        if (!legsNode.isArray()) {
            throw new IllegalArgumentException("AI response missing 'legs' array");
        }

        Map<String, String> narrativesByKey = new HashMap<>();
        for (JsonNode legNode : legsNode) {
            String destinationName = legNode.path("destinationName").asText(null);
            JsonNode daysNode = legNode.path("days");
            if (destinationName == null || !daysNode.isArray()) {
                throw new IllegalArgumentException("AI response leg missing destinationName or days");
            }
            for (JsonNode dayNode : daysNode) {
                int dayNum = dayNode.path("dayNumberInLeg").asInt(-1);
                String narrative = dayNode.path("narrative").asText(null);
                if (dayNum < 1 || narrative == null || narrative.isBlank()) {
                    throw new IllegalArgumentException("AI response day missing dayNumberInLeg or narrative");
                }
                narrativesByKey.put(destinationName + "::" + dayNum, narrative);
            }
        }

        // Cross-check: every provided item name must appear in its day's narrative.
        for (LegDraft leg : draft.getLegs()) {
            for (ItineraryDayDraft day : leg.getDays()) {
                String key = leg.getDestinationName() + "::" + day.getDayNumberInLeg();
                String narrative = narrativesByKey.get(key);
                if (narrative == null) {
                    throw new IllegalArgumentException("AI response missing narrative for " + key);
                }
                for (ItineraryItemDraft item : day.getItems()) {
                    if (item.getType() == ItemType.NOTE) continue;
                    if (!narrative.toLowerCase().contains(item.getName().toLowerCase())) {
                        throw new IllegalArgumentException(
                                "AI narrative for " + key + " does not mention required item: " + item.getName());
                    }
                }
            }
        }

        return narrativesByKey;
    }

    private String stripCodeFences(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```[a-zA-Z]*\\n", "");
            trimmed = trimmed.replaceFirst("```\\s*$", "");
        }
        return trimmed.trim();
    }
}
