import { formatAmount, formatRange } from '../../utils/format';

export default function ItinerarySummary({ itinerary }) {
  const { totalDays, budget, estimatedCostMin, estimatedCostMax, overBudget, budgetNote, travelStyle, aiNarrated } = itinerary;

  return (
    <div className={`summary-banner ${overBudget ? 'summary-banner--warning' : 'summary-banner--ok'}`}>
      <div className="summary-banner__stats">
        <div>
          <span className="summary-banner__label">Trip length</span>
          <span className="summary-banner__value">{totalDays} day{totalDays > 1 ? 's' : ''}</span>
        </div>
        <div>
          <span className="summary-banner__label">Your budget</span>
          <span className="summary-banner__value">{formatAmount(budget)}</span>
        </div>
        <div>
          <span className="summary-banner__label">Estimated cost</span>
          <span className="summary-banner__value">{formatRange(estimatedCostMin, estimatedCostMax)}</span>
        </div>
        <div>
          <span className="summary-banner__label">Style</span>
          <span className="summary-banner__value">{travelStyle}</span>
        </div>
      </div>
      {budgetNote && <p className="summary-banner__note">{budgetNote}</p>}
      {aiNarrated === false && (
        <p className="summary-banner__ai-note">
          Note: this itinerary was generated using our standard planning format
          (the AI writer was unavailable when this was built).
        </p>
      )}
    </div>
  );
}
