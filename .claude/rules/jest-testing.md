---

paths:
  - "modules/**/*-web/test/**/*.{js,ts,tsx}"

---

# Jest Testing

Conventions for Jest and React Testing Library tests across `*-web` modules in the portal. Apply them whenever a test file is created or edited under any module.

## Principles

Tests assert behavior perceptible to the user, not internal implementation. Ask before each assertion: does this matter to the user?

## File Naming

Place tests in a sibling `test` directory at the module root, mirroring the subpath under `src/main/resources/META-INF/resources/js`, and name each one after its unit with a `.test` suffix before the extension. A source file at `src/main/resources/META-INF/resources/js/utils/convertRGBtoHex.ts` has its test at `test/utils/convertRGBtoHex.test.ts`, and a source file at `src/main/resources/META-INF/resources/js/components/color_picker/ColorPicker.tsx` has its test at `test/components/color_picker/ColorPicker.test.tsx`.

The shared Jest config in `modules/frontend-sdk/node-scripts/util/jest/getJestConfig.js` picks up every file under `test` via a `testMatch` pattern, with or without the suffix, except `test/stories` and `test/__lib__`, which are excluded so they can hold fixtures, helpers, and Storybook artifacts. The suffix does not keep a file out of the run: put shared mocks in `test/__lib__`, or exclude their folder in the module's `package.json` with `jest.testPathIgnorePatterns`, as `mcp-server-web` does for `test/mocks`.

Do not colocate tests next to source files, and do not use `__tests__` directories.

## Imports

Import testing utilities from `@testing-library/react` (`render`, `screen`, `cleanup`, `fireEvent`, `waitFor`, `within`, `renderHook`, `act`) and `@testing-library/user-event`.

Do not import from `@testing-library/react-hooks`; React 18 moved `renderHook` and `act` into `@testing-library/react`.

Matchers like `toBeInTheDocument` and `toBeVisible` are autoregistered at runtime by the shared setup, but TypeScript cannot see them without the side-effect import, and the `yarn checkFormat` type check fails on every matcher call. Add the import on its own line after the external imports:

```typescript
import '@testing-library/jest-dom';
```

Stop at the side-effect import: named imports from that package or manual `expect.extend` calls duplicate what the shared setup already registers.

## Anatomy

A typical test renders the unit, drives it through `userEvent`, and asserts on the rendered output:

```typescript
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import MappingInput from '../../../../src/main/resources/META-INF/resources/js/seo/display_page_templates/components/MappingInput';

describe('MappingInput', () => {
	it('adds the selected field to the input', async () => {
		render(<MappingInput fieldTypes={['text']} fields={[{key: 'k1', label: 'L1', type: 'text'}]} label="Field" name="test" />);

		await userEvent.click(screen.getByTitle('map'));
		await userEvent.selectOptions(screen.getByLabelText('field'), 'k1');
		await userEvent.click(screen.getByText('add-field'));

		expect(screen.getByLabelText('Field')).toHaveValue('${k1:L1}');
	});
});
```

## Mocking

Mock at the perimeter, not inside the application. Network calls and browser APIs the test should not hit (`fetch`, timers, `crypto`) are fair targets. Internal modules of the module under test are not; stubbing them isolates the test from real behavior and hides bugs the integration would catch.

The shared setup under `modules/frontend-sdk/node-scripts/util/jest` installs the perimeter mocks tests need (`Liferay.*`, `fetch`, `jest-dom` matchers, timezone) so individual test files do not wire them. Use that surface as-is rather than reinstalling globals or replacing them with `jest.fn()`.

## Shared Setup

Every module that runs `yarn test` inherits two setup files from `modules/frontend-sdk/node-scripts/util/jest`: `setup.js` (via `setupFiles`) and `setupAfterEnv.js` (via `setupFilesAfterEnv`). Do not reinstall or reassign what they already provide.

What comes preinstalled:

1. **`global.Liferay`**: full mock from `mocks/Liferay.js` (events, `Language`, `ThemeDisplay`, `Util`, `Session`, `PortletKeys`, `FeatureFlags`, `Icons`, ...). Non-obvious defaults: `Language.get(key)` returns the key itself, `ThemeDisplay.getLanguageId()` returns `'en_US'`, `Util.openToast()` returns `true`.

1. **`global.fetch`**: `jest-fetch-mock` enabled portal-wide. Drive responses with `fetch.mockResponseOnce(JSON.stringify(...))`. A `beforeEach` hook reinstalls a spy that throws on unmocked calls, so tests must declare every expected network call.

1. **`@testing-library/jest-dom` matchers**: registered globally.

1. **Polyfills and Environment**: `global.crypto`, `Headers`, `Image.prototype.decode`, `createRange()`, `regenerator-runtime`, `TextEncoder`/`TextDecoder`. CSS and SCSS imports route to an empty stub. Timezone is locked to `UTC`.

### Overriding the Shared Mocks

When a single case needs a different value than the shared default (for example, `Liferay.Util.openToast` returning a value other than `true`), override the shared `jest.fn` directly and restore the default in teardown. Do not wrap it in `jest.spyOn` and `mockRestore`: the shared mock is itself a `jest.fn`, so `mockRestore` strips its implementation and leaves it returning `undefined` for every later test in the worker.

```javascript
beforeEach(() => {
	Liferay.Util.openToast.mockReturnValue(false);
});

afterEach(() => {
	Liferay.Util.openToast.mockReturnValue(true);
});
```

Antipatterns that shadow the shared mock and leak into every later test in the same worker:

```typescript
// Reassigning the Liferay surface
(global as any).Liferay.Language.get = jest.fn((key) => `T(${key})`);
global.Liferay = {Language: {get: jest.fn()}};

// Conflicting with jest-fetch-mock
global.fetch = jest.fn().mockResolvedValue({...});

// Duplicating stubs the shared mock already provides
jest.mock('frontend-js-web', () => ({sub: jest.fn(), fetch: jest.fn()}));
```

### Feature Flags

The shared mock starts with `Liferay.FeatureFlags = {}`. Toggle a flag for a test by mutating that object directly: set the key in `beforeEach` (or `beforeAll` when the whole file needs it) and reset in the matching teardown.

```typescript
describe('RulesModal', () => {
	beforeEach(() => {
		Liferay.FeatureFlags['LPS-169837'] = true;
	});

	afterEach(() => {
		Liferay.FeatureFlags['LPS-169837'] = false;
	});

	it('allows saving a rule', async () => {
		renderComponent();

		await userEvent.click(screen.getByText('save'));

		expect(addRule).toHaveBeenCalled();
	});
});
```

### Before Writing a New Mock

Check these locations in order before implementing your own:

1. **`modules/frontend-sdk/node-scripts/util/jest/mocks`** for portal-wide stubs.

1. **Your Module's `test`** tree for project-local fixtures and helpers.

## Lifecycle

1. `@testing-library/react` autocalls `cleanup` after every test as long as an `afterEach` exists in scope, so an explicit `afterEach(cleanup)` is not required. Tests that already include it are fine; do not add it in new code unless a specific case demands it.

1. Call `jest.clearAllMocks()` in `beforeEach` only when the suite asserts on the call history of a mock shared between tests, such as a `Liferay.*` global, a module level `jest.fn()`, or a shared spy. A mock created inside a test or passed as a prop on each render starts empty, and the shared `setupAfterEnv.js` restores `fetch` after every test, so none of them needs clearing. The shared Jest config does not set `clearMocks: true`, which is why a shared history persists.

1. Stub browser APIs and external modules with `jest.spyOn`, then call `jest.restoreAllMocks()` in `afterEach` so the spy does not leak into other suites. Note that `restoreAllMocks` only undoes `jest.spyOn` setups; for `jest.fn()` mocks, use `clearAllMocks` (history) or `resetAllMocks` (history + implementation) as needed.

## Element Queries

Follow the [Testing Library query priority](https://testing-library.com/docs/queries/about#priority):

1. **Accessible to Everyone**: `getByRole`, `getByLabelText`, `getByPlaceholderText`, `getByText`, `getByDisplayValue`.

1. **Semantic Queries**: `getByAltText`, `getByTitle`.

1. **Test IDs**: `getByTestId`.

Try to avoid `data-testid` and `container.querySelector` when a higher-priority query exists. `data-testid` ships to production unless stripped, creating an unofficial API; `querySelector` against a presentational class breaks with any visual refactor.

## User Interaction

Prefer `userEvent` over `fireEvent` and `await` every interaction; `userEvent` simulates the full event sequence (focus, keydown, input, keyup, change) that `fireEvent` skips.

With fake timers, instantiate `userEvent` via `setup({advanceTimers: jest.advanceTimersByTime})`; otherwise its delays block on the frozen clock and the test hangs.

```javascript
jest.useFakeTimers();

const user = userEvent.setup({advanceTimers: jest.advanceTimersByTime});

await user.upload(input, file);

act(() => {
	jest.runAllTimers();
});

jest.useRealTimers();
```

## Debugging

The `node-scripts test` wrapper appends `--silent` to every Jest run, so `console.log` and `screen.debug()` output never reaches the terminal. Reenable it with the `--force-debug` flag:

```bash
yarn test path/to/Foo.test.tsx --force-debug
```

Every other Jest CLI flag passes through unchanged, so the usual iteration helpers compose with it:

```bash
yarn test path/to/Foo.test.tsx --force-debug --testNamePattern "<test title>"
```

Failure output (the DOM dump under a failed query, assertion diffs) is printed regardless of `--silent`; the flag is only needed for output the test itself logs.

## Accessibility

Tests for UI components should include accessibility assertions using `checkAccessibility` from `@liferay/layout-js-components-web/test/__lib__`. It wraps `axe-core` to assert zero violations against WCAG 2.1a, 2.1aa, and 2.2aa rulesets:

```typescript
// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

it('has no accessibility violations', async () => {
	const {container} = render(<MyComponent {...DEFAULT_PROPS} />);

	await checkAccessibility({bestPractices: true, context: container});
});
```

The `bestPractices: true` flag enables additional checks beyond strict WCAG.

Color contrast is not reliably testable in JSDOM and is disabled by default. Cover that case in Playwright tests if relevant.

## Source

- [Liferay Frontend Testing Guidelines](https://github.com/liferay/liferay-frontend-projects/tree/master/guidelines/general/testing)
- [Testing Library Documentation](https://testing-library.com/docs)