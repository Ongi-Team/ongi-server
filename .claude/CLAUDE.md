# CLAUDE.md

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