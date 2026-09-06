package tz.ac.dit.safarismart.service.planning.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LegDraft {
    private Long destinationId;
    private String destinationName;
    private int sequenceOrder;
    private int daysAllocated;
    private Double latitude;
    private Double longitude;
    private List<ItineraryDayDraft> days = new ArrayList<>();
}
