# Focus Analytics Frontend

React dashboard for AI Focus Analytics Platform.

## What it does

- Logs focus sessions through the gateway
- Shows recent sessions as dashboard cards
- Displays AI-generated insight details
- Derives weekly productivity summaries from existing session data

## Run locally

```bash
npm install
npm run dev
```

The app expects the backend gateway at `http://localhost:8080/api`.

## Authentication

The app uses OAuth2 PKCE through Keycloak with:

- Authorization endpoint: `http://localhost:8181/realms/focus-analytics/protocol/openid-connect/auth`
- Token endpoint: `http://localhost:8181/realms/focus-analytics/protocol/openid-connect/token`

## API contract

The frontend calls:

- `GET /api/focus-sessions`
- `POST /api/focus-sessions`
- `GET /api/insights/focus-session/{id}`

Notes:

- The frontend keeps compatibility field names like `focusScore` and `additionalMetrics` at the API boundary.
- Product copy and dashboard layout are already aligned to the productivity domain.
