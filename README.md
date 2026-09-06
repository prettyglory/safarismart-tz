# SafariSmart TZ

An AI-Assisted Tourism Planning and Recommendation System for Tanzania —
a final-year Computer Engineering project.

This repository contains:

- **`backend/`** — Spring Boot REST API, PostgreSQL, JWT auth, the
  recommendation/budget-optimization engine, and AI-narrated itinerary
  generation. See `backend/README.md` for full status and setup.
- **`frontend/`** — React + Vite single-page application. See
  `frontend/README.md` for full status and setup.

There's no database *files* in this repo — PostgreSQL is a running service
you connect to, not a folder — but the steps below walk through setting it
up with pgAdmin.

## Current build status

Backend: through **Phase 14** (trip history — save/list/get/delete are
live, backed by a new `narrative` column migration).

Frontend: through **Phase 14** (the full loop — plan, generate, view with
map, save, browse history, reopen, delete — works end to end for
registered users; guests can still generate and view without an account,
per the original design decision).

All 14 feature-building phases of the original 17-phase roadmap are now
complete. Phases 15–17 (testing, deployment, documentation) are process
and writeup phases rather than new features.

## Quick start

### 1. Database (via pgAdmin)

This assumes you already have a PostgreSQL **server** installed and running
locally (pgAdmin is a GUI that connects to one — it doesn't run the database
itself). If you installed PostgreSQL via the official installer, pgAdmin was
likely bundled with it and PostgreSQL's own service is already running in
the background.

1. Open **pgAdmin** and connect to your local server (usually already
   registered as "PostgreSQL 16" or similar in the left sidebar; default
   superuser is `postgres`, using the password you set when you installed it).
2. Right-click **Login/Group Roles** → **Create** → **Login/Group Role**.
   - General tab → Name: `safarismart_user`
   - Definition tab → Password: `changeme` (or your own — just remember it
     for the backend's environment variables below)
   - Privileges tab → toggle **Can login?** on
   - Save
3. Right-click **Databases** → **Create** → **Database**.
   - Database: `safarismart`
   - Owner: `safarismart_user`
   - Save
4. That's it — you don't need to create any tables. Flyway (inside the
   Spring Boot app) creates and versions the schema automatically the first
   time the backend starts, from `backend/src/main/resources/db/migration/`
   (`V1`, `V2`, `V3`). You can watch the tables appear under
   **safarismart → Schemas → public → Tables** in pgAdmin after that first run.

If you used a different username, password, host, or port than the
defaults above, set these before running the backend:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/safarismart
export DB_USERNAME=safarismart_user
export DB_PASSWORD=changeme
```

### 2. Backend

```bash
cd backend
export JWT_SECRET=$(openssl rand -base64 32)
export AI_API_KEY=your_llm_api_key
mvn spring-boot:run
```

### 3. Frontend

```bash
cd frontend
npm install
cp .env.example .env
npm run dev
```

### 4. Try it

Visit `http://localhost:5173`. Log in as the seeded admin
(`admin@safarismart.tz` / `ChangeMe123!`) to explore the admin routes, or
register a new tourist account.

## Development roadmap

See `backend/README.md` and `frontend/README.md` for the detailed phase-by-phase
status. At a glance, the full 17-phase plan:

1. Requirements & architecture — done
2. Database design — done
3. Spring Boot setup — done
4. Entities/repositories/services/controllers — done
5. JWT authentication — done
6. Admin CRUD APIs — done
7. Recommendation & budget optimization engine — done
8. AI integration — done
9. React frontend setup — done
10. Trip planner interface — done
11. Itinerary results interface — done
12. Maps integration — done
13. Admin dashboard — done
14. Trip history — done
15. Testing & debugging — next
16. Deployment
17. Documentation
