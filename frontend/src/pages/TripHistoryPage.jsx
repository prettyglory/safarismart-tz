import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { listSavedTrips, getSavedTrip, deleteSavedTrip } from '../api/tripApi';
import { formatAmount } from '../utils/format';

export default function TripHistoryPage() {
  const navigate = useNavigate();
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [busyId, setBusyId] = useState(null);

  function load() {
    setLoading(true);
    listSavedTrips()
      .then(setTrips)
      .catch(() => setError('Could not load your saved trips.'))
      .finally(() => setLoading(false));
  }

  useEffect(load, []);

  async function handleView(id) {
    setBusyId(id);
    try {
      const itinerary = await getSavedTrip(id);
      navigate('/itinerary', { state: { itinerary } });
    } catch {
      setError('Could not open this trip.');
    } finally {
      setBusyId(null);
    }
  }

  async function handleDelete(id) {
    if (!window.confirm('Delete this saved trip? This cannot be undone.')) return;
    setBusyId(id);
    try {
      await deleteSavedTrip(id);
      load();
    } catch {
      setError('Could not delete this trip.');
    } finally {
      setBusyId(null);
    }
  }

  if (loading) return <p className="placeholder-page">Loading your trips…</p>;

  return (
    <div className="trip-history">
      <h1>My trips</h1>
      {error && <p className="form-error">{error}</p>}

      {trips.length === 0 ? (
        <p className="placeholder-page">You haven't saved any trips yet.</p>
      ) : (
        <div className="trip-history__list">
          {trips.map((trip) => (
            <div key={trip.id} className="trip-card">
              <div className="trip-card__info">
                <h3>{trip.destinationNames.join(' → ')}</h3>
                <p>
                  {trip.totalDays} day{trip.totalDays > 1 ? 's' : ''} · {formatAmount(trip.budget)} · {trip.travelStyle}
                </p>
                <p className="trip-card__date">
                  Saved {new Date(trip.createdAt).toLocaleDateString()}
                </p>
              </div>
              <div className="trip-card__actions">
                <button className="btn btn--primary btn--small" onClick={() => handleView(trip.id)} disabled={busyId === trip.id}>
                  View
                </button>
                <button className="btn btn--ghost btn--small crud-table__delete" onClick={() => handleDelete(trip.id)} disabled={busyId === trip.id}>
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
