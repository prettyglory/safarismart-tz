export default function DestinationRow({ index, value, destinations, onChange, onRemove, canRemove }) {
  const destinationListId = `destination-options-${index}`;

  function handleNameChange(e) {
    onChange(index, { ...value, name: e.target.value });
  }

  function handleDaysChange(e) {
    const raw = e.target.value;
    onChange(index, { ...value, days: raw === '' ? null : Number(raw) });
  }

  return (
    <div className="destination-row">
      <input
        type="text"
        list={destinationListId}
        value={value.name}
        onChange={handleNameChange}
        placeholder="Select or type a destination"
        aria-label={`Destination ${index + 1}`}
        required
      />
      <datalist id={destinationListId}>
        {destinations.map((d) => (
          <option key={d.id} value={d.name} />
        ))}
      </datalist>

      <input
        type="number"
        min={1}
        placeholder="Days (optional)"
        value={value.days ?? ''}
        onChange={handleDaysChange}
        className="destination-row__days"
      />

      {canRemove && (
        <button type="button" className="destination-row__remove" onClick={() => onRemove(index)} aria-label="Remove destination">
          ✕
        </button>
      )}
    </div>
  );
}
