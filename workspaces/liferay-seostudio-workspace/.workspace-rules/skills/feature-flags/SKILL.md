---

description: Audit, prompt, and enable Liferay feature flags required by a given workflow. Use when a skill needs a flag set (LPD-63311 for MCP, LPD-35443 for Site API, LPD-17564 for object collaborators, etc.), when the user asks to enable a flag, when an API returns 404, or when a Headless endpoint silently returns 400 UnsupportedOperationException.
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

Persist changes per the environment — see "Enabling a Flag" below. Then bounce or restart according to the environment.

### Verify

Reread the state and confirm every required flag is in the desired state. Report success or remaining gaps to the caller skill.

## Enabling a Flag

### Tomcat

Add to the source file, then sync to the runtime and restart:

```
feature.flag.LPD-XXXXX=true
```

- **Source**: `configs/local/portal-ext.properties` or `configs/common/portal-ext.properties` (or the active environment from `liferay.workspace.environment`).
- **Runtime sync**: copy the changed file to `bundles/portal-ext.properties`. The destination is destructive — diff first if `bundles/portal-ext.properties` may have hand edits.
- **Restart**: stop with `bundles/tomcat*/bin/shutdown.sh`, wait for the HTTP probe to fail, start with `bundles/tomcat*/bin/startup.sh`, wait for the HTTP probe to succeed.

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

**Worked example** — `feature.flag.LPD-35443=true` becomes:

```
LIFERAY_FEATURE_PERIOD_FLAG_PERIOD_LPD_MINUS_35443=true
```

**Best practice**: rather than hand encoding, look up the exact `Env:` variable name shown beneath each property in the Liferay Learn *Portal Properties* reference, and cross check against existing entries in `liferay.env` if present.

## Critical Flags (Quick Reference)

For the full table see `rules/feature-flags-catalog.md`.

| Flag | Default | Unlocks |
| --- | --- | --- |
| `LPD-63311` | off | MCP server (current path `/o/mcp`; `/o/mcp/sse` only on 2025.Q4) |
| `LPD-35443` | off | Headless Admin Site public layout API |
| `LPD-39244` | off | Headless Admin Fragment / composition API |
| `LPD-17564` | off | Object collaborators API |
| `LPD-52006` | off | Object entry folders |
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

## References

- Feature Flags admin doc: `https://learn.liferay.com/w/dxp/security-and-administration/administration/configuring-liferay/feature-flags`