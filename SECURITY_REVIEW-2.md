# Security Review — Aikido Scan Results

**Repository:** axiell/liferay-portal (`feature/security-patch`, repo ID 2406495)
**Scan date:** 2026-06-26
**Total issues:** 49
**Source:** https://app.aikido.dev/repositories/2406495?sidebarIssue=33507407

---

## HAProxy Protection Layer (active)

All inbound browser traffic passes through HAProxy before reaching Liferay. The following controls
are currently active in `arena.cfg` and affect the severity assessment of several findings below:

| Protection | Rule | Effect |
|---|---|---|
| **JSONWS allowlist** | `be-portal` — strict allowlist of 14 named JSONWS services + calendar call + 3 object-graph services. Everything else → 403 `service-not-allowed`. | Any vulnerability requiring an unallowlisted JSONWS call is **unexploitable from the internet**. Covers the vast majority of Liferay's RPC surface. |
| **HTTP-parameter-pollution guard** | `jsonws-dup-cmd` rejects bodies with duplicate `cmd=` | Prevents HPP-based ACL bypass on JSONWS. |
| **Multi-service batch block** | `jsonws-multi-service` rejects `cmd` values containing multiple service objects | Prevents chained/batched JSONWS exploitation. |
| **WEB-INF block** | `is-forbidden path_beg /WEB-INF` → deny | Blocks path-traversal requests targeting Java webapp internals (Spring MVC CVE-2018-1271 primary attack vector). |
| **Webdav block** | `is-forbidden path_beg /webdav` → deny | Removes WebDAV attack surface entirely. |
| **Admin page block** | `urlp(ConfigurationPage)`, `urlp(PortletConfigurationPageBase)`, `urlp(wicket:bookmarkablePage)` → deny | Direct URL-parameter navigation to admin portlet config pages blocked. |
| **Internal API restriction** | `/local-rest/api/internal/` restricted to whitelist IPs | Internal management endpoints not reachable from public internet. |
| **HTTPS enforcement** | All HTTP → redirect HTTPS | Prevents cleartext interception. |
| **Internal IP bypass** | `jsonws-internal src -f arena-whitelist.txt` | Server-to-server calls skip JSONWS allowlist; all external calls subject to it. |

---

## Requested Issue: madler/zlib (issue 33507407)

**Severity:** Critical  
**Type:** Unmanaged C/C++ Dependency  
**Affected version:** zlib 1.2.8  
**Fix:** upgrade to zlib >= 1.2.12  
**HAProxy impact:** None — zlib is in build-time Node.js binaries, not the running portal.

zlib 1.2.8 is embedded in the Node.js v6.6.0 binary used for OSGi module frontend builds
(`modules/apps/*/build/node/bin/node`). These binaries are never deployed to the Tomcat WAR —
they run only on developer machines and CI runners. Runtime exposure is **build environment only**.

| CVE | Severity | Description | Fix version |
|---|---|---|---|
| CVE-2022-37434 | Critical | Heap buffer overflow in `inflate()` via large gzip header extra field → RCE. Exploit on GitHub. | >= 1.2.12 |
| CVE-2018-25032 | High | Out-of-bounds write in `deflate()` during memory compression → crash or RCE. Exploit on GitHub. | >= 1.2.12 |
| CVE-2016-9842 | High | Undefined left-shift behavior in `inftrees.c` | >= 1.2.9 |
| CVE-2016-9843 | High | Undefined right-shift behavior in `crc32_big()` | >= 1.2.9 |
| CVE-2016-9841 | High | Undefined pointer arithmetic in `inffast.c` | >= 1.2.9 |
| CVE-2016-9840 | Medium | Undefined left-shift behavior in `inflate.c` | >= 1.2.9 |

**Fix:** Upgrade the Gradle Node.js plugin toolchain to Node.js >= 18 LTS (ships zlib >= 1.2.12).
Locate `node` version pins in each module's `build.gradle` or the root `com.liferay.node` plugin
config and bump the version. CI/developer machine change only — no portal deployment needed.

---

## Full Scan — All 49 Issues

### Critical — Dependency CVEs

| Dependency | Version | Finding | HAProxy mitigation | Fix |
|---|---|---|---|---|
| Apache Tomcat | (bundled) | EOL — no security updates for 8 years | HTTPS termination at HAProxy reduces TLS-layer exposure; application-layer CVEs not mitigated | Accept for security-patch branch; note for next major upgrade |
| nodejs | v6.6.0 | EOL — bundles zlib 1.2.8, openssl 1.0.x, v8 5.x, c-ares 1.x | Build-time only — not reachable from internet | Upgrade Gradle Node.js toolchain to Node 18 LTS |
| madler/zlib | 1.2.8 | Memory corruption → crash or RCE (6 CVEs, see above) | Build-time only | Resolved by Node.js upgrade |
| openssl/openssl | (Node.js 6.6.0) | OS command injection | Build-time only | Resolved by Node.js upgrade |
| v8/v8 | 5.x (Node.js 6.6.0) | Memory operation restrictions abuse | Build-time only | Resolved by Node.js upgrade |
| org.codehaus.groovy:groovy-all | 2.0.1 | Deserialization → RCE (CVE-2015-3253, CVE-2016-6497) | SDK tooling in `tools/sdk/` — **not deployed to portal**; JSONWS allowlist also blocks any API-based Groovy script execution from the internet | Upgrade jar in `tools/sdk/dependencies/` to >= 2.4.21 |
| Log4j | 1.x | CVE-2019-17571 SocketServer RCE | **Accepted risk** — SocketServer never started. Documented in SECURITY_REVIEW-1.md | No action |

### High — Dependency CVEs

| Dependency | Version | Finding | HAProxy mitigation | Fix |
|---|---|---|---|---|
| spring-webmvc / spring-webmvc-portlet | 4.1.9.RELEASE | Path traversal (CVE-2018-1271, CVE-2018-1272) | **CVE-2018-1271 primary vector blocked**: HAProxy denies all `path_beg /WEB-INF` requests → attacker cannot reach `WEB-INF/` files through traversal. CVE-2018-1272 (multipart path traversal) not directly addressed by current rules. | For CVE-2018-1271: **effectively mitigated by HAProxy**. For completeness, upgrade Spring to 4.3.x+ and/or add `path_reg \.\.` deny rule. Pins in `lib/portal/dependencies.properties`. |
| xalan:xalan | 2.7.2 | XSLT integer truncation → arbitrary bytecode injection (CVE-2022-34169) | No direct mitigation — XSLT transforms triggered via authenticated admin portlet, not via JSONWS | No patch for 2.7.x. Exploitable only by authenticated admin. Suppress in Aikido with justification, or replace with Saxon-HE. `lib/portal/xalan.jar` |
| xerces:xercesImpl | (lib/portal) | DoS via infinite loop during XML parsing | Malformed XML must reach the parser through a portal endpoint; HAProxy does not filter XML bodies | Documented in SECURITY_REVIEW-1.md; Xerces upgrade required |
| c-ares/c-ares | (Node.js 6.6.0) | Missing input validation in DNS resolution | Build-time only | Resolved by Node.js upgrade |

### Critical — SAST

| File | Finding | HAProxy mitigation | Assessment & Fix |
|---|---|---|---|
| `portal-kernel/.../service/http/TunnelUtil.java:128` | TLS hostname verification disabled when `TunnelUtil.verify.ssl.hostname=false`. `verify()` unconditionally returns `true`. | **None** — TunnelUtil handles inter-node cluster tunnel calls (server-to-server), not inbound browser traffic. HAProxy is not in this path. | Real risk. **Action: verify `com.liferay.portal.kernel.service.http.TunnelUtil.verify.ssl.hostname` in production `portal-ext.properties`**. If false or unset, set to `true`. Alternatively hard-remove the bypass block in TunnelUtil. |
| `frontend-js-web/.../misc/xp_progress.js`<br>`frontend-js-aui-web/.../liferay/deprecated.js` | RCE via `eval()`-type functions | None — JS responses are not filtered by HAProxy | Legacy AUI/AlloyUI. Inputs flow from portal config, not direct user input. Low practical exploitability. Accept or track separately. |

### High — SAST

| File | Finding | HAProxy mitigation | Assessment & Fix |
|---|---|---|---|
| `portal-kernel/.../process/local/LocalProcessLauncher.java`<br>`portal-impl/.../bean/BeanPropertiesImpl.java` + 2 others | Object deserialization → RCE | LocalProcessLauncher: local IPC pipe — **no internet exposure, HAProxy irrelevant**. BeanPropertiesImpl: Struts param binding for portlet actions — goes through HAProxy but body not filtered. | LocalProcessLauncher: controlled input only, no action needed. BeanPropertiesImpl: audit whether deserialised input is user-controlled before acting. |
| `util-java/.../axis/SimpleHTTPSender.java`<br>`portal-kernel/.../SocketUtil.java` + 10 others | HTTP requests may enable SSRF | **Partial**: JSONWS allowlist blocks SSRF via any non-allowlisted JSONWS service. SSRF through portlet actions or admin workflows not covered. | Audit callers for user-controlled URLs. Most are admin-only or system operations. **Additional HAProxy hardening possible**: block direct access to internal service ports at network layer if not already done. |
| `portal-impl/.../BaseUpgradePortletId.java`<br>`portal-impl/.../DBInspector.java` + 12 others | SQL injection via string-based query concatenation | **Effectively unreachable from internet** — upgrade/schema utilities run at portal startup, not in response to HTTP requests | Not web-exploitable. Parameterise as housekeeping; no urgency. |
| `portal-kernel/.../WebServerServlet.java`<br>`portal-kernel/.../DynamicResourceServlet.java` + 5 others | Path traversal attack possible | **Partial**: HAProxy blocks `path_beg /WEB-INF` — the highest-value traversal target. Traversal to other paths not blocked. | Verify `getResourceAsStream()` calls normalise against docroot. **Additional HAProxy rule possible**: add `path_reg (\.\./|%2e%2e)` deny if analysis shows traversal sequences can reach sensitive paths. |
| `calendar-web/.../js/message_util.js` | SSTI via `express.render()` | None — JS served as static resource | `express` here is YUI template engine (not Node.js Express). Verify template input is not user-controlled. Likely low risk. |
| `frontend-js-web/.../xp_progress.js`<br>`frontend-js-aui-web/.../widget.js` + 5 others | XSS via `document.write()` | None — JS response bodies not filtered | Legacy AUI, inputs from portal config. Accept alongside eval() findings. |

### Exposed Secrets — Triage (all false positives)

| File | Finding | Assessment |
|---|---|---|
| `util-java/.../ColorUtil.java` | 1 exposed secret | No credentials on inspection — FP on hex colour constants. **Ignore in Aikido.** |
| `sync-engine/.../LanPEMParserUtil.java` | Private key identified | `"-----BEGIN PRIVATE KEY-----"` is a PEM format string literal, not a key value. **Ignore in Aikido.** |
| `shopping-service/.../portlet.properties` (x2) | 4 exposed secrets | No credentials in source; build output `classes/` copies also flagged. **Ignore in Aikido.** |
| `portal-security-ldap/.../LDAPUserImporterImpl.java` | Generic password field | `password` is a method parameter, not a hardcoded credential. **Ignore in Aikido.** |
| `.idea/workspace.xml` | 1 exposed secret | IntelliJ workspace file; no secrets visible. **Add `.idea/` to `.gitignore`** to stop future scans picking it up. |

---

## Possible Additional HAProxy Hardening

The following rules do not exist yet but could further reduce the attack surface with low effort.
Add to `be-portal` in `arena.cfg`:

```haproxy
# Block path-traversal sequences before they reach Liferay
acl path-traversal path_reg (\.\./|%2e%2e%2f|\.\.%2f|%2e%2e/)
http-request deny deny_status 400 if path-traversal

# Block direct access to Liferay Script Console (admin Groovy execution)
# URL: /group/control_panel/manage?p_p_id=82 (portlet id 82 = Script portlet)
acl is-script-console urlp(p_p_id) -m str 82
http-request deny deny_status 403 if is-script-console !jsonws-internal

# Block XSLT/FreeMarker admin template editor portlets (DDM templates)
# Portlet ID 167 = DDM template; restrict to internal IPs
acl is-ddm-template urlp(p_p_id) -m str 167
http-request deny deny_status 403 if is-ddm-template !jsonws-internal
```

> These are suggestions — verify portlet IDs against the actual Arena deployment before applying,
> and browser-test through the deploy-and-verify loop described in `.claude/skills/haproxy-api-lockdown/SKILL.md`.

---

## Priority Order for Remediation

HAProxy mitigations are noted in brackets.

1. **TunnelUtil.java TLS bypass (Critical SAST — no HAProxy protection)** — check production `portal-ext.properties` immediately; set `verify.ssl.hostname=true` if missing.
2. **Node.js v6.6.0 upgrade (Critical — build env only, no runtime HAProxy needed)** — resolves zlib + openssl + v8 + c-ares + nodejs EOL in one change. Upgrade Gradle toolchain to Node 18 LTS.
3. **groovy-all 2.0.1 (Critical dep — SDK only, JSONWS lockdown also blocks API exploitation)** — upgrade jar in `tools/sdk/dependencies/` to >= 2.4.21.
4. **xalan 2.7.2 (High — admin-authenticated only, no HAProxy mitigation)** — suppress in Aikido with justification or replace with Saxon-HE. `lib/portal/xalan.jar`.
5. **spring-webmvc CVE-2018-1271 (High — CVE-2018-1271 effectively mitigated by `/WEB-INF` block)** — consider adding `path_reg (\.\./|%2e%2e)` rule for belt-and-suspenders, then close in Aikido.
6. **SAST path traversal (High — WEB-INF target blocked, others need verification)** — audit WebServerServlet/DynamicResourceServlet; add HAProxy path-traversal regex if needed.
7. **SAST SSRF (High — partially mitigated via JSONWS allowlist)** — audit non-JSONWS callers of SimpleHTTPSender/SocketUtil for user-controlled URLs.
8. **SAST SQL injection (High — startup code, not web-reachable)** — parameterise as housekeeping; low urgency.
9. **SAST Object deserialization (High — LocalProcessLauncher not web-reachable; BeanPropertiesImpl needs audit)** — triage each flagged file individually.
10. **Exposed secrets (High — all FPs)** — bulk-ignore in Aikido; add `.idea/` to `.gitignore`.
11. **xerces, commons-lang, struts** — carried over from SECURITY_REVIEW-1.md; no change in status.
