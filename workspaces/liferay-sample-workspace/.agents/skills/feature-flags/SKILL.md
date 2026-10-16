---

description: Audit, prompt, and enable Liferay feature flags required by a given workflow. Use when a skill needs a flag set (LPD-63311 for MCP, LPD-17564 for CMS and object collaborators on 2026.Q1 and 2026.Q2, etc.), when the user asks to enable a flag, when an API returns 404, or when a Headless endpoint silently returns 400 UnsupportedOperationException.
name: feature-flags

---

# Feature Flags

Detect the current state of feature flags, report gaps against required sets, and prompt before writing. Many flag gated Headless endpoints fail silently — `400 UnsupportedOperationException` with no log output — when the required flag is off. Always check proactively.

## When to Invoke

- A skill declares one or more required flags (the caller passes the list).
- The user says "check feature flags", "enable LPD-XXXXX", or "what flags do I need for this".
- An HTTP 404 on a Headless endpoint suggests the flag is off.
- An HTTP 400 returns `UnsupportedOperationException` with no useful log output — almost always a flag gated endpoint with the flag disabled.

## Workflow

### Read Current State

Check both sources, in order:

1. `bundles/portal-ext.properties` for `feature.flag.LPD-XXXXX=true|false` entries.

1. `bundles/osgi/configs/com.liferay.portal.feature.flag*.config` for runtime overrides.

A flag missing from both files takes the default declared in `rules/feature-flags-catalog.md`.

When Tomcat is running and the user is signed in as `test@liferay.com`, also fetch the UI source of truth at `http://localhost:${PORT}/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_factoryPid=com.liferay.portal.feature.flag.internal.configuration.FeatureFlagsConfiguration` for confirmation.

For Docker workspaces, see "Enabling a Flag" below — flag state is read from `liferay.env` (prebuilt image) or `configs/docker/portal-ext.properties` (custom image) instead.

### Report the Gap

Print a table with: flag, required state, current state, what the flag unlocks, source line. Use `rules/feature-flags-catalog.md` for the metadata. Group rows by status (`OK`, `NEEDS_ENABLE`, `NEEDS_DISABLE`).

### Prompt Before Writing

Never edit `portal-ext.properties` (or `liferay.env`) without explicit confirmation. Ask once, list every flag the write will touch, and only proceed on a clear yes. Do not bundle unrelated flags into the same prompt.

### Write per Environment

Persist changes per the environment — see "Enabling a Flag" below. Then bounce or restart according to the environment. Follow the restart policy in `liferay-rules.md` → "Server Restarts". Prefer the live runtime toggle below when it applies, since it needs no restart.

### Verify

Reread the state and confirm every required flag is in the desired state. Report success or remaining gaps to the caller skill.

## Enabling a Flag

### Live, No Restart (Runtime Toggle)

A flag can be flipped on the running portal with `POST /o/com-liferay-feature-flag-web/set-enabled`, form params `key` and `enabled=true`. It takes **session auth only** (a browser session or `Liferay.Util.fetch`), not Basic auth or OAuth. `POST /o/com-liferay-feature-flag-web/is-enabled` with `key` reads one flag back. The change takes effect immediately with no restart and is stored as a portal preference, the same mechanism as the Feature Flags toggle in Instance Settings (System Settings for system flags). Prefer it for iterating, and still record the flag in `portal-ext.properties` so a fresh environment matches.

### Gotcha: A Stored Toggle Overrides `portal-ext.properties`

Once a flag has been toggled in the UI or through `set-enabled`, the stored preference wins over `feature.flag.<KEY>` in `portal-ext.properties` (or `liferay.env`) on every boot. The property is only the default when no preference exists. So a flag someone switched off in the UI stays off after a restart, whatever the properties file says. Fix it with the same toggle.

A new database does this to every Deprecation flag. On the first start of each company, the portal stores every flag of type `deprecation` as off, so `feature.flag.<KEY>=true` in `portal-ext.properties` never applies to one. Turn it on with the toggle. See `rules/feature-flags-catalog.md` → "Gotcha: A New Database Stores Every Deprecation Flag as Off".

System flags, marked `feature.flag.<KEY>.system=true` in `portal.properties` (for example `LPD-78863`), live on the **Deprecation** or **Developer** tab under **System Settings → Feature Flags**, not Instance Settings. The failure is silent: the gated feature behaves as if off. **Verify by effect, not by the properties file.**

### Tomcat

Add to the source file, then sync to the runtime and restart:

```
feature.flag.LPD-XXXXX=true
```

- **Source**: `configs/local/portal-ext.properties` or `configs/common/portal-ext.properties` (or the active environment from `liferay.workspace.environment`).
- **Runtime sync**: copy the changed file to `bundles/portal-ext.properties`. The destination is destructive — diff first if `bundles/portal-ext.properties` may have hand edits.
- **Restart** (per `liferay-rules.md` → "Server Restarts"): stop with `bundles/tomcat*/bin/shutdown.sh`, wait for the HTTP probe to fail, start with `bundles/tomcat*/bin/startup.sh`, wait for the HTTP probe to succeed.

Do NOT use `blade gw initBundle` — it deletes and reinitializes the entire bundle directory.

### Docker (Prebuilt Image)

For workspaces using a prebuilt `liferay/dxp` image, flags are set as environment variables in `liferay.env` — not in `portal-ext.properties`. Liferay converts these env vars to portal properties at startup. Add the encoded variable to `liferay.env` and restart the container (`docker compose down && docker compose up`).

### Docker (Custom Image)

If the compose file uses `build:`, add the flag to `configs/docker/portal-ext.properties` or `configs/common/portal-ext.properties`, then rebuild the image to apply changes.

## Env Var Encoding (Docker Prebuilt Image)

Liferay derives the env var name by: (1) prefixing `LIFERAY_`, (2) **uppercasing the whole property name**, and (3) replacing each character that is not a letter, digit, or underscore with its CharPool/Unicode endpoint wrapped in underscores. Letters and digits pass through unchanged (the name just must not *start* with a digit).

| Character in property name | Env var encoding |
|---|---|
| `.` (dot) | `_PERIOD_` (or `_46_` Unicode) |
| `-` (hyphen) | `_MINUS_` |
| letters / digits | passed through; letters uppercased — **no per character encoding** |

**Worked example** — `feature.flag.LPD-63311=true` becomes:

```
LIFERAY_FEATURE_PERIOD_FLAG_PERIOD_LPD_MINUS_63311=true
```

**Best practice**: rather than hand encoding, look up the exact `Env:` variable name shown beneath each property in the Liferay Learn *Portal Properties* reference, and cross check against existing entries in `liferay.env` if present.

## Critical Flags (Quick Reference)

For the full table see `rules/feature-flags-catalog.md`.

| Flag | Default | Unlocks |
| --- | --- | --- |
| `LPD-63311` | off | MCP server (current path `/o/mcp`; `/o/mcp/sse` only on 2025.Q4); on 2026.Q3, only together with the `MCPServerConfiguration` `enabled` switch, which is not a flag (see `skills/mcp-server/SKILL.md`) |
| `LPD-39244` | off | Headless Admin Fragment / composition API (absent on 2026.Q1, hidden dev on 2026.Q2, beta on 2026.Q3) |
| `LPD-17564` | off | CMS, including the object collaborators API, on 2026.Q1 and 2026.Q2 (needs `LPD-34594`). The flag is gone from 2026.Q3, where neither needs a flag |
| `LPD-32867` | off | Content provider for dynamic content sets (headless-delivery) |
| `LPD-74328` | off | Page element / page-specification creation and update |

## Diagnosing an Unknown Flag Gate

If a `headless-admin-site` (or other Headless) operation returns `400 UnsupportedOperationException` and the logs are silent, a missing feature flag is the most likely cause. The Java pattern is:

```java
if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-XXXXX")) {
    throw new UnsupportedOperationException();
}
```

JAX-RS converts the bare `UnsupportedOperationException()` to a 400 with no useful response body. Ask the user to identify the relevant flag — it is embedded in the `*ResourceImpl.class` of the implementation jar, but requires decompiling to find. Alternatively, grep the Liferay Portal source on GitHub for `LPD-` references in the relevant Resource Impl file.

## See Also

- `skills/workspace-init/SKILL.md` — broader workspace context (Tomcat vs Docker prebuilt vs custom).
- `skills/deploy-and-verify/SKILL.md` — destructive copy warning for the `portal-ext.properties` sync step.

## Success Signal

TODO / inferred — verify against a running bundle. The `### Verify` step above (reread state; the endpoint that prompted the check no longer returns `404` or `400 UnsupportedOperationException`) is the observable completion check; confirm on a live bundle.

## References

- Feature Flags admin doc: `https://learn.liferay.com/w/dxp/security-and-administration/administration/configuring-liferay/feature-flags`