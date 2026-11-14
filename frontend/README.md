# PharmaTrack Frontend

React + Vite + Bootstrap frontend for the existing PharmaTrack Spring Boot backend.

## Requirements
- Node.js 20.19+ or 22.12+
- Existing Spring Boot backend running on port 8080
- Existing database and backend configuration

## Run
Open PowerShell in this `pharmatrack-frontend` folder and run:

```powershell
npm install
npm run dev
```

Open the URL printed by Vite, normally:

http://localhost:5173

Keep the Spring Boot backend running in a separate terminal at:

http://localhost:8080

Vite proxies `/api` requests to `http://localhost:8080`, so the frontend does not need a separate CORS configuration.

## Modules
- Dashboard: dashboard and expiry overview
- Medicines: create, list, update and delete
- Warehouses: create, list, update and deactivate
- Inventory: create batches, summary, medicine filter and FEFO view
- Substitutions: create, list, approve and reject
- Reservations: create, list, confirm, cancel and process expiry
- Stock transfers: list, filter, generate recommendations, approve and reject
- Analytics: expiry, reservations, transfers and consumption

## Build check
```powershell
npm run build
```

## Notes
- This frontend uses the backend routes supplied for the project.
- API response wrappers (`success`, `message`, `data`) are handled centrally in `src/api.js`.
- Transfer and analytics endpoints return their response data directly; these are handled as direct JSON.
- No backend Java files or database tables are changed by this frontend.
