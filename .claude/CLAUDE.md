# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build              # full build with tests
./gradlew build -x test      # skip tests

# Run (default profile is prod; use dev locally)
./gradlew bootRun --args='--spring.profiles.active=dev'

# Test
./gradlew test
./gradlew test --tests "com.ssu.ongi.<package>.<TestClassName>"
```

## Architecture

Package root: `com.ssu.ongi`

```
common/
  base/          # BaseEntity (createdAt/updatedAt via JPA auditing), BaseStatus
  config/        # SecurityConfig, MqttConfig, FirebaseConfig, RedisConfig, SshTunnelConfig
  exception/     # GeneralException + GeneralExceptionAdvice (centralized error handler)
  filter/        # DeviceAuthFilter (ESP32 device-token auth)
  jwt/           # JwtAuthenticationFilter, JwtTokenProvider, TokenCommandService, RefreshTokenRepository
  mqtt/          # MqttPublisher, DeviceTopic (topic name helpers)
  response/      # ApiResponse<T> wrapper, PageResponse
  sms/           # SmsService interface, CoolSmsService (prod), MockSmsService (dev)
  status/        # ErrorStatus, SuccessStatus enums

domain/          # Feature modules; each follows controller → service → repository layering
  auth/          # Signup, login, logout, token reissue, phone verification
  member/        # Member (guardian) CRUD, FCM token, password update, withdrawal
  elder/         # Elder (care recipient) management per member
  device/        # IoT device registration, heartbeat, slot management
  medicine/      # Medicine schedule registration, medication record sync
  health/        # Health check endpoint only
```

**Request flow**: `JwtAuthenticationFilter` → `DeviceAuthFilter` → Controller → Service → Repository

- `JwtAuthenticationFilter`: validates `Authorization: Bearer <token>`, sets `MemberPrincipal` (memberId + loginMode) in `SecurityContext`.
- `DeviceAuthFilter`: runs only on ESP32-only paths (`/api/device/heartbeat`, `/api/device/medication-status`). Validates `Device-Token` header and sets `deviceId` as a request attribute.

**Authentication modes** (`LoginMode` enum): `GUARDIAN` (full access) vs `ELDER` (restricted — no logout, no member-only features). Both share the same JWT flow; `loginMode` is embedded in the access token claim.

**Token flow** (`TokenCommandService`): Login → delete old refresh token → issue new pair (RTR pattern). Reissue validates the stored token against the incoming one — mismatch triggers forced logout.

**API responses** always use `ApiResponse<T>` with `isSuccess`, `code`, `message`, `data` fields.

**Error handling** is centralized in `GeneralExceptionAdvice`. Add new error codes to `ErrorStatus`, new success codes to `SuccessStatus`.

**Swagger docs**: Swagger annotations go on the `*ControllerDocs` interface, not the controller class.

## Database & Profiles

- **dev profile**: MySQL tunneled via SSH to RDS (`SshTunnelConfig` opens a local tunnel on port 13306). Run with `--spring.profiles.active=dev`.
- **prod profile**: direct RDS connection via env vars (`${DB_URL}`, `${DB_USERNAME}`, etc.).
- `ddl-auto: update` on dev, `validate` on prod.
- `Member` uses `@SQLRestriction("deleted_at IS NULL")` for soft delete — always query through the repository (never raw JPQL that bypasses this filter). Soft delete via `Member.softDelete()` only; never call `memberRepository.delete()` directly.
- `MedicationRecord` has a unique constraint on `(medicine_id, recorded_at)`.

## MQTT

`MqttPublisher` sends commands to the ESP32 device. Topic patterns are defined in `DeviceTopic`:
- `device/{deviceToken}/command/open-all`
- `device/{deviceToken}/command/close-all`
- `device/{deviceToken}/command/schedule-updated`

Device token (`UUID`) is auto-generated on `Device.create()` and never rotated.

## SMS (Phone Verification)

`SmsService` has two implementations: `CoolSmsService` (prod, via CoolSMS SDK) and `MockSmsService` (dev, logs the code instead of sending). Verification codes are stored in Redis with TTL via `PhoneVerificationRepository`.

## Public paths (no JWT required)

`/api/auth/**` signup/login/check-id/find-id/password/reissue/phone/*, `/actuator/health`, `/api/health`, Swagger routes, and the device-only paths.

## CQRS Split

Services are split into `*CommandService` (writes, `@Transactional`) and `*QueryService` (reads, `@Transactional(readOnly = true)`). Complex queries live in `*QueryRepository` classes (JPQL / QueryDSL) alongside simple Spring Data JPA repositories.

---

## Project Overview

This is a backend server project.

The assistant should prioritize consistency with the existing codebase over introducing new patterns. Avoid unnecessary large-scale refactoring and focus on minimal, clear changes.

---

## Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA
- Redis
- MySQL
- Docker
- GitHub Actions
- AWS (EC2, RDS, S3)

---

## General Rules

- Do not perform unnecessary large-scale refactoring.
- Preserve existing package structure and naming conventions.
- Prefer small, focused changes.
- Explain the reason when modifying existing logic.
- Do not remove validation, exception handling, or security logic unless explicitly requested.
- Do not hardcode secrets, tokens, or private keys.

---

## Architecture Rules

- Controller: handles HTTP request/response only.
- Service: contains business logic.
- Repository: handles database access only.
- Do not expose Entity directly in API responses.
- Separate Request DTO and Response DTO.
- Do not put business logic in DTOs.
- Entity should protect its own state.

---

## Service Layer Rules

- Use `@Transactional` at the service layer.
- Use `@Transactional(readOnly = true)` for read operations.
- Do not use transactions in controllers.
- Avoid calling external APIs inside long transactions.
- Keep methods small and focused.

---

## Repository Rules

- Use Spring Data JPA for simple queries.
- Use JPQL or QueryDSL for complex queries.
- Prevent N+1 problems.
- Use fetch join when necessary.
- Avoid unnecessary entity loading.

---

## Error Handling Rules

- Use custom exceptions.
- Maintain consistent API response format.
- Do not expose internal error details.
- Log useful debugging information without sensitive data.

---

## Security Rules

- Follow existing Spring Security configuration.
- Handle JWT validation in filters.
- Do not bypass authentication or authorization.
- Never trust client input.
- Validate request DTOs.
- Do not log tokens or passwords.

---

## API Design Rules

- Follow RESTful principles.
- Use proper HTTP methods:
  - GET: read
  - POST: create/action
  - PATCH: partial update
  - PUT: full update
  - DELETE: delete
- Maintain consistent response structure.
- Use pagination for list endpoints.

---

## DTO Rules

- Request DTO: validate input.
- Response DTO: return only necessary fields.
- Do not use Entity as DTO.
- Use static factory methods like `from()` or `of()` when appropriate.

---

## Entity Rules

- Avoid excessive use of setters.
- Use meaningful methods for state changes.
- Keep JPA no-args constructor.
- Encapsulate domain logic inside Entity.

---

## Redis Rules

- Use for cache, tokens, or temporary data.
- Always set TTL.
- Do not store sensitive data in plain text.
- Prefer hashed tokens if possible.

---

## Logging Rules

- Log important failures.
- Avoid excessive logging in normal flow.
- Do not log sensitive information.

---

## Testing Rules

- Add tests when modifying business logic.
- Prefer unit tests for services.
- Prefer integration tests for repositories.
- Cover both success and failure cases.

---

## Code Style

- Write clean and readable code.
- Prefer early return over deep nesting.
- Avoid duplication.
- Use meaningful variable names.

---

## Git Rules

- Do not modify unrelated files.
- Keep changes scoped to the task.
- Clearly explain modified parts.

---

## Decision Guidelines

- Analyze existing code first.
- Follow current project patterns.
- Choose the least invasive solution.
- Ask before introducing major changes.

---

## Preferred Backend Style

- Follow clean layered architecture.
- Use CQRS pattern when applicable.
- Separate Query and Command services.
- Do not access repositories directly from controllers.
- Use authenticated user info from security context.
- Keep transaction boundaries clear.

---

## Commit Message Rules

- Use only the following types:
  - feat
  - fix
  - chore
  - refactor
  - deploy

- Format:
  - `<type>: <message>`

- The message must be:
  - Written in Korean
  - A single short sentence
  - Simple and clear

- Type meanings:
  - feat: 새로운 기능 추가
  - fix: 에러 또는 버그 수정
  - chore: 설정, 환경 구성, 기타 작업
  - refactor: 코드 구조 개선 또는 리팩토링
  - deploy: 배포 관련 작업

- Writing rules:
  - Do not write in English
  - Do not use multiple lines
  - Do not add bullet points
  - Do not add detailed descriptions
  - Keep it concise (one line only)

- Examples:
  - feat: 로그인 API 추가
  - fix: 토큰 검증 오류 수정
  - chore: Redis 설정 추가
  - refactor: UserService 로직 개선
  - deploy: 운영 서버 배포 설정 변경