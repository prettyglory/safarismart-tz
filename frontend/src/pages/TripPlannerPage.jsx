import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllDestinations } from '../api/destinationApi';
import { generateTrip } from '../api/tripApi';
import DestinationRow from '../components/planner/DestinationRow';

const INTEREST_OPTIONS = [
  { value: 'wildlife', label: 'Wildlife' },
  { value: 'beaches', label: 'Beaches' },
  { value: 'mountains', label: 'Mountains' },
  { value: 'culture', label: 'Culture' },
  { value: 'cuisine', label: 'Cuisine' },
  { value: 'adventure', label: 'Adventure' },
];

const emptyDestination = () => ({ name: '', days: null });

export default function TripPlannerPage() {
  const navigate = useNavigate();

  const [destinations, setDestinations] = useState([]);
  const [loadingDestinations, setLoadingDestinations] = useState(true);
  const [loadError, setLoadError] = useState(null);

  const [form, setForm] = useState({
    destinations: [emptyDestination()],
    totalDays: '',
    budget: '',
    travelers: 1,
    interests: [],
    travelStyle: 'MODERATE',
    language: 'ENGLISH',
  });
  const [submitError, setSubmitError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    getAllDestinations()
      .then(setDestinations)
      .catch(() => setLoadError('Could not load destinations. Please try again shortly.'))
      .finally(() => setLoadingDestinations(false));
  }, []);

  function updateDestination(index, updated) {
    const next = [...form.destinations];
    next[index] = updated;
    setForm({ ...form, destinations: next });
  }

  function addDestination() {
    setForm({ ...form, destinations: [...form.destinations, emptyDestination()] });
  }

  function removeDestination(index) {
    setForm({ ...form, destinations: form.destinations.filter((_, i) => i !== index) });
  }

  function toggleInterest(value) {
    const has = form.interests.includes(value);
    setForm({
      ...form,
      interests: has ? form.interests.filter((i) => i !== value) : [...form.interests, value],
    });
  }

  function validate() {
    if (form.destinations.some((d) => !d.name)) {
      return 'Please select a destination for every row, or remove empty rows.';
    }
    const anyMissingDays = form.destinations.some((d) => !d.days);
    if (anyMissingDays && !form.totalDays) {
      return 'Enter a total number of days, or specify days for every destination.';
    }
    if (!form.budget || Number(form.budget) <= 0) {
      return 'Enter a budget greater than zero.';
    }
    if (!form.travelers || Number(form.travelers) < 1) {
      return 'At least one traveler is required.';
    }
    return null;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const validationError = validate();
    if (validationError) {
      setSubmitError(validationError);
      return;
    }

    setSubmitError(null);
    setSubmitting(true);

    const payload = {
      destinations: form.destinations.map((d) => ({ name: d.name, days: d.days || null })),
      totalDays: form.totalDays ? Number(form.totalDays) : null,
      budget: Number(form.budget),
      travelers: Number(form.travelers),
      interests: form.interests,
      travelStyle: form.travelStyle,
      language: form.language,
    };

    try {
      const itinerary = await generateTrip(payload);
      navigate('/itinerary', { state: { itinerary } });
    } catch (err) {
      setSubmitError(err.response?.data?.message || 'Could not generate your itinerary. Please try again.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="planner">
      <h1>Plan your trip</h1>
      <p className="planner__intro">
        Tell us where you're headed and what matters to you — we'll build a day-by-day plan
        from verified local data and fit it to your budget.
      </p>

      {loadingDestinations && <p>Loading destinations…</p>}
      {loadError && <p className="form-error">{loadError}</p>}

      {!loadingDestinations && !loadError && (
        <form className="planner-form" onSubmit={handleSubmit}>
          <fieldset>
            <legend>Where are you going?</legend>
            {form.destinations.map((d, i) => (
              <DestinationRow
                key={i}
                index={i}
                value={d}
                destinations={destinations}
                onChange={updateDestination}
                onRemove={removeDestination}
                canRemove={form.destinations.length > 1}
              />
            ))}
            <button type="button" className="btn btn--ghost btn--small" onClick={addDestination}>
              + Add another destination
            </button>
          </fieldset>

          <fieldset>
            <legend>Trip length & budget</legend>
            <label>
              Total days
              <input
                type="number"
                min={1}
                max={30}
                value={form.totalDays}
                onChange={(e) => setForm({ ...form, totalDays: e.target.value })}
                placeholder="e.g. 7"
              />
            </label>
            <label>
              Budget (TZS)
              <input
                type="number"
                min={1}
                value={form.budget}
                onChange={(e) => setForm({ ...form, budget: e.target.value })}
                placeholder="e.g. 1500000"
                required
              />
            </label>
            <label>
              Travelers
              <input
                type="number"
                min={1}
                value={form.travelers}
                onChange={(e) => setForm({ ...form, travelers: e.target.value })}
                required
              />
            </label>
          </fieldset>

          <fieldset>
            <legend>What are you interested in?</legend>
            <div className="checkbox-group">
              {INTEREST_OPTIONS.map((opt) => (
                <label key={opt.value} className="checkbox-group__item">
                  <input
                    type="checkbox"
                    checked={form.interests.includes(opt.value)}
                    onChange={() => toggleInterest(opt.value)}
                  />
                  {opt.label}
                </label>
              ))}
            </div>
          </fieldset>

          <fieldset>
            <legend>Style & language</legend>
            <label>
              Travel style
              <select
                value={form.travelStyle}
                onChange={(e) => setForm({ ...form, travelStyle: e.target.value })}
              >
                <option value="BUDGET">Budget</option>
                <option value="MODERATE">Moderate</option>
                <option value="LUXURY">Luxury</option>
              </select>
            </label>
            <label>
              Language
              <select
                value={form.language}
                onChange={(e) => setForm({ ...form, language: e.target.value })}
              >
                <option value="ENGLISH">English</option>
                <option value="SWAHILI">Swahili</option>
              </select>
            </label>
          </fieldset>

          {submitError && <p className="form-error">{submitError}</p>}

          <button type="submit" className="btn btn--primary btn--large" disabled={submitting}>
            {submitting ? 'Building your itinerary…' : 'Generate my itinerary'}
          </button>
        </form>
      )}
    </div>
  );
}
