# Security Review 3 - Aikido Feature-Branch Scan 141607394

Source: `https://app.aikido.dev/featurebranch/scan/141607394`, saved locally as `~/Downloads/Feature branch diffs - Axiell-PL-GitLab - Aikido Security.html`.

Scan context visible in the saved page:
- Repository: `liferay-portal`
- Branch: `feature/security-patch`
- Compared commits: `3070ead` vs `66a232c`
- Aikido summary: `1570 issues introduced`, `1 issue solved`, CI gate passed
- Code quality scan skipped: too many changed files

## Executive Summary

The scan does not show new actionable source-code vulnerabilities in this branch. It shows one real fix: the `TunnelUtil` TLS hostname-verification bypass is solved.

All parsed introduced findings are generated or ignored build artifacts, not normal tracked source files. They fall under `modules/.npmscripts/build/`, `*/build/`, `*/classes/`, `*/tmp/`, `.gradle/`, or Aikido `/scan/` pseudo-paths. Spot checks confirm those path classes are ignored by `modules/.gitignore` (`build/`, `classes/`, `tmp/`) and are not tracked by git.

Recommendation: do not patch application code for this scan. Re-run Aikido from a clean checkout, or configure the scanner to exclude generated build outputs while still scanning intentional vendored dependencies such as `lib/portal/*.jar`.

## Parsed Findings

The saved HTML was rendered client-side, not a structured export. I extracted visible text into `/tmp/opencode/aikido-141607394-text.txt` and a parsed TSV into `/tmp/opencode/aikido-141607394-items.tsv`.

Parsed active introduced findings: 1567.

| Severity | Count | Assessment |
|---|---:|---|
| High | 289 | Exposed-secret detections in generated JavaScript build artifacts and generated `classes/` output |
| Medium | 1276 | Exposed-secret detections in generated Gradle/Bnd temp files |
| Low | 2 | Exposed-secret detections in generated test build temp files |

The saved page also shows 3 auto-ignored exposed-secret findings in generated `classes/` / `build/jspc` output, bringing the UI total to 1570 introduced issues.

## Finding Classes

| Class | Count | Example | Assessment |
|---|---:|---|---|
| `.npmscripts` build artifacts | 270 | `modules/.npmscripts/build/artifacts/@liferay/accessibility-menu-web/buildinfo.json` | Generated hash manifests / bundled JS artifacts; not source |
| Gradle/Bnd temp files | 1276 | `modules/apps/accessibility/accessibility-menu-web/build/tmp/jar/bnd8002896144911894456.bnd` | Generated Bnd properties; includes empty password/username properties and local paths, not committed secrets |
| `classes/` output | 13 | `modules/apps/login/login-web/classes/META-INF/resources/forgot_password.jsp` | Generated/copy output ignored by `modules/.gitignore` |
| Other `build/` output | 6 | `modules/apps/frontend-taglib/frontend-taglib-clay/build/node/packageRunBuild/...` | Generated bundled JS; not source |
| `.gradle` wrapper/cache output | 1 | `.gradle/wrapper/dists/.../getting-started.html` | Local Gradle cache/doc output; not source |
| Aikido `/scan/` temp path | 1 | `scan/modules/apps/frontend-js/frontend-js-aui-web/tmp/.../dockerfile.js` | Ace editor Dockerfile snippet in generated `tmp/`; not an actual container image |

Non-secret finding:

| Severity | Finding | Path | Assessment |
|---|---|---|---|
| Medium | Docker container runs as default root user | `scan/modules/apps/frontend-js/frontend-js-aui-web/tmp/META-INF/resources/aui/aui-ace-editor/ace/snippets/dockerfile.js` | False positive. This is an Ace editor syntax-snippet JavaScript file under generated `tmp/`, not a Dockerfile used to build a container. |

## Solved Finding

| Severity | Finding | Path | Assessment |
|---|---|---|---|
| Critical | Turning off TLS verification enables man-in-the-middle attacks | `/scan/portal-kernel/src/com/liferay/portal/kernel/service/http/TunnelUtil.java` | Solved by removing the conditional always-true hostname verifier. Matches Task 1 / commit `df331ab9ac42d`. |

## Spot Checks

These representative scan paths are untracked and ignored:

```text
modules/.npmscripts/build/artifacts/@liferay/accessibility-menu-web/buildinfo.json
modules/apps/accessibility/accessibility-menu-web/build/tmp/jar/bnd8002896144911894456.bnd
modules/apps/login/login-web/classes/META-INF/resources/forgot_password.jsp
modules/apps/frontend-js/frontend-js-aui-web/tmp/META-INF/resources/aui/aui-ace-editor/ace/snippets/dockerfile.js
```

`git check-ignore -v` maps them to `modules/.gitignore` rules:
- `build/`
- `classes/`
- `tmp/`

The `.npmscripts` sample `buildinfo.json` contains file SHA hashes, not credentials. The Bnd sample contains generated build properties with empty `systemProp.repository.private.username=` / `systemProp.repository.private.password=` values and local app-server paths, not committed secrets.

## What To Fix

No application-code fix is justified by this scan.

Operational scanner hygiene to prevent another noisy scan:
1. Re-run from a clean checkout without local build outputs.
2. Exclude generated paths from feature-branch scans: `.gradle/`, `modules/.npmscripts/`, `**/build/`, `**/classes/`, `**/tmp/` if Aikido supports glob excludes.
3. Keep scanning intentional vendored runtime dependencies (`lib/portal/*.jar`, OSGi module dependencies, WAR contents) so real dependency findings are still visible.

Open items from earlier reviews remain separate from this noisy scan:
- Node.js 6.6.0 build-tool findings are accepted risk for this security-patch branch because Node 18 breaks legacy Liferay theme `gulp` 3 builds.
- HAProxy hardening still needs deployment/runtime verification.
- `BeanPropertiesImpl` request-binding audit found a likely `class.*` property-path issue; fix needs explicit sign-off because it affects global request binding.
