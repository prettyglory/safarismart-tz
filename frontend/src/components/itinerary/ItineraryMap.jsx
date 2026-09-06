import { MapContainer, TileLayer, Marker, Popup, Polyline } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// react-leaflet's default marker icon breaks under Vite's bundling --
// point it at the same CDN-hosted images Leaflet ships with instead of
// relying on webpack-specific asset resolution.
const defaultIcon = L.icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
});

export default function ItineraryMap({ legs }) {
  const stops = legs
    .filter((leg) => leg.latitude != null && leg.longitude != null)
    .map((leg) => ({
      position: [leg.latitude, leg.longitude],
      name: leg.destinationName,
      sequenceOrder: leg.sequenceOrder,
      daysAllocated: leg.daysAllocated,
    }));

  if (stops.length === 0) {
    return (
      <div className="itinerary-map itinerary-map--empty">
        <p>Map locations aren't available yet for these destinations.</p>
      </div>
    );
  }

  const center = stops[0].position;
  const routeLine = stops.length > 1 ? stops.map((s) => s.position) : null;

  return (
    <div className="itinerary-map">
      <MapContainer center={center} zoom={7} scrollWheelZoom={false} style={{ height: '320px', width: '100%' }}>
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {routeLine && <Polyline positions={routeLine} pathOptions={{ color: '#2B4735', weight: 3, dashArray: '6 8' }} />}
        {stops.map((stop) => (
          <Marker key={stop.sequenceOrder} position={stop.position} icon={defaultIcon}>
            <Popup>
              <strong>Stop {stop.sequenceOrder}: {stop.name}</strong>
              <br />
              {stop.daysAllocated} day{stop.daysAllocated > 1 ? 's' : ''}
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
}
