# Security Review — Aikido Scan Results

**Repository:** axiell/liferay-portal (`arena-7.0.6-ga7`)
**Scan date:** 2026-06-16 (run ID 27645018133)
**Total issues:** 20
**Source:** https://app.aikido.dev/repositories/2327591

## CI Failure Note

The GitHub Actions Aikido job failed with `Endpoint no longer accessible` when polling for the final verdict — this was an Aikido API-side transience, not a gating failure. The scan itself completed and uploaded all results successfully.

---

## Triage by Runtime Exposure

### Runtime — `lib/portal/` (ships in portal WAR `WEB-INF/lib`)

All genuinely in production. `lib/portal/` JARs are copied to the portal classpath on deploy.

| Severity | Dependency | JAR | Aikido finding |
|---|---|---|---|
| Critical | `com.thoughtworks.xstream:xstream` | `xstream.jar` | Attacker can inject own code to run (RCE) |
| Critical | `commons-collections:commons-collections` | `commons-collections.jar` | Unsafe deserialization → RCE |
| Critical | `Apache Commons FileUpload` | `commons-fileupload.jar` | Improper access control |
| Critical | `Log4j` | `log4j.jar` | SQL injection attack possible |
| High | `commons-beanutils:commons-beanutils` | `commons-beanutils.jar` | Improper access control |
| High | `org.apache.struts:struts-core` | `struts-core.jar` | Missing input validation |
| High | `org.json:json` | `json-java.jar` | Memory corruption → crash/RCE |
| High | `xerces:xercesImpl` | `xercesImpl.jar` | DoS via infinite loop |
| High | `org.apache.httpcomponents:httpclient` | `httpclient.jar` | Missing input validation |
| Medium | `org.apache.struts:struts-tiles` | `struts-tiles.jar` | Path traversal |
| Medium | `commons-lang:commons-lang` | `commons-lang.jar` | DoS via infinite loop |
| Medium | `commons-io:commons-io` | `commons-io.jar` | Missing input validation |

XStream is actively used by the export/import kernel (`portal-kernel/src/com/liferay/exportimport/kernel/xstream/`). A task for XStream is already open and assigned to Igor Sitdikov.

### Runtime — `opensocial-portlet` (legacy WAR with `.lfrbuild-portal`)

Deploys as its own WAR with an independent `WEB-INF/lib`. Shiro and Sanselan come exclusively from here.

| Severity | Dependency | Version | Aikido finding |
|---|---|---|---|
| Critical | `org.apache.shiro:shiro-core` | 1.0.0-incubating | Authorization bypass |
| Critical | `org.apache.shiro:shiro-web` | 1.0.0-incubating | Path traversal |
| Medium | `org.apache.sanselan:sanselan` | 0.97-incubator | DoS via infinite loop |

**Action required:** Confirm whether Arena deployments include the opensocial portlet. If not deployed, these three issues should be ignored in Aikido rather than remediated. If deployed, shiro 1.0.0-incubating is severely outdated and should be upgraded or the portlet removed.

### Runtime — OSGi modules (deployed bundles)

| Severity | Dependency | Where | Aikido finding |
|---|---|---|---|
| Medium | `org.apache.commons:commons-lang3` | `petra-doulos`, `portal-template-soy` (both `.lfrbuild-portal`) | DoS via infinite loop |
| Medium | `com.google.protobuf:protobuf-java` | `portal-search-elasticsearch7-impl` build bundle | Missing input validation |
| Medium | `bootstrap` / `bootstrap-sass` | Compiled into theme CSS/JS via `liferay-theme-deps-7.0` | XSS attack possible |

Bootstrap XSS risk is limited to Bootstrap JS components (data-attribute driven dropdowns/tooltips). Only exploitable if those components are used without sanitisation on user-controlled attributes.

### Test/build artifacts only — not runtime

| Dependency | Location | Reason not runtime |
|---|---|---|
| JARs inside `sample-struts-portlet.autodeployed.war` | `portal-osgi-web-wab-generator/src/test/resources/` | Unit test fixture, never deployed |
| Extracted `elasticsearch-7.17.14/` JARs | `portal-search-elasticsearch7-impl/build/` | Local build extraction; bundled inside the OSGi JAR, not independently on classpath |

---

## Priority Order for Remediation

1. **XStream (Critical, RCE)** ✓ — upgraded 1.4.7→1.4.21. Security framework (deny-all + allowlist via `NoTypePermission.NONE` + `allowTypes`) was already wired in `PortletDataContextImpl`. JAR swap only, no code changes required.
2. **commons-collections (Critical, RCE)** — unsafe deserialization; frequently chained with XStream exploits.
3. **Log4j (Critical)** — ~~confirm which CVE Aikido is flagging~~ CVE-2019-17571 (`SocketServer` RCE via deserialization). **Not exploitable:** Liferay never starts a SocketServer; only file appenders are used (`portal-log4j.xml`). `log4j-over-slf4j` cannot replace `log4j.jar` here because `log4j-extras` (`EnhancedPatternLayout`, `RollingFileAppender`) depends on log4j 1.x internals. Full remediation requires a Log4j 2.x migration (out of scope for security-patch branch). **Status: accepted risk — SocketServer not reachable.**
4. **commons-fileupload (Critical)** — improper access control on file upload handling.
5. **shiro-core / shiro-web (Critical)** — conditional on opensocial being deployed.
6. **High severity deps** — beanutils ✓ upgraded 1.9.2→1.9.4; struts-core: **mitigated** — `PortalRequestProcessor.processPopulate()` blocks `class.*`/`[class]` parameters via `struts.portlet.ignored.parameters.regexp` (CVE-2014-0114); 1.3.10 is final 1.x release, no upstream patch. json, xerces, httpclient: assess exploitability in context of how each is used.
7. **Medium severity** — commons-io ✓ upgraded 2.5→2.15.1; commons-lang3 ✓ upgraded 3.4→3.14.0 (petra-doulos, portal-template-soy build.gradle); commons-lang 2.6: final 2.x release (EOL), no same-package upgrade path; struts-tiles 1.3.10: same EOL situation as struts-core; protobuf: inside elasticsearch bundle, not independently on classpath (no action needed); bootstrap 4.3.1: transitive via liferay-theme-deps-7.0, XSS only via unsanitised user-controlled data-* attributes on Bootstrap JS components — accepted risk.
8. **Low — `core` ReDoS** — likely inside the elasticsearch bundle; low exploitability.
