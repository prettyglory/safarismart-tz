-- ==========================================================
-- SafariSmart TZ -- Initial Schema (V1)
-- ==========================================================

-- ---------- ENUM TYPES ----------
CREATE TYPE user_role AS ENUM ('TOURIST', 'ADMIN');
CREATE TYPE travel_style AS ENUM ('BUDGET', 'MODERATE', 'LUXURY');
CREATE TYPE app_language AS ENUM ('ENGLISH', 'SWAHILI');
CREATE TYPE transport_type AS ENUM ('BUS', 'FLIGHT', 'FERRY', 'PRIVATE_CAR', 'TRAIN', 'OTHER');

-- ---------- USERS ----------
CREATE TABLE app_user (
    id              BIGSERIAL PRIMARY KEY,
    full_name       VARCHAR(150) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            user_role NOT NULL DEFAULT 'TOURIST',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------- REGION / DESTINATION ----------
CREATE TABLE region (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE destination (
    id              BIGSERIAL PRIMARY KEY,
    region_id       BIGINT NOT NULL REFERENCES region(id) ON DELETE RESTRICT,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (region_id, name)
);
CREATE INDEX idx_destination_region ON destination(region_id);

-- ---------- ATTRACTION ----------
CREATE TABLE attraction (
    id                  BIGSERIAL PRIMARY KEY,
    destination_id      BIGINT NOT NULL REFERENCES destination(id) ON DELETE RESTRICT,
    name                VARCHAR(200) NOT NULL,
    category            VARCHAR(100) NOT NULL,           -- e.g. 'wildlife','waterfall','cultural_site'
    interest_tags       TEXT[] NOT NULL DEFAULT '{}',    -- e.g. {'wildlife','adventure'}
    description         TEXT,
    entrance_fee_min    NUMERIC(12,2) NOT NULL CHECK (entrance_fee_min >= 0),
    entrance_fee_max    NUMERIC(12,2) NOT NULL CHECK (entrance_fee_max >= entrance_fee_min),
    avg_duration_hours  NUMERIC(4,1) NOT NULL CHECK (avg_duration_hours > 0),
    is_community_based  BOOLEAN NOT NULL DEFAULT FALSE,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_attraction_destination ON attraction(destination_id);
CREATE INDEX idx_attraction_tags ON attraction USING GIN (interest_tags);

-- ---------- ACCOMMODATION ----------
CREATE TABLE accommodation (
    id              BIGSERIAL PRIMARY KEY,
    destination_id  BIGINT NOT NULL REFERENCES destination(id) ON DELETE RESTRICT,
    name            VARCHAR(200) NOT NULL,
    style           travel_style NOT NULL,
    price_min       NUMERIC(12,2) NOT NULL CHECK (price_min >= 0),
    price_max       NUMERIC(12,2) NOT NULL CHECK (price_max >= price_min),
    description     TEXT,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_accommodation_destination ON accommodation(destination_id);
CREATE INDEX idx_accommodation_style ON accommodation(style);

-- ---------- TRANSPORT (within/local to a destination, e.g. day-tour transport) ----------
CREATE TABLE transport_option (
    id              BIGSERIAL PRIMARY KEY,
    destination_id  BIGINT NOT NULL REFERENCES destination(id) ON DELETE RESTRICT,
    type            transport_type NOT NULL,
    description     TEXT,
    price_min       NUMERIC(12,2) NOT NULL CHECK (price_min >= 0),
    price_max       NUMERIC(12,2) NOT NULL CHECK (price_max >= price_min),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_transport_destination ON transport_option(destination_id);

-- ---------- INTER-DESTINATION ROUTE (travel BETWEEN legs of a multi-destination trip) ----------
CREATE TABLE inter_destination_route (
    id                      BIGSERIAL PRIMARY KEY,
    from_destination_id     BIGINT NOT NULL REFERENCES destination(id) ON DELETE RESTRICT,
    to_destination_id       BIGINT NOT NULL REFERENCES destination(id) ON DELETE RESTRICT,
    type                    transport_type NOT NULL,
    estimated_duration_hours NUMERIC(4,1) NOT NULL CHECK (estimated_duration_hours > 0),
    price_min               NUMERIC(12,2) NOT NULL CHECK (price_min >= 0),
    price_max               NUMERIC(12,2) NOT NULL CHECK (price_max >= price_min),
    is_active               BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (from_destination_id <> to_destination_id)
);
CREATE INDEX idx_route_from ON inter_destination_route(from_destination_id);
CREATE INDEX idx_route_to ON inter_destination_route(to_destination_id);

-- ---------- RESTAURANT ----------
CREATE TABLE restaurant (
    id              BIGSERIAL PRIMARY KEY,
    destination_id  BIGINT NOT NULL REFERENCES destination(id) ON DELETE RESTRICT,
    name            VARCHAR(200) NOT NULL,
    cuisine_type    VARCHAR(100),
    price_range     travel_style NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_restaurant_destination ON restaurant(destination_id);

-- ---------- RESPONSIBLE TOURISM GUIDELINE ----------
-- Scope: general (both NULL), region-level, or destination-level.
CREATE TABLE responsible_tourism_guideline (
    id              BIGSERIAL PRIMARY KEY,
    region_id       BIGINT REFERENCES region(id) ON DELETE CASCADE,
    destination_id  BIGINT REFERENCES destination(id) ON DELETE CASCADE,
    category        VARCHAR(100) NOT NULL,   -- 'environment','wildlife','culture','waste'
    guideline_text  TEXT NOT NULL
);
CREATE INDEX idx_guideline_region ON responsible_tourism_guideline(region_id);
CREATE INDEX idx_guideline_destination ON responsible_tourism_guideline(destination_id);

-- ---------- TRIP ----------
-- user_id is NOT NULL: guest-generated itineraries are never persisted.
-- A Trip row only ever exists for an authenticated user who chose to save it.
CREATE TABLE trip (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    total_days      INT NOT NULL CHECK (total_days BETWEEN 1 AND 30),
    budget          NUMERIC(14,2) NOT NULL CHECK (budget > 0),
    travelers       INT NOT NULL CHECK (travelers >= 1),
    interests       TEXT[] NOT NULL DEFAULT '{}',
    travel_style    travel_style NOT NULL,
    language        app_language NOT NULL DEFAULT 'ENGLISH',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_trip_user ON trip(user_id);

-- ---------- TRIP DESTINATION (ordered legs of a multi-destination trip) ----------
CREATE TABLE trip_destination (
    id                  BIGSERIAL PRIMARY KEY,
    trip_id             BIGINT NOT NULL REFERENCES trip(id) ON DELETE CASCADE,
    destination_id      BIGINT NOT NULL REFERENCES destination(id) ON DELETE RESTRICT,
    sequence_order      INT NOT NULL CHECK (sequence_order >= 1),
    days_allocated      INT NOT NULL CHECK (days_allocated >= 1),
    UNIQUE (trip_id, sequence_order)
);
CREATE INDEX idx_tripdest_trip ON trip_destination(trip_id);
CREATE INDEX idx_tripdest_destination ON trip_destination(destination_id);

-- ---------- ITINERARY DAY ----------
CREATE TABLE itinerary_day (
    id                  BIGSERIAL PRIMARY KEY,
    trip_destination_id BIGINT NOT NULL REFERENCES trip_destination(id) ON DELETE CASCADE,
    day_number          INT NOT NULL CHECK (day_number >= 1),  -- day number within this leg
    UNIQUE (trip_destination_id, day_number)
);
CREATE INDEX idx_itinday_tripdest ON itinerary_day(trip_destination_id);

-- ---------- ITINERARY ITEM ----------
-- Exactly one of attraction_id / accommodation_id / transport_id / route_id must be set.
CREATE TABLE itinerary_item (
    id                  BIGSERIAL PRIMARY KEY,
    itinerary_day_id    BIGINT NOT NULL REFERENCES itinerary_day(id) ON DELETE CASCADE,
    attraction_id       BIGINT REFERENCES attraction(id) ON DELETE RESTRICT,
    accommodation_id    BIGINT REFERENCES accommodation(id) ON DELETE RESTRICT,
    transport_id        BIGINT REFERENCES transport_option(id) ON DELETE RESTRICT,
    route_id            BIGINT REFERENCES inter_destination_route(id) ON DELETE RESTRICT,
    estimated_cost_min  NUMERIC(12,2) NOT NULL CHECK (estimated_cost_min >= 0),
    estimated_cost_max  NUMERIC(12,2) NOT NULL CHECK (estimated_cost_max >= estimated_cost_min),
    notes               TEXT,
    CONSTRAINT chk_exactly_one_item_type CHECK (
        (CASE WHEN attraction_id    IS NOT NULL THEN 1 ELSE 0 END +
         CASE WHEN accommodation_id IS NOT NULL THEN 1 ELSE 0 END +
         CASE WHEN transport_id     IS NOT NULL THEN 1 ELSE 0 END +
         CASE WHEN route_id         IS NOT NULL THEN 1 ELSE 0 END) = 1
    )
);
CREATE INDEX idx_item_day ON itinerary_item(itinerary_day_id);
