# Codex Project Context

## Project Goal

This repository started as a fitness tracking microservices app, but the target direction is now a different product:

`Work Productivity / Focus Analytics Platform`

The goal is to rebrand and migrate the existing backend and frontend with minimal structural disruption, while making the final project look and feel like an original productivity product rather than a fitness clone.

The migration should be done step by step so the app remains understandable, testable, and easy to push to Git in real work-sized increments.

## Important Constraint

Do not treat this as a cosmetic rename only. The public-facing product story, route names, domain entities, UI labels, sample data, and AI prompts should all be changed to the productivity domain.

## Current System Overview

The system is a Spring Cloud microservices architecture with:

- `gateway-service` on `8080`
- `eureka-server` registry on `8761`
- `config-server` on `8888`
- `user-service` on `8081`
- `focus-session-service` on `8082`
- `insight-service` on `8083`
- `focus-analytics-frontend` as the React client

### Current runtime flow

1. Frontend sends requests to the gateway-service.
2. Gateway authenticates requests with JWT from Keycloak.
3. Gateway syncs user data into `user-service` when needed.
4. Frontend activity requests are routed to `focus-session-service`.
5. `focus-session-service` validates user identity through `user-service`, stores activity records in MongoDB, and publishes events to Kafka.
6. `insight-service` consumes Kafka activity events, generates AI recommendations with Gemini, and stores results in MongoDB.
7. Frontend reads activities and recommendations through the gateway-service.

## Key Existing Gateway Behavior

Reference file:

- [gateway-service-service.yml](C:/Projects/fitness-micro-hindi/config-server/src/main/resources/config/gateway-service-service.yml)

Current gateway-service responsibilities:

- register with Eureka
- validate JWTs using the Keycloak JWKS endpoint
- route API requests to the appropriate downstream service
- currently expose fitness routes:
  - `/api/users/**`
  - `/api/activities/**`
  - `/api/recommendations/**`

## Target Product Direction

The new product should be presented as a work productivity platform, not a fitness app.

Recommended product name:

`AI Focus Analytics Platform`

Alternative acceptable naming:

- `Work Productivity Dashboard`
- `Focus Intelligence Platform`
- `Productivity Insights System`

## Recommended Domain Mapping

Use this terminology consistently across backend, frontend, docs, and route names:

- `Activity` -> `FocusSession`
- `ActivityType` -> `WorkMode` or `SessionType`
- `Recommendation` -> `Insight`
- `focusScore` -> `focusScore` or `productivityScore`
- `additionalMetrics` -> `notes`, `tags`, `distractionReasons`, or `taskMetadata`
- `activity-events` -> `focus-session-events`

## Suggested Product Features

The system should feel like a real productivity product, not a renamed clone. Good feature directions:

- focus session logging
- project/task tagging
- productivity score tracking
- best working hours analysis
- distraction tracking
- weekly productivity summary
- AI-generated next-step suggestions
- consistency streaks

## Service-by-Service Meaning

### `user-service`

Current role:

- user registration
- profile lookup
- Keycloak-linked user identity validation

New role:

- employee or user profile service for the productivity platform

### `focus-session-service`

Current role:

- track fitness activities
- validate users through `user-service`
- persist activity records in MongoDB
- publish events to Kafka

New role:

- track focus sessions
- validate users through `user-service`
- persist focus sessions in MongoDB
- publish focus session events to Kafka

### `insight-service`

Current role:

- consume activity events
- generate recommendations with Gemini
- persist recommendations in MongoDB

New role:

- consume focus session events
- generate productivity insights with Gemini
- persist insights in MongoDB

### `gateway-service`

Current role:

- single entry point for the frontend
- JWT auth
- user sync against `user-service`
- route API traffic to the correct service

New role:

- same technical role
- new public API paths and renamed route meaning

### `config-server`

Current role:

- central configuration source for all services

New role:

- same role, but config files must reflect the productivity domain

### `eureka-server`

Current role:

- service discovery

New role:

- same role

### `fitness-frontend`

Current role:

- fitness UI talking to gateway-service APIs

New role:

- productivity dashboard UI

## Files And Areas That Matter Most

High-priority areas for the migration:

- `config-server/src/main/resources/config/gateway-service-service.yml`
- `config-server/src/main/resources/config/user-service.yml`
- `config-server/src/main/resources/config/activity-service.yml`
- `config-server/src/main/resources/config/ai-service.yml`
- `gateway-service/src/main/java/com/fitness/gateway-service/SecurityConfig.java`
- `gateway-service/src/main/java/com/fitness/gateway-service/KeycloakUserSyncFilter.java`
- `gateway-service/src/main/java/com/fitness/gateway-service/user/UserService.java`
- `user-service/src/main/java/com/fitness/user-service/controller/UserController.java`
- `user-service/src/main/java/com/fitness/user-service/services/UserService.java`
- `focus-session-service/src/main/java/com/fitness/focus-session-service/controller/ActivityController.java`
- `focus-session-service/src/main/java/com/fitness/focus-session-service/service/ActivityService.java`
- `insight-service/src/main/java/com/fitness/insight-service/controller/RecommendationController.java`
- `insight-service/src/main/java/com/fitness/insight-service/service/RecommendationService.java`
- `insight-service/src/main/java/com/fitness/insight-service/service/ActivityMessageListener.java`
- `insight-service/src/main/java/com/fitness/insight-service/service/GeminiService.java`
- `fitness-frontend/src/services/api.js`
- `fitness-frontend/src/components/*`
- `fitness-frontend/src/App.jsx`
- `fitness-frontend/src/App.css`
- `fitness-frontend/src/index.css`

## Migration Rules

- Prefer minimal structural change where possible.
- Keep the distributed architecture intact.
- Rename public domain terms to productivity language.
- Update endpoint paths to the new product vocabulary.
- Keep technical behavior stable unless a change is needed for the new domain.
- Add new functionality incrementally so each commit is meaningful.
- Do not do fake or backdated commits.

## Commit Strategy

The work should be split into real, independent phases so the commit history looks natural because the work was actually done in increments.

Suggested commit order:

1. Rebrand and gateway-service route migration
2. Backend DTO and model rename
3. Service logic and config migration
4. AI prompt and insight flow migration
5. Frontend rename and dashboard updates
6. Productivity-specific feature additions
7. Tests, cleanup, and documentation

## Task Backlog

### Phase 0: Maven and package identity

- [x] Rename the repository identity to `Focus Analytics Platform`.
- [x] Set the root Maven `groupId` to `com.focusanalytics`.
- [x] Set the root Maven `artifactId` to `focus-analytics-platform`.
- [x] Set the root Maven `name` to `focus-analytics-platform` or `Focus Analytics Platform`.
- [x] Rename the frontend package/folder identity to `focus-analytics-frontend`.
- [x] Rename the frontend component filenames and exports to match the new frontend identity.
- [x] Remove the stale top-level `FitnessApp` folder from the workspace.
- [x] Standardize module artifactIds:
  - `configserver` -> `config-server`
  - `eureka` -> `eureka-server`
  - `gateway` -> `gateway-service`
  - `userservice` -> `user-service`
  - `activityservice` -> `focus-session-service`
  - `aiservice` -> `insight-service`
- [x] Standardize Java package roots:
  - `com.focusanalytics.configserver`
  - `com.focusanalytics.eureka`
  - `com.focusanalytics.gateway`
  - `com.focusanalytics.userservice`
  - `com.focusanalytics.focussessionservice`
  - `com.focusanalytics.insightservice`
- [x] Rename application classes to match the new service names:
  - `ActivityserviceApplication` -> `FocusSessionServiceApplication`
  - `AiserviceApplication` -> `InsightServiceApplication`
  - `UserserviceApplication` -> `UserServiceApplication`
  - `GatewayApplication` -> `GatewayServiceApplication`
  - `ConfigserverApplication` -> `ConfigServerApplication`
  - `EurekaApplication` -> `EurekaServerApplication`
- [ ] Verify Phase 0 runtime startup with external configuration supplied:
  - `activityservice` still needs Kafka configuration available
  - `aiservice` still needs Gemini API properties available
  - `userservice` still needs JDBC datasource configuration available
  - this is a runtime verification gap, not a rename gap

### Frontend status

- [x] Structural frontend rename complete:
  - `fitness-frontend` -> `focus-analytics-frontend`
  - component filenames and exports renamed to `FocusSession*`
- [x] Frontend wording moved to productivity terminology:
  - labels and copy now use focus-session language in the main UI
  - activity/recommendation names still exist in API methods and routes for Phase 2 migration
- [ ] Frontend behavior still needs domain migration:
  - service calls still use `getActivities`, `addActivity`, and `getActivityDetail`
  - this is Phase 3+ work, not Phase 2
  - note: the test source filenames are now aligned with the renamed application classes.

### Phase 1: Rebrand the product

- [x] Choose final naming for the product and all major concepts.
- [x] Replace fitness wording in README, project overview, and frontend UI copy.
- [x] Standardize the domain vocabulary for user-facing text.

### Phase 2: Gateway migration

- [x] Update `gateway-service.yml`.
- [x] Rename route paths from fitness terms to productivity terms.
- [x] Update frontend API calls to the new paths.
- [x] Keep authentication and Eureka integration working.
- [x] Verify the Keycloak realm/issuer settings match the deployed identity provider.

### Phase 3: Backend domain rename

- [x] Rename activity-related models, DTOs, repositories, controllers, and services.
- [x] Rename recommendation-related models, DTOs, repositories, controllers, and services.
- [x] Update all fitness-specific messages and field names.

### Phase 4: Data model cleanup

- [x] Repurpose or rename fields that are fitness-specific.
- [x] Keep persistence shape where possible to reduce risk.
- [x] Align Mongo and PostgreSQL naming with the productivity domain.

### Phase 5: User service alignment

- [x] Keep user registration and validation.
- [x] Reframe the service as a profile or account service.
- [x] Confirm Keycloak user sync still works.

### Phase 6: Activity service behavior

- [x] Convert activity tracking to focus session tracking.
- [x] Keep user validation.
- [x] Publish focus events to Kafka.
- [x] Improve error handling if needed.

### Phase 7: AI service behavior

- [x] Rewrite Gemini prompts for productivity insights.
- [x] Convert the recommendation flow to productivity insight generation.
- [x] Persist insights properly.
- [x] Ensure Kafka consumer still deserializes correctly.

### Phase 8: Frontend migration

- [x] Replace fitness UI text with productivity UI text.
- [x] Update API paths.
- [x] Rework dashboard labels, forms, and summaries.
- [x] Add productivity-oriented visual sections.

### Phase 9: Feature upgrades

- [x] Add weekly productivity summary.
- [x] Add project or task tagging.
- [x] Add best working hours analysis.
- [x] Add distraction tracking.
- [x] Add AI-generated next-day planning.

### Phase 10: Configuration and environment

- [x] Update config files for renamed routes and terminology.
- [x] Verify all ports and local URLs.
- [x] Check environment variables for Gemini and Keycloak.

### Phase 11: Tests

- [x] Add or update backend tests for renamed endpoints and services.
- [ ] Add event-processing tests where practical.
- [x] Add frontend verification where practical.

### Phase 12: Documentation

- [x] Rewrite README for the productivity product.
- [x] Add architecture and API overview.
- [x] Add setup instructions and sample payloads.

## How To Use This File

When starting a new task, first read this file and then follow the matching phase from the backlog.

When a task is completed, update this file by marking the relevant phase or subtask as done.

If a new domain decision is made, update the mapping section here first so the rest of the codebase can stay consistent.

