package tz.ac.dit.safarismart.service.planning.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItineraryDayDraft {
    private int dayNumberInLeg;
    private List<ItineraryItemDraft> items = new ArrayList<>();
}
