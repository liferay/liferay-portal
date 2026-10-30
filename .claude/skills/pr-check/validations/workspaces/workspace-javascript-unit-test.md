# Workspace JavaScript Unit Tests

## Match

`. &! \.java$`

## Command

```bash
cd "${BUILD_ROOT}"

git grep --extended-regexp --quiet '"test"[[:space:]]*:' -- ':(glob)**/package.json'
```

When the command exits nonzero, no `package.json` in the workspace declares a `test` script, so there is no JavaScript suite to run. Report **NOT APPLICABLE**, since `packageRunTest` exists only in such a workspace and naming a task Gradle cannot find fails the whole command before any test runs. Otherwise run:

```bash
cd "${BUILD_ROOT}"

./gradlew --continue packageRunTest
```

`packageRunTest` runs every JavaScript unit test in the workspace, with no integration test and no product bundle. Every suite runs rather than a selection by counterpart.

The match leaves out only the paths that cannot change a JavaScript unit test, the Java sources. Any other path, such as a `build.gradle`, a `package.json`, or a resource, still runs the suite.

`--continue` keeps one failing module from hiding the rest.

Decide PASS or FAIL from Jest's **`Test Suites:`** line, not from the exit status and not from the `Tests:` line. A suite that fails to load contributes no test case, so `Tests:` reports it as zero failures while the run exits nonzero.

Report FAIL when the diff changed a failing test or the code it exercises, quoting the test and its assertion. When the diff touches neither the failing test nor the code it exercises, the workspace is already broken, so report **NOT VERIFIED** for it and name the commit that last changed each of them. Decide this from the diff rather than by checking out the base branch, which would disturb the working tree.

## Time Estimate

~1-5 min.