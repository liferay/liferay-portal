---

description: Deploy a client extension, module, or theme to the running Liferay server (Tomcat or Docker) and verify it started. Use at the end of any skill that produces deployable artifacts, when the user asks to deploy, when verifying that a previous deployment took effect, or when troubleshooting a bundle that does not appear in Liferay.
name: deploy-and-verify

---

# Deploy and Verify

Package a target, push it to the running Liferay runtime, and confirm OSGi started the bundle.

## When to Invoke

- A build skill (`scaffold-client-extension`, `scaffold-fragment`, `theme-and-design`, etc.) finishes producing source files.
- The user says "deploy", "redeploy", "push to Liferay".
- A previously deployed bundle is missing or in an inconsistent state.

## Hybrid Execution Paths

Pick the path that matches your current capabilities.

### Path A: CLI Capable Agent

Run the deploy command and verify via logs and HTTP. Continue with the Workflow below.

### Path B: Text Only Agent

Provide the user with the exact command (Tomcat: `blade gw deploy`; Docker: `./gradlew deploy "-Ddeploy.docker.container.id=$(docker compose ps --quiet liferay)"`). Ask them to paste back any lines containing `STARTED` or `Error`. For UI verification, instruct them to check the "Client Extensions" or "App Manager" Control Panel.

## Workflow

### Identify the Target

Target type drives the command:

| Target type | Detection | Tomcat command | Docker command |
| --- | --- | --- | --- |
| Client extension | `client-extension.yaml` under `client-extensions/<name>/` | `cd client-extensions/<name> && blade gw deploy` | `./gradlew deploy "-Ddeploy.docker.container.id=$(docker compose ps --quiet liferay)"` |
| OSGi module | `bnd.bnd` and `build.gradle` under `modules/<name>/` | `cd modules/<name> && blade gw deploy` | Same as client extension (run from module dir) |
| Theme | `liferay-theme.json` under `themes/<name>/` | `cd themes/<name> && blade gw deploy` | Same as client extension (run from theme dir) |
| Fragment collection (file based) | `fragments/group/<collection>/` under a site initializer | Deploys via the parent `siteInitializer` client extension | Same |

When the target lives across multiple modules (siteInitializer with batch + layouts + fragments), deploy from the client extension root.

**Docker note**: do NOT use `docker cp` or `docker exec` for deployment. The Gradle deploy targets the running container directly via `-Ddeploy.docker.container.id`. The container must be running and healthy first.

**Initial Docker startup** (first run only): before the first `docker compose up`, build artifacts so they are present when the container initializes:

```
./gradlew deploy
docker compose up
```

The workspace Gradle plugin outputs client extensions to a docker build directory, which docker-compose mounts into the container at `/opt/liferay/osgi/client-extensions`. After the container is up, switch to iterative deploys above.

### Run the Deploy

Always invoke through Blade for Tomcat (`blade gw deploy`) or Gradle for Docker. Capture the build output. A failure here is a build problem; surface the message and stop.

**Parallel execution**: run the deploy command as a nonblocking background process so log watching can happen concurrently. Use whatever background execution mechanism your tool provides.

### Tail for STARTED

Watch the log until either of these markers appears:

- `STARTED <bundle-symbolic-name>_<version>` — success (for a `batch` or `siteInitializer` CET, only that the bundle started: see below)
- `Error processing <path>` or stack trace mentioning the bundle — failure

For Tomcat, the log is `bundles/logs/liferay.<YYYY-MM-DD>.log`. For Docker, use `docker compose logs --follow liferay`.

**catalina.out caveat (Tomcat only)**: `bundles/tomcat*/logs/catalina.out` is append only across reboots — a previous boot's `Server startup in` line will false positive on a naive grep. Bookmark the line count before booting and check only lines added after:

```bash
START_LINE=$(wc --lines < bundles/tomcat*/logs/catalina.out 2>/dev/null || echo 0)
blade server start
until tail --lines +$(( ${START_LINE} + 1 )) bundles/tomcat*/logs/catalina.out 2>/dev/null \
      | grep --quiet "Server startup in"; do sleep 3; done
```

Use `until grep` rather than `tail --follow=name --retry` polling.

The bundle symbolic name comes from `Bundle-SymbolicName` in `bnd.bnd`. A client extension project's bundle is named after the **project directory with its dashes removed**, not after an entry key: a project `acme-theme` logs `STARTED acmetheme_7.4.13` (seen on 2026.Q2.7 with workspace plugin 17.1.5), so a barrier grepping for `STARTED acme-theme` never matches. For a frontend CX, also wait for `/o/<project directory>/` to answer `200`: that is when its CETs are registered.

**For a `batch` or `siteInitializer` CET, `STARTED` is not the end of its work.** Wait for what the bundle does after it starts:

- **`batch`:** each file logs `Started batch engine import task <id>` and `Finished batch engine import task <id> in N ms` (`BatchEngineImportTaskExecutorImpl`). On a **redeploy** the bundle logs `STARTED` first and the tasks run seconds later, after its OAuth application's configuration event; on a first deploy they ran before `STARTED`. So wait until as many tasks have finished, since your own deploy, as there are batch files, then read each task's `executeStatus` (`GET /o/headless-batch-engine/v1.0/import-task/{id}`): "Finished" is logged for a failed task too, and a failed file does not stop the later ones. Measured on 2026.Q2.7.
- **`siteInitializer`:** wait for `Initialized <siteName> for group <id> in N ms` (`BundleSiteInitializer`), taking the log mark just before your own deploy, one initializer at a time (`rules/site-initializer-format.md`).

### Smoke Check

After STARTED:

- **Custom element / iframe / globalCSS / globalJS**: hit a page that hosts the resource and confirm three things — the network panel loads the asset (HTTP `200 OK`), the page HTML actually contains a `<script>` or `<link>` tag referencing the CE asset, and API calls from the widget still succeed when the page is viewed as a nonadmin user (catches Guest visibility gaps).
- **Theme**: visit a site using the theme and confirm the styles applied.
- **Object action / workflow action / notification type / object validation rule / object entry manager**: visit Control Panel and confirm the entry appears in the relevant admin panel (Objects → Actions, Workflow → Definitions, etc.).
- **Batch / site initializer**: verify the configured site or data exists by listing it via REST (e.g. `GET /o/headless-admin-site/v1.0/sites`).

For Headless APIs and Client Extension assets, success means HTTP `200 OK` on the resource URL — not just a successful deploy exit code.

### Troubleshoot

When STARTED never appears:

- **Tomcat down**: probe `http://localhost:${PORT}`. If down, start it.
- **Bundle in `INSTALLED` state but not `ACTIVE`**: open a Gogo shell with `telnet localhost 11311` and run `lb | grep <bsn>` to see the wired state, then `diag <id>` to read the resolution error.
  - **In a Docker container whose 11311 is not published**, script it with the container's own `telnet`, which answers Gogo's option negotiation: `docker exec <container> sh -c '(sleep 3; printf "lb -s <bsn>\r\n"; sleep 4; printf "disconnect\r\n"; sleep 1; printf "y\r\n") | telnet localhost 11311'`. A bash `/dev/tcp/localhost/11311` socket gets the echo of the command and never its output (verified on a 2026.Q2.7 image).
  - **A core bundle replaced at boot logs no `STARTED` line.** A patched `osgi/portal` jar shows `STOPPED …_<old>` / `STARTED …_<new>` when hot-swapped, but after a container restart it starts silently with the framework, so check `lb -s <bsn>` (state `Active`, the patched version) rather than grepping the log.
- **Stale cache**: stop Tomcat, delete `bundles/osgi/state/` and `work/`, start Tomcat. This is a restart under `liferay-rules.md` → "Server Restarts".
- **Missing dependency**: read `bnd.bnd` `Import-Package` against the OSGi registry.
- **`STARTED` appears, yet the module does nothing: an optional import that missed at resolve time.** A package imported `resolution:=optional` is wired only if its provider is already installed when the bundle resolves, and stays unwired after the provider arrives until the bundle is refreshed. Then any DS component with a field of that package's type cannot be built: SCR logs `Field [_x] not found` for **every** field of the class, and a JAX-RS application holding it is not registered (its path answers `404`). Verified on 2026.Q2.7: a fresh build deployed a bridge module before the module exporting its API (deploy order is alphabetical by project), `inspect requirement osgi.wiring.package <bsn>` listed no provider for that package, and a later hot redeploy of the bridge wired it. When the module is useless without the package anyway, import it as mandatory, with a mandatory `@Reference`: the bundle then resolves whenever the provider is installed, in either order (FileInstall resolves waiting bundles), and is refreshed with it on a redeploy. Measured on a second fresh build with the same order: it started after the provider, wired.
- **A module's log lines never appear.** The base `portal-log4j.xml` sets `<Logger level="ERROR" name="com.liferay" />`. So a custom module whose packages start with `com.liferay.` logs nothing below ERROR: its `_log.warn` and `_log.info` lines are dropped, and `isInfoEnabled()` is false (verified on 2026.Q2.7: a probe's WARN lines were missing while its `System.out` lines showed). Choices:
  - log what must be seen at ERROR;
  - add a `<Logger level="INFO" name="<your package>" />` to `META-INF/portal-log4j-ext.xml` on the portal classpath, which is read only at startup;
  - set the level for the session in Server Administration → Log Levels.

  To debug, check by effect, for example a field the module writes, rather than by its log.

## MultiCET Deployment Sequencing

When deploying multiple client extensions in the same cycle, deploy in this order so each later layer can resolve everything it depends on:

1. **Branding / CSS CETs** (`themeCSS`, `themeFavicon`, `globalCSS`) — must be live before any markup references them.

1. **Data layer Batch CETs** (`batch`) — Object Definitions and seed data must exist before pages or widgets reference them.

1. **Custom Element CETs** (`customElement`) — must be `STARTED` before a Site Initializer tries to place them on a page.

1. **Site Initializers** (`siteInitializer`) — last, after every CET they depend on is verified active.

**Site Initializer timing pitfall.** Site Initializers place Custom Element widgets onto pages during deploy. If a Custom Element CET is not yet `STARTED` when the Site Initializer runs, the widget placement is **silently skipped**: `catalina.out` shows a `Cannot invoke` warning and the initializer reports success anyway. If widgets are missing on the provisioned site, redeploy the Site Initializer once every referenced CE shows `STARTED` in the log. Treat any `Cannot invoke` line in `catalina.out` during initializer deploy as a hard failure even when the overall task reports success.

## Starting and Stopping the Liferay Server

**Tomcat only**: `blade server run` (foreground, real time logs) or `blade server start` (background). Prefer `blade server run` when debugging — `blade server start` can silently exit without console output. These commands do **not** work for Docker.

**Docker**: `docker compose up` (foreground) or `docker compose up --detach` (background). Use `docker compose down` to stop.

## Environment Specifics

- **Tomcat**: if `configs/local/portal-ext.properties` changed, copy it to `bundles/portal-ext.properties` and restart the server, following `liferay-rules.md` → "Server Restarts". Do NOT use `blade gw initBundle` — it deletes the entire bundle directory.
- **Docker (prebuilt image)**: configure via `liferay.env` environment variables. Changes require a container restart (`docker compose down && docker compose up`), under the same restart policy.
- **Docker (custom image)**: if `docker-compose.yaml` uses `build:` instead of `image:`, configuration files in `configs/docker/` are baked into the image by the workspace Gradle plugin. Rebuild the image to apply changes.

## portal-ext.properties Sync (Tomcat)

Copying `configs/local/portal-ext.properties` to `bundles/portal-ext.properties` is destructive. If any property exists only in `bundles/portal-ext.properties` (e.g. added via the portal UI or a manual runtime edit), a blind copy silently deletes it. Diff the two files before syncing and migrate any `bundles/`-only properties to `configs/local/` first. Verify the property is honored at runtime after restart — matching file contents is not the same as runtime activation.

## Success Signal

TODO / inferred — verify against a running bundle. The Tail for STARTED and Smoke Check steps above are the observable completion checks; confirm on a live bundle.

## References

- Blade CLI: `https://learn.liferay.com/w/dxp/development/tooling/blade-cli`
- Client extensions: `https://learn.liferay.com/w/dxp/development/client-extensions`