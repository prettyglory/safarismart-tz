import { useEffect, useRef, useState } from 'react';

export default function DestinationRow({ index, value, destinations, onChange, onRemove, canRemove }) {
  const [isOpen, setIsOpen] = useState(false);
  const rowRef = useRef(null);

  const filteredDestinations = destinations.filter((destination) => {
    const destinationName = destination.name || destination.destinationName || '';
    return destinationName.toLowerCase().includes(value.name.toLowerCase());
  });

  useEffect(() => {
    function closeDropdown(event) {
      if (!rowRef.current?.contains(event.target)) {
        setIsOpen(false);
      }
    }

    document.addEventListener('mousedown', closeDropdown);
    return () => document.removeEventListener('mousedown', closeDropdown);
  }, []);

  function handleNameChange(e) {
    onChange(index, { ...value, name: e.target.value });
    setIsOpen(true);
  }

  function selectDestination(name) {
    const selected = destinations.find((destination) =>
      (destination.name || destination.destinationName) === name
    );
    onChange(index, { ...value, name, days: value.days || selected?.recommendedDays || 2 });
    setIsOpen(false);
  }

  function handleDaysChange(e) {
    const raw = e.target.value;
    onChange(index, { ...value, days: raw === '' ? null : Number(raw) });
  }

  return (
    <div className="destination-row" ref={rowRef}>
      <div className="destination-row__select">
        <input
          type="text"
          value={value.name}
          onChange={handleNameChange}
          onFocus={() => setIsOpen(true)}
          placeholder="Select or type a destination"
          aria-label={`Destination ${index + 1}`}
          aria-expanded={isOpen}
          required
        />
        {isOpen && (
          <div className="destination-row__options" role="listbox">
            {filteredDestinations.length > 0 ? filteredDestinations.map((destination) => (
              <button
                type="button"
                key={destination.id}
                className="destination-row__option"
                onMouseDown={(event) => event.preventDefault()}
                onClick={() => selectDestination(destination.name || destination.destinationName)}
              >
                {destination.name || destination.destinationName}
              </button>
            )) : (
              <span className="destination-row__empty">No destinations found</span>
            )}
          </div>
        )}
      </div>

      <input
        type="number"
        min={1}
        max={30}
        placeholder="Days"
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
