# Per-Module Compile

## Trigger

A module is in the deploy set AND **Full Portal Build** did not deploy it. The latter holds when:

- **Full Portal Build** did not fire.

- OR **Full Portal Build** fired but the module lacks `.lfrbuild-portal` (so `ant all` did not deploy it).

**Command** builds the deploy set. Its size N is used by [full-portal-build.md](full-portal-build.md)'s cost comparison.

Two consumer surfaces are [cross-module-compile.md](cross-module-compile.md)'s instead, and this validation excludes both: modules carrying `.lfrbuild-portal-deprecated`, which only the `portal-deprecated` profile configures, and `testIntegration` sources in `-test` modules, which `deploy` never compiles. Archived modules are not among them, since the project graph reaches those normally.

Both behavior-change and surface-only edits fire this validation — the build verifies compile and resource bundling regardless of intent.

## Match

`^modules/.+\.(java|js|jsx|mjs|cjs|ts|tsx|css|scss|sass|ftl|jsp|jspf|properties)$|^modules/.+/(bnd\.bnd|gradle\.properties|package-lock\.json|yarn\.lock|package\.json)$`

## Command

Build the deploy set from the diff:

```bash
MERGE_BASE=$(git merge-base HEAD master)

git diff --name-only "${MERGE_BASE}...HEAD" -- modules
```

A module is in the deploy set when it has changed sources or resources: `*.java`, `*.{js,jsx,mjs,cjs,ts,tsx}`, frontend resources (`*.{css,scss,sass}`, `*.ftl`, `*.jsp`, `*.jspf`), lockfiles (`package-lock.json`, `yarn.lock`), module `*.properties`, or OSGi configuration (`bnd.bnd`, `gradle.properties`, `package.json` keys other than `test`).

That list is the **Match** regex above restated, and the two have to stay in step. A module whose only change is a `.lfrbuild-*` marker is not in the deploy set, since the marker changes what the build configures rather than what the module contains, and [module-registration.md](module-registration.md) handles it.

A changed file's module is its **nearest ancestor directory holding a `bnd.bnd`**. Do not use `build.gradle`, which app group directories also carry, so `modules/apps/questions/questions-web/package.json` would resolve to `modules/apps/questions`. Module depth is not fixed either, running three to five segments below `modules`, so never strip a set number of them.

Exclude modules whose **only** Java change is under `src/testIntegration`. Integration Test Compile already runs `compileTestIntegrationJava` for those, and `-test` modules do not deploy a runtime bundle — `gradlew :path:deploy` would be redundant. A diff that touches `src/testIntegration` *and* anything else in the same module still puts the module in the deploy set.

Convert each deploy set module directory to a Gradle project path by stripping `modules/` and replacing `/` with `:`, so `modules/apps/blogs/blogs-api` becomes `apps:blogs:blogs-api`. The expansions below match on that form, not on the directory.

Expand by consumers only when the change can break one. An added `public` or `protected` member is source and binary compatible, so it expands nothing. A removed member, or one whose signature changed, does break consumers. Collect the removed and added member lines separately and expand only on a removal with no matching addition, since a member that was moved or reformatted appears as both and breaks nobody:

```bash
git diff "${MERGE_BASE}...HEAD" -- '<changed file>' | command grep --extended-regexp '^-\s*(public|protected)\b'
git diff "${MERGE_BASE}...HEAD" -- '<changed file>' | command grep --extended-regexp '^\+\s*(public|protected)\b'
```

Take the consumers that name the changed **type**, not every module that declares a dependency on its project. A project edge means a module could see the type; only a source reference means it does. Search the index, since a recursive `command grep` over `modules` descends into `build` and `node_modules` and does not finish:

```bash
git grep --cached --files-with-matches --word-regexp '<TypeName>' -- '*.java' \
	| sed 's|/src/.*||' | sort --unique
```

The difference is not marginal. Removing a member from a mid sized API class put 188 modules on the project edge and 6 on the type reference, and only 2 of those were production consumers that could break. Match the type name rather than the member name, which collides across unrelated classes.

Drop any match that is a `-test` or `-test-util` module or carries `.lfrbuild-portal-deprecated`, per the Trigger, and resolve each remaining path to its module the same way a changed file is resolved, by its nearest ancestor holding a `bnd.bnd`.

Apply the handoff below to the set you now have, **before** capping it. When the handoff does not fire, cap the consumers at 12 in sorted path order so two runs on the same diff build the same set, and name the full consumer count in the result.

This expansion reads Java signatures, so it says nothing about a consumer that breaks any other way. A marker change is [module-registration.md](module-registration.md)'s, and a resource or configuration change reaches consumers this test cannot see.

Expand by shared build tooling: `modules/frontend-sdk/**` and `modules/node-scripts.config.js` feed every module's JavaScript build, so a change to either breaks deploys the diff never touched, and no `project(":...")` edge leads to them.

Take one of three branches for `modules/node-scripts.config.js`, in order.

1. The change is only a comment or a blank line, so it cannot alter a build. Skip it and expand nothing.

1. The content changed but the `hash` field did not. The file's own header declares it generated from each module's own `node-scripts.config.js`, so this is a hand edit. Report it as a finding, name the file, and expand nothing.

1. The file was regenerated, `hash` and all. Add the modules whose entry in the `imports` map is a nonempty array. Those entries are the ones that expose a package to other modules' builds, so they are the edges a change travels along, while the empty ones expose nothing. Strip any `@liferay/` prefix and resolve the name to a module directory under `modules/apps` or `modules/dxp/apps`.

When the deploy set exceeds **40 modules**, stop and hand off to [full-portal-build.md](full-portal-build.md). One `ant all` costs about 8 minutes and covers every module, while each module here is a separate `gradlew` invocation measured at about 11 seconds on a warm daemon, almost all of it per invocation overhead. That puts the crossover near 44, and lower on a cold tree. A build that covers everything beats a sample of a set too large to finish.

Report the count and the cost math, run Full Portal Build in this validation's place, and give this row **its** result. When Full Portal Build is not in the run or does not produce one, report **NOT VERIFIED** naming the count, since this validation then has no result of its own either. The verdict rules below apply only when no handoff happened.

Run the lockfile check regardless, since it needs no build and a handoff does not make a mismatched dependency any less broken.

Set up once, then deploy each module:

```bash
(cd "${REPO_ROOT}" && ant compile install-portal-snapshots)
```

The setup step is a precondition: it rebuilds the `portal-kernel`/`portal-impl` snapshot from the branch tree before any module compiles, so a module referencing a portal-core symbol is checked against the branch's kernel rather than a stale snapshot. A kernel change from a separate, not-yet-merged PR is only caught once local `master` includes it, since pr-check never fetches a remote.

```bash
("${REPO_ROOT}/gradlew" \
	--parallel \
	--project-dir "${REPO_ROOT}/modules" \
	:<path>:deploy)
```

FAIL when a module the diff **changed** reports `BUILD FAILED`, and name the module and the compiler error.

A module reached only by an expansion is one the branch never touched, so judge its failure by cause. FAIL when the error names a symbol from the changed module. When it names none of them, the module is already broken on the merge base, so report it and do not fail the branch. The common shape is a `-test` module failing on a package that exists in the tree, which fails on every branch alike. Confirm that by compiling one module with no dependency on anything the diff touched, and when it fails the same way the breakage is the environment's.

A `deploy` never installs a newly added npm dependency, so it cannot tell you whether one resolves. `modules` is a yarn workspace, the root `yarnInstall` owns installs, and both it and every module's `npmInstall` report `SKIPPED` on a normal deploy whatever the diff changed. `packageRunBuild` then runs against whatever is already in `node_modules`.

Check the lockfile instead, which needs no build at all. A dependency the diff adds or changes in a `package.json` has to be matched by a change to `modules/yarn.lock`, or CI's `yarn install --frozen-lockfile` rejects the branch. FAIL when the diff changes a `dependencies` or `devDependencies` entry and touches no lockfile, and name the package and the range.

Confirm the range against what is actually locked, since an entry can be present at a version that does not satisfy it:

```bash
command grep --fixed-strings '<package>@' "${REPO_ROOT}/modules/yarn.lock"
```

Do not hand this to [javascript-unit-test.md](javascript-unit-test.md). Jest resolves files rather than version ranges, so a module whose sources never import the package, or which resolves the hoisted copy, passes its suite with the declared range never evaluated.

Treat `UP-TO-DATE` on a changed module's own `compileJava` with the same suspicion. Gradle's cache has served a stale output in this repository before, so confirm the change reached the jar rather than reading the task line as proof.

A deploy set that came out empty is a broken derivation rather than a pass, since this validation only fires when the diff changed a module source or resource in the first place; report that as a FAIL too. PASS when every module the diff changed reports `BUILD SUCCESSFUL`.

## Checklist

```
- [ ] Setup: ant compile install-portal-snapshots
- [ ] (One subitem per deploy-set module:) Deploy <module path>
```

## Time Estimate

3 min setup, then about 10 sec per module on a warm daemon and a minute or more on a cold one. Each module is its own `gradlew` invocation paying its own configuration, and `--parallel` works within an invocation rather than across them, so the cost is linear in N and the cap above is what keeps it bounded.