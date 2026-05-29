# AGENTS.md

This file provides guidance for AI agents and contributors working on ClayUI in this repository.

## Scope

This guidance applies to the ClayUI monorepo area under

- `clay/clay-*` (published `@clayui/*` packages)
- `clay/clay-css` (CSS/Sass foundation)
- `clay/www` (Clay documentation site)

## Clay in `frontend-js-clay-web`

- This module contains a merged copy of the Clay monorepo under `./clay`.
- ClayUI is now developed in `liferay-portal`; the standalone `liferay/clay` repo is kept as a read-only mirror synced from portal.
- This setup keeps Clay source changes and DXP integration changes in the same repository context.

## ClayUI in Liferay DXP

- ClayUI is the shared frontend component and styling foundation used across Liferay DXP applications.
- The `clay/clay-*` packages provide reusable React building blocks that product modules consume to implement consistent UI behavior and patterns.
- The `@clayui/css` package provides the Lexicon-based CSS/Sass layer that keeps visual language and theming consistent across DXP.
- This module (`frontend-js-clay-web`) is the integration layer that exposes ClayUI packages to the larger Liferay portal build/runtime, so changes here can affect many applications at once.
- In practice: prefer backward-compatible changes, treat public API/CSS changes as platform-impacting, and document behavior changes clearly for downstream DXP teams.

## Source of Truth and Sync Model

- `./clay` is the Clay source of truth for this module and for DXP integration work.
- The `clay` directory is synced back to the upstream `liferay/clay` repository on a nightly, read-only basis.

## Package Manager and Command Context

- Package-level scripts in `clay/clay-*` are Yarn-based (see each package `package.json` and `.yarnrc`).
- Repository-level scripts for this module are exposed from the root `package.json`.
- Prefer running commands from the appropriate root for the task:
    - module root: `frontend-js-clay-web/`
    - package root: `frontend-js-clay-web/clay/clay-<package>/`
    - docs root: `frontend-js-clay-web/clay/www/`

## Common Commands

### Module Root (`frontend-js-clay-web/`)

```bash
yarn run format
yarn run checkFormat
yarn test
yarn run storybook
yarn run storybook:build
yarn run build
```

### Clay Package (`clay/clay-*/`)

```bash
yarn build
yarn buildTypes
```

> **Do not run `yarn test` from a package root.** Jest depends on `node_modules`
> hoisted at `modules/`, so package-level test runs fail with `Cannot find
> module 'browserslist-config-clay'` and similar resolution errors. Always run
> tests from the module root (`frontend-js-clay-web/`) — see
> [Testing Expectations](#testing-expectations).

### Clay CSS (`clay/clay-css/`)

```bash
yarn compile
# Creates the static icon spritemap
yarn buildIcons
yarn build
```

### Docs Site (`clay/www/`)

```bash
yarn run dev
yarn run build
yarn run lint
```

## Repository Architecture

- ClayUI is split into many publishable packages under `clay/clay-*` (`@clayui/button`, `@clayui/core`, etc.).
- `@clayui/shared` is an internal package for shared utilities used between ClayUI components and should not be treated as a primary end-user entry point.
- Most component packages follow this structure:
    - `src/` component source and tests
    - `stories/` Storybook stories
    - `README.md`, `CHANGELOG.md`, `BREAKING.md`
    - `tsconfig.json`, `tsconfig.declarations.json`
- Shared Jest configuration is at module root: `jest.config.js`.
- Running unit tests from the module root uses node-scripts to run the tests in the correct context.
- Package builds use `clay/build-package-esbuild.js` for CJS/ESM outputs.

## Build and Publish Model

- Build logic for Clay packages is centralized in `clay/build-package-esbuild.js`.
- Individual `clay-*` packages call the shared build script from their package scripts; avoid invoking the build script directly unless debugging build internals.
- Publishing is centralized in `clay/publish-clay-packages.mjs`, which manages
    - version alignment across publishable Clay packages
    - internal `@clayui/*` dependency updates
    - package build/type generation (when applicable)
    - npm publish flow
- The unified changelog boundary is derived from the most recent commit whose subject matches a recognized release phrasing. Recognized phrasings are:
    - `Bump Clay versions to X.Y.Z` (also matches `Bump Clay version to X.Y.Z`)
    - `Publish vX.Y.Z`
    - `Release Clay vX.Y.Z`
    - `Update Clay to X.Y.Z`
- Release commits must use one of these phrasings (case insensitive) or changelog boundary detection will pick the wrong commit, causing stale commits to leak into the rendered changelog.

## DXP vs npm Consumption

- DXP consumes Clay from source in this repository.
- Published npm packages are for external consumers and type/package distribution.

## Release and Versioning Policy

- Publishable Clay packages are version-locked and released with the same version.
- Even when only one package changes, the release version is aligned across all publishable Clay packages.

## Releasing Clay (Examples)

* Run from `modules/apps/frontend-js/frontend-js-clay-web`:

```bash
# Preview version and dependency changes only
node clay/publish-clay-packages.mjs --target-version=3.160.0 --preview-changes

# Dry run publish (no real npm publish)
node clay/publish-clay-packages.mjs --target-version=3.160.0 --dry-run

# Publish for real (default tag: latest)
node clay/publish-clay-packages.mjs --target-version=3.160.0

# Optional: publish under next tag
NPM_TAG=next node clay/publish-clay-packages.mjs --target-version=3.160.0

# Optional: skip version bump (advanced)
SKIP_VERSION_BUMP=true node clay/publish-clay-packages.mjs --target-version=3.160.0
```

* Review `package.json` version updates in `modules/apps/frontend-js/frontend-js-clay-web`

* Go to `/portal-impl` and run `ant format-source-all -Dsource.check.names=JSONPackageJSONDependencyVersionCheck -Dsource.file.extensions=json`. It should update all `clayui` package references across `liferay-portal`.

* Go to `/modules` and run `yarn && npx yarn-deduplicate yarn.lock && yarn`. Verify the yarn.lock diff to check if it makes sense. If its numbers are considerably off (more + than - or vice versa), review the contents to find an explanation.

* Review and commit changes

Release notes:

- npm auth is required (`npm whoami` must succeed).
- Tests run before publish steps (except preview mode).
- Version lockstep applies across publishable Clay packages.

## Pull Request Preview Integrations

- The Platform Experience team maintains Vercel previews for `clayui.com` and `storybook.clayui.com`.
- On their portal fork, Clay-related PRs should generate preview deployments for those URLs.
- When making changes to Clay, the platform team should always review the code on their fork, `liferay-platform-experience/liferay-portal`

## Seeing `@clayui/css` Changes in DXP

- For local verification of Clay CSS changes in DXP, deploy both
    - `frontend-js-clay-web`
    - the corresponding theme in `frontend-theme`
- Deploying only one can lead to incomplete or misleading style results.

## Code Conventions

- Follow existing formatting and style conventions enforced by node-scripts at portal root.
- Keep public API changes intentional and typed; match existing prop/type naming patterns in the package.
- Never import across package source paths (for example, `../../clay-button/src`). Always import from package entry points (`@clayui/...`).
- If a cross-package import is needed and the dependency is missing, add that package as an explicit dependency in the consuming package.
- Re-export a package's public symbols from its entry point (`src/index.tsx`) using the specifier form — `export {Foo, Bar};` — rather than inline `export const`/`function`/`class`. DXP consumes Clay from source, and the DXP export bridge (`modules/frontend-sdk/node-scripts/util/esbuild/util/getExportedSymbols.mjs`) infers a package's exports by AST-parsing the entry module. It only recognizes export specifiers; inline `export const Foo = ...` declarations are silently dropped, so DXP modules that `import {Foo}` fail to build with `No matching export ... for import "Foo"`. Sibling packages such as `clay-drop-down` and `clay-button` follow the specifier form.
- For CSS/Sass work, follow `clay/clay-css/CONTRIBUTING.md`:
    - hard tabs
    - alphabetical property ordering
    - longhand declarations by default
    - naming and accessibility rules documented there

## Developing Components

- Start with low-level building blocks first, then compose high-level abstractions from those primitives.
- Keep business logic out of reusable components when possible so components stay flexible and composable.
- Favor composition over inheritance: expose composable APIs so consumers can assemble behavior for their own use cases.
- High-level components should only exist when the abstraction is clear and broadly useful.
- In Clay naming, the `With` keyword commonly signals a higher-level convenience abstraction (for example, `ButtonWithIcon`) built on top of lower-level parts.

## High-Level vs Low-Level Guidance

- Low-level components are the primary foundation: small, focused, and reusable across multiple features/components.
- High-level components orchestrate several low-level components into opinionated patterns for common workflows.
- Build low-level APIs first, then add high-level wrappers only when the composed usage is stable and ergonomic.

## Common Component Pitfalls

- Keep component props local to `IProps`, and type component parameters directly as `({ ... }: IProps)` so API docs tooling can consume the type.
- When exporting a component with attached subcomponents, type the merged shape and export with `Object.assign(...)` to avoid declaration/type issues.
- Set `displayName` explicitly so React Developer Tools shows the intended component name.

## Testing Expectations

- Add or update tests in `src/__tests__/` for behavior changes.
- Prioritize unit tests for component behavior; E2E/integration coverage is handled in DXP (for example, playwright).
- Keep tests deterministic and aligned with current Jest + Testing Library patterns in the package.
- Prefer explicit, behavior-focused assertions for specific cases.
- Avoid snapshot testing for new coverage unless there is a clear reason and strong review value.
- Update stories when component behavior, props, or visuals change.
- **Running tests**: **Always run from the `frontend-js-clay-web` module root, never from a package root** (`clay/clay-*/`). Package-level runs fail because `node_modules` is hoisted to `modules/`.
    - Running `yarn test` with no arguments will run tests for all packages.
    - To run tests for a specific package, specify it as the first positional argument. Use the `-t` flag to isolate specific tests.
    - Example: `yarn test clay/clay-drop-down`
    - Example (filtered): `yarn test clay/clay-drop-down -t "navigates forwards when clicking through menus"`
- Run relevant package tests before submitting changes; run broader root tests for cross-cutting changes.

## Test Coverage Strategy

- Base test behavior on Lexicon component specifications and expected UX behavior.
- Organize tests by suite purpose when applicable:
    - `BasicRendering`: composition/customization and markup-oriented behavior
    - `IncrementalInteractions`: interaction flows and expected runtime behavior
    - `Internationalization` (optional): locale/i18n behavior for components that need it (for example, `DatePicker`)
- Use organic coverage growth instead of chasing arbitrary test counts.
- Keep thresholds close to real package coverage and adjust pragmatically when shipping meaningful changes.
- Raise thresholds when coverage improves; lower only when an additional reasonable test is not possible.
- Prefer a practical minimum threshold around `50%` for low-coverage packages, except deprecated packages.

## Change Management

- If a change affects public behavior or API, update package docs and changelog files as appropriate.
- For breaking changes, document them in `BREAKING.md` when relevant.
- For `clay-css` icon pipeline changes, run compile/build steps and commit generated artifacts required by the package workflow.

## PR Readiness Checklist

- Code formatted (`yarn run format` or equivalent)
- Relevant tests pass (`yarn test` in changed package, and/or root `yarn test`)
- Build passes for changed package(s)
- Stories/docs/changelog updated when user-facing behavior changes
- PR approved by platform-experience-team

## Contributor Notes

- Prefer top-level scripts in this guide for day-to-day development.
- Treat package build behavior as package-driven (packages invoke the shared build script).
- Treat publish behavior as centralized in `clay/publish-clay-packages.mjs`.