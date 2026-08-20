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

Before generating a new Fragment, Client Extension, Object definition, or Commerce product/SKU from scratch, **explicitly load the relevant skill from the Skill Index below**, even if no matching files exist yet in the workspace. Glob based autoloading will not fire on an empty workspace; this preflight step ensures the correct patterns and antihallucination guards are applied from the first line of code.

## MCP Server

Liferay provides an MCP server for AI agent integration, gated by a feature flag and available in specific DXP versions. When present and enabled, prefer MCP over raw `curl` for content, page, and object operations. See `skills/mcp-server/SKILL.md` for setup, transport details, version requirements, and quirks.

## Skill Index

Skills live under `skills/` and load on demand. Each addresses one workflow.

**Foundations**
- `workspace-init` — bootstrap a workspace and bundle
- `feature-flags` — audit and enable required flags
- `deploy-and-verify` — deploy a target and confirm startup
- `mcp-server` — MCP server setup, workflow, quirks
- `production-standards` — production readiness guardrails

**Backend (data and logic)**
- `manage-objects` — object definitions, fields, relationships, picklists, validations
- `manage-object-logic` — object actions, workflows, notifications
- `setup-oauth` — companion OAuth applications for client extensions
- `integrate-external-data` — back objects with external services

**Frontend (look and composition)**
- `scaffold-fragment` — page fragments with editable regions
- `manage-pages` — site pages, navigation, SEO, page templates
- `theme-and-design` — themes, master pages, style books
- `react-custom-elements` — React based Custom Element CETs

**Cross cutting**
- `scaffold-client-extension` — any client extension type
- `guided-client-extension` — beginner walkthrough for a first Client Extension
- `manage-roles-permissions` — roles, ACL, object and page permissions
- `manage-environments` — `configs/{env}/`, data migration, siteInitializer capture
- `commerce-catalogs` — Commerce catalogs, products, SKUs, B2B onboarding

**Cloud**
- `manage-cloud-project` — deploy and operate Liferay Cloud (LXC) projects via the `lcp` CLI

**Orchestrator**
- `build-site` — compose objects, pages, fragments, and roles into a complete site experience

Site building is **site initializer first**: the `siteInitializer` CET tree is the single source of truth. Build by triggering the initializer, then iterate by editing the source tree and applying each change live (theme, objects, fragments) or by reprovisioning (pages). See `rules/site-initializer-format.md`.

## Reference Cards

Reference cards under `rules/` hold the data skills look up. Skills cite the card path explicitly.

- `rules/client-extension-types.md` — client extension types with their yaml and file layout
- `rules/headless-apis.md` — REST modules, base URIs, OAuth scopes
- `rules/feature-flags-catalog.md` — flag table with defaults and dependencies
- `rules/site-initializer-format.md` — site initializer directory tree and batch JSON envelope
- `rules/object-actions-catalog.md` — triggers, conditions, action types
- `rules/oauth-scopes.md` — `Liferay.*` scope strings for `oAuthApplicationHeadlessServer` blocks in CET scaffolding
- `rules/page-types.md` — page types and their applicable APIs

## Information Sources

The authoritative documentation is [learn.liferay.com](https://learn.liferay.com). To find a topic, search `site:learn.liferay.com <topic>` or browse from the area roots:

- Client extensions: `https://learn.liferay.com/w/dxp/development/client-extensions`
- Objects: `https://learn.liferay.com/w/dxp/low-code/objects`
- Headless APIs: `https://learn.liferay.com/w/dxp/integration/headless-apis`
- Site building: `https://learn.liferay.com/w/dxp/sites`
- Workspace tooling: `https://learn.liferay.com/w/dxp/development/tooling/liferay-workspace`

The Liferay Portal source code at [github.com/liferay/liferay-portal](https://github.com/liferay/liferay-portal) is canonical for architectural patterns and code samples; working client extension examples live at `workspaces/liferay-sample-workspace/client-extensions/`.