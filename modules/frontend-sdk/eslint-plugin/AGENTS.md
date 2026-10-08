# AGENTS.md

## Purpose

This package (`@liferay/eslint-plugin`) defines Liferay-specific ESLint rules and presets used across the monorepo, especially `liferay-portal/modules`.

Primary goals:
- Provide reusable presets (`general`, `react`, `metal`, `portal`)
- Enforce frontend coding conventions through custom rules
- Keep rule behavior testable and documented
- Support monorepo-specific enforcement (for example deep-import boundaries)

## Package Map

- Entry point: `index.js`
  - Exposes `configs` and merged `rules`.
- Presets: `configs/general.js`, `configs/react.js`, `configs/metal.js`, `configs/portal.js`
- Rule families:
  - `rules/general/*`
  - `rules/portal/*`
  - `rules/aui/*`
- Rule test harness:
  - `scripts/MultiTester.js`
  - `scripts/setupJest.js`
  - `test/integration.js`

Rule family layout convention:
- Implementation: `rules/<family>/lib/rules/<rule-name>.js`
- Unit tests: `rules/<family>/tests/lib/rules/<rule-name>.js`
- Docs: `rules/<family>/docs/rules/<rule-name>.md`
- Export map: `rules/<family>/index.js`

## Preset Inheritance

- `general` -> base preset (`eslint:recommended`, prettier compatibility, core + Liferay rules)
- `react` -> extends `general` and adds React + Hooks rules
- `metal` -> extends `react` and disables React rules that conflict with MetalJS
- `portal` -> extends `react` and enables portal-specific restrictions

Key nuance:
- Some implemented rules are exported but intentionally not enabled in a preset by default (for example `@liferay/no-arrow`, `@liferay/portal/no-cross-module-deep-import`).

## Rule Authoring Contract

When adding/updating a rule,

1. Add implementation file in `lib/rules`.

1. Export rule from the family `index.js`.

1. Add unit tests using `MultiTester`.

1. Add markdown docs in `docs/rules`.

1. Decide whether to enable in preset config (`configs/*.js`) or keep opt-in.

1. If autofix is provided, verify behavior with integration-level interactions if needed.

Implementation expectations:
- Export `{create, meta}`.
- `meta` should include `docs`, `schema`, `type`, and fixability where applicable.
- Prefer stable messages for assertions (`messageId` + `messages`) when practical.
- Keep rules deterministic and file-path aware only when strictly necessary.

## Testing Standards

Use parser-matrix testing via `MultiTester` to reduce parser-specific regressions:
- `espree`
- `@babel/eslint-parser`
- `@typescript-eslint/parser`

Unit test pattern:
- `valid` and `invalid` arrays
- explicit `errors`
- `output` when autofix is expected
- `skip` per parser only when required

Integration tests (`test/integration.js`) verify rule interaction under full preset application (especially autofix ordering and compatibility).

Run tests:
- `yarn test`

## Docs Standards

Each rule doc should include
- Problem statement / intent
- Incorrect examples
- Correct examples
- Optional "Further Reading" links

Keep doc filenames exactly aligned with rule names:
- `[rule-name].md` for each rule implementation.

## How Enforcement Works In `liferay-portal`

Main enforcement entry:
- `modules/.eslintrc.js` extends `plugin:@liferay/portal`.

Monorepo behavior:
- Root config enables/disables specific plugin rules for portal needs.
- Root config passes generated `nodeScriptsConfig` into `@liferay/portal/no-cross-module-deep-import`.
- Additional module-level `.eslintrc.js` files may apply local overrides.

Workspace wiring:
- `modules/package.json` includes `@liferay/eslint-plugin` in `devDependencies`.
- Yarn workspaces include `frontend-sdk/*`, so this package participates directly in monorepo development.

## Deep Import Rule Enforcement

`@liferay/portal/no-cross-module-deep-import` depends on
- `modules/node-scripts.config.js` (auto-generated import map / allowlist)

If this file is stale or missing expected exports, the deep-import rule reports false positives/negatives. Treat config generation state as part of lint correctness.

## Contributor Checklist (Rule Changes)

Before submitting:
- Rule implementation is added/updated
- Rule is exported in family `index.js`
- Unit tests are added/updated
- Docs are added/updated
- Preset activation decision is made and reflected in `configs/*.js`
- `README.md` rule listings are updated if applicable
- `yarn test` passes

## Current Coverage Notes

Known gaps currently present in repository:
- Some implemented rules have no docs and/or no unit tests (especially in `portal`, and a few in `general`).
- Keep new changes from increasing this gap; prefer full implementation + docs + tests for every new rule.