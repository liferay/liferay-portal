---

allowed-tools: [Agent, Bash, Glob, Grep, Read]
description: Generate a targeted local test plan for branch changes. Use when the user wants to know which tests to run before merging, asks for a test plan, wants to validate changes locally, or mentions running tests for their branch. This skill analyzes commits on top of master. It builds and starts the portal, then writes and runs a shell script of integration, Playwright, and Poshi tests that fits a 20 minute local budget.
name: test-plan

---

# Test Plan Generator

Produce a runnable shell script that executes the tests most likely to regress given the current branch's changes compared to master.

The full test suite takes hours to run, so the team merges aggressively and relies on a daily full-suite run that delivers results within 24 hours of merge. This skill produces a focused premerge script (under 20 minutes) that mitigates risk without attempting full coverage — its goal is to catch the most likely regressions, not every possible one.

Include only integration, Playwright, and Poshi tests. Never add a unit test.

## How This Project Organizes Tests

Consult the reference file for detailed test organization patterns:

```
${CLAUDE_SKILL_DIR}/references/test-organization.md
```

## Workflow

### Understand the Changes

```bash
git diff master...HEAD --name-only
git log master..HEAD --oneline
```

Read the changed files to understand what the changes actually do — not merely which files were touched, but what behavior changed. This understanding drives the test selection.

### Required Tests

Every plan includes these tests:

- **Smoke Tests**: For each row of the table after this list that the diff matches, include that row's test from `modules/test/portal-smoke-test`.
- **Tests the Branch Changed**: Include every integration test, Playwright spec, and Poshi testcase that `git diff --name-only master...HEAD` lists.

| Changed Path | Condition | Test |
| --- | --- | --- |
| `*.batch-engine-data.json` | Any change | `CompanyLocalServiceTest` |
| `resource-actions/*.xml` | Any change | `ResourceActionsDefinitionTest` |
| `src/main/java/**/*Configuration.java` | Any change | `ModuleConfigurationLocalizationTest` |
| `src/main/java/**/*Portlet.java` | Added | `PortletTitleTest` |
| `src/main/java/**/*ResourceImpl.java` | Added, or its `implements` line changed | `JAXRSResourceTest` |
| `src/main/resources/site-initializer/**` | Any change | `BundleSiteInitializerTest` |

### Identify What Could Regress

Consider the blast radius of each change:

- **API or Interface Changes** (`portal-kernel`, `*-api` modules): Anything that depends on the changed API could regress. Search for consumers.
- **Mechanical or Repetitive Changes** (e.g., adding a property across 200 files): The core logic test is essential. Also include a representative sample of end-to-end tests from affected modules to verify the mechanical change does not introduce regressions.
- **Service Implementation Changes**: Tests for the service itself, along with tests for features that depend on it.
- **Shared Infrastructure Changes** (e.g., a registry, a framework class, a base class): Every module that relies on that infrastructure could regress. Select representative tests spanning the affected modules.
- **Web Layer Changes**: Playwright and Poshi tests for the affected UI.

The objective is not to "find every test in modules that were touched" — it is to find tests that exercise the code paths that changed.

### Find the Tests

For each area that could regress, search for test files. Use parallel Agent and Glob calls for speed. Consult `${CLAUDE_SKILL_DIR}/references/test-organization.md` for the exact patterns and conventions.

**Verify every test file exists** before including it in the plan.

### Prioritize Within the 20-Minute Budget

Put every required test in the script's `# Required` block. **Write the Script** shows where that block goes. Never drop a required test for any reason, including the budget.

The 20 minutes include starting or restarting the portal. They do not include building it. Probe the portal as `.claude/rules/tomcat.md` describes before estimating the budget. Fill the `# Selected` block from what is left of the 20 minutes. Add only the tests the change needs, in this priority order:

1. Integration tests that directly exercise the changed functionality and are not already required.

1. Representative integration tests from affected downstream modules — choose a few that cover distinct usage patterns rather than running them all.

1. Playwright tests for affected web modules (approximately 1–3 minutes per spec).

1. Poshi tests (approximately 2–5 minutes each).

Drop a candidate test only when it does not fit the budget or when reading the test shows that it does not reach the change. Never drop a test on reasoning about the change alone, without reading the test.

When the change affects many modules (for example, a framework change), do not attempt to test every single module. Choose a diverse sample that covers different usage patterns of the changed code.

### Build and Start the Portal

When `<bundles>` holds no `tomcat-*` directory, build the bundle first. From the repository root, run `ant -buildfile build-dist.xml unzip-tomcat`. Then run `ant all -Dgradle.stop.daemon.enabled=false`.

Deploy the branch to the bundle unless the step above or the `pr-check` skill already did. The `pr-check` skill already deployed the branch when a log under `build/pr-check/full-portal-build` or `build/pr-check/per-module-compile` reports `BUILD SUCCESSFUL`. The log must also be newer than the commit time that `git log --format=%ct --max-count=1 HEAD` prints. Otherwise, run `ant all -Dgradle.stop.daemon.enabled=false` from the repository root when the branch changes portal core, and run `<gradlew> deploy` from each changed module outside portal core whose name does not end in `-test`. Portal core is `portal-impl`, `portal-kernel`, `portal-test`, `portal-web`, `support-tomcat`, and the `util-*` projects.

Probe the portal as `.claude/rules/tomcat.md` describes. Bounce it when the branch changes portal core. Otherwise, start it when it is not ready.

### Write the Script

Write the script to `build/test-plan/test.sh` under the repository root. Create the directory if it does not exist. Delete any `build/test-plan/result.json` from an earlier run and any `test.sh` left at the repository root. The script must be self-contained and runnable via `bash build/test-plan/test.sh`. Use this structure:

```bash
#!/usr/bin/env bash
#
# Test plan for branch: <branch-name>
# Generated: <date> at <head-SHA>
# Estimated time: <X>m / 20m budget
#
# Changes: <N> commits, <N> files changed
# Affected areas: <list of module groups or components>
#
# Dropped:
# - <test>: <reason>
#

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/../.."

_EXIT_CODE=0
_RESULTS=()

function _run {
	if "${@}"
	then
		_RESULTS+=("{\"command\": \"${*}\", \"result\": \"passed\"}")
	else
		_EXIT_CODE=1

		_RESULTS+=("{\"command\": \"${*}\", \"result\": \"failed\"}")
	fi
}

# Required

<test commands — one per line, no blank lines between them>

# Selected

<test commands — one per line, no blank lines between them>

printf '{"head": "%s", "tests": [%s]}\n' "$(git rev-parse HEAD)" "$(IFS=,; echo "${_RESULTS[*]-}")" > build/test-plan/result.json

exit ${_EXIT_CODE}
```

**Critical rules for the script:**

- Replace each `<test commands>` block with the actual commands. Start every command with `_run`. The script exits with `${_EXIT_CODE}` at the end — `0` when all tests pass, `1` when any fail.
- Use `./gradlew --project-dir ./modules` for Gradle tasks (the script's `cd` puts the repository root as the working directory).
- Use `npx --prefix ./modules/test/playwright playwright test` for Playwright.
- Precede each command with a single line comment explaining why it is in the plan. State the rationale; do not restate the test name or module.
- When nothing is required, write `# Nothing required` under the `# Required` comment. Do not leave the comment out.
- List every dropped candidate with its reason under `# Dropped:` in the script's header comment.

After writing the script, mark it executable with `chmod +x build/test-plan/test.sh`.

### Run the Script

Run `build/test-plan/test.sh`. Report each test's result from `build/test-plan/result.json`. Leave the portal running.

## Guidelines

- Verify every test file exists before adding it to the script.
- When nothing is required and the changes are purely cosmetic, such as formatting or comments, say so. Skip building and starting the portal. Generate a script that exits `0`, with a header that explains why.