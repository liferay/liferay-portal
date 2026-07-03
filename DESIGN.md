# Design — Arena Liferay Portal 7.0.6-GA7

## Overview

Security-patch fork of Liferay Portal CE 7.0.6 maintained by Axiell (Arena). Branch `arena-7.0.6-ga7`; PRs target `6.2.x`. Only security fixes are applied — no feature work.

## Repository Layout

```
portal-kernel/    Public API — kernel interfaces, model definitions, service interfaces
portal-impl/      Core implementation — services, utilities, portlets (Struts-based)
portal-web/       JSP layer, themes, Struts action classes
portal-test/      Shared test utilities
modules/
  apps/           Feature OSGi bundles (217+ apps: blogs, dl, announcements, …)
  core/           Core OSGi bundles (portal bootstrap layer)
  util/           Shared utility OSGi bundles
util-bridges/     Bridge utilities (JSF, Spring MVC, …)
util-java/        General Java utilities
util-taglib/      JSP tag library implementations
sql/              Database DDL/DML
tools/            SDK and build tooling
support-tomcat/   Tomcat-specific startup/config support
```

## Architecture

**Kernel / Impl split**: Public contracts live in `portal-kernel`; implementations in `portal-impl`. OSGi modules import `portal-kernel` only — never `portal-impl`.

**OSGi layer**: `modules/` are OSGi bundles managed by Felix, deployed to `${liferay.home}/osgi/`. Destination path is controlled by marker files in the module root:

| Marker file | Deploy target |
|---|---|
| `.lfrbuild-portal` | `osgi/portal` |
| `.lfrbuild-static` | `osgi/static` |
| `.lfrbuild-app-server-lib` | `WEB-INF/lib` |
| (none) | `osgi/modules` |

Module symbolic names starting with `com.liferay.portal.` also deploy to `osgi/portal`; all others to `osgi/modules`.

**Service Builder**: ORM/service layer generated from `service.xml` files under `modules/apps/*/`. Run `../gradlew buildService` inside a module to regenerate. DTD versions vary per module (`7_0_0` or `6_2_0`).

**Web layer**: Portal core uses Struts 1 (action classes in `portal-web/`). OSGi apps use portlets (JSP or FreeMarker).

**Build system**: Two parallel systems — Ant for portal core, Gradle for OSGi modules. They share classpath via `portal-kernel` and the Tomcat bundle.

## Building From Source

Use Java 8. This legacy branch builds with Ant for portal core and Gradle 3.3 for `modules/`; keep the Gradle Node pin at Node 6.6.0 because the bundled Liferay theme stack uses `gulp` 3 and fails on Node 12+.

**Full local build:**

```bash
ant clean all
```

**Build and deploy portal core to the local Tomcat bundle:**

```bash
ant deploy
```

The default app server location is `../bundles` relative to this repo unless overridden by `build.${user.name}.properties`.

**Build and deploy the Elasticsearch 6 integration:**

```bash
ant -f build-test-elasticsearch6.xml deploy-elasticsearch6
```

This branch only ships Elasticsearch 6 modules; there is no `portal-search-elasticsearch7` module in this tree.

**Build or deploy OSGi modules:**

```bash
cd modules
../gradlew deploy
../gradlew test
../gradlew :apps:blogs:blogs-web:deploy
```

Run module Gradle tasks from `modules/` with task paths rooted at `:apps:...`, not `:modules:apps:...`.

### Dead Liferay Repositories — Dependency Bootstrap (fixed 2026-07-06)

Liferay's public repos (`cdn.lfrs.sl`, `repository.liferay.com` Nexus) are gone — 403/404 on everything. On a fresh clone (no caches anywhere), run the bootstrap once before building:

```bash
scripts/sweep-deps.sh   # idempotent; needs curl + unzip; gh (authenticated) or python3
ant clean all
```

Verified end-to-end 2026-07-06: pristine local clone (no `.m2`, no `.gradle`, no `~/.liferay/mirrors` seed) → script → `ant clean all` BUILD SUCCESSFUL in one shot.

The script stages every artifact the dead repos used to serve:

1. **`tools/gradle-3.3.LIFERAY-PATCHED-1-bin.zip`** — the Gradle distribution the wrapper points at (`modules/gradle/wrapper/gradle-wrapper.properties` uses a relative path); not tracked in git, fetched from `github.com/liferay/liferay-binaries-cache-2017` (Liferay's official offline dependency cache).
2. **SDK bootstrap Ivy jar** → `~/.liferay/mirrors/cdn.lfrs.sl/.../org.apache.ivy/2.4.0.LIFERAY-PATCHED-1-SNAPSHOT/` — seeded under the exact dead-snapshot path `MirrorsGetTask` requests, sourced from Central's `2.4.0.LIFERAY-PATCHED-1` release (same patch). Do NOT fix this via `tools/sdk/build.properties`: `ant clean` deletes `tools/sdk` and `setUpSdk` re-extracts it from the stock SDK zip, and `MirrorsGetTask` downgrades URLs to `http://`, which Central rejects.
3. **`lib/*/dependencies.properties` oddballs** (`com.liferay:*` repackages, `sun-jaxb:jaxb-api`, `javax.jms:jms`, …) → installed into `portal/.m2` from the checked-in `lib/**.jar` files themselves.
4. **Pinned `*-LIFERAY-CACHED` Equinox artifacts** (`org.eclipse.osgi`, `osgi.services`, `equinox.metatype`, `equinox.console`) → fetched from `liferay-binaries-cache-2017`, which stores them under exactly those versions.
5. **Special sources**: `com.liferay.webjars:com.liferay.webjars.lexicon:1.0.25a` from Axiell Artifactory `ext-release-local` (anonymous read); `com.oracle.jdbc:ojdbc8:12.2.0.1` from Central's later `com.oracle.database.jdbc:ojdbc8` coordinates; `com.liferay:org.jamwiki:1.0.7` (version hidden behind a Gradle variable).
6. **Full sweep** of every literal `group:name:version` in `modules/**/build.gradle` (~890 coords) — anything not on Central is fetched from `liferay-binaries-cache-2017` into `portal/.m2` (jspwiki, portletbridge, ical4j, …).
7. **SDK `ivy.xml` dependencies** (`:ivySetUpSdk` etc.) — coordinates like `com.liferay:net.sf.jargs:1.0` declared in `tools/sdk/ivy.xml` and `tools/sdk/dependencies/*/ivy.xml`; parsed from the extracted SDK or from the SDK zip fetched off Central. Only those two path shapes — the `tools/templates/**` ivies (JSF plugin templates) are never resolved by the build and reference artifacts gone everywhere.

Poms fetched from `liferay-binaries-cache-2017` that declare a `<parent>` are replaced with minimal poms — the parent chains (e.g. ical4j → `net.modularity:modularity-parent`) resolve nowhere.

Everything lands in gitignored, `ant clean`-proof locations: `portal/.m2` (mavenLocal — first in every module's repo search order) and `~/.liferay/mirrors`. Committed repo fixes that make Central reachable at all:

- `modules/gradle.properties` — `systemProp.repository.url` points the default repo at Maven Central (all released `com.liferay` artifacts are mirrored there; the build scripts already honored this override).
- `modules/build-buildscript.gradle`, `build-portal.gradle`, `util.gradle` — added `https://plugins.gradle.org/m2` fallback (the dead Nexus also proxied the Gradle Plugin Portal, e.g. `gradle-license-report`).

**Traps (learned the hard way):**
- Do not add repositories inside the `gradle.beforeProject` block in `modules/build.gradle` — a non-empty project repo list makes the Liferay defaults plugin skip adding the default (Central) repo and breaks `:core:` module resolution. Seed `portal/.m2` instead.
- If an npm-based module fails with `Cannot find module '../lib/index'` from a `node_modules/.bin` stub, that `node_modules` was restored without symlinks — delete it and let `npmInstall` recreate it (the bootstrap script also detects and wipes these).

## Production Deployment (Maven Artifact Handoff)

This repo does not deploy to production directly. Build artifacts are published to a Maven repository, then consumed by the separate Arena application (`/opt/projects/arena-parent/4.7.x/arena-parent`) which owns the actual production deploy.

**Flow:**

1. Build the Ant bundle (`ant deploy` or equivalent) into `bundles.org` — the directory `scripts/deploy-maven-artifacts.sh` reads from (`bundles_dir=/opt/projects/liferay/portal/arena-7.0.6-ga7/bundles.org`).
2. Run `scripts/deploy-maven-artifacts.sh`. It downloads/repackages the built OSGi bundle set and portal core jars, then `mvn deploy-file`s each to Artifactory (`https://artifactory.axiell.com/artifactory/simple/ext-release-local/`), groupId `com.liferay.portal` (portal core) / `com.liferay` (theme, webjars).
3. Artifacts published, keyed by `version=7.0.6f` in the script:
   - `com.liferay.portal.osgi` (zip) — full `osgi/` tree from the bundle, minus excluded theme wars and Elasticsearch jar
   - `com.liferay.frontend.theme.unstyled` (jar, groupId `com.liferay`)
   - `com.liferay.portal.client`, `com.liferay.portal.impl`, `com.liferay.portal.kernel`, `com.liferay.support.tomcat`, `com.liferay.util.bridges`, `com.liferay.util.java`, `com.liferay.util.slf4j`, `com.liferay.util.taglib` (jars)
   - `com.liferay.portal.web` (war)
   - `com.liferay.webjars.lexicon` (jar, groupId `com.liferay.webjars`, separate `lexicon_version`)
4. The Arena 4.7.x project consumes these via `<version>` properties in its parent POM (`arena-parent/pom.xml`, e.g. `com.liferay.portal.osgi.version`, `com.liferay.portal.kernel.version`, currently pinned to `7.0.6f` — **must be bumped to match `version=` in the script after a new publish** (`/release-cut` skill automates the checklist)). `portal/build/pom.xml` in the Arena project unpacks `com.liferay.portal.osgi` into `${liferay.home}` and copies `com.liferay.portal.kernel`/`com.liferay.support.tomcat` etc. into Tomcat's `lib/ext` as part of its own build/deploy.

**When cutting a new security patch release:**
1. Bump `version=` (and `lexicon_version=` if changed) at the top of `scripts/deploy-maven-artifacts.sh`.
2. Build the bundle, run the script to publish to Artifactory.
3. In `arena-parent/pom.xml`, bump the matching `com.liferay.portal.*.version` / `com.liferay.util.*.version` / `com.liferay.support.tomcat.version` properties to the new version string.
4. Proceed with Arena's own build/deploy to push the new portal artifacts to production.

## HAProxy `/api/jsonws` Lockdown

All browser JSONWS calls go through `/api/jsonws/invoke` as `application/x-www-form-urlencoded` POST with `cmd=<json>&p_auth=<token>`. HAProxy ACLs allowlist only the services actually called by Liferay browser JS.

**Key paths:**
| What | Path |
|---|---|
| HAProxy config source (edit here) | `/opt/projects/arena-install/develop/arena-install/stage/haproxy/arena/arena.cfg` |
| HAProxy config live (deployed) | `/etc/haproxy/arena/arena.cfg` |
| HAProxy log | `/var/log/haproxy.log` |
| IP whitelist (internal bypass) | `/etc/haproxy/arena/arena-whitelist.txt` |
| Liferay browser JS source | `modules/apps/foundation/frontend-js/` in this repo |
| Arena-install project root | `/opt/projects/arena-install/develop/arena-install/` |

Deploy: `cp <source> /etc/haproxy/arena/arena.cfg && systemctl reload haproxy`

**Two call shapes:**
- Plain: `{"/service/method":{...}}` — key starts with `"/`
- Object-graph: `{"$varname = /service":{..., "$nested = /service2":{...}}}` — key starts with `"$`

**Critical encoding detail:** jQuery encodes spaces as `+` in form bodies. HAProxy `url_dec` only decodes `%XX`, not `+`. Object-graph key `"$var = /service"` arrives as `"$var+=+/service"` in the raw body. HAProxy ACL regexes must match `\+=\+` not ` = `.

**Diagnostics:** Frontend captures first 300 chars of `url_dec(cmd)` and the `X-Jsonws-Deny-Reason` response header in `/var/log/haproxy.log`. On a 403, grep for the deny reason to find the matching deny rule, then read the cmd prefix to diagnose regex mismatches.

**Internal bypass:** Requests from IPs in `/etc/haproxy/arena/arena-whitelist.txt` bypass the allowlist entirely (server-side calls, admin tools).

**When something breaks (new 403 on a Liferay feature):**
1. Open `.claude/skills/haproxy-api-lockdown/SKILL.md` or invoke `/haproxy-api-lockdown`
2. Grep the log: `grep "service-not-allowed\|403" /var/log/haproxy.log | tail -10`
3. The captured cmd prefix in the log shows the exact service being called
4. Find the matching `Liferay.Service(` call in `modules/apps/foundation/frontend-js/`
5. Add the service to `jsonws-allowed-service` (plain) or `jsonws-allowed-graph` (object-graph) in the source cfg
6. Deploy and browser-test in a loop until clean

**When locking down a different endpoint entirely:** invoke `/haproxy-api-lockdown` — the skill walks through the full design workflow (grilling → ACLs → deploy → verify loop) for any endpoint, not just JSONWS.

## Known Security Debt

See `SECURITY_REVIEW-1.md` and `SECURITY_REVIEW-2.md` for the full Aikido scan triage (the `SECURITY_REVIEW.md` this section used to point to does not exist).

Resolved (verified 2026-07-03, jar/version present in `lib/portal/dependencies.properties` and build output): XStream 1.4.21, commons-collections 3.2.2, commons-fileupload 1.6.0, commons-beanutils 1.9.4; `opensocial-portlet` shiro-core/shiro-web stripped from the WAR at build time (`modules/apps/opensocial/opensocial-portlet/build.xml`) — moot regardless of deployment status since the jars never reach the artifact. Log4j 1.x (`lib/portal/log4j.jar`) migrated to `reload4j` 1.2.26 (real jar swap + `dependencies.properties` + OSGi `system.packages.extra.bnd` version bump, verified via clean Gradle rebuild) — see `SECURITY_REVIEW-1.md` item 3.

Still open / accepted risk: Tomcat EOL; xalan suppression; Spring upgrade deferred to next major; commons-lang 2.6 / struts 1.3.10 EOL accepted with mitigations in place.

**Stale finding, not applicable:** `SECURITY_REVIEW-1.md` flags `protobuf-java` via `portal-search-elasticsearch7-impl` — that module does not exist in this tree (`modules/apps/portal-search-elasticsearch7/` is empty). This fork only ships `portal-search-elasticsearch6-impl` (built via `build-test-elasticsearch6.xml`); confirmed no jar under `bundles.org/osgi` contains any `com/google/protobuf` classes. Drop this line item from triage.

**Version caveat:** this is a legacy branch — see [@AGENTS.md](AGENTS.md) — do not cross-reference the Axiell Obsidian vault or current `arena-parent` docs when assessing what's deployed here.
