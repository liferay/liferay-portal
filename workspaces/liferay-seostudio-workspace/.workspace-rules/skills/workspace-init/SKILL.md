---

description: Bootstrap a Liferay Workspace, download and verify the bundle, set up auth verifiers, and complete first login bootstrap. Use when the user is starting fresh, when the server appears down or unreachable, when gradle.properties or bundles/ is missing, or when the user asks to set up, initialize, or scaffold a workspace.
name: workspace-init

---

# Workspace Init and Preflight Checks

Stand up a working Liferay Workspace from zero, or diagnose a workspace that looks uninitialized. Covers `blade init`, bundle download, license setup, BasicAuth verifier, server start, and first login bootstrap.

> **When to apply this skill:** the user asks for setup help, the server appears down or unreachable, or the workspace looks uninitialized (missing `bundles/`, missing `gradle.properties`). Do not run through all steps for a returning user whose server is already running.

> **Note:** These rules apply to local workspace initialization and must not be used to configure higher environments (UAT/Prod).

## Version and Tooling Check

- **Check version**: read `liferay.workspace.product` in `gradle.properties`.
- **Verify tooling**: check that `blade` (`blade --version`) and Java (`javac -version`) are installed. If missing, provide installation steps.
- **DXP License**: if `liferay.workspace.product` contains `dxp`, a license file is required. Liferay identifies license files by XML content, not filename — check `bundles/deploy/` for any `.xml` file whose root element is `<license>` or `<licenses>`. If none is found, stop and ask the user to place their license file in `bundles/deploy/` before continuing. For Docker, apply the same check to the directory mounted to the container's deploy path. Community Edition and free tier products do not require a license — skip this check for those.

## Environment Readiness and State Sync

### Configure MCP Before Starting the Server

If the Liferay MCP server is supported in your DXP version (see `skills/mcp-server/SKILL.md`), configure it **now**, before starting Liferay. CLI agents load MCP settings at startup only — configuring it after the server is already running means the agent will need to restart, costing another full server boot. Do it in this order:

1. Follow the enablement and client configuration steps in `skills/mcp-server/SKILL.md`.

1. Prompt the user to exit and restart their CLI session.

1. After restarting, verify the MCP server entry appears in your client's server list. A disconnected or failed status is expected — the server is not running yet. If the entry is absent, recheck the MCP configuration before proceeding.

1. Then continue below to start the Liferay server.

Skip this block if MCP is not supported in your DXP version.

### Tomcat

- **Initialize**: run `blade server init` if `bundles/` does not exist.
- **BasicAuth verifier (dev only — required for headless REST and MCP)**: add to `configs/local/portal-ext.properties` BEFORE first boot:

  ```
  auth.verifier.BasicAuthHeaderAuthVerifier.urls.includes=/api/*,/xmlrpc/*,/o/*
  ```

  Every headless REST endpoint and MCP `call-http-endpoint` hits `/o/*`. Without this, those calls return 403. Adding it reactively costs an extra edit + sync + restart cycle.

  **Security note**: this is for local development only. BasicAuth sends credentials in every request. For production, use OAuth2 with `OAuth2HeaderAuthVerifier` instead. Never enable BasicAuth on `/o/*` in production.

- **Configuration sync**: if `bundles/portal-ext.properties` differs from `configs/local/`, copy it: `cp configs/local/portal-ext.properties bundles/portal-ext.properties`. (This copy is destructive — see `skills/deploy-and-verify/SKILL.md` for the diff before sync rule.)

- **Skip first login bootstrap (dev only, optional)**: to avoid the mandatory browser login step on a fresh instance, add to `configs/local/portal-ext.properties` before first boot (it will be synced in the next step):

  ```
  terms.of.use.required=false
  passwords.default.policy.change.required=false
  ```

  Never use in production or staging.

- **Start server**: `blade server run` (foreground, recommended for debugging) or `blade server start` (background). These commands are Tomcat only — do not use them for Docker.

- **Login**: use `test@liferay.com` / `test` (or credentials found in `portal-ext.properties`).

### Docker

- **Locate compose file**: search the workspace for `docker-compose.yaml` or `docker-compose.yml` — its location varies by project.
- **Database**: verify the compose file defines a database service (MySQL). Docker has no embedded database; both the database and Liferay containers must be running.
- **Configuration**: check whether the compose file uses `image:` (prebuilt) or `build:` (custom image):
  - `image: liferay/dxp:...` (prebuilt) → configuration is in `liferay.env` (env vars). See `skills/feature-flags/SKILL.md` for the env var encoding.
  - `build: ...` → configuration is baked into the image via `configs/docker/`. Rebuild the image to apply changes.
- **Initial build**: before the first `docker compose up`, build and output client extensions to the volume mounted directory: `./gradlew deploy`.
- **Start**: `docker compose up` (foreground) or `docker compose up --detach` (background). Run from the directory containing the compose file.
- **Login**: use credentials defined in `liferay.env` (default: `test@liferay.com` / `test`).

## Server Verification

- **Tomcat**: watch `bundles/tomcat*/logs/catalina.out` for `Server startup in [X] ms`. Then verify `http://localhost:${PORT}` is reachable.
- **Docker**: poll the health check endpoint until it returns `200`: `curl --fail http://localhost:${PORT}/c/portal/status`. Then verify `http://localhost:${PORT}` is reachable. (Port may differ if the compose file maps a different host port.)

## First Login Bootstrap (Mandatory Before API/MCP Calls)

On a fresh Liferay instance, the default admin `test@liferay.com` is created with `passwordReset=true` and `agreedToTermsOfUse=false` in the database. Until both flags are cleared, every authenticated API call (REST, MCP `call-http-endpoint`) returns 403 — including for the Omni Admin user.

Skip this step if `terms.of.use.required=false` and `passwords.default.policy.change.required=false` were already set in `portal-ext.properties` before first boot — the flags were never set in that case.

Otherwise, prompt the user to log into `http://localhost:${PORT}` as `test@liferay.com` / `test`, accept the Terms of Use, and complete the password change (use `test` as the new password so existing credentials stay valid). Wait for their reply before making any API or MCP calls.

Do not automate the browser login flow — Liferay's login form structure varies across versions and automation is brittle.

## MCP Connection Check (When MCP Is Configured)

With the server running, verify the MCP connection using your client's built in connection test (see `skills/mcp-server/SKILL.md`). If it returns 401/403, stop and ask the user for updated credentials. If MCP tools are not visible, ensure the CLI session was restarted after configuration.

Only fall back to direct REST APIs if MCP has been configured correctly and is still returning errors. "Not yet configured" is not a valid fallback condition — configure it first.

> **Feature flags**: before starting a task, check `skills/feature-flags/SKILL.md` for any flags required by the operation.

## Guiding the User

If the user prompts for setup assistance, guide them through these steps one by one. Do not skip ahead. Explain what you are checking (e.g., "I am verifying that your Liferay server is up") and wait for processes to complete before writing code.

## Verification

- `gradle.properties` and `settings.gradle` present
- `bundles/tomcat*/` present
- HTTP request to `http://localhost:${PORT}` returns 200
- User can sign in with the default credentials
- BasicAuth verifier configured (if doing local REST/MCP work)
- First login bootstrap complete (or skipped via preboot flag settings)

## References

- Workspace docs: `https://learn.liferay.com/w/dxp/development/tooling/liferay-workspace`
- Creating a workspace: `https://learn.liferay.com/w/dxp/development/tooling/liferay-workspace/creating-a-liferay-workspace`
- Blade CLI: `https://learn.liferay.com/w/dxp/development/tooling/blade-cli`
- Course: `https://learn.liferay.com/course/mastering-liferay-workspaces-and-tooling/w-t-introduction/w-t-introduction`