# Arena Liferay Portal 7.0.6-GA7

Security-patch branch of Liferay Portal CE 7.0.6 maintained by Axiell (Arena). Branch: `arena-7.0.6-ga7`. Main branch for PRs: `6.2.x`.

Architecture and security debt details: [@DESIGN.md](DESIGN.md)

## Axiell Vault

Do NOT consult the Axiell Obsidian vault for this project. All relevant context is in `DESIGN.md` and `SECURITY_REVIEW.md` in this repo.

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
