---

alwaysApply: true
description: General Liferay standards, architectural principles, and rule routing for AI agents in a Liferay Workspace

---

# Liferay Workspace Rules

This file is the entry point for any AI agent operating inside this workspace. It establishes the runtime context every skill below depends on.

## Establish Context First

Before answering, identify three things:

1. **Workspace root**: the directory containing `gradle.properties` and `settings.gradle`. If neither exists, the user has not initialized a workspace; invoke the `workspace-init` skill.

1. **DXP version**: read `liferay.workspace.product` from `gradle.properties`. Quarterly releases (`-Qx`) and 7.4 lines use the modern path (Client Extensions, Objects, Fragments). Versions below 7.4 use legacy OSGi modules.

1. **Bundle state**: `bundles/` exists when `blade server init` has run. The Tomcat install lives at `bundles/tomcat*/`. The active HTTP port is the `port` attribute on the `<Connector protocol="HTTP/1.1">` element in `bundles/tomcat*/conf/server.xml`. Default is 8080.

## Project Paths

| Purpose | Path |
| --- | --- |
| Client extensions | `client-extensions` |
| OSGi modules | `modules` |
| Themes | `themes` |
| Per environment properties | `configs/{common,local,dev,uat,prod,docker}` |
| Runtime OSGi configs | `bundles/osgi/configs` |
| Logs | `bundles/tomcat*/logs/catalina.out` and `bundles/logs/liferay.<YYYY-MM-DD>.log` |
| Deployed bundles | `bundles/osgi/modules` and `bundles/osgi/client-extensions` |

`configs/common` holds shared settings. `configs/local` is the default for development. Promotion order is `local` to `dev` to `uat` to `prod`.

## Tooling

Use Blade as the primary CLI. Prefer `blade gw <task>` over invoking Gradle directly; this guarantees the workspace Gradle wrapper. Key commands:

- `blade init` to scaffold a workspace
- `blade server init` to download the bundle
- `blade server start --tail` to start Tomcat and tail the log
- `blade gw deploy` to package and deploy a module or client extension
- `blade gw tasks` to list available Gradle tasks

## AI Agent Guidelines

- **Parallel execution**: when tailing logs during deployment, run the deploy command as a nonblocking background process so log watching can happen concurrently. Use whatever background execution mechanism your tool provides.
- **Verification**: success is defined by runtime activation (`STARTED` log status), not just a successful command exit code.
- **CLI capability check**: before using a CLI tool to scaffold or initialize a project, verify what it supports (e.g., `blade --help`, `blade <command> --help`) rather than assuming. Do not assume a command supports a given task without checking first.

## Preflight Rule for New Code Generation

Before writing the **first line** of any artifact below, load its skill. Always, and before authoring — not after something fails. Glob based autoloading does not fire on an empty workspace, so nothing else will surface these.

| About to author | Load first |
| --- | --- |
| Object definition, field, picklist, relationship | `manage-objects` |
| Object action, notification template, workflow | `manage-object-logic` |
| Page, `page-definition.json`, navigation menu | `manage-pages` |
| Fragment (`fragment.json`, `index.html`) | `scaffold-fragment` |
| Theme, style book, master page | `theme-and-design` |
| Any `client-extension.yaml` | `scaffold-client-extension` |
| Role, or any `resource-permissions.json` grant | `manage-roles-permissions` |
| Commerce product or SKU | `commerce-catalogs` |

Most real tasks match two or more rows — load all of them. Skipping one is not the smaller risk: much of what these skills document are failures that are **silent**, where the build succeeds and the defect only shows later, and the skill is the only place that behavior is written down. Reading portal source instead is not a substitute — the source shows what the code does, not which of its behaviors have already cost someone a day.

If a skill turns out to be wrong or to omit the answer, fix the skill as part of the task rather than working around it locally.

## MCP Server

Liferay provides an MCP server for AI agent integration, gated by a feature flag and available in specific DXP versions. When present and enabled, prefer MCP over raw `curl` for content, page, and object operations. See `skills/mcp-server/SKILL.md` for setup, transport details, version requirements, and quirks.

## Skill Router

Every skill lives under `skills/` and owns one workflow. Match the user's intent to a skill below and load it on demand — even in an empty workspace (see the Preflight Rule above).

| User Intent | Skill |
| --- | --- |
| First time user: a guided first run creating a workspace and starting the server | `initial-setup-guide` |
| Set up, initialize, or repair a workspace and bundle | `workspace-init` |
| Check, prompt for, or enable a required feature flag | `feature-flags` |
| Deploy a target and confirm it started | `deploy-and-verify` |
| Set up the MCP server or diagnose an MCP call | `mcp-server` |
| Enforce production readiness on code bound for a nonlocal environment | `production-standards` |
| Create or change an object definition, field, relationship, picklist, or validation | `manage-objects` |
| Add object business logic — actions, workflows, notifications | `manage-object-logic` |
| Create the OAuth application a client extension needs, or call a CET from browser code | `setup-oauth` |
| Back an object with an external REST, database, or SaaS data source | `integrate-external-data` |
| Build a page fragment or reusable page component | `scaffold-fragment` |
| Build a form field fragment that binds to an object field | `scaffold-form-fragment` |
| Create pages, navigation, SEO, or page and display templates | `manage-pages` |
| Change the theme, colors, fonts, master page, or style book | `theme-and-design` |
| Build a React based Custom Element widget | `react-custom-elements` |
| Scaffold any client extension type | `scaffold-client-extension` |
| Walk a beginner through a first client extension | `guided-client-extension` |
| Create roles or grant permissions on objects, pages, or sites | `manage-roles-permissions` |
| Manage environment configs, promote to UAT, or capture a site initializer | `manage-environments` |
| Manage Commerce catalogs, products, SKUs, or B2B accounts | `commerce-catalogs` |
| Deploy and operate a Liferay Cloud (LXC) project via `lcp` | `manage-cloud-project` |
| Build an entire site experience from one prompt (orchestrator; calls the others) | `build-site` |

Site building is **site initializer first**: the `siteInitializer` CET tree is the single source of truth. Build by triggering the initializer, then iterate by editing the source tree and applying each change live (theme, objects, fragments) or by reprovisioning (pages). See `rules/site-initializer-format.md`.

## Reference Cards

Reference cards under `rules/` hold the data skills look up. Skills cite the card path explicitly. Every card is loaded in every session, so a card states the **fact** and routes to the skill that holds the **procedure** — read the card, then load the skill it names.

- `rules/client-extension-types.md` — client extension types, their yaml, and which types may share a project
- `rules/guest-access.md` — what an anonymous visitor can and cannot read; read before building anything public
- `rules/headless-apis.md` — REST modules, base URIs, OAuth scopes
- `rules/feature-flags-catalog.md` — flag table with defaults and dependencies
- `rules/site-initializer-format.md` — site initializer directory tree and per entity file formats
- `rules/object-actions-catalog.md` — triggers, conditions, action types
- `rules/oauth-scopes.md` — `Liferay.*` scope strings for `oAuthApplicationHeadlessServer` blocks in CET scaffolding
- `rules/page-types.md` — page types and their applicable APIs

## Information Sources

The authoritative documentation is [learn.liferay.com](https://learn.liferay.com); search `site:learn.liferay.com <topic>` to find a topic.

The Liferay Portal source code at [github.com/liferay/liferay-portal](https://github.com/liferay/liferay-portal) is canonical for architectural patterns and code samples; working client extension examples live at `workspaces/liferay-sample-workspace/client-extensions/`.