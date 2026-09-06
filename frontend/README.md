# SafariSmart TZ — Frontend

React + Vite frontend for the AI-Assisted Tourism Planning and
Recommendation System for Tanzania.

## Status

This snapshot reflects **Phase 14: trip history** — the final planned
phase before testing, deployment, and documentation.

Completed so far:
- Vite + React project scaffold
- Axios API layer (`src/api/`) — `axiosClient`, `authApi`, `destinationApi`,
  a fully expanded `adminApi`, and now a fully expanded `tripApi`
  (`generateTrip`, `saveTrip`, `listSavedTrips`, `getSavedTrip`, `deleteSavedTrip`)
- `AuthContext`, route guards, `Navbar`
- Working pages: Home, Login, Register
- Trip planner form — fully functional
- Itinerary results display with map — fully functional, and now with a
  real **Save this trip** button (previously a placeholder alert) that
  posts to the backend and confirms success with a link to My trips
- Admin dashboard — fully functional across all 9 resource tabs
- **Trip history** (`src/pages/TripHistoryPage.jsx`) — fully functional:
  - Lists a logged-in user's saved trips (destinations, days, budget, style,
    save date)
  - **View** re-fetches the full saved itinerary and reuses
    `ItineraryResultsPage` to display it — the same components render both
    a freshly-generated and a reopened saved trip
  - **Delete** removes a saved trip (always scoped to the logged-in user)
  - A small correctness fix: `ItinerarySummary` now checks
    `aiNarrated === false` explicitly rather than `!aiNarrated`, so a
    reopened saved trip (where this flag's meaning is murkier) never shows
    an incorrect "AI writer was unavailable" disclaimer

Not yet implemented:
- "Download itinerary as PDF" — actual implementation (planned; the button
  still shows a placeholder message)
- Testing, deployment, and documentation phases (15–17) — these are process/
  writeup phases rather than feature work

## Setup

```bash
npm install
cp .env.example .env
npm run dev
```

The app runs at `http://localhost:5173` by default. Make sure the backend
is running and its `CORS_ALLOWED_ORIGINS` includes this origin.

`.env` should point at your running backend:
```
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

## Try it

1. Start the backend (see `backend/README.md`) — it will apply the new
   `V3` migration automatically on startup.
2. `npm run dev` here.
3. Register a tourist account, add some tourism data via the admin account
   if you haven't already, generate a trip, click **Save this trip**, then
   visit **My trips** and confirm it appears, opens correctly, and can be
   deleted.
