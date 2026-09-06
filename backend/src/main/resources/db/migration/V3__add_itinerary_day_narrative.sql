-- The AI-narrated (or template) text for a day was only ever returned in the
-- API response, never persisted. Trip history needs it, so it's added here.
ALTER TABLE itinerary_day ADD COLUMN narrative TEXT;
