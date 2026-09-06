import { useState } from 'react';
import { useLocation, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { saveTrip } from '../api/tripApi';
import ItinerarySummary from '../components/itinerary/ItinerarySummary';
import ItineraryMap from '../components/itinerary/ItineraryMap';
import LegSection from '../components/itinerary/LegSection';

export default function ItineraryResultsPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const itinerary = location.state?.itinerary;

  const [saving, setSaving] = useState(false);
  const [saveMessage, setSaveMessage] = useState(null);

  if (!itinerary) {
    return (
      <div className="placeholder-page">
        <h2>No itinerary to show</h2>
        <p>Generate a trip first to see your results here.</p>
        <Link to="/plan" className="btn btn--primary">Plan a trip</Link>
      </div>
    );
  }

  async function handleSaveClick() {
    setSaving(true);
    setSaveMessage(null);
    try {
      await saveTrip(itinerary);
      setSaveMessage({ type: 'success', text: 'Saved! View it anytime in My trips.' });
    } catch (err) {
      setSaveMessage({ type: 'error', text: err.response?.data?.message || 'Could not save this trip.' });
    } finally {
      setSaving(false);
    }
  }

  function handleDownloadClick() {
    alert('Downloading your itinerary as a PDF is coming soon.');
  }

  return (
    <div className="itinerary-results">
      <div className="itinerary-results__header">
        <h1>Your Tanzania itinerary</h1>
        <div className="itinerary-results__actions">
          {isAuthenticated && (
            <button className="btn btn--ghost" onClick={handleSaveClick} disabled={saving}>
              {saving ? 'Saving…' : 'Save this trip'}
            </button>
          )}
          <button className="btn btn--ghost" onClick={handleDownloadClick}>Download PDF</button>
        </div>
      </div>

      {saveMessage && (
        <p className={saveMessage.type === 'error' ? 'form-error' : 'form-success'}>
          {saveMessage.text}
          {saveMessage.type === 'success' && (
            <> <button className="link-button" onClick={() => navigate('/trips')}>View my trips</button></>
          )}
        </p>
      )}

      <ItinerarySummary itinerary={itinerary} />
      <ItineraryMap legs={itinerary.legs} />

      {itinerary.legs.map((leg) => (
        <LegSection key={leg.sequenceOrder} leg={leg} />
      ))}
    </div>
  );
}
