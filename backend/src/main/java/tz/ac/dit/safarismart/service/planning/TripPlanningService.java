package tz.ac.dit.safarismart.service.planning;

import tz.ac.dit.safarismart.dto.planning.TripGenerateRequest;
import tz.ac.dit.safarismart.service.planning.model.TripPlanDraft;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TripPlanningService {

    private final RecommendationEngine recommendationEngine;
    private final BudgetOptimizer budgetOptimizer;

    public TripPlanningService(RecommendationEngine recommendationEngine, BudgetOptimizer budgetOptimizer) {
        this.recommendationEngine = recommendationEngine;
        this.budgetOptimizer = budgetOptimizer;
    }

    @Transactional(readOnly = true)
    public TripPlanDraft generateDraft(TripGenerateRequest request) {
        TripPlanDraft draft = recommendationEngine.buildDraft(request);
        budgetOptimizer.optimize(draft);
        return draft;
    }
}
