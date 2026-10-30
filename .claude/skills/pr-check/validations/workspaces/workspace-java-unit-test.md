# Workspace Java Unit Tests

## Match

`. &! \.(cjs|css|js|jsx|mjs|scss|ts|tsx)$`

## Command

```bash
cd "${BUILD_ROOT}"

./gradlew --continue test
```

`test` runs every Java unit test in the workspace, with no integration test and no product bundle. A workspace runs its whole Java suite in a few minutes, so every test runs rather than a selection by counterpart, which also catches a module whose tests break through an API another module changed.

The match leaves out only the paths that cannot change a Java unit test, the frontend sources. Any other path, such as a `build.gradle`, a `bnd.bnd`, or a resource, still runs the suite.

`--continue` keeps one failing module from hiding the rest.

Judge the tests from the `TEST-*.xml` reports under each module's `build/test-results/test`, counting `tests`, `failures`, and `errors`, rather than from the Gradle exit status alone.

Report FAIL when the diff changed a failing test or the class it exercises, quoting the test and its assertion. When the diff touches neither the failing test nor the class it exercises, the workspace is already broken, so report **NOT VERIFIED** for it and name the commit that last changed each of them. Decide this from the diff rather than by checking out the base branch, which would disturb the working tree.

When a changed class under `src/main/java` has no test of the same name under the module's `src/test/java`, add a note naming it, since the run passes without anything exercising it.

## Time Estimate

~1-3 min.