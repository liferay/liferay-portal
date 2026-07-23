# Periodic Cryptographic Health Verification — On-Demand Endpoint

**Ticket:** LPD-93272 (reduced scope)
**Branch:** `LPD-93272-temp`
**Date:** 2026-07-23

## Background

Long-running instances need a way to re-verify the crypto subsystem without a
restart. The validated FIPS provider runs its self-tests at startup; Liferay DXP
needs an authenticated REST endpoint to re-trigger them on demand, callable by a
human Crypto Officer or by an external scheduler.

LPD-93272 originally carried a third acceptance criterion covering the failure
path (stop crypto operations, transition to Error State, emit a critical FIPS
audit event). That criterion is being split into a separate story that depends
on LPD-93276 ("Behavior in Error State"). This spec covers only the reduced
scope: the on-demand re-verification endpoint that runs the provider self-tests
and reports the outcome, with **no** Error State transition, **no** crypto halt,
and **no** audit event.

## Goal

Provide an authenticated REST endpoint that triggers the validated provider's
self-tests on demand and returns the result, with no runtime side effects.

## Acceptance Criteria (in scope)

1. An authenticated REST endpoint is exposed for the Crypto Officer to trigger
   provider self-test re-verification on demand.

1. The endpoint is also callable by an external scheduler (AWS EventBridge, GCP
   Cloud Scheduler, cron) without an active Crypto Officer session, authenticated
   via NPE (OAuth2 client-credentials) credentials.

The Crypto Officer is modeled as a dedicated Liferay regular role.

## Out of Scope (moved to the split story, depends on LPD-93276)

- Stopping crypto operations on failure.
- Transitioning to Error State.
- Emitting the critical `periodic-health-failure` audit event.
- Verification in the FIPS CI environment (LPD-80674).

## Architecture

### Kernel — side-effect-free self-test (`portal-kernel`)

- `FIPSHealthCheckResult` — immutable value type. `enum Status { FAILED, HEALTHY,
  NOT_APPLICABLE }`; carries provider name, failed test, FIPS state, and provider
  message. Factories: `healthy(String)`, `failed(String, String, String, String)`,
  `notApplicable()`.
- `FIPSSelfTestException` — thrown by an executor when a self-test fails; carries
  provider name, failed test, FIPS state, and message.
- `FIPSSelfTestExecutor` — seam interface: `String execute() throws Exception`
  (returns the provider name on success).
- `ReflectionFIPSSelfTestExecutor` — reflective implementation. Drives BCFIPS
  (`FipsStatus.runSelfTests()`, `FipsStatus.isReady()`,
  `CryptoServicesRegistrar.isInApprovedOnlyMode()`) and Amazon Corretto
  (`assertHealthy()`) without a compile-time dependency on the provider jars.
  Fails closed: any reflective or provider error becomes a `FIPSSelfTestException`.
- `FIPSModeValidator.runSelfTests()` — new static entry point. Returns
  `NOT_APPLICABLE` when FIPS is disabled; otherwise delegates to the executor and
  returns `HEALTHY` or `FAILED`. **No** error-state flag, **no** guard in
  `validateAlgorithm`/`validateKey`, **no** `isInErrorState()`. Running the tests
  has no effect on subsequent crypto operations.

### REST module cluster (`modules/apps/portal-security`)

Four sibling modules: `portal-security-fips-rest-{api,impl,client,test}`.
Application baseURI `/crypto-health`, package
`com.liferay.portal.security.fips.rest`.

- **Endpoint:** `POST /o/crypto-health/v1.0/health-verifications`.
- **DTO `HealthVerification`:** `status`, `providerName`, `failedTest`,
  `fipsState`, `providerMessage`, `date`.
- **Authorization (Crypto Officer role):** `HealthVerificationResourceImpl`
  allows the call when the permission checker is an omniadmin, or when
  `RoleLocalService.hasUserRole(userId, companyId, "Crypto Officer", true)` is
  true. Otherwise it throws `PrincipalException.MustHavePermission`, which the
  Vulcan `PrincipalExceptionMapper` renders as 403 on POST.
- **Crypto Officer role provisioning:** a `PortalInstanceLifecycleListener`
  creates the "Crypto Officer" regular role per company on startup.
- **Response mapping:**
  - `HEALTHY` → 200 with `{status, providerName}`.
  - `NOT_APPLICABLE` (FIPS disabled) → 409.
  - `FAILED` → 503 with `{status, providerName, failedTest, fipsState,
    providerMessage}`.
  - No audit routing, no `AuditRouter` reference, no crypto halt.

## Scheduler Authentication (AC2)

An external scheduler authenticates through Liferay's OAuth2 **client-credentials**
grant — machine-to-machine, no session:

1. Register an OAuth2 application (headless/service type) with the Client
   Credentials grant enabled. Liferay issues a client ID and client secret and
   ties the application to a service-account user (the NPE).

1. The scheduler requests a bearer token from the token endpoint
   (`/o/oauth2/token`) with `grant_type=client_credentials`.

1. It calls the endpoint with `Authorization: Bearer <token>`. Liferay resolves
   the token to the service-account user and runs the request as that user.

Because authorization gates on Crypto Officer role membership, the OAuth2
application's service-account user must itself hold the Crypto Officer role (or
be an omniadmin). The same check therefore covers both a human Crypto Officer
session and the NPE scheduler — the scheduler is a Crypto Officer that happens to
be a service account, so AC2 needs no separate code path.

## Data Flow

1. Caller (Crypto Officer session or NPE client-credentials token) issues
   `POST /o/crypto-health/v1.0/health-verifications`.

1. Resource checks authorization (omniadmin or Crypto Officer role membership).

1. Resource calls `FIPSModeValidator.runSelfTests()`.

1. Kernel returns a `FIPSHealthCheckResult`.

1. Resource maps the result to the HTTP response (200 / 409 / 503) and returns
   the DTO.

## Error Handling

- Unauthorized caller → `PrincipalException.MustHavePermission` → 403.
- FIPS disabled → 409 (`NOT_APPLICABLE`); the self-test is not meaningful.
- Self-test failure (including reflective/provider errors) → `FIPSHealthCheckResult`
  with `FAILED`; resource returns 503 with the failure detail. The executor fails
  closed, so an unexpected error still surfaces as `FAILED`, never as a false
  `HEALTHY`.

## Testing

- **Kernel unit tests:** `runSelfTests()` for HEALTHY, FAILED, NOT_APPLICABLE, and
  fail-closed on unexpected error; `FIPSHealthCheckResult` factories.
- **Impl unit tests:** resource 200/409/503 status mapping and 403 authorization.
- **Integration tests:** endpoint auth — 403 for an unauthorized caller, 409 for
  an authorized caller with FIPS disabled.

## Conventions

- Commits are prefixed with `LPD-93272` (from the branch name).
- Tests live in their own commits, separate from production code.
- Language keys are added to the global `Language.properties`; the generated
  `Language_<locale>.properties` files land in a separate `buildLang` commit.