# Java Unit Tests

Runs the unit tests that exercise a changed class. An integration test under `src/testIntegration` selects nothing, since **Cross-Module Compile** compiles it and no unit test runs it.

## Match

`^modules/.+\.java$|^portal-(impl|kernel)/.+\.java$ &! /src/testIntegration/`

## Preconditions

- Portal Classpath
- Portal Snapshots

## Command

A change with no behavior intent, such as a rename, formatting, a comment, or Javadoc, needs no unit test, since the compile step and Structural Smoke cover it. When every changed Java file is such a change, run nothing and report **NOT APPLICABLE**, naming the change as surface only.

Take the changed Java files:

```bash
bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}"
```

Locate the counterpart test by parallel name: `Foo.java` → `FooTest.java` in the same module's `src/test/java/**` (for OSGi modules) or `portal-impl/test/unit/**` / `portal-kernel/test/unit/**` (for portal-core).

A changed file already under one of those test trees is a test itself, so look for no counterpart. When it declares a `@Test` method, schedule it. When it declares none, it is a base class, rule, or utility that other tests run through, and a change to it alters every one of them, so schedule each test class in the same tree that references it and declares a `@Test` method. Find the references the way **Cross-Module Compile** does, by the fully qualified name outside the package and by the simple name inside it, since a simple name such as `Test` also matches every `import org.junit.Test`:

```bash
(cd "${REPO_ROOT}" && git grep --cached --files-with-matches --fixed-strings --word-regexp '<FullyQualifiedName>' -- '<test tree>/*.java')
(cd "${REPO_ROOT}" && git grep --all-match --cached --files-with-matches --fixed-strings --word-regexp -e 'package <package>;' -e '<TypeName>' -- '<test tree>/*.java')
```

Keep the files either search lists that declare a `@Test` method:

```bash
printf '%s\n' <referencing file>... \
	| sort --unique \
	| (cd "${REPO_ROOT}" && xargs grep --files-with-matches --fixed-strings --word-regexp '@Test')
```

Do not select `Log4jConfigUtilTest` or `SampleSQLBuilderTest`, even when their counterpart source changes. Both are in `test.batch.class.names.excludes.permanent` and neither runs in the normal CI flow, so pr-check does not run them either.

Verify each counterpart file exists before scheduling it.

When a changed class has no counterpart, nothing here can exercise it, whatever the module costs to build. Name it as having no unit test. When nothing was scheduled at all, neither a counterpart nor a changed test, report **NO COVERAGE**. Otherwise the scheduled runs decide the verdict, and a PASS names the uncovered classes in its note. Running a suite that never touches the changed class establishes no more than declining to run it, so module size must not decide the verdict.

Running the suite anyway is worth doing when it is cheap, since it can catch an unrelated break. It cannot change the verdict either way, because a green suite that never loaded the changed class does not make it a PASS and a red one does not make it a FAIL.

For OSGi modules, run only the specific test class, batching counterparts within the same module. Take the Gradle project path of each changed module:

```bash
bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" \
	| bash "${SKILL_DIR}/find_modules.sh" "${MERGE_BASE}" \
	| cut -d " " -f1 \
	| command grep '^modules/' \
	| sort --unique \
	| sed "s#^modules/##; s#/#:#g"
```

Run each module's counterparts together:

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

Read the module's own build file for the missing class's module, which separates the two cases mechanically. When the module declares it, the branch broke a dependency that used to resolve, so FAIL and name it. When the module never declared it and the portal jars the runner deploys before this validation do not carry it either, the run fails on every branch alike and says nothing about this one, so report **NOT VERIFIED** with that finding as the reason. Charging it to the branch sends a developer hunting a regression that is not there, and the sibling module usually shows the declaration that is missing.

Selecting by parallel name reaches tests no CI batch runs, since `modules-unit` takes a curated class name list per suite rather than every `*Test.java`. A module's test runtime classpath is its own declared dependencies plus those portal jars, which is how a test extending `LiferayUnitTestRule` reaches `com.liferay.petra.process` and `log4j` without declaring either.

## Checklist

Add one subitem per affected module:

```
- [ ] <module path>: <test class names>
```

## Time Estimate

~30 sec - 2 min depending on counterpart count.