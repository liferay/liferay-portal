# JavaScript Unit Tests

## Trigger

Fires when one of these changed:

- JS or TS source with behavior intent (logic added, removed, or modified). Surface only edits (renames, formatting, comments, JSDoc) do not fire this validation. The build's bundling step is enough.

- A JS relevant `package.json` key (`dependencies`, `devDependencies`, `scripts.build`, `scripts.test`).

- A lockfile (`package-lock.json`, `yarn.lock`) fires regardless of intent, because a transitive dependency pin can affect any code path.

## Match

`^modules/.+\.(js|jsx|mjs|cjs|ts|tsx)$|^modules/.+/(package\.json|package-lock\.json|yarn\.lock)$`

## Command

Take the changed modules from the diff. A module qualifies when one of its changed paths is a `.js`, `.jsx`, `.mjs`, `.cjs`, `.ts`, or `.tsx` source, or its `package.json`, `package-lock.json`, or `yarn.lock`. The module is the nearest ancestor directory holding a `package.json`, and never `modules` itself, whose `package.json` is the workspace root rather than a module. Shared tooling that sits there belongs to [per-module-compile.md](per-module-compile.md), which builds it but runs no suite against it.

`modules/node-scripts.config.js` is the exception worth naming. Its `imports` map decides which package files every module's build exposes, so a change there can break the suites of modules the diff never touched while no rule above selects anything. When the diff changes it, run the suites of the modules whose entry in that map the diff altered.

```bash
MERGE_BASE=$(git merge-base HEAD master)

git diff --name-only "${MERGE_BASE}...HEAD" -- ':/modules'
```

Run each selected module's **full Jest suite**; do not select individual specs by name:

```bash
(cd <module> && npm test)
```

With no `test` script there is no suite, so record that and continue to the sweeps below rather than exiting.

**Cross-module consumer snapshots.** The changed module's own suite does not cover a snapshot stored in a *consumer* module. When another module declares the changed module by name in its `dependencies` or `devDependencies`, also run those consumers. Do not read `"private": true` as ruling this out, since Liferay modules depend on each other by bare name with `"*"` and a private module is still a workspace dependency:

1. Read the changed module's `package.json` `name` (for example `@clayui/*` for clay packages).

1. Grep other modules' `package.json` `dependencies`/`devDependencies` for that name.

1. Run the full suite of each consumer that declares a `test` script, taking the consumers whose spec or `__snapshots__` files import the package first.

Cap the consumer set at 8. When more modules depend on the package, note the blast radius rather than running them all.

**Stale stub lists.** A module that stubs a shared package family for Jest keeps its own copy of the stub list, so adding a package to that family leaves every copy stale.

Sweep only when the addition changes what **other** modules resolve. A dependency pinned to a version the workspace root cannot satisfy installs under the changed module's own `node_modules`, so every other module still resolves what it resolved before and none of them can have gone stale. Compare the added range against the version `modules/yarn.lock` resolves for that package. Read the lockfile rather than `modules/node_modules`, which is gitignored and absent on a fresh clone. When the locked version satisfies the range, or the package is not locked at all, the resolution is shared and the sweep is worth running. Otherwise say the addition is module local and skip it.

When it does apply, take each third party package the diff adds to the changed module's `dependencies`, use its npm scope, or the package name itself when it is unscoped, and run every module whose `jest-setup.config.js` or `jest-setup.config.ts` mocks anything in that scope rather than the added package itself, since the modules still missing it are exactly the ones the sweep is for. Do not cap this set. It is bounded in practice by how few modules carry a `jest-setup.config`, so report its size rather than assuming that holds.

That file is the only stub list this sweep reads. A module can also stub through a `__mocks__` directory, through `moduleNameMapper` in its `package.json`, or through `setupFilesAfterEnv`, and a stale list in any of those is invisible here.

**Oversized suites.** When a module's suite is large enough to blow the time budget, fall back to the specs under the module's `test` tree that name each changed source, plus every changed file in that tree, and note the reduced scope in the result. Match on the source's base name appearing in a spec path rather than on a `Foo.test.tsx` convention: this repository's `testMatch` is `${rootDir}/test/**/*.{js,ts,tsx}`, so specs look like `test/js/components/Answer.es.js` and carry no `.test.` infix, and searching for one selects nothing at all.

Decide PASS or FAIL from Jest's **`Test Suites:`** line, not from the exit status and not from the `Tests:` line. A suite that fails to load contributes no test case, so `Tests:` reports it as zero failures while the run exits nonzero, and reading `Tests:` alone passes a module whose suites never ran. `Test Suites: 6 failed, 20 passed` is a failure whatever `Tests:` says.

A module with no `test` script has no suite to judge and does not fail this validation.

When a suite fails, separate the two causes before charging it to the branch. Read the **frame that fails**, not every frame in the trace: an import of the added dependency can appear in the stack while the error is raised somewhere else entirely, in a file the diff never touched and reached through a module it never changed.

FAIL when the failing frame is in something the diff changed. When it is not, the module is already broken, so report **NOT VERIFIED** for it and name the error. This covers an assertion that fails as much as a suite that never loads, and the sweeps above are what make the distinction matter, since they deliberately run modules the diff never touched and a red test already sitting in one of them is not this branch's to answer for. Confirm it either way by resolving the import as the failing module would (`require.resolve` with that module's directory as `paths`), which shows whether the added dependency is even on the path that broke. `modules` is a yarn workspace, so a module with no local `node_modules` resolves from the workspace root and runs normally, and a genuine setup failure is a whole repository condition rather than a per module one.

## Checklist

Add one subitem per affected module:

```
- [ ] <module path>: full suite
```

## Time Estimate

~1 - 5 min per module suite. The oversized suite fallback caps larger ones.