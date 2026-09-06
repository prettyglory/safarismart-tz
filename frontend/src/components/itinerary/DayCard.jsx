import ItemRow from './ItemRow';

export default function DayCard({ day }) {
  return (
    <div className="day-card">
      <h4 className="day-card__title">Day {day.dayNumberInLeg}</h4>
      <p className="day-card__narrative">{day.narrative}</p>
      <div className="day-card__items">
        {day.items.map((item, i) => (
          <ItemRow key={i} item={item} />
        ))}
      </div>
    </div>
  );
}
