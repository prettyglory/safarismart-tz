# SafariSmart TZ — Backend

AI-Assisted Tourism Planning and Recommendation System for Tanzania.
Final-year project — Spring Boot backend.

## Status

This snapshot reflects **Phase 14: trip history**, plus everything from
Phase 8 (AI integration) and small additions from Phases 12–13 (destination
coordinates, admin list endpoints).

**Phase 14 additions:**
- `V3__add_itinerary_day_narrative.sql` — a schema gap fix. The AI-narrated
  text for each day was only ever returned in the API response, never
  persisted (Phase 2's schema had no column for it). Since saving a trip is
  pointless if you can't read its narrative again, `itinerary_day` gains a
  `narrative TEXT` column.
- `dto/history/` — `SaveTripRequest` (+ nested leg/day/item request DTOs),
  `TripSummaryDto`, `TripDetailDto` (which reuses `ItineraryResponse` so the
  frontend renders a freshly-generated and a reopened saved itinerary with
  the exact same components).
- `TripHistoryService` — persists a generated itinerary into
  `Trip`/`TripDestination`/`ItineraryDay`/`ItineraryItem` entities, lists a
  user's saved trips, fetches one, and deletes one (always scoped to the
  requesting user's `userId`, never a bare `id` lookup).
- `TripController` gained `POST /api/v1/trips` (save), `GET /api/v1/trips`
  (list mine), `GET /api/v1/trips/{id}` (view mine), `DELETE /api/v1/trips/{id}`
  (delete mine) — all authenticated via `@AuthenticationPrincipal
  CustomUserDetails`, falling under Phase 5's `anyRequest().authenticated()`
  rule with no security config changes needed.

**Known, deliberate simplifications worth stating in your report:**
- `NOTE` items (the "no verified data available" flags shown during
  generation) are **not persisted** on save — the database's
  `chk_exactly_one_item_type` constraint requires a real reference per item,
  and a NOTE has none. They're generation-time feedback, not itinerary
  content, so dropping them on save is a reasonable scope boundary, not an
  oversight.
- If a referenced attraction/accommodation/transport/route was deleted or
  deactivated between generation and save, that single item is silently
  skipped rather than failing the whole save.
- `budgetNote` and `aiNarrated` are not tracked historically on a saved
  trip; `TripDetailDto` recomputes `overBudget` from the currently-saved
  item costs and omits/defaults the rest, since they were true only at
  generation time.

Completed so far:
- Phase 1 — Requirements, system design, architecture
- Phase 2 — PostgreSQL schema (see `src/main/resources/db/migration/V1__init_schema.sql`)
- Phase 3 — Spring Boot project setup
- Phase 4 — JPA entities for all tables, Spring Data repositories, read-only
  DTOs, global exception handling, and a first vertical slice
  (`DestinationService` + `DestinationController`)
- Phase 5 — Stateless JWT authentication (register/login, security config, filter)
- Phase 6 — Admin CRUD over all tourism reference data + admin user management
  (seeded admin: `admin@safarismart.tz` / `ChangeMe123!` — change before real deployment)
- Phase 7 — Deterministic recommendation and budget optimization engine
  (`service/planning/`)
- Phase 8 — AI integration (`service/ai/`), completing the full pipeline:
  - `AnthropicAiClient` — calls Anthropic's Messages API server-side only
    (never exposed to the frontend). Swappable via the `AiClient` interface
    if you prefer OpenAI or another provider.
  - `PromptContextBuilder` — builds a closed-list prompt from the
    `TripPlanDraft` so the AI can only narrate verified data, never invent it.
  - `AiResponseValidator` — parses the AI's JSON response and checks every
    day's narrative mentions every item it was given; throws on any
    structural or content failure.
  - `AiItineraryService` — orchestrates the above and **falls back to a
    deterministic template narrative** (built straight from the draft, no
    LLM) whenever AI narration fails validation for any reason, so the
    tourist always gets a usable, fact-accurate itinerary.
  - **`POST /api/v1/trips/generate`** is now live (`TripController`), public
    per Phase 5's security rules.

**Before running:** set `AI_API_KEY` (and optionally `AI_MODEL` /
`AI_API_URL` if using a different provider or a newer model string --
verify the current model identifier against your provider's live docs
before relying on the default).

**Try it:**
```bash
curl -X POST http://localhost:8080/api/v1/trips/generate \
  -H "Content-Type: application/json" \
  -d '{
        "destinations": [{"name": "Mbeya", "days": 3}],
        "budget": 800000,
        "travelers": 2,
        "interests": ["mountains", "waterfalls", "culture"],
        "travelStyle": "MODERATE",
        "language": "ENGLISH"
      }'
```
Check the `aiNarrated` field: `true` means the LLM's narrative passed
validation; `false` means the fallback template was used (expected if
`AI_API_KEY` isn't set, or if no destinations/attractions have been added
yet via the admin API -- this build ships with no seeded tourism data).

Not yet implemented (upcoming phases):
- React frontend (Phases 9–12)
- Admin dashboard (Phase 13)
- Trip history — save/view/delete saved trips (Phase 14). This is also where
  a generated `ItineraryResponse`/`TripPlanDraft` gets converted into
  persisted `Trip`/`TripDestination`/`ItineraryDay`/`ItineraryItem` entities
  for a logged-in user who saves it.
- "Download itinerary as PDF" (planned, slotted in after the itinerary
  results UI, Phase 11)
- Testing, deployment, documentation (Phases 15–17)

The `util/` package under `src/main/java/tz/ac/dit/safarismart/` is still
an empty placeholder.

## Prerequisites

- Java 25
- Maven
- PostgreSQL (running locally, or via a connection string you provide)

## Setup

1. Create the database:
   ```bash
   createdb safarismart
   ```

2. Set the required environment variables (recommended over editing
   `application-local.properties`):
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/safarismart
   export DB_USERNAME=safarismart_user
   export DB_PASSWORD=your_password
   export JWT_SECRET=a_long_random_base64_string_at_least_256_bits
   export AI_API_KEY=your_llm_api_key
   ```

3. Run the app:
   ```bash
   mvn spring-boot:run
   ```

Flyway will automatically apply `V1__init_schema.sql` to your database on
startup. Watch the console log for:
```
Flyway ... Successfully applied 1 migration
```

## Notes

- `spring.jpa.hibernate.ddl-auto=validate` — Hibernate will never
  auto-generate or alter your schema. Flyway migrations are the single
  source of truth for the database structure.
- `application-local.properties` is a template only and is listed in
  `.gitignore` — never commit real secrets. Prefer real environment
  variables over filling this file in.
- The LLM API key is used **server-side only** and is never exposed to
  the React frontend.
