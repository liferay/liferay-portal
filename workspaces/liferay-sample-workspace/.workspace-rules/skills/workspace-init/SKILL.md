---

description: Bootstrap a Liferay Workspace, download and verify the bundle, set up auth verifiers, and complete first login bootstrap. Use when the user is starting fresh, when the server appears down or unreachable, when gradle.properties or bundles/ is missing, or when the user asks to set up, initialize, or scaffold a workspace.
name: workspace-init

---

# Workspace Init

Stand up a working Liferay Workspace from zero, or diagnose a workspace that looks uninitialized. Covers `blade init`, bundle download, license setup, BasicAuth verifier, server start, and first login bootstrap.

## When to Invoke

- The user asks to set up, initialize, or scaffold a workspace, or asks for setup help.
- The server appears down or unreachable.
- The workspace looks uninitialized — missing `bundles/`, or missing `gradle.properties`.
- Do not run through all steps for a returning user whose server is already running.

> **Note:** These rules apply to local workspace initialization and must not be used to configure higher environments (UAT/Prod).

## Workflow

### Version and Tooling Check

- **Check version**: read `liferay.workspace.product` in `gradle.properties`.
- **Verify tooling**: check that `blade` (`blade --version`) and Java (`javac -version`) are installed. If missing, provide installation steps.
- **DXP License**: if `liferay.workspace.product` contains `dxp`, a license file is required. Liferay identifies license files by XML content, not filename — check `bundles/deploy/` for any `.xml` file whose root element is `<license>` or `<licenses>`. If none is found, stop and ask the user to place their license file in `bundles/deploy/` before continuing. For Docker, apply the same check to the directory mounted to the container's deploy path. Community Edition and free tier products do not require a license — skip this check for those.
- **License is consumed on boot**: Liferay registers the key into the database and deletes the file, so an empty `bundles/deploy/` on an already booted instance is the normal licensed state, not a missing license. Confirm activation from the log instead — `grep --extended-regexp --ignore-case 'license validation passed|License registered' bundles/tomcat*/logs/catalina.out`. Because the license now lives in the database, **clearing `bundles/data` also discards it**; copy a key again into `bundles/deploy/` as part of any such reset.

### Environment Readiness and State Sync

#### Configure MCP Before Starting the Server

If the Liferay MCP server is supported in your DXP version (see `skills/mcp-server/SKILL.md`), configure it **now**, before starting Liferay. CLI agents load MCP settings at startup only — configuring it after the server is already running means the agent will need to restart, costing another full server boot. Do it in this order:

1. Follow the enablement and client configuration steps in `skills/mcp-server/SKILL.md`.

1. Prompt the user to exit and restart their CLI session.

1. After restarting, verify the MCP server entry appears in your client's server list. A disconnected or failed status is expected — the server is not running yet. If the entry is absent, recheck the MCP configuration before proceeding.

1. Then continue below to start the Liferay server.

Skip this block if MCP is not supported in your DXP version.

#### Tomcat

- **Initialize**: run `blade server init` if `bundles/` does not exist.

- **Clear the seeded database (mandatory on the first start)**: `blade server init` unpacks a bundle that already carries a seeded Hypersonic database, and in it `test@liferay.com` exists with `passwordReset=true` and `agreedToTermsOfUse=false` already written to the row. Boot that database as is and every `/o/*` call returns 403 regardless of what `portal-ext.properties` contains, because the properties in the two bullets below are read only at the moment the admin is created — which, for the shipped database, already happened. Removing `bundles/data` is what makes the first start recreate the default company and the `test@liferay.com` admin from scratch, this time with those properties in effect. That recreation is the whole point of the first start — there is no separate step that creates the user, and no property that retrofits one that already exists.

  So immediately after `blade server init`, and before the first `blade server run` or `blade server start`:

  ```bash
  rm -rf bundles/data bundles/osgi/state
  ```

  `bundles/osgi/state` goes with it: the OSGi bundle cache holds resolved state keyed to the old database, and leaving it behind produces bundles wired to rows that no longer exist.

  **Only for a bundle that has never been started.** Confirm both before running the command — `bundles/logs` is empty (or absent) and nothing answers on `http://localhost:${PORT}`. On a bundle that has booted, `bundles/data` is real portal data and, for DXP, holds the registered license key; deleting it discards both. There is no property that undoes an already created admin, so an instance that is past its first boot goes through the manual login in First Login Bootstrap instead.

- **BasicAuth verifier (dev only — required for headless REST and MCP)**: add to `configs/local/portal-ext.properties` BEFORE first boot:

  ```
  auth.verifier.BasicAuthHeaderAuthVerifier.urls.includes=/api/*,/xmlrpc/*,/o/*
  ```

  Every headless REST endpoint and MCP `call-http-endpoint` hits `/o/*`. Without this, those calls return 403. Adding it reactively costs an extra edit + sync + restart cycle.

  **Security note**: this is for local development only. BasicAuth sends credentials in every request. For production, use OAuth2 with `OAuth2HeaderAuthVerifier` instead. Never enable BasicAuth on `/o/*` in production.

- **Instance and admin properties (dev only — before first boot)**: Liferay reads these only while creating the default company and its admin user, so they must be in `configs/local/portal-ext.properties` before the bundle has ever booted:

  ```
  admin.email.from.address=test@liferay.com
  admin.email.from.name=Test Test
  company.default.time.zone=UTC
  company.default.web.id=liferay.com
  default.admin.email.address.prefix=test
  passwords.default.policy.change.required=false
  setup.wizard.enabled=false
  terms.of.use.required=false
  ```

  `default.admin.email.address.prefix` and `company.default.web.id` combine into the admin login — `test` plus `liferay.com` is what makes the documented `test@liferay.com` / `test` credentials work. `setup.wizard.enabled=false`, `terms.of.use.required=false`, and `passwords.default.policy.change.required=false` remove the setup wizard, the Terms of Use screen, and the forced password change — the three blockers standing between a fresh instance and a usable admin session. `company.default.time.zone=UTC` keeps portal timestamps aligned with the UTC timestamps in `catalina.out`, which otherwise disagree with the local shell and make log correlation misleading.

  `liferay.home` belongs in the same file, and its value is the absolute path of the `bundles` directory that `blade server init` created. Resolve it rather than hardcoding it, so the file stays correct when the workspace is cloned elsewhere:

  ```bash
  echo "liferay.home=$(cd bundles && pwd)" >> configs/local/portal-ext.properties
  ```

- **Developer mode (dev only)**: `portal-developer.properties` ships inside `portal-impl.jar` and switches off the caching and minification that make source changes invisible until a restart — `theme.css.fast.load=false`, `minifier.enabled=false`, `layout.template.cache.enabled=false`, `template.engine.cache.enabled=false`, and `direct.servlet.context.reload=true`. Pull it in from `configs/local/portal-ext.properties`:

  ```
  include-and-override=portal-developer.properties
  ```

  This is an additional line, not a replacement — a workspace typically already has `include-and-override=portal-env.properties`, and repeated `include-and-override` keys are each processed rather than the last one winning (`portal-impl/src/portal.properties` declares eight of them). Keep the include lines together at the top of the file.

  Two consequences worth knowing before adding it:

  - **An included file overrides the file that includes it.** So anything `portal-developer.properties` sets wins over the same key in your `portal-ext.properties`. None of the properties above collide with it today, but a later addition can, and the losing line looks correct in the file while having no effect.

  - **It opens the OSGi console on `localhost:11311`** (`module.framework.properties.osgi.console`). That is a second port that two workspaces cannot share — the same class of conflict as 8080 described below, so check it alongside the port check if a second workspace is in play. Never enable developer mode outside local development; it disables the ETag and header filters and turns off CAPTCHA enforcement.

- **Configuration sync — before the first start**: copy the local config into the bundle: `cp configs/local/portal-ext.properties bundles/portal-ext.properties`. (This copy is destructive — see `skills/deploy-and-verify/SKILL.md` for the diff before sync rule.)

  The properties above only take effect if this copy happens before the bundle has ever booted. See First Login Bootstrap below.

- **Free port 8080 first**: every Liferay workspace defaults to 8080, so another workspace left running holds it. Run `ss -ltnp | grep ':8080 '` and **wait for the result before launching** — do not batch the check with the start command. If the port is taken, stop that instance with its own `bundles/tomcat*/bin/shutdown.sh` or move this workspace to another port. A bind conflict is easy to misread: Tomcat still logs `Server startup in [N] milliseconds` even though the connector never came up, so the boot looks fine while every request is served by the *other* instance and its separate database — which surfaces as inexplicable login or data failures. Treat `Address already in use` in `catalina.out` as a failed boot regardless of the startup line, and confirm the listening pid belongs to this bundle.

- **Start server**: `blade server run` (foreground, recommended for debugging) or `blade server start` (background). These commands are Tomcat only — do not use them for Docker. On the first start after `bundles/data` was cleared, this boot is what recreates `test@liferay.com`; expect it to take longer than a subsequent start, since Liferay is building the schema and seeding the instance.

- **Login**: use `test@liferay.com` / `test` (or credentials found in `portal-ext.properties`).

#### Docker

- **Locate compose file**: search the workspace for `docker-compose.yaml` or `docker-compose.yml` — its location varies by project.
- **Database**: verify the compose file defines a database service (MySQL). Docker has no embedded database; both the database and Liferay containers must be running.
- **Configuration**: check whether the compose file uses `image:` (prebuilt) or `build:` (custom image):
  - `image: liferay/dxp:...` (prebuilt) → configuration is in `liferay.env` (env vars). See `skills/feature-flags/SKILL.md` for the env var encoding.
  - `build: ...` → configuration is baked into the image via `configs/docker/`. Rebuild the image to apply changes.
- **Initial build**: before the first `docker compose up`, build and output client extensions to the volume mounted directory: `./gradlew deploy`.
- **Start**: `docker compose up` (foreground) or `docker compose up --detach` (background). Run from the directory containing the compose file.
- **Login**: use credentials defined in `liferay.env` (default: `test@liferay.com` / `test`).

### Server Verification

- **Tomcat**: watch `bundles/tomcat*/logs/catalina.out` for `Server startup in [X] ms`. Then verify `http://localhost:${PORT}` is reachable.
- **Docker**: poll the health check endpoint until it returns `200`: `curl --fail http://localhost:${PORT}/c/portal/status`. Then verify `http://localhost:${PORT}` is reachable. (Port may differ if the compose file maps a different host port.)

### First Login Bootstrap (Mandatory Before API/MCP Calls)

On a fresh Liferay instance, the default admin `test@liferay.com` is created with `passwordReset=true` and `agreedToTermsOfUse=false` in the database. Until both flags are cleared, every authenticated API call (REST, MCP `call-http-endpoint`) returns 403 — including for the Omni Admin user.

- **Cleared and synced before its first start**: nothing to do. The admin was created with the properties in effect, so the flags were never set.

- **Already started**: the manual login is the only way through — syncing the properties now will not clear flags already in the database.

Prompt the user to log into `http://localhost:${PORT}` as `test@liferay.com` / `test`, accept the Terms of Use, and complete the password change (use `test` as the new password so existing credentials stay valid). Wait for their reply before making any API or MCP calls.

Do not automate the browser login flow — Liferay's login form structure varies across versions and automation is brittle.

### MCP Connection Check (When MCP Is Configured)

With the server running, verify the MCP connection using your client's built in connection test (see `skills/mcp-server/SKILL.md`). If it returns 401/403, stop and ask the user for updated credentials. If MCP tools are not visible, ensure the CLI session was restarted after configuration.

Only fall back to direct REST APIs if MCP has been configured correctly and is still returning errors. "Not yet configured" is not a valid fallback condition — configure it first.

> **Feature flags**: before starting a task, check `skills/feature-flags/SKILL.md` for any flags required by the operation.

## Guiding the User

If the user prompts for setup assistance, guide them through these steps one by one. Do not skip ahead. Explain what you are checking (e.g., "I am verifying that your Liferay server is up") and wait for processes to complete before writing code.

## Success Signal

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