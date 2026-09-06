package tz.ac.dit.safarismart.service.planning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ItineraryItemDraft {
    private ItemType type;
    private Long refId;          // id of the source Attraction/Accommodation/TransportOption/Route, or null for NOTE
    private String name;
    private BigDecimal costMin;
    private BigDecimal costMax;
    private String notes;
    private int priorityScore;   // higher = keep; used by the optimizer when trimming

    public static ItineraryItemDraft note(String message) {
        return new ItineraryItemDraft(ItemType.NOTE, null, "Notice", BigDecimal.ZERO, BigDecimal.ZERO, message, Integer.MAX_VALUE);
    }
}
