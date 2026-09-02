# Java Unit Tests

## Trigger

A Java source file changed with behavior intent — logic added, removed, or modified. Surface only edits (renames, formatting, comments, javadoc) do not fire this validation — the build's compile step plus **Structural Smoke** are enough.

Test code (`**/src/test/**`) is in scope: when a test class itself changed, run it. Integration test sources (`**/src/testIntegration/**`) are not in scope here — IT execution is out of scope; signature breaks in IT are caught by **Integration Test Compile**.

## Match

`^modules/.+\.java$|^portal-(impl|kernel)/.+\.java$`

## Command

Take the changed Java files from the diff:

```bash
MERGE_BASE=$(git merge-base HEAD master)

git diff --name-only "${MERGE_BASE}...HEAD" -- ':/*.java'
```

Locate the counterpart test by parallel name: `Foo.java` → `FooTest.java` in the same module's `src/test/java/**` (for OSGi modules) or `portal-impl/test/unit/**` / `portal-kernel/test/unit/**` (for portal-core).

Do not select `Log4jConfigUtilTest` or `SampleSQLBuilderTest`, even when their counterpart source changes. Both are in `test.batch.class.names.excludes.permanent` and neither runs in the normal CI flow, so pr-check does not run them either.

Verify each counterpart file exists before scheduling it.

When no counterpart exists, nothing here can exercise the change, whatever the module costs to build. Report **NOT VERIFIED** and name the changed class as having no unit test, rather than as uncovered. The same name often exists as an integration test in the sibling `-test` module, which this validation does not run but which does cover the class, so name that file when it exists or the report sends a developer to write a test that is already there. Running a suite that never touches the changed class establishes no more than declining to run it, so module size must not decide the verdict.

Running the suite anyway is worth doing when it is cheap, since it can catch an unrelated break. It cannot change the verdict either way, because a green suite that never loaded the changed class does not make it a PASS and a red one does not make it a FAIL. Report what the suite did alongside the **NOT VERIFIED**.

Install the portal snapshot before running any module test, since the module compiles against it. Without it the run fails resolving `com.liferay.portal.kernel` and writes no `TEST-*.xml`, which the rule below would otherwise read as a FAIL against the branch.

```bash
(cd "${REPO_ROOT}" && ant compile install-portal-snapshots)
```

For OSGi modules — run only the specific test class, batching counterparts within the same module. Convert the module directory to a Gradle project path by stripping `modules/` and replacing `/` with `:`:

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

`test.class` takes an Ant fileset include pattern, so several classes go in one run separated by spaces, and a `**/` prefix matches the name at any package depth.

Delete the module's existing `test-results` tree before running, or an earlier run's XML is read as this one's and a suite that never ran reports its old counts.

Decide PASS or FAIL from the `tests`, `failures`, and `errors` counts in the `TEST-*.xml` files the run writes, not from a `BUILD SUCCESSFUL` marker, which does not distinguish tests that failed from tests that never ran. Gradle writes them under the module directory in `test-results/unit/test`, and the Ant target under `portal-impl/test-results/unit`.

A run that executed no test is a FAIL, since a suite that ran nothing is not a suite that passed, unless it died on a class the module never declared, which the rule below sends to **NOT VERIFIED** instead. When several modules run, the validation fails when any one of them does.

A run can die before any test method executes, as when a test rule's static initializer throws `NoClassDefFoundError`. JUnit still writes a results file, recording a synthesized `classMethod` entry carrying `failures="1"`, so the counts alone read as an ordinary failing test.

Read the module's own build file for the missing class's module, which separates the two cases mechanically. When the module declares it, the branch broke a dependency that used to resolve, so FAIL and name it. When the module never declared it, the run fails on every branch alike and says nothing about this one, so report **NOT VERIFIED** with that finding as the reason. Charging it to the branch sends a developer hunting a regression that is not there, and the sibling module usually shows the declaration that is missing.

Selecting by parallel name reaches tests no CI batch runs, since `modules-unit` takes a curated class name list per suite rather than every `*Test.java`. A module's test classpath is built from that module's own declared dependencies, so a test whose rule needs classes the module never declares cannot run whatever the branch does. Tests extending `LiferayUnitTestRule` are the common instance, needing a chain that reaches `com.liferay.petra.process` and beyond.

## Checklist

Add one subitem per affected module:

```
- [ ] <module path>: <test class names>
```

## Time Estimate

~30 sec - 2 min depending on counterpart count.