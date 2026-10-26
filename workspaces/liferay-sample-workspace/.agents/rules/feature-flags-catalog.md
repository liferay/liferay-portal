---

paths:
  - "**/*.config"
  - "**/portal-ext.properties"

---

# Feature Flags Catalog

> **Before authoring:** Load `feature-flags` before enabling anything here — it owns the per environment mechanism and the restart.

Liferay feature flags that gate APIs and behavior the skills depend on. The full registry lives in `portal-impl/src/portal.properties` of the portal source. This file is the working set the skills reach for.

Defaults change between quarterly releases. Reverify against the running portal at Control Panel → Instance Settings → Feature Flags. The `feature-flags` skill is the canonical source of truth at runtime.

## Critical Flags for Site, Object, and MCP Workflows

| Key | Default | Status | Unlocks | Used By | Dependent On |
| --- | --- | --- | --- | --- | --- |
| `LPD-63311` | off | beta; release on 2026.Q3+ | MCP server at `/o/mcp` (`/o/mcp/sse` on 2025.Q4); on 2026.Q3, only together with the `MCPServerConfiguration` `enabled` switch (see `skills/mcp-server/SKILL.md`) | `build-site`, `manage-objects` (when MCP first) | none |
| `LPD-38869` | on | deprecation | Private layout REST access | `manage-pages` (private pages) | none |
| `LPD-35443` | off | beta on 2026.Q2; release on 2026.Q3 | Page Management API: `headless-admin-site` `site-pages` and `master-pages` (`400 UnsupportedOperationException` "Feature flag LPD-35443 is disabled" without it) | `manage-pages`, `theme-and-design`, `build-site` | none |
| `LPD-39244` | off | release on master; beta on 2026.Q3; hidden dev on 2026.Q2; absent on 2026.Q1 | Headless Admin Fragment / page composition REST API | `manage-pages`, `scaffold-fragment` | none |
| `LPD-74328` | off | dev (hidden: no `.type` line, so not in the UI; the property still works) on every 2026 release | Page element / page-specification creation and update (headless-admin-site) | `manage-pages`, `build-site` | none |
| `LPD-17564` | off; removed in 2026.Q3 | release | CMS (its flag title). On 2026.Q1 and 2026.Q2 it also gates sharing an entry with one user and an object definition's `workflowDefinitionLinks`. Gone from 2026.Q3, where neither needs a flag (`manage-roles-permissions` → "Share an Entry With One User") | `manage-objects`, `manage-roles-permissions`, `manage-object-logic` | `LPD-34594` (and `LPD-11235` on 2026.Q1) |
| `LPD-32867` | off | dev (hidden: no `.type` line) on every 2026 release | Content provider integration for dynamic content sets (headless-delivery) | (informational) | none |
| `LPD-36010` | on | deprecation (system) | Legacy object behavior toggle | (informational) | none |
| `LPD-78863` | on | deprecation (system) | DXP Segments, including segment membership evaluation (`BaseSegmentsEntryProvider.getSegmentsEntryIds` returns no segments while it is off). Off means every user gets the default Segment Experience. A new database stores it as off; see the gotcha below | `manage-pages`, `build-site` (persona experiences) | none |
| `LPS-135430` | off | release | Any `storageType` on an object definition created over REST, including `function#<cet-erc>` for an `objectEntryManager` CET. Without it the create answers `400 ObjectDefinitionStorageTypeException`, and a site initializer whose tree holds such a definition fails the same way | `integrate-external-data`, `manage-objects` | none |
| `LPS-164563` | off | release | Data Sets: the `data-set-admin` API (`/o/data-set-admin/...`) and the Data Set admin app; `404` without it. Note the `LPS` prefix | `manage-data-sets` | `LPD-34594` on 2026.Q1 and 2026.Q2; none from 2026.Q3 |

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

A Tomcat bounce is required for most flags to take effect. The skill handles the bounce, under the session restart policy in `liferay-rules.md` → "Server Restarts".

### Gotcha: A New Database Stores Every Deprecation Flag as Off

On a database created new, every flag of type `deprecation` reads off, whatever `portal.properties` or `portal-ext.properties` says. On the first start, `CompanyModelListener` in `feature-flag-web` stores `false` for each Deprecation flag in the portal preferences, for the system scope and for each company, and marks the pass done (`deprecationProcessed`). A stored value wins over the property (`PreferenceAwareFeatureFlag.isEnabled`), so neither `feature.flag.<KEY>=true` nor a restart turns the flag on. An upgraded database is marked done with nothing stored (`FeatureFlagUpgradeProcess`), so there the property still decides. From source: `CompanyModelListener.java:50-101`, `PreferenceAwareFeatureFlag.java:28-40`, `FeatureFlagUpgradeProcess.java:32-68`; the same code is in 2026.Q1, 2026.Q2, and 2026.Q3. Verified on 2026.Q3 with `LPD-78863`.

Turn such a flag on in the UI. A flag with `feature.flag.<KEY>.system=true` (`LPD-78863`, `LPD-36010`) is under System Settings → Feature Flags → Deprecation; any other Deprecation flag (`LPD-38869`) is under Instance Settings → Feature Flags → Deprecation. The UI calls `POST /o/com-liferay-feature-flag-web/set-enabled`, which accepts only a signed in portal session, not Basic auth (`FeatureFlagApplication.java:33-40`). A direct call took effect on 2026.Q3.8 but not on 2026.Q2.12 (`feature-flags` → "Live, No Restart"). Verify by effect: `Liferay.FeatureFlags` on a rendered page shows the value in effect.

Without the UI, delete the stored value while the portal is stopped; after the next start the property decides, and the pass does not run again. The row is in `portalpreferencevalue`, `namespace` `com.liferay.portal.kernel.feature.flag.FeatureFlag`, `key_` the flag, under the `portalpreferences` row with `ownertype` `1` and `ownerid` `0` for a system flag or the company ID otherwise.

With `LPD-78863` off, persona Segment Experiences collapse to the default for every user. The two symptoms are (a) a role keyed persona page serves the default composition to everyone, and (b) two users with different segments resolve to the same `segmentsExperienceId` in the page HTML.

## Verifying State

Three sources, in trust order:

1. Runtime configuration at Control Panel → Instance Settings → Feature Flags, and System Settings → Feature Flags for flags with `feature.flag.<KEY>.system=true`. Each lists Release, Beta, Deprecation, and Developer tabs. A Deprecation flag on a new database is stored as off (see the gotcha above).

1. `bundles/osgi/configs/com.liferay.portal.feature.flag*.config` for runtime overrides.

1. `<active-env>/portal-ext.properties` for declared overrides.

A flag missing from both files takes the portal default declared by `feature.flag.<KEY>` in the running JVM (built from `portal-impl/src/portal.properties` and downstream property files).

## References

- Configuring feature flags: `https://learn.liferay.com/w/dxp/security-and-administration/administration/configuring-liferay/feature-flags`
- Quarterly default changes:
	- `https://learn.liferay.com/w/dxp/self-hosted-installation-and-upgrades/upgrading-liferay/deprecations-and-breaking-changes-reference/2025-deprecations-and-breaking-changes/2025-q4-default-setting-and-feature-flag-changes`
	- `https://learn.liferay.com/w/dxp/self-hosted-installation-and-upgrades/upgrading-liferay/deprecations-and-breaking-changes-reference/2025-deprecations-and-breaking-changes/2025-q3-default-setting-and-feature-flag-changes`