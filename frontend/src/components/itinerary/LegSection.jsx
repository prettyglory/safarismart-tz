import DayCard from './DayCard';

export default function LegSection({ leg }) {
  return (
    <section className="leg-section">
      <div className="leg-section__header">
        <span className="leg-section__badge">Stop {leg.sequenceOrder}</span>
        <h2>{leg.destinationName}</h2>
        <span className="leg-section__days">{leg.daysAllocated} day{leg.daysAllocated > 1 ? 's' : ''}</span>
      </div>
      <div className="leg-section__days-list">
        {leg.days.map((day) => (
          <DayCard key={day.dayNumberInLeg} day={day} />
        ))}
      </div>
    </section>
  );
}
