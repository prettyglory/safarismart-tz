package tz.ac.dit.safarismart.service.ai;

import tz.ac.dit.safarismart.service.planning.model.*;
import org.springframework.stereotype.Component;

@Component
public class PromptContextBuilder {

    private static final String SYSTEM_PROMPT = """
            You are a travel-itinerary writer for a Tanzania tourism planning system.
            You will be given a strict, closed list of verified items (attractions,
            accommodation, transport, restaurants) organized by destination and day.

            RULES YOU MUST FOLLOW EXACTLY:
            1. Only reference items that appear in the provided list. Never invent
               attractions, accommodation, prices, businesses, or locations.
            2. For each day, write one short, engaging narrative paragraph (2-4
               sentences) that mentions EVERY item name given for that day, exactly
               as written.
            3. Do not invent prices or numbers beyond what is given.
            4. Respond with ONLY valid JSON in this exact shape, no other text:
            {
              "legs": [
                {
                  "destinationName": "string",
                  "days": [
                    { "dayNumberInLeg": 1, "narrative": "string" }
                  ]
                }
              ]
            }
            """;

    public String buildSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    public String buildUserPrompt(TripPlanDraft draft) {
        StringBuilder sb = new StringBuilder();
        sb.append("Trip details: ").append(draft.getTotalDays()).append(" day(s), ")
                .append(draft.getTravelers()).append(" traveler(s), style=")
                .append(draft.getTravelStyle()).append(", interests=")
                .append(String.join(", ", draft.getInterests() == null ? java.util.List.of() : draft.getInterests()))
                .append(".\n\n");

        for (LegDraft leg : draft.getLegs()) {
            sb.append("Destination: ").append(leg.getDestinationName())
                    .append(" (day ").append(leg.getSequenceOrder() == 1 ? "1" : "leg start")
                    .append(", ").append(leg.getDaysAllocated()).append(" day(s))\n");

            for (ItineraryDayDraft day : leg.getDays()) {
                sb.append("  Day ").append(day.getDayNumberInLeg()).append(" items:\n");
                for (ItineraryItemDraft item : day.getItems()) {
                    if (item.getType() == ItemType.NOTE) continue; // notes are for the tourist, not narration input
                    sb.append("    - [").append(item.getType()).append("] ").append(item.getName()).append("\n");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
