# AGENTS.md

## Purpose

`@liferay/stylelint-plugin` provides Liferay-specific Stylelint plugins plus a `defaultConfig` used as the base ruleset in `liferay-portal`.

## Repository Map

- `index.js`: exports plugin list and attaches `defaultConfig`.
- `plugins.js`: registers rule implementations via `stylelint.createPlugin()`.
- `rules/*.js`: custom stylelint rule implementations
- `package.json`: package metadata and release scripts

Current custom rules in this package:

- `liferay/no-block-comments`
- `liferay/no-hardcoded-colors`
- `liferay/no-import-extension`
- `liferay/single-imports`
- `liferay/sort-imports`
- `liferay/trim-comments`

## How It Interacts With `liferay-portal`

- `modules/.stylelintrc.js` imports `{defaultConfig}` from this package and spreads `defaultConfig.rules`.
- `modules/.stylelintrc.js` loads plugin runtime with
  - `plugins: require.resolve('@liferay/stylelint-plugin')`
- Portal then applies local overrides (for example disabling `liferay/no-block-comments`).
- `modules/package.json` pins `@liferay/stylelint-plugin` as a dev dependency.

## Rule Behavior Notes

- Several rules support autofix through Stylelint `context.fix` and optional `disableFix` secondary option.
- `single-imports` and `sort-imports` both manipulate `@import` ordering/splitting and must preserve whitespace semantics.
- `trim-comments` has special handling for inline blank comments and blank-line retention around removals.

## Contribution Workflow

When adding/changing rules,

1. Implement/update rule in `rules/<rule-name>.js`.

1. Register it in `plugins.js`.

1. Add/update `defaultConfig.rules` in `index.js` if it should be enabled by default.

1. Validate downstream effect in `modules/.stylelintrc.js` (which may override defaults).

1. Run:
   - `yarn run format:check`

## Guardrails

- Preserve rule names under `liferay/*` namespace.
- Keep autofix logic conservative and deterministic.
- If adding default-enabled rules, verify they are safe for broad portal usage; prefer opt-in first if risky.
- Keep `defaultConfig` backward compatible where possible, because many modules inherit it transitively.

## Release Notes

- `preversion` runs format check.
- `postversion` publishes via `liferay-js-publish`.
- Follow `CONTRIBUTING.md` for changelog + release flow.