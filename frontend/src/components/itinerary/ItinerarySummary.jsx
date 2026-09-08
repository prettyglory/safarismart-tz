import { formatAmount, formatRange } from '../../utils/format';

export function FeasibilitySummary({ feasibility, onContinue, onSelectAlternative }) {
  const isOverBudget = feasibility.status === 'OVER_BUDGET';
  const statusClass = isOverBudget || feasibility.status === 'TIGHT_BUDGET'
    ? 'summary-banner--warning'
    : 'summary-banner--ok';
  const alternatives = feasibility.alternativeDestinations || [];

  return (
    <div className={`summary-banner feasibility-summary ${statusClass}`}>
      <div className="summary-banner__stats">
        <div>
          <span className="summary-banner__label">Status</span>
          <span className="summary-banner__value">{feasibility.status}</span>
        </div>
        <div>
          <span className="summary-banner__label">Your budget</span>
          <span className="summary-banner__value">{formatAmount(feasibility.budget)}</span>
        </div>
        <div>
          <span className="summary-banner__label">Estimated minimum</span>
          <span className="summary-banner__value">{formatAmount(feasibility.estimatedCostMin)}</span>
        </div>
        <div>
          <span className="summary-banner__label">Estimated maximum</span>
          <span className="summary-banner__value">{formatAmount(feasibility.estimatedCostMax)}</span>
        </div>
      </div>

      {feasibility.shortfall !== null && feasibility.shortfall !== undefined && (
        <p className="summary-banner__note">
          <strong>Shortfall:</strong> {formatAmount(feasibility.shortfall)}
        </p>
      )}
      <p className="summary-banner__note">{feasibility.message}</p>

      {isOverBudget && alternatives.length > 0 && (
        <div className="feasibility-alternatives">
          <h3>Alternative destinations</h3>
          <div className="feasibility-alternatives__list">
            {alternatives.map((alternative) => (
              <div className="feasibility-alternative" key={alternative.id}>
                <div>
                  <strong>{alternative.name}</strong>
                  <span>{alternative.regionName || alternative.region || 'Region unavailable'}</span>
                </div>
                <div className="feasibility-alternative__details">
                  <span><strong>Estimated cost:</strong> {alternative.estimatedCostMin !== undefined
                    ? formatRange(alternative.estimatedCostMin, alternative.estimatedCostMax ?? alternative.estimatedCostMin)
                    : 'Not provided'}</span>
                  <span><strong>Status:</strong> {alternative.status || 'AFFORDABLE ALTERNATIVE'}</span>
                  <span><strong>Reason:</strong> {alternative.reason || 'Lower estimated cost with matching interests.'}</span>
                </div>
                {(alternative.status === 'AFFORDABLE' || alternative.status === undefined) && (
                  <button
                    type="button"
                    className="btn btn--ghost btn--small"
                    onClick={() => onSelectAlternative(alternative)}
                  >
                    Use this destination
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      <button type="button" className="btn btn--primary" onClick={onContinue}>
        {isOverBudget ? 'Continue anyway' : feasibility.status === 'TIGHT_BUDGET' ? 'Confirm tight budget' : 'Continue to itinerary'}
      </button>
    </div>
  );
}

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
