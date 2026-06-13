# AI Focus Analytics Platform

AI Focus Analytics Platform is a Spring Cloud and React application for logging focus sessions, analyzing productivity patterns, and generating AI-assisted insights for better work planning.

## Architecture

- `config-server` serves centralized configuration on `8888`
- `eureka-server` provides service discovery on `8761`
- `gateway-service` is the single public API entry point on `8080`
- `user-service` manages profile and Keycloak-linked identity data on `8081`
- `focus-session-service` stores focus sessions and publishes Kafka events on `8082`
- `insight-service` consumes focus-session events and stores AI insights on `8083`
- `focus-analytics-frontend` is the React dashboard client

## Core Flow

1. The user signs in through Keycloak.
2. The gateway validates the JWT and syncs the user profile if needed.
3. The frontend posts focus sessions through the gateway.
4. `focus-session-service` validates the user, stores the session, and publishes a Kafka event.
5. `insight-service` consumes the event, calls Gemini, and stores the generated insight.
6. The frontend reads session history and insight details through the gateway.

## API Overview

### Focus sessions

- `POST /api/focus-sessions`
- `GET /api/focus-sessions`

Example payload:

```json
{
  "type": "DEEP_WORK",
  "duration": 50,
  "focusScore": 85,
  "startTime": "2026-06-03T09:00:00",
  "additionalMetrics": {
    "notes": "Planning sprint goals #planning #backend"
  }
}
```

### Insights

- `GET /api/insights/user/{userId}`
- `GET /api/insights/focus-session/{focusSessionId}`

Example insight response:

```json
{
  "focusSessionId": "session-1",
  "type": "CASUAL_CODING",
  "userId": "user-1",
  "insight": "Overall: Good session.",
  "improvements": ["Focus: Reduce context switching"],
  "suggestions": ["Next block: Plan a deep work block"],
  "safety": ["Take regular breaks"]
}
```

### User profiles

- `POST /api/users/register`
- `GET /api/users/{userId}`
- `GET /api/users/{userId}/validate`

## Local Setup

Prerequisites:

- Java 17+
- Maven
- Node.js 18+
- PostgreSQL
- MongoDB
- Kafka
- Keycloak on `http://localhost:8181`

Recommended local service order:

1. `config-server`
2. `eureka-server`
3. `user-service`
4. `focus-session-service`
5. `insight-service`
6. `gateway-service`
7. `focus-analytics-frontend`

Useful environment values:

- `USER_DB_URL=jdbc:postgresql://localhost:5432/focus-analytics-user`
- `GEMINI_URL=<your Gemini endpoint>`
- `GEMINI_KEY=<your Gemini API key>`

## Notes

- The frontend still uses compatibility field names like `focusScore` and `additionalMetrics` at the API boundary.
- The product vocabulary in the UI, config, and backend is aligned around focus sessions and insights.
