# Java Unit Tests

## Trigger

A Java source file changed with behavior intent — logic added, removed, or modified. Surface-only edits (renames, formatting, comments, javadoc) do not fire this validation — the build's compile step plus **Structural Smoke** are enough.

Test code (`**/src/test/**`) is in scope: when a test class itself changed, run it. Integration test sources (`**/src/testIntegration/**`) are not in scope here — IT execution is out of scope; signature breaks in IT are caught by **Integration Test Compile**.

## Match

`^modules/.+\.java$|^portal-(impl|kernel)/.+\.java$`

## Selection

Locate the counterpart test by parallel name: `Foo.java` → `FooTest.java` in the same module's `src/test/java/**` (for OSGi modules) or `portal-impl/test/unit/**` / `portal-kernel/test/unit/**` (for portal-core).

Do not select `Log4jConfigUtilTest` or `SampleSQLBuilderTest`, even when their counterpart source changes. Both are in `test.batch.class.names.excludes.permanent` and neither runs in the normal CI flow, so pr-check does not run them either.

Verify each counterpart file exists before scheduling it. Fall back to the module's full unit suite only when no parallel-name counterpart exists and the module is small enough that running everything is cheap.

## Command

For OSGi modules — run only the specific test class, batching counterparts within the same module:

```bash
"${REPO_ROOT}/gradlew" \
	--continue \
	--project-dir "${REPO_ROOT}/modules" \
	-Dtest.ignore.failures=false \
	:<path>:test \
	--tests "<FQN1>" \
	--tests "<FQN2>"
```

`--continue` keeps Gradle going when a downstream task fails so the test results still surface; `-Dtest.ignore.failures=false` overrides Liferay's default of swallowing test failures.

Each `--tests` flag is an option on the `test` task rather than a Gradle flag, so it follows the task path and breaks the usual alphabetical flag order. When a `--tests` option comes before the task path, Gradle rejects it and runs nothing.

For portal-core — `test-class` (defined in `build-common.xml`) is the target that filters by `test.class` (`test-unit` ignores it and runs the full suite):

```bash
(cd "${REPO_ROOT}/portal-impl" && ant test-class -Dtest.class="<ClassA>.class **/<ClassB>")
```

The space-separated Ant fileset pattern is the same one used by **Structural Smoke** — see that file for the explanation.

Decide PASS or FAIL from the `tests`, `failures`, and `errors` counts in the `TEST-*.xml` files the run writes, not from a `BUILD SUCCESSFUL` marker, because the `tail` pipe discards the build's exit status and an absent marker does not distinguish tests that failed from tests that never ran. Gradle writes the files under `modules/<path>/test-results/unit/test` and the Ant target writes them under `portal-impl/test-results/unit`. A command that was given test classes and executed none of them is a FAIL. When Selection finds no test class to run there is nothing to execute, so the validation reports PASS.

## Checklist

Add one subitem per affected module:

```
- [ ] <module path>: <test class names>
```

## Time Estimate

~30 sec - 2 min depending on counterpart count.