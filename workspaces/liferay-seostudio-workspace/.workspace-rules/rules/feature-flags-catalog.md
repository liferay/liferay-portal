# Feature Flags Catalog

Liferay feature flags that gate APIs and behavior the skills depend on. The full registry lives in `portal-impl/src/portal.properties` of the portal source. This file is the working set the skills reach for.

Defaults change between quarterly releases. Reverify against the running portal at Control Panel → Instance Settings → Feature Flags. The `feature-flags` skill is the canonical source of truth at runtime.

## Critical Flags for Site, Object, and MCP Workflows

| Key | Default | Status | Unlocks | Used By | Dependent On |
| --- | --- | --- | --- | --- | --- |
| `LPD-63311` | off | beta | MCP server at `/o/mcp/sse`; `call-http-endpoint` tool | `build-site`, `manage-objects` (when MCP first) | none |
| `LPD-35443` | off | beta | Headless Admin Site public layout REST API | `build-site`, `manage-pages` | none |
| `LPD-38869` | on | deprecation | Private layout REST access | `manage-pages` (private pages) | none |
| `LPD-39244` | off | beta | Headless Admin Fragment / page composition REST API | `manage-pages`, `scaffold-fragment` | none |
| `LPD-74328` | off | beta | Page element / page-specification creation and update (headless-admin-site) | `manage-pages`, `build-site` | `LPD-35443` |
| `LPD-17564` | off | release | Object collaborators API (per entry permissions) | `manage-objects`, `manage-roles-permissions` | none |
| `LPD-52006` | off | beta | Object entry folders (nested folder structure) | `manage-objects` | `LPD-17564` |
| `LPD-32867` | off | beta | Content provider integration for dynamic content sets (headless-delivery) | (informational) | none |
| `LPD-36010` | on | deprecation | Legacy object behavior toggle | (informational) | none |

## How to Read This Table

- **Default**: the value when no override is set in `portal-ext.properties`. `off` means the flag must be enabled before its features work.
- **Status**: `beta` means experimental and may move; `release` means stable behind the flag for a transition period; `deprecation` means the previous default behavior is being phased out.
- **Unlocks**: the user visible capability the flag controls.
- **Used By**: the skills that probe this flag.
- **Dependent On**: prerequisite flags that must be enabled in tandem.

## Enabling a Flag

The `feature-flags` skill writes one line per flag to the active environment's `portal-ext.properties`:

```properties
feature.flag.LPD-XXXXX=true
```

A Tomcat bounce is required for most flags to take effect. The skill handles the bounce.

## Verifying State

Three sources, in trust order:

1. Runtime configuration at Control Panel → Instance Settings → Feature Flags (Beta or Release tab).

1. `bundles/osgi/configs/com.liferay.portal.feature.flag*.config` for runtime overrides.

1. `<active-env>/portal-ext.properties` for declared overrides.

A flag missing from both files takes the portal default declared by `feature.flag.<KEY>` in the running JVM (built from `portal-impl/src/portal.properties` and downstream property files).

## References

- Configuring feature flags: `https://learn.liferay.com/w/dxp/security-and-administration/administration/configuring-liferay/feature-flags`
- Quarterly default changes:
	- `https://learn.liferay.com/w/dxp/self-hosted-installation-and-upgrades/upgrading-liferay/deprecations-and-breaking-changes-reference/2025-deprecations-and-breaking-changes/2025-q4-default-setting-and-feature-flag-changes`
	- `https://learn.liferay.com/w/dxp/self-hosted-installation-and-upgrades/upgrading-liferay/deprecations-and-breaking-changes-reference/2025-deprecations-and-breaking-changes/2025-q3-default-setting-and-feature-flag-changes`