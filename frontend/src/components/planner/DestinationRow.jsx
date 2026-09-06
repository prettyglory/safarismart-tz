export default function DestinationRow({ index, value, destinations, onChange, onRemove, canRemove }) {
  function handleNameChange(e) {
    onChange(index, { ...value, name: e.target.value });
  }

  function handleDaysChange(e) {
    const raw = e.target.value;
    onChange(index, { ...value, days: raw === '' ? null : Number(raw) });
  }

  return (
    <div className="destination-row">
      <select value={value.name} onChange={handleNameChange} required>
        <option value="" disabled>Select a destination…</option>
        {destinations.map((d) => (
          <option key={d.id} value={d.name}>{d.name}</option>
        ))}
      </select>

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
