-- ==========================================================
-- SafariSmart TZ
-- V5 - Remove retired tourism planning features
-- ==========================================================
--
-- Removed features:
--   - Admin seed account
--   - AI itinerary / trip planning
--   - Trip history
--   - Destinations
--   - Attractions
--   - Accommodation
--   - Restaurants
--   - Transport options
--   - Inter-destination routes
--   - Responsible tourism guidelines
--   - Regions
--
-- V1-V4 are intentionally left unchanged.
-- ==========================================================


-- ----------------------------------------------------------
-- REMOVE SEEDED ADMIN ACCOUNT
-- ----------------------------------------------------------

DELETE FROM app_user
WHERE email = 'admin@safarismart.tz'
  AND role = 'ADMIN';


-- ----------------------------------------------------------
-- REMOVE TRIP / ITINERARY TABLES
--
-- Drop child tables first because of foreign-key constraints.
-- ----------------------------------------------------------

DROP TABLE itinerary_item;

DROP TABLE itinerary_day;

DROP TABLE trip_destination;

DROP TABLE trip;


-- ----------------------------------------------------------
-- REMOVE DESTINATION-RELATED TABLES
-- ----------------------------------------------------------

DROP TABLE responsible_tourism_guideline;

DROP TABLE restaurant;

DROP TABLE inter_destination_route;

DROP TABLE transport_option;

DROP TABLE accommodation;

DROP TABLE attraction;

DROP TABLE destination;

DROP TABLE region;


-- ----------------------------------------------------------
-- REMOVE ENUM TYPES NO LONGER USED
-- ----------------------------------------------------------

DROP TYPE app_language;

DROP TYPE transport_type;

DROP TYPE travel_style;