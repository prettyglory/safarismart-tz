package tz.ac.dit.safarismart.service.planning.model;

import tz.ac.dit.safarismart.entity.AppLanguage;
import tz.ac.dit.safarismart.entity.TravelStyle;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TripPlanDraft {
    private List<LegDraft> legs = new ArrayList<>();
    private int totalDays;
    private BigDecimal budget;
    private int travelers;
    private List<String> interests;
    private TravelStyle travelStyle;
    private AppLanguage language;

    private BigDecimal totalCostMin = BigDecimal.ZERO;
    private BigDecimal totalCostMax = BigDecimal.ZERO;
    private boolean overBudget = false;
    private String budgetNote;

    public void recalculateTotals() {
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.ZERO;
        for (LegDraft leg : legs) {
            for (ItineraryDayDraft day : leg.getDays()) {
                for (ItineraryItemDraft item : day.getItems()) {
                    min = min.add(item.getCostMin());
                    max = max.add(item.getCostMax());
                }
            }
        }
        this.totalCostMin = min;
        this.totalCostMax = max;
    }
}
