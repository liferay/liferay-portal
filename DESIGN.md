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

See `SECURITY_REVIEW.md` for the full Aikido scan triage. Critical open items:

- **XStream (RCE)** — used in export/import kernel (`portal-kernel/src/com/liferay/exportimport/kernel/xstream/`). Assigned to Igor Sitdikov.
- **commons-collections (RCE)** — unsafe deserialization, often chained with XStream exploits.
- **Log4j** — `lib/portal/log4j.jar` is 1.x (not Log4Shell-vulnerable, but has its own CVE set).
- **commons-fileupload** — improper access control.
- **opensocial-portlet shiro 1.0.0-incubating** — critical auth bypass/path traversal; verify whether Arena deployments include this portlet before remediating or suppressing.

Consult `SECURITY_REVIEW.md` for full triage, priority order, and suppression guidance.
