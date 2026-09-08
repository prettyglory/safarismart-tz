import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllDestinations } from '../api/destinationApi';
import { checkTripFeasibility, generateTrip } from '../api/tripApi';
import DestinationRow from '../components/planner/DestinationRow';
import { FeasibilitySummary } from '../components/itinerary/ItinerarySummary';

const CURRENCY_OPTIONS = [
  { code: 'TZS', label: 'TZS (Tanzanian shilling)', toTzs: 1 },
  { code: 'USD', label: 'USD (US dollar)', toTzs: 2500 },
  { code: 'EUR', label: 'EUR (euro)', toTzs: 2700 },
  { code: 'GBP', label: 'GBP (pound sterling)', toTzs: 3150 },
  { code: 'KES', label: 'KES (Kenyan shilling)', toTzs: 19 },
];

const INTEREST_RULES = [
  { value: 'game_drives', label: 'Game drives', terms: ['serengeti', 'ngorongoro', 'tarangire', 'manyara', 'mikumi', 'ruaha', 'nyerere', 'katavi', 'mkomazi', 'arusha', 'rubondo', 'saanane'] },
  { value: 'big_five', label: 'Big Five viewing', terms: ['serengeti', 'ngorongoro', 'tarangire', 'manyara', 'ruaha', 'nyerere'] },
  { value: 'migration', label: 'Wildebeest migration', terms: ['serengeti'] },
  { value: 'predator_watching', label: 'Predator watching', terms: ['serengeti', 'ngorongoro', 'ruaha', 'nyerere', 'katavi'] },
  { value: 'birdwatching', label: 'Bird watching', terms: ['serengeti', 'ngorongoro', 'kilimanjaro', 'tarangire', 'manyara', 'mikumi', 'ruaha', 'nyerere', 'katavi', 'saadani', 'rubondo', 'saanane', 'mkomazi', 'gombe', 'mahale'] },
  { value: 'wildlife_photography', label: 'Photography', terms: ['serengeti', 'ngorongoro', 'kilimanjaro', 'tarangire', 'manyara', 'mikumi', 'ruaha', 'nyerere', 'katavi', 'saadani', 'rubondo', 'saanane', 'mkomazi', 'gombe', 'mahale'] },
  { value: 'sunrise_sunset_safaris', label: 'Sunrise and sunset safaris', terms: ['serengeti', 'ngorongoro', 'tarangire', 'ruaha', 'nyerere', 'mikumi', 'katavi'] },
  { value: 'nature_walks', label: 'Nature walks', terms: ['ngorongoro', 'tarangire', 'manyara', 'ruaha', 'nyerere', 'mikumi', 'katavi', 'saadani', 'arusha', 'mkomazi', 'rubondo', 'saanane'] },
  { value: 'walking_safaris', label: 'Walking safari', terms: ['serengeti', 'ngorongoro', 'ruaha', 'nyerere', 'katavi', 'mkomazi', 'saadani'] },
  { value: 'hot_air_balloon', label: 'Hot-air balloon safari', terms: ['serengeti'] },
  { value: 'local_communities', label: 'Cultural visits', terms: ['serengeti', 'kilimanjaro', 'ruaha', 'nyerere', 'mikumi', 'tarangire', 'manyara', 'katavi', 'saadani', 'mkomazi', 'arusha'] },
  { value: 'crater_game_drive', label: 'Crater game drive', terms: ['ngorongoro'] },
  { value: 'maasai_cultural_visits', label: 'Maasai cultural visits', terms: ['ngorongoro'] },
  { value: 'olduvai_gorge', label: 'Olduvai Gorge', terms: ['ngorongoro'] },
  { value: 'mountains', label: 'Hiking', terms: ['kilimanjaro', 'arusha'] },
  { value: 'mount_kilimanjaro', label: 'Mountain climbing', terms: ['kilimanjaro'] },
  { value: 'trekking', label: 'Trekking', terms: ['kilimanjaro'] },
  { value: 'waterfalls', label: 'Waterfalls', terms: ['kilimanjaro', 'arusha'] },
  { value: 'cultural_village_visits', label: 'Cultural village visits', terms: ['kilimanjaro'] },
  { value: 'beaches', label: 'Beach relaxation', terms: ['zanzibar', 'stone town', 'pemba', 'mafia', 'saadani'] },
  { value: 'snorkeling', label: 'Snorkeling', terms: ['zanzibar', 'pemba', 'mafia'] },
  { value: 'scuba_diving', label: 'Scuba diving', terms: ['zanzibar', 'pemba', 'mafia'] },
  { value: 'dolphin_tours', label: 'Dolphin tours', terms: ['zanzibar', 'mafia'] },
  { value: 'spice_tours', label: 'Spice tours', terms: ['zanzibar', 'stone town'] },
  { value: 'heritage', label: 'Stone Town history', terms: ['zanzibar', 'stone town'] },
  { value: 'cuisine', label: 'Local food', terms: ['zanzibar', 'stone town', 'mafia', 'pemba'] },
  { value: 'culture', label: 'Cultural experiences', terms: ['zanzibar', 'stone town', 'mafia', 'pemba'] },
  { value: 'sunset_cruises', label: 'Sunset cruises', terms: ['zanzibar', 'stone town', 'pemba', 'mafia'] },
  { value: 'kite_surfing', label: 'Kite surfing', terms: ['zanzibar', 'pemba'] },
  { value: 'whale_sharks', label: 'Whale shark tours', terms: ['mafia'] },
  { value: 'fishing', label: 'Fishing', terms: ['mafia', 'pemba', 'rubondo', 'saanane', 'gombe', 'mahale', 'saadani'] },
  { value: 'marine_wildlife', label: 'Marine wildlife', terms: ['mafia', 'pemba', 'zanzibar', 'saadani'] },
  { value: 'island_culture', label: 'Island culture', terms: ['mafia', 'pemba'] },
  { value: 'boat_safaris', label: 'Boat safaris', terms: ['nyerere', 'saadani', 'rubondo', 'gombe', 'mahale'] },
  { value: 'kayaking', label: 'Boat trips', terms: ['gombe', 'mahale', 'rubondo', 'nyerere'] },
  { value: 'chimpanzee_trekking', label: 'Chimpanzee trekking', terms: ['gombe', 'mahale'] },
  { value: 'forest_hiking', label: 'Forest hiking', terms: ['gombe', 'mahale'] },
  { value: 'swimming', label: 'Swimming', terms: ['gombe', 'mahale', 'zanzibar', 'pemba', 'mafia'] },
  { value: 'wildlife', label: 'Wildlife viewing', terms: ['ruaha', 'nyerere', 'mikumi', 'tarangire', 'manyara', 'katavi', 'saadani', 'arusha', 'rubondo', 'saanane', 'mkomazi'] },
  { value: 'camping', label: 'Camping & stargazing', terms: ['serengeti', 'ngorongoro', 'tarangire', 'ruaha', 'katavi', 'mkomazi'] },
  { value: 'adventure', label: 'Adventure activities', terms: ['kilimanjaro', 'gombe', 'mahale', 'mafia', 'pemba', 'arusha'] },
];

const emptyDestination = () => ({ name: '', days: null });

export default function TripPlannerPage() {
  const navigate = useNavigate();

  const [destinations, setDestinations] = useState([]);
  const [loadingDestinations, setLoadingDestinations] = useState(true);
  const [loadError, setLoadError] = useState(null);

  const [form, setForm] = useState({
    destinations: [emptyDestination()],
    budget: '',
    currency: 'USD',
    travelers: 1,
    interests: [],
    travelStyle: 'MODERATE',
    language: 'ENGLISH',
  });
  const [submitError, setSubmitError] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [feasibility, setFeasibility] = useState(null);

  const totalDays = form.destinations.reduce((sum, destination) => sum + (Number(destination.days) || 0), 0);
  const selectedDestinationText = form.destinations.map((destination) => destination.name.toLowerCase()).join(' ');
  const interestOptions = INTEREST_RULES.filter((interest) =>
    interest.terms.some((term) => selectedDestinationText.includes(term))
  );

  useEffect(() => {
    getAllDestinations()
      .then(setDestinations)
      .catch(() => setLoadError('Could not load destinations. Please try again shortly.'))
      .finally(() => setLoadingDestinations(false));
  }, []);

  function updateDestination(index, updated) {
    const next = [...form.destinations];
    next[index] = updated;
    const availableInterestValues = INTEREST_RULES
      .filter((interest) => interest.terms.some((term) => next.map((destination) => destination.name.toLowerCase()).join(' ').includes(term)))
      .map((interest) => interest.value);
    setForm({
      ...form,
      destinations: next,
      interests: form.interests.filter((interest) => availableInterestValues.includes(interest)),
    });
    setFeasibility(null);
    setSubmitError(null);
  }

  function addDestination() {
    setForm({ ...form, destinations: [...form.destinations, emptyDestination()] });
    setFeasibility(null);
  }

  function removeDestination(index) {
    setForm({ ...form, destinations: form.destinations.filter((_, i) => i !== index) });
    setFeasibility(null);
  }

  function toggleInterest(value) {
    const has = form.interests.includes(value);
    setForm({
      ...form,
      interests: has ? form.interests.filter((i) => i !== value) : [...form.interests, value],
    });
    setFeasibility(null);
  }

  function validate() {
    if (form.destinations.some((d) => !d.name)) {
      return 'Please select a destination for every row, or remove empty rows.';
    }
    if (!totalDays) {
      return 'Select your destinations so we can calculate the trip length.';
    }
    if (!form.travelers || Number(form.travelers) < 1) {
      return 'At least one traveler is required.';
    }
    if (!destinations.some((destination) => destination.name === form.destinations[0].name)) {
      return 'Please select a starting destination from the suggestions.';
    }
    return null;
  }

  function buildPayload() {
    const startingDestination = destinations.find(
      (destination) => destination.name === form.destinations[0].name
    );
    const payload = {
      destinations: form.destinations.map((d) => ({ name: d.name, days: d.days || null })),
      totalDays,
      startingDestinationId: startingDestination?.id || null,
      budget: form.budget ? Number(form.budget) * CURRENCY_OPTIONS.find((currency) => currency.code === form.currency).toTzs : null,
      travelers: Number(form.travelers),
      interests: form.interests,
      travelStyle: form.travelStyle,
      language: form.language,
    };
    return payload;
  }

  async function continueToItinerary(payload = buildPayload()) {
    setSubmitError(null);
    setSubmitting(true);

    try {
      const itinerary = await generateTrip(payload);
      navigate('/itinerary', { state: { itinerary } });
    } catch (err) {
      setSubmitError(err.response?.data?.message || 'Could not generate your itinerary. Please try again.');
    } finally {
      setSubmitting(false);
    }
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
    try {
      const result = await checkTripFeasibility(buildPayload());
      setFeasibility(result);
    } catch (err) {
      setSubmitError(err.response?.data?.message || 'Could not check trip feasibility. Please try again.');
    } finally {
      setSubmitting(false);
    }
  }

  function selectAlternative(alternative) {
    const nextDestinations = [...form.destinations];
    nextDestinations[0] = { ...nextDestinations[0], name: alternative.name };
    setForm({ ...form, destinations: nextDestinations });
    setFeasibility(null);
    setSubmitError(null);
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
      {!loadingDestinations && !loadError && destinations.length === 0 && (
        <p className="planner__notice">
          No destinations are available yet. An admin needs to add destinations before you can plan a trip.
        </p>
      )}

      {!loadingDestinations && !loadError && (
        <>
          {feasibility && (
            <FeasibilitySummary
              feasibility={feasibility}
              onContinue={() => continueToItinerary()}
              onSelectAlternative={selectAlternative}
            />
          )}
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
            <p className="planner__calculated">
              Trip length from destinations is <strong>{totalDays || '—'} day{totalDays === 1 ? '' : 's'}</strong>
            </p>
            <label>
              Budget (optional)
              <select
                value={form.currency}
                onChange={(e) => setForm({ ...form, currency: e.target.value })}
                aria-label="Budget currency"
              >
                {CURRENCY_OPTIONS.map((currency) => <option key={currency.code} value={currency.code}>{currency.label}</option>)}
              </select>
              <input
                type="number"
                min={1}
                value={form.budget}
                onChange={(e) => setForm({ ...form, budget: e.target.value })}
                placeholder="Leave blank for no budget limit"
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
            <legend>What do you want to experience?</legend>
            {interestOptions.length > 0 ? (
              <div className="checkbox-group">
              {interestOptions.map((opt) => (
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
            ) : (
              <p className="planner__hint">Select a destination to see experiences available there.</p>
            )}
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
            {submitting ? 'Checking trip feasibility…' : 'Check trip feasibility'}
          </button>
          </form>
        </>
      )}
    </div>
  );
}
