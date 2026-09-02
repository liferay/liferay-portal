# Structural Smoke

## Trigger

Runs when the diff touches a file that one of the three scanners reads; see **Selection** for the path-to-scanner mapping.

## Match

`/configuration/.*Configuration\.java$|^portal-impl/src/portal-osgi-configuration\.properties$|^lib/|^\.classpath$|\.iml$|^\.idea/|/nbproject/project\.(properties|xml)$|\.gradle$|(^|/)(bnd|app)\.bnd$|^modules/.+/\.gitignore$|^modules/.+/README\.md$|^modules/.+/package\.json$|portal-log4j(-ext)?\.xml$|\.lfrbuild|^\.github/`

## Command

### Install Portal Snapshots

Run before the scanners:

```bash
(cd "${REPO_ROOT}" && ant compile install-portal-snapshots)
```

### Selection

Run only the scanners whose inputs the diff touches:

| Diff Touches | Run |
| --- | --- |
| `*Configuration.java` under a `configuration` package, or `portal-impl/src/portal-osgi-configuration.properties` | `ConfigurationEnvBuilderTest` |
| `lib/**`, `.classpath`, `*.iml`, `.idea/**`, `nbproject/project.{properties,xml}` | `LibraryReferenceTest` |
| `*.gradle`, `bnd.bnd`, `app.bnd`, `package.json`, a module `.gitignore` or `README.md`, `portal-log4j*.xml`, `.lfrbuild*`, `.github/**` | `ModulesStructureTest` |

### Scanners

Run the selected scanners:

```bash
(cd "${REPO_ROOT}/portal-impl" && ant test-class -Dtest.class="ConfigurationEnvBuilderTest")
```

```bash
(cd "${REPO_ROOT}/portal-kernel" && ant test-class -Dtest.class="LibraryReferenceTest")
```

```bash
(cd "${REPO_ROOT}/portal-kernel" && ant test-class -Dtest.class="ModulesStructureTest")
```

Run only the scanners the table selected. A diff that matches no row selects none, which is not a failure: report **NOT VERIFIED** and name the diff as outside every scanner's inputs.

Read the table's first column as basenames at any depth, except `portal-impl/src/portal-osgi-configuration.properties`, which is a path from the repository root. A deletion counts as touching, since removing one of these inputs changes the structure as surely as adding one.

### Verdict

**Judge from the JUnit XML, never from the exit status.** `build.properties` sets `junit.halt.on.failure=false`, so `ant test-class` prints `BUILD SUCCESSFUL` and exits 0 with failing tests in the same run. The results land at `<project>/test-results/unit/TEST-<FQCN>.xml`, under `portal-impl` or `portal-kernel` to match the scanner:

```bash
command grep \
	--extended-regexp \
	--only-matching \
	--regexp='(tests|failures|errors)="[0-9]+"' \
	"${REPO_ROOT}/portal-kernel/test-results/unit/TEST-com.liferay.portal.modules.ModulesStructureTest.xml"
```

Delete that tree before running. A stale file from an earlier run reads exactly like a fresh one, and a clean 8 of 8 sitting on disk will be read as this run's result.

PASS when every selected scanner reports `failures="0" errors="0"` with `tests` greater than zero. FAIL on any failure, and report the assertion message.

**Never rerun a failed scanner to confirm it.** `ModulesStructureTest` repairs what it finds: several of its assertions delete or move the offending file and then fail on it, so the second run passes with nothing having been fixed. Treat the first run as the verdict and check `git status` afterwards to see what it changed.

A `.lfrbuild*` change selects `ModulesStructureTest` but is not asserted on by it. The only `.lfrbuild-portal` rule forbids the marker in archived modules and asserts nothing about live ones, so the scanner runs, checks the module tree's other invariants, and says nothing about the marker itself. Report the scanner's own result, and say that the marker change went unexamined here. [module-registration.md](module-registration.md) is what reports that case.

## Checklist

One subitem per selected scanner:

```
- [ ] portal-impl: ConfigurationEnvBuilderTest
- [ ] portal-kernel: LibraryReferenceTest
- [ ] portal-kernel: ModulesStructureTest
```

## Notes

Only these three scanners belong here. Do not add `Log4jConfigUtilTest` or `SampleSQLBuilderTest` — they do not run in CI, and **Java Unit Tests** skips them.

## Time Estimate

~1-2 min for the scanners, plus the `install-portal-snapshots` build (fast when already built, a few minutes on a fresh checkout).