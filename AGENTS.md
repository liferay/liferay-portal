# Arena Liferay Portal 7.0.6-GA7

Security-patch branch of Liferay Portal CE 7.0.6 maintained by Axiell (Arena). Branch: `arena-7.0.6-ga7`. Main branch for PRs: `6.2.x`.

**This is a legacy/old version** of both Arena Liferay Portal and its consuming project, Arena (`arena-parent`). Do not assume feature parity with current Arena — e.g. this repo only ever built `elasticsearch6` (`portal-search-elasticsearch6-impl` + `build-test-elasticsearch6.xml`); `portal-search-elasticsearch7` does not exist in this tree's `modules/apps/`.

Architecture and security debt details: [@DESIGN.md](DESIGN.md)

## Related repos

- **arena-parent** (old 4.7.x line): `/opt/projects/arena-parent/4.7.x/arena-parent` — the consuming
  Arena application; deploys `arena-portlet.war` into this Liferay. See `DESIGN.md` here for how
  its build consumes this repo's published `com.liferay.portal.*` artifacts.
- **arena-liferay-modules 4.7.x** (old): `/opt/projects/arena-liferay-modules/4.7.x/arena-liferay-modules` —
  Gradle/OSGi workspace of bundles deployed into this same Liferay instance. Independent of
  arena-parent; no dependency in either direction on this repo's source, only on its published
  bundle jars at runtime.
- **eHub 4.7.x**: `/opt/projects/ehub/4.7.x/ehub` — separate Maven reactor (Java 17) consumed by
  arena-parent; not deployed into this Liferay and has no direct relationship with this repo.
- Note: `/opt/projects/liferay/portal/arena-7.0.6-ga7` (one level up from this repo) is the
  runtime/bundle install root (`bundles/`, `bundles.bak/`, DB backups), not a checkout — this repo
  (`arena-7.0.6-ga7/portal`) is the actual git source.

## Axiell Vault

Do NOT consult the Axiell Obsidian vault for this project. The vault documents the **current/newer** versions of Arena Liferay Portal and `arena-parent` — its architecture, module list, and dependency versions do not describe this legacy branch and will mislead. All relevant context for this repo is in `DESIGN.md` and `SECURITY_REVIEW-1.md`/`SECURITY_REVIEW-2.md` in this repo.

## Build System

Two parallel build systems coexist:

- **Ant** (`build.xml`) — portal core (`portal-impl`, `portal-kernel`, `portal-web`, `util-*`). Java 8, source/target 1.8.
- **Gradle** (`modules/`) — OSGi modules under `modules/apps/`, `modules/core/`, `modules/util/`. Uses `./gradlew` wrapper (Gradle 3.3).

Do not edit `build.wos.properties` or `release.wos.properties` directly; override per-user via `build.${user.name}.properties` / `release.${user.name}.properties`.

App server defaults to Tomcat; parent dir is `../bundles` relative to the repo.

## Common Commands

### Portal Core (Ant)
```bash
ant compile                    # compile portal-impl + portal-kernel
ant deploy                     # compile + deploy to Tomcat bundle
ant test-unit                  # run unit tests
ant test-integration           # run integration tests
ant test-class -Dtest.class=MyClassTest   # run a single test class
```

### OSGi Modules (Gradle — run from module dir or repo root)
```bash
cd modules
../gradlew deploy              # build + deploy one or all modules
../gradlew test                # unit tests for a module
../gradlew testIntegration     # integration tests for a module
../gradlew buildService        # regenerate Service Builder output from service.xml
../gradlew compileJava -DcompileJava.lint=deprecation,unchecked
```

Running from repo root also works:
```bash
./gradlew :modules:apps:blogs:blogs-web:deploy
```

### Source Formatter
```bash
ant format-source                        # format portal core sources
cd modules && ../gradlew formatSource    # format a module's sources
```

## Key Conventions

- Source max line length: 80 characters (`source.formatter.max.line.length=80`).
- Gradle dependencies: sorted alphabetically, configurations separated by blank lines, double-quoted strings.
- Tests: unit-only modules use `testCompile`; integration-only modules use `testIntegrationCompile`.

## CI / Workflows

- **Aikido** (`.github/workflows/aikido.yml`): dependency vulnerability scan on PRs targeting `arena-7.0.6-ga7` and `arena-7.4.3.129-ga129`. Fails on critical CVEs. Requires `AIKIDO_SECRET_KEY` secret.
- **Qodana** (`.github/workflows/qodana_code_quality.yml`): static code quality scan.
