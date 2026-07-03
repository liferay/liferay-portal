# Aikido Security Remediation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remediate or formally triage all 49 Aikido findings from `SECURITY_REVIEW-2.md` on branch `feature/security-patch`.

**Architecture:** Three work streams — (1) code/dependency fixes in this repo, (2) HAProxy hardening rules in the arena-install repo with deploy-and-verify loop, (3) Aikido dashboard triage (suppressions with justification). Ordered by the priority list in `SECURITY_REVIEW-2.md` §Priority Order.

**Tech Stack:** Ant (portal core, Java 8), Gradle 3.3 (`modules/`, com.liferay.node plugin), HAProxy, Aikido dashboard.

**Current Status (verified 2026-07-03):** Tasks 1 and 3 are complete and committed: Task 1 `df331ab9ac42d` (TunnelUtil bypass removed), Task 3 `1d4f6caf9ec6` (SDK `groovy-all.jar` 2.4.21). Task 2 was attempted in `1c7742d5fb79c` but rejected after `ant clean all` exposed legacy theme `gulp` 3 incompatibility with Node 18; `modules/build.gradle:138` is rolled back to Node `6.6.0` to keep the release build green. Continue at Task 4 unless explicitly auditing completed work.

## Global Constraints

- Branch: `feature/security-patch`. Security fixes only — no feature work, no refactors beyond the fix.
- Source max line length: 80 chars (`source.formatter.max.line.length=80`).
- Git: **stage only, never commit without explicit user approval** (user global rule overrides commit steps below — every "Commit" step means: stage the listed files, then ask the user).
- HAProxy config source of truth: `/opt/projects/arena-install/develop/arena-install/stage/haproxy/arena/arena.cfg`. Deploy = `cp <source> /etc/haproxy/arena/arena.cfg && systemctl reload haproxy`.
- Aikido dashboard actions are manual browser steps (repo ID 2406495) — record each suppression justification verbatim in `SECURITY_REVIEW-2.md`.
- Verified facts (2026-07-03): `portal-impl/src/portal.properties:5887` already defaults `TunnelUtil.verify.ssl.hostname=true`; `.idea/` is NOT git-tracked and `/.idea` is already in `.gitignore:27`; Node pin is `modules/build.gradle:138`; groovy-all.jar manifest confirms 2.0.1; none of the proposed HAProxy hardening ACLs exist yet in `arena.cfg` (159 lines, arena-install HEAD `f533bda5`); already fixed in tree per `lib/portal/dependencies.properties` and opensocial `build.xml`: xstream 1.4.21, commons-collections 3.2.2, commons-fileupload 1.6.0, beanutils 1.9.4, commons-io 2.15.1, json 20240303, xercesImpl 2.12.2, shiro jars stripped from opensocial WAR.

---

### Task 1: TunnelUtil TLS hostname-verification bypass — hard-remove

**Status: DONE** — committed as `df331ab9ac42d`; code re-verified in the main checkout on 2026-07-03. `TunnelUtil.java` no longer contains `_VERIFY_SSL_HOSTNAME`, `HostnameVerifier`, `HttpsURLConnection`, or `SSLSession`; `portal.properties` no longer defines the property. `portal_ja.properties` still contains a localized copy of the now-dead property text; it is inert after the code removal but may be removed in a doc-cleanup pass if desired.

Priority 1 in the review. The bypass block installs an always-true `HostnameVerifier` when the property is `false`. The shipped default is already `true` (`portal-impl/src/portal.properties:5887`), so removing the bypass changes nothing for correctly configured systems and closes the misconfiguration hole permanently.

**Files:**
- Modify: `portal-kernel/src/com/liferay/portal/kernel/service/http/TunnelUtil.java:122-137` (bypass block) and `:148-149` (`_VERIFY_SSL_HOSTNAME` constant)
- Modify: `portal-impl/src/portal.properties:5884-5887` (remove the now-dead property and its comment block)

**Interfaces:**
- Consumes: nothing from other tasks.
- Produces: nothing other tasks rely on. `TunnelUtil._getConnection()` keeps its signature; only the conditional bypass disappears.

- [x] **Step 1: Confirm current state**

Run: `grep -n "_VERIFY_SSL_HOSTNAME" portal-kernel/src/com/liferay/portal/kernel/service/http/TunnelUtil.java`
Expected: two hits — the `if (!_VERIFY_SSL_HOSTNAME && ...)` block and the constant declaration.

- [x] **Step 2: Remove the bypass block**

In `TunnelUtil.java`, delete this entire block (inside `_getConnection`, right after `httpURLConnection.setDoOutput(true);`):

```java
		if (!_VERIFY_SSL_HOSTNAME &&
			(httpURLConnection instanceof HttpsURLConnection)) {

			HttpsURLConnection httpsURLConnection =
				(HttpsURLConnection)httpURLConnection;

			httpsURLConnection.setHostnameVerifier(
				new HostnameVerifier() {

					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}

				});
		}
```

- [x] **Step 3: Remove the dead constant and unused imports**

Delete:

```java
	private static final boolean _VERIFY_SSL_HOSTNAME = GetterUtil.getBoolean(
		PropsUtil.get(TunnelUtil.class.getName() + ".verify.ssl.hostname"));
```

Then remove imports that are now unused. Check each before removing (they may be used elsewhere in the file):

Run: `grep -n "HostnameVerifier\|HttpsURLConnection\|SSLSession\|GetterUtil\|PropsUtil" portal-kernel/src/com/liferay/portal/kernel/service/http/TunnelUtil.java`

Remove the `import javax.net.ssl.HostnameVerifier;`, `import javax.net.ssl.HttpsURLConnection;`, `import javax.net.ssl.SSLSession;` lines if their only uses were in the deleted block; same test for `GetterUtil`/`PropsUtil` imports.

- [x] **Step 4: Remove the property from portal.properties**

In `portal-impl/src/portal.properties` around line 5884-5887, delete the property line and its immediately preceding comment block:

```properties
    #
    # Set this property to true to verify the SSL hostname when tunneling.
    #
    com.liferay.portal.kernel.service.http.TunnelUtil.verify.ssl.hostname=true
```

(Match the actual comment text in the file — delete the whole stanza for this one property, nothing around it.)

- [x] **Step 5: Compile portal core**

Run: `ant compile`
Expected: BUILD SUCCESSFUL. If `TunnelUtil` fails on a missing import you removed, restore that import.

- [x] **Step 6: Stage and request commit approval**

```bash
git add portal-kernel/src/com/liferay/portal/kernel/service/http/TunnelUtil.java portal-impl/src/portal.properties
```

Ask user to approve commit: `sec: remove TunnelUtil TLS hostname-verification bypass`

- [ ] **Step 7: Production config check (ops, informational)**

Ask the user to confirm production `portal-ext.properties` does not set `com.liferay.portal.kernel.service.http.TunnelUtil.verify.ssl.hostname=false`. After this task deploys, the override becomes inert either way — this check is to know whether any cluster node was relying on the bypass (self-signed certs between nodes), which would surface as tunnel-auth SSL errors after deploy.

---

### Task 2: Node.js toolchain upgrade 6.6.0 → 18 LTS

**Status: REJECTED / TRIAGE ONLY** — initially committed as `1c7742d5fb79c`, then rolled back in the working tree on 2026-07-03 because `ant clean all` fails in legacy theme modules. Root cause: `frontend-theme-fjord` and sibling themes use `gulp` 3 / `vinyl-fs` 0.3 / `graceful-fs` 3, which crashes on Node 12+ with `ReferenceError: primordials is not defined`. Node `14.21.3`, `16.20.2`, and `18.20.4` are therefore not viable without a broad theme-toolchain migration. Current accepted state: keep `modules/build.gradle:138` at `nodeVersion = "6.6.0"`; handle nodejs/zlib/openssl/v8/c-ares as build-tool-only Aikido triage items.

Priority 2. One change resolves five findings: nodejs EOL, zlib 1.2.8 (6 CVEs incl. CVE-2022-37434), openssl 1.0.x, v8 5.x, c-ares. Build-environment only — nothing deploys to the portal.

**Files:**
- Modify: `modules/build.gradle:135-140` (the `com.liferay.node` plugin block)

**Interfaces:**
- Consumes: nothing.
- Produces: no code fix. Task 7 must accept/triage the five Node-related findings instead of marking them resolved.

- [x] **Step 1: Record the current failing state**

Run: `grep -n "nodeVersion" modules/build.gradle`
Expected: `138:				nodeVersion = "6.6.0"`

- [x] **Step 2: Bump the version (attempted, then rolled back)**

In `modules/build.gradle`, change:

```gradle
		pluginManager.withPlugin("com.liferay.node") {
			node {
				global = false
				nodeVersion = "6.6.0"
			}
```

to:

```gradle
		pluginManager.withPlugin("com.liferay.node") {
			node {
				global = false
				nodeVersion = "18.20.4"
			}
```

- [x] **Step 3: Verify a representative frontend module builds**

Run from `modules/` after removing stale module build output. The Gradle task can otherwise falsely pass with an old `build/node` from Node 6.6.0:
```bash
rm -rf apps/foundation/frontend-js/frontend-js-web/build
../gradlew :apps:foundation:frontend-js:frontend-js-web:deploy --stacktrace
apps/foundation/frontend-js/frontend-js-web/build/node/bin/node --version
```
Expected: BUILD SUCCESSFUL, with the node download step fetching 18.20.4.

This check was insufficient: the representative module passed, but `ant clean all` later failed in `:apps:frontend-theme-fjord:frontend-theme-fjord:gulpBuild`.

**Fallback ladder if the build fails** (old com.liferay.node plugin or legacy npm scripts may choke on modern Node): try `"16.20.2"`, then `"14.21.3"`. **Acceptance rule:** the pinned version must contain the CVE-2022-37434 fix — 18.x >= 18.7.0, 16.x >= 16.17.0, 14.x >= 14.21.0. If nothing at or above 14.21.0 builds, stop and report; do not pin below it.

- [x] **Step 4: Verify a second module (npm-heavy) builds**

Run:
```bash
rm -rf apps/web-experience/journal/journal-web/build
../gradlew :apps:web-experience:journal:journal-web:deploy --stacktrace
```
Expected: BUILD SUCCESSFUL. If this module has no node build step it completes trivially — that is fine; the point is no regression.

This check was also insufficient because it did not exercise legacy Liferay theme modules.

- [x] **Step 4b: Verify full-build theme compatibility**

Run:
```bash
cd modules
rm -rf apps/frontend-theme-fjord/frontend-theme-fjord/build_gradle
../gradlew :apps:frontend-theme-fjord:frontend-theme-fjord:gulpBuild --stacktrace --info
```
Observed under Node `18.20.4`: `ReferenceError: primordials is not defined` from `gulp` 3 / `graceful-fs` 3. Since the plan's accepted fallback floor is Node `14.21.0` and the legacy gulp stack fails on Node 12+, the upgrade is rejected for this security-patch branch.

- [x] **Step 5: Roll back and request Aikido triage approval**

```bash
git add modules/build.gradle
```

Ask user to approve commit: `sec: keep legacy Node toolchain for theme build compatibility`

---

### Task 3: groovy-all 2.0.1 → 2.4.21 (SDK tooling)

**Status: DONE** — committed as `1d4f6caf9ec6`; binary re-verified in the main checkout on 2026-07-03. Manifest reports `Implementation-Version`, `Bundle-Version`, and `Specification-Version` all as `2.4.21`.

Priority 3. Deserialization RCE CVE-2015-3253 / CVE-2016-6497. Jar lives in `tools/sdk/dependencies/org.codehaus.groovy/lib/groovy-all.jar`, not deployed to the portal; JSONWS allowlist already blocks API-side Groovy execution.

**Files:**
- Modify (binary replace): `tools/sdk/dependencies/org.codehaus.groovy/lib/groovy-all.jar`
- Optional follow-up (not part of the completed fix): `tools/sdk/dependencies/org.codehaus.groovy/ivy.xml` still declares `rev="2.0.1"`. Current SDK Ant wiring uses the vendored `lib/*.jar` fileset directly, so this is inert unless an Ivy resolve/retrieve path is introduced later.

**Interfaces:**
- Consumes: nothing.
- Produces: nothing other tasks rely on.

- [x] **Step 1: Confirm current version**

Run:
```bash
unzip -p tools/sdk/dependencies/org.codehaus.groovy/lib/groovy-all.jar META-INF/MANIFEST.MF | grep -m1 -i "Bundle-Version\|version="
```
Expected: shows 2.0.1.

- [x] **Step 2: Download groovy-all 2.4.21 from Maven Central** *(requires user permission — external download)*

Ask the user to approve, then (use the current session's scratchpad dir, `$SCRATCH` below):
```bash
curl -fL -o "$SCRATCH/groovy-all-2.4.21.jar" \
  https://repo1.maven.org/maven2/org/codehaus/groovy/groovy-all/2.4.21/groovy-all-2.4.21.jar
curl -fL -o "$SCRATCH/groovy-all-2.4.21.jar.sha1" \
  https://repo1.maven.org/maven2/org/codehaus/groovy/groovy-all/2.4.21/groovy-all-2.4.21.jar.sha1
cd "$SCRATCH" && \
  echo "$(cat groovy-all-2.4.21.jar.sha1)  groovy-all-2.4.21.jar" | sha1sum -c -
```
Expected: `groovy-all-2.4.21.jar: OK`

- [x] **Step 3: Replace the jar**

```bash
cp /tmp/claude-1000/-opt-projects-liferay-portal-arena-7-0-6-ga7-portal/5b7264fb-eabf-4bd5-8bc6-a4d7a39d334b/scratchpad/groovy-all-2.4.21.jar \
   tools/sdk/dependencies/org.codehaus.groovy/lib/groovy-all.jar
```

- [x] **Step 4: Find and exercise the SDK consumers**

Run: `grep -rn "org.codehaus.groovy\|groovy" tools/sdk --include=build.xml --include="*.gradle" --include="*.properties" | grep -v Binary | head -40`

The real consumer is `tools/sdk/build-common.xml:105-109`, which defines a fileset over `tools/sdk/dependencies/org.codehaus.groovy/lib/*.jar` and registers the Groovy Ant task. `ant compile` and `portal-scripting-groovy:compileJava` are useful sanity checks, but they do not directly exercise this vendored SDK jar. Verify the Ant task loads the new jar with a small scratch Ant file or an SDK target that imports `build-common.xml`; expected output should include `groovy ok: 2.4.21`.

Still run the broader sanity checks:
```bash
ant compile
cd modules && ../gradlew :apps:foundation:portal-scripting:portal-scripting-groovy:compileJava
```
Expected: both succeed. (Groovy 2.4 is source-compatible with 2.0 scripts; if a consumer fails on a removed API, report the exact error and stop — do not downgrade silently.)

- [x] **Step 5: Stage and request commit approval**

```bash
git add -f tools/sdk/dependencies/org.codehaus.groovy/lib/groovy-all.jar
```

Ask user to approve commit: `sec: upgrade SDK groovy-all 2.0.1 -> 2.4.21 (CVE-2015-3253, CVE-2016-6497)`

---

### Task 4: HAProxy hardening — path traversal, admin portlets, security headers

Priorities 5 and 6, plus the review's §Possible Additional HAProxy Hardening. Closes the residual vector of spring-webmvc CVE-2018-1272 and non-`/WEB-INF` traversal targets flagged in the SAST path-traversal findings (WebServerServlet, DynamicResourceServlet et al.), locks the Script-console and DDM-template portlets to internal IPs, and adds response security headers.

Config layout (re-verified 2026-07-03 at arena-install HEAD `f533bda5`; the 4 commits since `0839c242` — incl. `6660ea9a` "PLCB-24854 Liferay Remote Code Execution vulnerability from 2020" — touch nothing under `stage/haproxy`, all line anchors below re-confirmed; Task 7 note: PLCB-24854 landed independently, don't double-count it in Aikido triage): source dir `/opt/projects/arena-install/develop/arena-install/stage/haproxy/` contains `haproxy.cfg` (global + `ARENA-DEFAULTS`), `arena/arena.cfg` (159 lines: frontend `ARENA` lines 1-46, backends incl. `be-portal` lines 98-159), `stats.cfg`, `certs/`, `errors/`, `static/`. Existing `be-portal` structure: `is-forbidden` block at lines 107-112, JSONWS lockdown section lines 114-150 with `jsonws-internal` ACL (`src -f /etc/haproxy/arena/arena-whitelist.txt`) declared at line 116. Deny-reason diagnostics are **already active**: frontend `declare capture request len 64` (line 14) + `set-var(req.jsonws_blocked)`/`http-request capture ... id 0` in `be-portal` (lines 144-147) — capture slot id 0 is taken; do not add another frontend request capture without bumping ids. Frontend routes `/local-rest`, `/arena.pa.palma`, `/federated-search`, `/transaction`, `*.html`, emedia vhosts to other backends — anything placed only in `be-portal` does not protect those paths.

**Files:**
- Modify: `/opt/projects/arena-install/develop/arena-install/stage/haproxy/arena/arena.cfg` (frontend `ARENA` + backend `be-portal`)

**Interfaces:**
- Consumes: nothing.
- Produces: deployed rules that Task 5's audit and Task 7's Aikido closures cite as mitigation.

- [ ] **Step 1: Verify the portlet IDs against the live deployment** *(precondition — review flags this)*

Ask the user (or check via internal-whitelisted browser access) whether the Arena deployment exposes the Script console (portlet id `82`) and DDM template editor (portlet id `167`) at all. If Arena's control panel is already unreachable from the internet, note it — the two portlet ACLs are then defense-in-depth, still worth adding.

- [ ] **Step 2: Add the path-traversal deny to the frontend (covers ALL backends)**

In `arena/arena.cfg`, frontend `ARENA`, after the `seo-crawlers` deny (line 28) and before the emedia vhost ACLs (line 30), insert:

```haproxy
    # SECURITY_REVIEW-2: block path-traversal sequences before any backend
    # (spring-webmvc CVE-2018-1272 residual; SAST WebServerServlet/
    # DynamicResourceServlet findings). path_reg scope is the path only —
    # query-string parameter values are intentionally not matched.
    acl path-traversal path_reg -i (\.\./|%2e%2e%2f|\.\.%2f|%2e%2e/)
    http-request deny deny_status 400 if path-traversal
```

Frontend placement (not `be-portal`) is deliberate: it also shields `be-local`, `be-transaction`, and `be-federated-search`, whose Java services are equally traversal-prone. `-i` catches uppercase `%2E` encodings.

- [ ] **Step 3: Add the admin-portlet lockdown to `be-portal`**

Immediately after the `http-request deny if is-forbidden` line (line 112), insert:

```haproxy
    # SECURITY_REVIEW-2: admin portlets internal-only.
    # 82 = Script console (Groovy execution), 167 = DDM template editor
    # (XSLT/FreeMarker — xalan CVE-2022-34169 mitigation).
    acl is-internal-src  src -f /etc/haproxy/arena/arena-whitelist.txt
    acl is-admin-portlet urlp(p_p_id) -m str 82
    acl is-admin-portlet urlp(p_p_id) -m str 167
    http-request deny deny_status 403 if is-admin-portlet !is-internal-src
```

Note: do NOT reference `jsonws-internal` here — it is declared at line 116, *after* this insertion point, and HAProxy requires ACL declaration before first use. `is-internal-src` reads the same whitelist file.

- [ ] **Step 4: Add response security headers to `be-portal`**

Directly before the `filter compression` line near the end of `be-portal` (line 152 pre-edit), insert:

```haproxy
    # SECURITY_REVIEW-2: response hardening headers
    http-response set-header Strict-Transport-Security "max-age=31536000; includeSubDomains"
    http-response set-header X-Content-Type-Options "nosniff"
    http-response set-header X-Frame-Options "SAMEORIGIN"
    http-response set-header Referrer-Policy "strict-origin-when-cross-origin"
```

HSTS is safe here: the frontend already force-redirects HTTP→HTTPS (line 4) and the cert is a `*.axiell.com` wildcard. **Caveat:** if any Arena portlet page must be iframe-embedded by library-partner sites on other domains, `X-Frame-Options SAMEORIGIN` breaks it — ask the user before including that one header; drop it if embedding is required.

- [ ] **Step 5: Syntax-check the full staged set**

```bash
haproxy -c -f /opt/projects/arena-install/develop/arena-install/stage/haproxy/haproxy.cfg \
           -f /opt/projects/arena-install/develop/arena-install/stage/haproxy/stats.cfg \
           -f /opt/projects/arena-install/develop/arena-install/stage/haproxy/arena/arena.cfg
```
Expected: `Configuration file is valid`. The staged cfg references live paths (`/etc/haproxy/certs/...`, `/etc/haproxy/arena/arena-whitelist.txt`, `arena-vhosts.txt`) which exist on this host, so the parse check works from the stage dir. If it errors on the stats socket or chroot, those are runtime-only warnings — only `ALERT` lines block deployment.

- [ ] **Step 6: Confirm deny diagnostics are active (no edit needed)**

Deny-reason logging is already implemented and always-on (commit `0839c242` in arena-install): `be-portal` sets `req.jsonws_blocked` to `jsonws-Deny-Reason: service-not-allowed|multi-service|dup-cmd` and captures it into slot id 0 (lines 144-147). JSONWS denials are therefore attributable in `/var/log/haproxy.log` as-is. Do NOT uncomment the legacy commented capture block (frontend lines 16-21) — it would collide with capture slot id 0. New denials added by this task (path-traversal 400s, admin-portlet 403s) are distinguishable by status code and path in the standard httplog line; no extra capture needed.

- [ ] **Step 7: Deploy and reload**

```bash
sudo cp /opt/projects/arena-install/develop/arena-install/stage/haproxy/arena/arena.cfg /etc/haproxy/arena/arena.cfg
sudo systemctl reload haproxy
sudo systemctl status haproxy --no-pager | head -5
```
Expected: `active (running)`, no alert lines in `journalctl -u haproxy -n 20`.

- [ ] **Step 8: Verify — negative cases (must be blocked)**

```bash
H=<arena-vhost-from-arena-vhosts.txt>
curl -sk -o /dev/null -w "%{http_code}\n" "https://$H/documents/..%2f..%2fWEB-INF/web.xml"        # expect 400
curl -sk -o /dev/null -w "%{http_code}\n" "https://$H/a/../../etc/passwd"                          # expect 400
curl -sk -o /dev/null -w "%{http_code}\n" "https://$H/group/control_panel/manage?p_p_id=82"        # expect 403 (from non-whitelisted IP) or 200/redirect (from whitelisted — note which vantage was used)
curl -sk -o /dev/null -w "%{http_code}\n" "https://$H/group/control_panel/manage?p_p_id=167"       # same
curl -skI "https://$H/" | grep -i "strict-transport\|x-content-type\|x-frame\|referrer-policy"     # expect all four headers
```

If testing from a whitelisted IP, the portlet denies cannot be exercised — verify the rule logic via the haproxy log instead and note the limitation in the triage log.

- [ ] **Step 9: Verify — positive cases (must NOT break)**

Browser-test the main Arena flows per the deploy-and-verify loop in `.claude/skills/haproxy-api-lockdown/SKILL.md` — search, patron login, loans/reservations, calendar widget (exercises the JSONWS calendar allow), asset tag/category pickers (exercises `jsonws-allowed-service`). Then:

```bash
grep "jsonws-Deny-Reason\| 400 \| 403 " /var/log/haproxy.log | tail -20
```
Expected: no denials for legitimate traffic. **Watch specifically for `path-traversal` false positives** — Liferay themes occasionally emit relative `../` asset URLs that browsers normalize before sending, but proxied or scripted clients may not; if legit requests get 400'd, log the offending path and either fix the emitting template or exclude that exact path prefix with `acl path-traversal-exempt path_beg <prefix>` ahead of the deny.

- [ ] **Step 10: Commit in arena-install repo**

The arena-install repo has its own git. Stage the cfg there and ask the user to approve commit: `sec: HAProxy path-traversal deny, admin-portlet lockdown, security headers (SECURITY_REVIEW-2)`

---

### Task 5: SAST audits — path traversal, SSRF, BeanPropertiesImpl deserialization

Priorities 6, 7, 9 (audit halves). These are analysis tasks with a written verdict per finding; code changes only if the audit proves user-controlled input reaches a sink. Output is appended to `SECURITY_REVIEW-2.md` so Task 7 can triage in Aikido with citations.

**Files:**
- Read: `portal-kernel/src/com/liferay/portal/kernel/servlet/WebServerServlet.java` (if present — locate exact path in Step 1), `DynamicResourceServlet.java`, `util-java/src/com/liferay/util/axis/SimpleHTTPSender.java`, `portal-kernel/src/com/liferay/portal/kernel/util/SocketUtil.java`, `portal-impl/src/com/liferay/portal/bean/BeanPropertiesImpl.java`
- Modify: `SECURITY_REVIEW-2.md` (append `## SAST Audit Verdicts` section)

**Interfaces:**
- Consumes: Task 4's deployed path-traversal rule (cite as mitigation where applicable).
- Produces: verdict table in `SECURITY_REVIEW-2.md` that Task 7 uses for Aikido justifications.

- [ ] **Step 1: Locate the flagged files**

```bash
find portal-kernel portal-impl util-java -name "WebServerServlet.java" -o -name "DynamicResourceServlet.java" -o -name "SimpleHTTPSender.java" -o -name "SocketUtil.java" -o -name "BeanPropertiesImpl.java" 2>/dev/null
```

- [ ] **Step 2: Path-traversal audit**

For `WebServerServlet` and `DynamicResourceServlet`: read each file; for every filesystem/resource read driven by request path or parameters, answer: (a) is the path normalized (`GetterUtil`/`StringUtil` cleanup, `FileUtil.getAbsolutePath`, servlet-container normalization) before use? (b) can `../` survive to the sink? Record verdict per servlet: `SAFE (normalized at line N)` / `MITIGATED (HAProxy path-traversal rule, Task 4)` / `VULNERABLE (fix required)`. If VULNERABLE, stop and report to user before writing any fix — fix design is a separate decision.

- [ ] **Step 3: SSRF audit**

For `SimpleHTTPSender` and `SocketUtil` (and the "+10 others" — pull the exact file list from the Aikido finding detail in the dashboard): for each, identify who supplies the target URL/host. Verdict per file: `SYSTEM-CONFIG (admin-set URL, not user input)` / `MITIGATED (JSONWS allowlist)` / `VULNERABLE`. SimpleHTTPSender is Axis SOAP client plumbing — expect SYSTEM-CONFIG; verify rather than assume.

- [ ] **Step 4: BeanPropertiesImpl deserialization audit**

Read `BeanPropertiesImpl.java`. Question: does `copyProperties`/`setProperty` param binding instantiate arbitrary classes from request-supplied names (Struts1 ClassLoader-manipulation pattern, cf. CVE-2014-0114 in commons-beanutils), or only set properties on an already-typed bean? Check whether `class.`/`Class.` prefixed parameters are excluded. Verdict: `SAFE (typed bean, no class param)` / `VULNERABLE (needs param exclusion filter)`. If vulnerable, report to user — the fix (a deny-list on `class` property paths) touches request processing for every portlet and needs sign-off.

- [ ] **Step 5: Write the verdicts**

Append to `SECURITY_REVIEW-2.md`:

```markdown
## SAST Audit Verdicts (Task 5, YYYY-MM-DD)

| Finding | File | Verdict | Evidence |
|---|---|---|---|
| Path traversal | WebServerServlet.java | <verdict> | <file:line — what normalizes/blocks> |
| Path traversal | DynamicResourceServlet.java | <verdict> | <file:line> |
| SSRF | SimpleHTTPSender.java | <verdict> | <who sets the URL, file:line> |
| SSRF | SocketUtil.java | <verdict> | <file:line> |
| Deserialization | BeanPropertiesImpl.java | <verdict> | <file:line> |
```

(One row per audited file, real verdicts, real line numbers — no placeholders.)

- [ ] **Step 6: Stage and request commit approval**

```bash
git add SECURITY_REVIEW-2.md
```

Ask user to approve commit: `docs: SAST audit verdicts for path traversal, SSRF, deserialization findings`

---

### Task 6: SQL-injection housekeeping — triage only

Priority 8. Upgrade/schema utilities (`BaseUpgradePortletId`, `DBInspector`, +12) run at startup, not per-request — not web-exploitable. Per YAGNI on a security-patch branch, do **not** parameterize 14 files of startup code; document acceptance instead.

**Files:**
- Modify: `SECURITY_REVIEW-2.md` (append one paragraph to the audit section from Task 5)

**Interfaces:**
- Consumes: Task 5's section exists in the file.
- Produces: acceptance text Task 7 cites in Aikido.

- [ ] **Step 1: Spot-check the claim**

```bash
grep -rn "runSQL\|executeUpdate" portal-impl/src/com/liferay/portal/upgrade/BaseUpgradePortletId.java 2>/dev/null | head -5
grep -rn "class BaseUpgradePortletId" portal-impl/src --include=*.java -l
```
Confirm the flagged concatenation sites take their variables from DB metadata / upgrade constants, not request input. If any flagged file turns out to be request-reachable, escalate to user — it moves to Task 5's treatment.

- [ ] **Step 2: Document acceptance**

Append to the `## SAST Audit Verdicts` section of `SECURITY_REVIEW-2.md`:

```markdown
### SQL injection (14 files) — accepted

Upgrade/schema utilities executed at portal startup with inputs from database
metadata and upgrade constants; no request-derived data reaches the
concatenation sites (spot-checked BaseUpgradePortletId, DBInspector).
Not web-exploitable. Accepted for the security-patch branch; parameterization
deferred to the next major upgrade.
```

- [ ] **Step 3: Stage together with Task 5's edit** (same file; if Task 5 already committed, stage and ask again with message `docs: accept startup-time SQLi findings as not web-exploitable`)

---

### Task 7: Aikido dashboard triage — suppressions and closures

Priorities 4, 10, plus closures earned by Tasks 1-6. All steps are manual browser actions at `https://app.aikido.dev/repositories/2406495` (user is logged in; drive via Chrome extension or hand the checklist to the user). **Acting on the dashboard modifies external state — confirm with the user before starting, and apply items one at a time.**

**Files:**
- Modify: `SECURITY_REVIEW-2.md` (append `## Aikido Triage Log` recording each action + justification)

**Interfaces:**
- Consumes: verdicts from Tasks 5-6; deployed mitigations from Task 4; fixes from Tasks 1-3.
- Produces: clean Aikido dashboard state; triage log.

- [ ] **Step 1: Suppress xalan 2.7.2 (CVE-2022-34169)**

Justification to enter: "No fixed 2.7.x release exists. XSLT transforms are reachable only by authenticated admins via the DDM template editor; HAProxy additionally blocks portlet id 167 from non-whitelisted IPs (arena.cfg, SECURITY_REVIEW-2 Task 4). Replacement with Saxon-HE deferred to next major upgrade."

- [ ] **Step 2: Ignore the five secrets false positives**

Per the review's §Exposed Secrets table: ColorUtil.java (hex color constants), LanPEMParserUtil.java (PEM header string literal), shopping-service portlet.properties ×2 (no credentials), LDAPUserImporterImpl.java (method parameter). Justification for each: copy the Assessment cell from the review table. Note: `.idea/` is already untracked and gitignored (verified 2026-07-03) — if Aikido still flags `.idea/workspace.xml`, the scan is reading a stale artifact; mark ignored with that note.

- [ ] **Step 3: Close/annotate findings fixed by Tasks 1-3**

- TunnelUtil SAST finding → mark resolved, cite the commit from Task 1.
- nodejs / zlib / openssl / v8 / c-ares → accept/triage, not resolved. Node 18 upgrade was attempted and rejected because `ant clean all` fails in legacy Liferay theme modules (`gulp` 3 / `graceful-fs` 3 `primordials` crash on Node 12+). Justification: build-tool-only binary, not deployed to portal runtime; theme-toolchain migration deferred to next major upgrade.
- groovy-all → resolved by Task 3 commit.

- [ ] **Step 4: Annotate HAProxy-mitigated findings**

- spring-webmvc CVE-2018-1271 → "mitigated: HAProxy denies path_beg /WEB-INF"; CVE-2018-1272 → "mitigated: HAProxy path-traversal regex (Task 4)". Mark accepted-risk pending Spring 4.3.x upgrade at next major.
- Log4j 1.x CVE-2019-17571 → already accepted (SECURITY_REVIEW-1.md, SocketServer never started) — ensure marked accepted in dashboard.
- eval()/document.write()/SSTI JS findings → accepted per review (legacy AUI, config-driven inputs), unless Task 5 found otherwise.
- Tomcat EOL → accepted for security-patch branch; note for next major upgrade.
- SAST verdicts from Tasks 5-6 → apply each verdict (SAFE/MITIGATED → accept with citation; any VULNERABLE verdicts stay open with a remediation note).

Already fixed in the tree by the recent "Security patches" commits (verified 2026-07-03 in `lib/portal/dependencies.properties` and `modules/apps/opensocial/opensocial-portlet/build.xml`) — mark resolved if the dashboard still shows them open, citing the versions: xstream 1.4.21, commons-collections 3.2.2, commons-fileupload 1.6.0, commons-beanutils 1.9.4, commons-io 2.15.1, json 20240303, xercesImpl 2.12.2 (REVIEW-2's "Xerces upgrade required" note is stale), shiro-core/shiro-web stripped from the opensocial WAR at build time (build.xml deletes both jars + shindig shiro sample classes; CVE-2010-3863, CVE-2014-0074).

- [ ] **Step 5: Write the triage log**

Append to `SECURITY_REVIEW-2.md` a `## Aikido Triage Log (YYYY-MM-DD)` section: one line per dashboard action — finding, action taken (suppressed/accepted/resolved), justification entered. Stage and ask to commit: `docs: Aikido triage log for SECURITY_REVIEW-2 remediation`

- [ ] **Step 6: Trigger a rescan and verify**

In the Aikido repo page, trigger a new scan (or wait for the next scheduled one). Expected end state: 0 open Critical findings; remaining open items are only those explicitly accepted with justification. Report the final count to the user.

---

### Task 8: Finalize and commit the doc refresh

**The bulk of this task is already applied as uncommitted working-tree edits (2026-07-03)** — do not re-derive it from the committed versions. Current uncommitted state: `DESIGN.md` §Known Security Debt already points to `SECURITY_REVIEW-1.md`/`SECURITY_REVIEW-2.md` (dangling `SECURITY_REVIEW.md` ref fixed), lists resolved items (XStream 1.4.21, commons-collections 3.2.2, commons-fileupload 1.6.0, beanutils 1.9.4, opensocial shiro stripped), lists still-open/accepted risk, and flags the protobuf-java finding as N/A; `SECURITY_REVIEW-1.md` has the matching protobuf-java N/A note; `AGENTS.md` has the legacy-branch warning. If the working tree no longer shows these edits (e.g. reverted), reconstruct per the description above.

**Files:**
- Modify: `DESIGN.md` (§Known Security Debt — final touch-up only)
- Stage: `DESIGN.md`, `AGENTS.md`, `SECURITY_REVIEW-1.md` (pre-existing uncommitted doc edits — no other task stages these)

**Interfaces:**
- Consumes: final state after Tasks 1-7.
- Produces: accurate onboarding doc; the doc refresh committed.

- [ ] **Step 1: Fold in the final post-plan state**

In `DESIGN.md` §Known Security Debt, verify the "Still open / accepted risk" line matches reality after Tasks 1-7 (expected: Tomcat EOL, xalan suppression, Spring upgrade deferred to next major, commons-lang 2.6 / struts 1.3.10 EOL accepted) and add any VULNERABLE verdicts from Task 5. Keep it under ~15 lines — DESIGN.md is an index, details live in the review files.

- [ ] **Step 2: Stage and request commit approval**

```bash
git add DESIGN.md AGENTS.md SECURITY_REVIEW-1.md
```

Ask user to approve commit: `docs: refresh Known Security Debt after SECURITY_REVIEW-2 remediation`
