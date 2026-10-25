---

allowed-tools: [Agent, Bash, Edit, Glob, Grep, Read, Skill, Write]
argument-hint: "[--skip-pr-reviewer <reason>]"
description: Check that a PR is ready to be sent for review.
name: pr-check

---

# PR Check

Run premerge checks against the current branch. The skill iterates through the validations listed below, runs each one whose scope covers a changed path, and reports PASS or FAIL. Integration tests, Playwright tests, and Poshi tests are out of scope. Use the `test-plan` skill when their coverage is needed.

## Repository Settings

Use the column for the repository that a remote of this checkout points at (check `git remote --verbose`).

| Setting | `liferay/liferay-portal` | `liferay/liferay-portal-ee` |
| --- | --- | --- |
| **Base Branch** | `master` | `master-private` |
| **Forward Target** | `brianchandotcom/liferay-portal` | |
| **Scopes** | branch, portal, workspaces | branch, workspaces |
| **Skipped Validations** | Workspace Source Format | Source Format |
| **Rules Commit** | `HEAD` | `master` |

`liferay-portal` skips Workspace Source Format because Source Format already formats every changed file, workspace files included. `liferay-portal-ee` skips Source Format because the repository has no `portal-impl` to run the formatter from, and Workspace Source Format formats each workspace instead.

In `liferay-portal-ee`, this skill and its validations are copied from local `master`, so local `master` is the rules commit there.

`${BASE_BRANCH}` below stands for the **Base Branch** setting, and `${SOURCE_SHA}` stands for the **Rules Commit** setting, which is the commit this document and its validations were read from.

The **Forward Target** is the repository `ci:forward` merges a pull request into. When it is empty, skip every step below that uses it.

## Preconditions

- **On a feature branch.** When `HEAD` is `${BASE_BRANCH}` or detached, exit with a one line message.

- **Working tree clean.** `git status --porcelain` must return empty. When dirty, abort and ask the developer to commit first.

- **Rebased on the latest `${BASE_BRANCH}`.** Resolve the remote. Prefer `upstream`, otherwise the remote whose URL points at the repository named by the column in use (check `git remote --verbose`). When none resolves, compare `git merge-base HEAD ${BASE_BRANCH}` to `git rev-parse ${BASE_BRANCH}`. Abort and tell the developer to rebase when the two differ, and warn that the branch was not checked against a remote. Otherwise run these steps.

	1. `git fetch <remote> ${BASE_BRANCH}`.

	1. Fast forward local `${BASE_BRANCH}` to the fetched tip. When `${BASE_BRANCH}` is checked out in another worktree, fast forward it there with `git -C <worktree> merge --ff-only <remote>/${BASE_BRANCH}`. Otherwise update it in place with `git fetch <remote> ${BASE_BRANCH}:${BASE_BRANCH}`, which also creates `${BASE_BRANCH}` when it does not exist. Both are fast forward only. When the command fails for any reason, such as a diverged `${BASE_BRANCH}`, a worktree that is not clean, or a denied permission, warn the developer and stop the run. Never continue against a stale base, since every validation would then compare the branch with a base that lacks the latest commits.

	1. When the settings name a forward target, fetch its `${BASE_BRANCH}` from its GitHub URL rather than a remote, since remote names vary, and record the tip. When the fetch fails, warn the developer that the branch was not checked against the forward target and continue without `${FORWARD_TIP}`.

	```bash
	git fetch --no-tags https://github.com/<forward target>.git ${BASE_BRANCH} && FORWARD_TIP=$(git rev-parse FETCH_HEAD)
	```

	With `${FORWARD_TIP}` set, the branch sits on the forward target when it holds commits of the target that `<remote>/${BASE_BRANCH}` lacks, which this command reports by exiting `1`:

	```bash
	git merge-base --is-ancestor "$(git merge-base HEAD "${FORWARD_TIP}")" "<remote>/${BASE_BRANCH}"
	```

	1. `git rebase <remote>/${BASE_BRANCH}`, or `git rebase "${FORWARD_TIP}"` for a branch that sits on the forward target. On a clean rebase, continue against the rebased branch. On conflict, list the unmerged files (`git diff --diff-filter=U --name-only`) and ask the developer who should resolve the conflicts. When the developer asks you to resolve them, fix the conflicts, `git add` the files, and run `git rebase --continue`. In every other case (the developer resolves them, the conflicts cannot be resolved, or the rebase fails otherwise) run `git rebase --abort` and stop the run.

	1. When `${FORWARD_TIP}` is set, test whether `ci:forward` can merge the branch into it.

	```bash
	git merge-tree --name-only --write-tree "${FORWARD_TIP}" HEAD
	```

	Exit `0` means the branch merges cleanly, so continue. Exit `1` means `ci:forward` will refuse the pull request. The lines after the first, up to the first blank line, name the conflicted files, and `git log --format='%h %s' "HEAD..${FORWARD_TIP}" -- <files>` names the commits of the forward target that touched them. Stop the run, give the developer both lists, and say what fixes it: rebase onto `${FORWARD_TIP}`, resolve the conflicts, run pr-check again, and send the pull request to the forward target directly. Offer to run the rebase, handle its conflicts the way the rebase step above does, and start pr-check over once it completes. Any other exit means the merge was not checked, so warn the developer and continue.

- **Skills current in `liferay-portal-ee`.** In `liferay-portal-ee` only, and only when this skill's directory lies inside `${REPO_ROOT}`, run `git fetch <remote> master`. A skill read from outside the checkout, such as a personal skill linked to a `liferay-portal` checkout, was not copied, so it cannot be stale and the check is skipped. When `git rev-parse master` differs from `git rev-parse FETCH_HEAD`, the copied skills are stale, so stop the run and tell the developer to copy them again from the latest `master`.

- **Diff baseline is `${BASE_REF}`.** That is `${FORWARD_TIP}` for a branch that sits on the forward target, and local `${BASE_BRANCH}` otherwise. After the rebase, the three dot diff against `${BASE_REF}` is the baseline.

- **Diff is nonempty.** When the three dot diff produces no files, exit with a one line message — no validation produces useful signal on a clean branch.

- **No source file hidden from git.** A file the build reads but git never sees passes every validation here and fails CI's clean checkout, as a Sass import of an uncommitted partial once did. The clean tree check above cannot see it when the developer's own ignore rules hide it, in `core.excludesFile` or `.git/info/exclude`, since `git status` honors those. List the ignored files under each changed module, keep those hidden by a rule outside the repository's own `.gitignore` files, and keep the source and build file types among them:

	```bash
	MERGE_BASE=$(git merge-base HEAD "${BASE_REF}")

	git diff --name-only --no-renames "${MERGE_BASE}...HEAD" \
		| bash <skill directory>/find_modules.sh "${MERGE_BASE}" \
		| cut -d " " -f1 \
		| command grep --invert-match '^-$' \
		| sort --unique \
		| xargs --no-run-if-empty git ls-files --directory --exclude-standard --ignored --others -- \
		| git check-ignore --stdin --verbose \
		| command grep --extended-regexp --invert-match '^([^/:][^:]*/)?\.gitignore:' \
		| command grep --extended-regexp '\.(bnd|cjs|css|ftl|gradle|java|js|json|jsp|jspf|jsx|mjs|properties|sass|scss|ts|tsx|xml)$'
	```

	Each line names the rule and the file it hides. When any line prints, abort and ask the developer to commit or delete each file. The type filter keeps a personally ignored `.DS_Store` or editor file from stopping the run, and `--directory` keeps a whole ignored directory, such as `build`, to one line that the filter then drops.

- **Shared Gradle build cache.** `gradlew` gives every checkout its own Gradle user home in `.gradle`, so a new worktree starts with an empty build cache, and its first full build compiles every module from scratch. Point this checkout's build cache at one directory every checkout on the machine shares. Use `${SHARED_GRADLE_CACHE}` when it is set, and `~/.liferay/gradle-build-cache` otherwise, beside the mirrors cache the build already keeps in `~/.liferay`:

	```bash
	REPO_ROOT=$(git rev-parse --show-toplevel)

	SHARED_GRADLE_CACHE=${SHARED_GRADLE_CACHE:-${HOME}/.liferay/gradle-build-cache}

	mkdir -p "${REPO_ROOT}/.gradle/caches" "${SHARED_GRADLE_CACHE}"

	BUILD_CACHE_DIR=${REPO_ROOT}/.gradle/caches/build-cache-1

	if [[ -d ${BUILD_CACHE_DIR} && ! -L ${BUILD_CACHE_DIR} ]]
	then
		rsync --archive --exclude="*.lock" --ignore-existing "${BUILD_CACHE_DIR}/" "${SHARED_GRADLE_CACHE}/"

		rm -fr "${BUILD_CACHE_DIR}"
	fi

	ln -fns "${SHARED_GRADLE_CACHE}" "${BUILD_CACHE_DIR}"
	```

	Link only the build cache, never the whole user home. Ant writes the properties Gradle needs, such as `liferay.home` and `baseline.jar.report.level`, into this checkout's own `.gradle/gradle.properties`, and a shared user home would read none of them. An existing cache folder is merged into the shared directory before the link replaces it, so nothing cached is lost: each entry is named by its cache key, and an entry already there holds the same content. Tell the developer which directory is in use the first time the link is created or repointed, in one line, such as `Using ~/.liferay/gradle-build-cache as the shared Gradle build cache.` When the link already points there, say nothing.

## Input

### Arguments

`--skip-pr-reviewer` skips the [PR Reviewer](validations/branch/pr-reviewer.md) validation for this run. A skip requires a reason: take it from the text following the flag when present, otherwise prompt the developer for one. Do not dispatch the validation, and leave it out of the Pass 1 estimate. List it in the Results Summary as `NOT VERIFIED` with the note `**PR Reviewer was skipped with --skip-pr-reviewer.** <reason>`, so that the skip and its reason travel into the PR description. Without the flag, run PR Reviewer whenever it fires.

### Diff

```bash
MERGE_BASE=$(git merge-base HEAD "${BASE_REF}")

git diff --name-only --no-renames "${MERGE_BASE}...HEAD"
```

Keep `--no-renames`. A detected rename collapses to its new path alone and hides the old one from every validation.

### Routing

The folder a validation file sits in decides its scope. A validation sees only the changed paths its scope covers.

- **Branch.** A file in `validations/branch` checks the branch as a whole and sees every changed path.

- **Portal.** A file in `validations/portal` sees every changed path that belongs to no workspace.

- **Workspaces.** A file in `validations/workspaces` runs once for each workspace the branch changed. A workspace is a directory named `workspaces/<name>-workspace`, and every path beneath it belongs to that workspace. The validation sees those paths relative to the workspace directory.

Paths directly under `workspaces` that sit in no workspace, such as the refresh scripts, belong to the portal scope.

In every workspace other than `liferay-sample-workspace`, a changed path that `workspaces/refresh_other_workspaces.sh` regenerates is seen only by [Generated Workspace File](validations/workspaces/generated-file.md). Such a path may change only through a refresh, and building it would only repeat what the sample workspace already checks. A path is regenerated when it does not match the regex that validation builds from the script's `--exclude` patterns. A workspace whose changed paths are all regenerated therefore runs no other workspace validation. Generated Workspace File in turn sees only regenerated paths, so it runs only for a workspace where the branch changed a regenerated path. A checkout without `workspaces/refresh_other_workspaces.sh`, such as `liferay-portal-ee`, regenerates nothing, so every changed path of a workspace goes to the other workspace validations and Generated Workspace File never runs.

Run only the validations in the scopes the settings enable, and skip every validation the settings name. When the portal scope is disabled, list every changed path that belongs to no workspace in the Results Summary as unchecked, so that a change nothing examined never reads as a pass.

### Match

Each validation in the branch and portal scopes fires on the changed paths, relative to the repository root, that match the regex under its `## Match`. ` &! ` splits a regex into an include side and an exclude side, and a path has to match the first and not the second.

The script [select_paths.sh](select_paths.sh) beside this document applies the routing and the regex. Given the merge base, a validation file, and, for a workspace validation, the workspace directory name, it prints the paths that the validation selects and exits 1 when it selects none. It prints a workspace validation's paths relative to the workspace.

### Module

The script [find_modules.sh](find_modules.sh) beside this document reads paths relative to the repository root on standard input and runs from any directory inside it. For each path, it finds the module and writes one line, `<module> <path>`, such as `modules/apps/blogs/blogs-api modules/apps/blogs/blogs-api/src/main/java/Foo.java`. A **Command** that works on modules pipes its paths through it.

- **Module** is the outermost directory above the path that the path's build root builds as a project. In a workspace, that is a directory holding `bnd.bnd` or `client-extension.yaml`, the rule the workspace Gradle plugin uses. The plugin also builds themes, wars, and JavaScript portlets, which no validation selects yet. Everywhere else, it is a directory holding `bnd.bnd`, `build.xml`, `gulpfile.js`, `src/main/resources/application.properties`, or a `package.json` with a `liferayTheme` key, not counting `modules` itself, the rule the Gradle settings plugin uses. It is `-` when there is none. A module under `modules` also has a Gradle project path, the directory without `modules/` and with `:` for `/`, such as `apps:blogs:blogs-api`.

### Build Root

The build root of a changed path is its workspace directory when it belongs to a workspace, and `${REPO_ROOT}` otherwise. A validation that needs a place to search, such as a sweep for references, searches the build root of the path it is examining. Every workspace validation runs its commands from `${BUILD_ROOT}`, which is the absolute path of its workspace directory.

## Expected Output

**`PASS`** or **`FAIL`**, followed by a **Results Summary** table the `pr` and `pr-check-publish` skills reuse to record what was tested on the GitHub PR.

The procedure runs in two passes over the validations, in the order below. The order is dependency-driven: drift first (later validations see the regenerated tree), then formatting, then build, then tests.

1. [Instance Wrapper Build](validations/portal/instance-wrapper-build.md)

1. [REST Builder](validations/portal/rest-builder.md)

1. [Service Builder](validations/portal/service-builder.md)

1. [Go Generate](validations/portal/go-generate.md)

1. [Generated Workspace File](validations/workspaces/generated-file.md)

1. [PR Reviewer](validations/branch/pr-reviewer.md)

1. [Source Format](validations/branch/source-format.md)

1. [Workspace Source Format](validations/workspaces/source-format.md)

1. [Go Source Format](validations/portal/go-source-format.md)

1. [Module Registration](validations/portal/module-registration.md)

1. [Portlet Title](validations/portal/portlet-title.md)

1. [Service Registration](validations/branch/service-registration.md)

1. [Transaction Usage](validations/branch/transaction-usage.md)

1. [ConcurrentMapperHashMap Usage](validations/branch/concurrent-mapper-hash-map-usage.md)

1. [CacheField Usage](validations/branch/cache-field-usage.md)

1. [Startup Sync Usage](validations/branch/startup-sync-usage.md)

1. [Service Holder Usage](validations/branch/service-holder-usage.md)

1. [HTML Escaping](validations/branch/html-escaping.md)

1. [Full Portal Build](validations/portal/full-portal-build.md)

1. [Per-Module Compile](validations/portal/per-module-compile.md)

1. [Cross-Module Compile](validations/portal/cross-module-compile.md)

1. [Baseline](validations/portal/baseline.md)

1. [Theme Build](validations/portal/theme-build.md)

1. [Workspace Compile](validations/workspaces/workspace-compile.md)

1. [Poshi Syntax](validations/portal/poshi-syntax.md)

1. [Structural Smoke](validations/portal/structural-smoke.md)

1. [Java Unit Tests](validations/portal/java-unit-test.md)

1. [PQL Validation](validations/portal/pql-validation.md)

1. [JavaScript Unit Tests](validations/portal/javascript-unit-test.md)

1. [Workspace Unit Tests](validations/workspaces/workspace-unit-test.md)

1. [Helm Unit Test Order](validations/portal/helm-unit-test-order.md)

Process each validation in a subagent.

### Pass 1: Estimate

Run [select_validations.sh](select_validations.sh) beside this document once, from `${REPO_ROOT}`. It prints a line for every validation: `== <file> (<count> paths)` for one that fires, followed by its `## Preconditions` and `## Time Estimate` sections, and `-- <file> (not fired)` for one that does not. A workspace validation names its workspace after the file. A validation fires when `select_paths.sh` prints a path, and a workspace validation is tried once for each workspace the branch changed, as **Routing** describes:

```bash
bash <skill directory>/select_validations.sh "$(git merge-base HEAD "${BASE_REF}")"
```

Leave out the validations the settings skip or whose scope they disable. Sum the time estimates of the rest for the cumulative total, counting a workspace validation once for each workspace it fired for. Add about 3 minutes once when any of them names **Portal Snapshots**, and up to 2 minutes once when any names **Portal Classpath**. Estimate from the path counts the script prints rather than resolving modules, since the total only decides whether to ask the developer.

When the total exceeds 20 minutes, surface the breakdown and ask the developer whether to trim a validation or proceed.

The output, less the validations the settings skip or whose scope they disable, is the **ledger**, the record of what this run owes. Pass 2 builds the table from it, so a validation that fired is accounted for whether or not it ever ran.

### Shared Preconditions

A validation names the setup it needs under `## Preconditions`, and its **Command** never performs that setup itself. Take the union of the names across the validations that fired and run each once, after Pass 1 and before Pass 2 dispatches anything. Deduplicate by name rather than by command, and take nothing from a validation that did not fire, so a diff of Markdown alone installs no snapshot. Run them in the order below, since **Portal Classpath** deploys the jars **Portal Snapshots** builds.

- **Portal Snapshots.** Build the top level Ant projects and install each as a snapshot under `${REPO_ROOT}/.m2`, so that a module compiles against the branch's own kernel rather than whatever an earlier build left there:

	```bash
	(cd "${REPO_ROOT}" && ant compile install-portal-snapshots)
	```

	A build that exits zero has not yet proved the tree usable. Confirm that each of the seven projects **Baseline** compares left its jar and installed its snapshot at the version its `bnd.bnd` declares. The loop prints each project that did not, so empty output is a pass:

	```bash
	for project in portal-impl portal-kernel portal-test util-bridges util-java util-slf4j util-taglib
	do
		artifact=com.liferay.$(echo "${project}" | tr - .)
		version=$(sed -e "s/^Bundle-Version: //p" -n "${REPO_ROOT}/${project}/bnd.bnd")

		if [[ ! -f ${REPO_ROOT}/${project}/${project}.jar || ! -f ${REPO_ROOT}/.m2/com/liferay/portal/${artifact}/${version}-SNAPSHOT/${artifact}-${version}-SNAPSHOT.jar ]]
		then
			echo "${project}"
		fi
	done
	```

	A snapshot that an earlier build installed at an older version looks present to anything but this check, and the first compile that needs the branch's version fails on `Could not find com.liferay.portal.test:<version>-SNAPSHOT`.

- **Portal Classpath.** Deploy the jars a module's test classpath reads from the app server, which `modules/build.gradle` takes from the bundle's `WEB-INF/lib` and `WEB-INF/shielded-container-lib`. A `testIntegration` compile gets `portal-kernel`, `portal-impl`, and `petra` only from there, and a unit test gets `log4j` only from there, so without them both fail on every branch alike. This is the unit test bundle CI's `prepare-test-bundles` builds, plus `util-taglib` and `modules/core`, which a `testIntegration` compile also reads. Each `ant deploy` copies the jar **Portal Snapshots** already built, so the deploys run in any order. Leave out the `unzip-tomcat` step CI runs first. It starts by deleting `${app.server.tomcat.dir}`, the bundle the developer runs and every checkout shares, and the deploys create the directories they write to without it:

	```bash
	(cd "${REPO_ROOT}" && ant deploy-additional-jars)

	for project in portal-impl portal-kernel portal-test util-java util-taglib
	do
		(cd "${REPO_ROOT}/${project}" && ant deploy)
	done

	("${REPO_ROOT}/gradlew" \
		--parallel \
		--project-dir "${REPO_ROOT}/modules/core" \
		deploy)
	```

- **SDK.** Set up the SDK the source formatter runs from:

	```bash
	(cd "${REPO_ROOT}" && ant setup-sdk)
	```

When a precondition fails, stop the run. Dispatch no validation, publish no Results Summary, and report the precondition, the decisive lines of its log, and the validations that named it. A validation cannot report this on its own behalf, since it sees only its own **Command** and cannot know that its setup never ran, and a `NOT VERIFIED` row in its place would still publish a `success` marker for a run that was never set up.

### Pass 2: Execute

The rules below divide in two. Dispatch, ordering, handoffs, the ledger, and the overall state belong to this runner. Reading a log, judging a result, and reporting a note belong to the subagent, which never sees this document and is told only what it needs.

Start every fired validation in the ledger at `NOT RUN`, then spawn one subagent for each, of type `pr-check-validation`, defined in [pr-check-validation.md](../../agents/pr-check-validation.md), which runs on Sonnet. A subagent spawned without that type inherits your model instead. **Give it only the `## Command` and `## Autocommit` sections of its validation.** Pass each section whole, from its heading to the next `## ` heading, and never through a line cap such as `head`, `tail`, or a fixed line range: a truncated section reads as complete, the subagent cannot know what it lost, and nothing downstream recovers it. A validation with no `## Autocommit` section makes no commit, so say so rather than leaving the subagent to infer it from an absence. That says nothing about the working tree, since a validation without one can still build and leave output behind. Record one of the results below, and capture any note the command directs it to return. Tell the subagent to run every command in the foreground and to return only once it has a verdict. A subagent that starts a build in the background and returns while it runs hands back no verdict, and nothing reports the build's result afterward. Do not halt on a failure, so the developer sees the full picture.

A validation returns one of five results:

- **`FAIL`**: it found a real defect.

- **`NO COVERAGE`**: it found something to check and nothing that could ever check it, such as a changed class with no unit test or changed modules with no integration tests. Of the results that do not block, it is the only one a developer can act on, by adding the coverage or judging the change by hand. A validation whose command cannot fail on any content of the diff reports this too, never `PASS`, since a green build that never read the change is not one that passed.

- **`NOT APPLICABLE`**: its work set came out empty, so there was nothing to check, as when every selected path was deleted or the change is surface only. Its row leaves the table, the way a validation that never fired does, and the Results Summary names it on one line instead.

- **`NOT VERIFIED`**: something could have checked the branch and this run did not, such as an environment failure, a red test in a module the diff never touched, or a handoff that produced no result.

- **`PASS`**: it examined the change and found nothing wrong.

`NO COVERAGE`, `NOT APPLICABLE`, and `NOT VERIFIED` do not block. Each carries a reason naming what went unexamined, and `NO COVERAGE` and `NOT VERIFIED` keep their row in the table with whatever detail the validation asks for beneath it.

A `FAIL` run still autocommits where its **Autocommit** section says to, because a formatter's repairs are worth keeping even when an unfixable violation blocks the branch, and so does a `PASS` run. A run that ends `NO COVERAGE`, `NOT APPLICABLE`, or `NOT VERIFIED` does not autocommit, since a run that established nothing has produced nothing worth recording and the tree it would stage may hold a half finished setup. Tell the subagent this when you dispatch it, since its **Autocommit** section reads as unconditional on its own.

Run workspace validations one workspace at a time. Each workspace build has its own Gradle daemon and heap and shares the Gradle cache with the others. Never pass `--offline` to a workspace build, since a cache miss under it prints as a dependency error that reads exactly like a compile failure.

Run a validation that autocommits with **nothing else that writes to the working tree** in flight, since `git add --all` cannot tell its own repair from one another validation made seconds earlier and commits the wrong work under its title. A validation that only reads is safe alongside anything, provided it reads a commit it pinned at the start rather than the working tree or the index. A concurrent validation moves the tree when it writes and the index when it stages, so only a pinned commit holds still for the whole run. Whether a validation reads or writes can depend on the diff, since **Module Registration** only reports when its markers are all removals and builds when one is added, so treat it as a writer unless its own text rules the writing branch out for the diff at hand. Keep tree writers off each other too, since several share build output such as `modules/build/node`.

A validation may hand off to another, as **Per-Module Compile** does when its deploy set grows past the point where one full build is cheaper. Run the validation it names, give the table that validation's row and result, and mark the one that handed off `NOT VERIFIED`. Pass 1 selects on the changed paths alone and cannot see a set Pass 2 derives, so a handoff is the only way those branches run.

An autocommit can change the diff, so recompute the ledger after a validation whose commit may add a path Pass 1 never saw, as Baseline's `packageinfo` and `bnd.bnd` repairs do, and dispatch whatever newly fires. Skip it after a validation that can only touch paths the branch already changed, such as a formatter running in current branch mode, since its commit cannot widen the diff.

Tell Per-Module Compile whether Full Portal Build is in the run and, when it is, whether it succeeded, since a successful build lets it narrow its work and a failed one does not.

Give the subagent everything that the validations use but none of them defines:

- `${REPO_ROOT}`, `${BASE_BRANCH}`, `${SOURCE_SHA}`, and `${MERGE_BASE}`.
- `${BUILD_ROOT}` for a workspace validation.
- `${SKILL_DIR}` and `${VALIDATION_FILE}` for a branch or portal validation, as the absolute paths of the directory holding this document and of its validation file.
- The shared preconditions that ran, and that they are satisfied.
- The ticket that its **Autocommit** section writes into a commit title as `<TICKET>`.
- The result that its own verdict implies for committing, since the rule above lives here and the subagent never reads this document.

Resolve `${REPO_ROOT}` with:

```bash
REPO_ROOT=$(git rev-parse --show-toplevel)
```

Resolve `<TICKET>` from the branch name the way [commit.md](../../rules/commit.md) does, which is the ticket pattern of uppercase letters, hyphen, and digits rather than the whole branch name, so `LRCI-8065-rules` and `LRCI-8065-fixture-pr1f` both give `LRCI-8065`. A subagent that is not given it commits under the literal string.

Tell it how to commit as well, since no validation says. The title is the whole message, with no body and no attribution footer of any kind, which is the repository's convention for a generated commit.

When the validation's **Command** is a build (gradle, ant, npm, jest), keep the whole log and bound only what is displayed. Keep the logs in `${REPO_ROOT}/build/pr-check/<validation>`, which `.gitignore` covers, so the developer can read them afterward and no `git add` sweeps them up. A workspace validation runs once per workspace, so add the workspace as a further directory, `<validation>/<workspace>`, or each run deletes the last one's logs. Clear that directory first, since a log left by an earlier run reads exactly like this one's:

```bash
LOG_DIR="${REPO_ROOT}/build/pr-check/<validation>"

rm -fr "${LOG_DIR}"

mkdir -p "${LOG_DIR}"

LOG_CHECK="${LOG_DIR}/check.log"
LOG_SETUP="${LOG_DIR}/setup.log"

<setup command> > "${LOG_SETUP}" 2>&1
<check command> > "${LOG_CHECK}" 2>&1

tail --lines=100 "${LOG_CHECK}"
```

Give each build its own log. A single binding reused across two builds means the second overwrites the first, and the evidence that setup succeeded is gone by the time you need it. A **Command** with one build needs only one.

Write nothing outside that directory. A file left in `${REPO_ROOT}` or its parent is litter at best, and an autocommit can sweep it into the branch. Leave the working tree as you found it apart from the validation's own autocommit, and restore any tracked file a build rewrote as a side effect, such as the release info tokens in `portal-kernel/src/com/liferay/portal/kernel/util/ReleaseInfo.java`.

Judge from each full log rather than from the tail. A source formatter prints its violations in the middle of a run and its stack trace at the end, so the last hundred lines carry the failure and not the reason for it. Search every log the run produced for the build tool's markers (`BUILD SUCCESSFUL`, `BUILD FAILED`, `Tests:`, `Test Suites:`) and for whatever the validation says its finding looks like. Apply this to build commands only, and leave inert commands like `git status --porcelain` untouched.

When a **Command** runs more than one build, keep a log per build and judge each on its own, since a setup step and the check it precedes fail for different reasons. The setup is the earlier build the later one depends on, and a validation that runs two checks rather than a setup and a check should say so.

A failing setup is positive evidence the run could not proceed, so report `NOT VERIFIED` naming it rather than a verdict on a check that never ran, and judge the check itself only once its setup succeeded. That is the case the rule below is about, even though its examples are all external.

A log can be far larger than you can read. Search it for the markers above rather than reading it through, and quote the lines you found. The displayed tail is for the developer, never the basis of a verdict.

Return the decisive lines verbatim with the verdict — the marker, count, or task line the validation names as its proof — rather than a description of them. A verdict that arrives without them is `NOT VERIFIED`: the runner can quote what it was given and cannot vouch for what it was not, and otherwise a subagent that skipped a command reads the same as one that ran it.

When a command ran and exited nonzero, that status alone does not separate a validation that found something from one that could not run. Report `NOT VERIFIED` there only on positive evidence that the run could not proceed, such as a failed download, a registry timeout, or a process killed for memory. Without that evidence report `FAIL`, since a subagent is given the Command and Autocommit sections alone and often cannot tell a finding from an infrastructure error by its wording, and defaulting to `FAIL` leaves a real defect blocking rather than passing it through as unverified.

That governs a nonzero exit and nothing else. A validation that names its own `NOT VERIFIED` case reports it whatever any command exited.

## Results Summary

After the two passes complete, emit a Results Summary block. It is the canonical record of what was tested, embedded verbatim by the `pr` skill into the PR description and reused by the `pr-check-publish` skill when recording a run on an existing PR.

Capture the tested commit with `git rev-parse HEAD` **after** Pass 2 completes, so the SHA reflects the tree that was actually exercised — including any autocommits the validations made, such as the `<TICKET> SF` source-format commit. This is the commit the `pr` skill pushes as the PR head and the commit the webhook binds the `pr-check` status to, so a reviewer can tell whether the current head is the one that was tested.

The block is the overall state and tested SHA, followed by a table with one row per **matched** validation — the validations that actually ran, in the execution order above, apart from those that returned `NOT APPLICABLE`. A workspace validation has one row for each workspace it ran for, named with the workspace in parentheses, such as `Workspace Compile (liferay-aihub-workspace)`. Validations that did not fire are omitted rather than listed as skipped, so the table reflects only what the diff exercised. When no validation fired, omit the table as well and say so in one line, since a header with no rows reads as a table that failed to render.

When the branch sits on the forward target, follow the overall state with one line saying that the diff was taken against `${FORWARD_TIP}` and that the pull request goes to the forward target.

Name every validation that returned `NOT APPLICABLE` on one line beneath the table, such as `Not applicable: HTML Escaping, Structural Smoke.`, and leave the line out when none did. A selection can come back wrongly empty, so without the line a broken selection reads exactly like a diff with nothing in it.

```markdown
**pr-check: PASS** — tested on `<head-SHA>`

| Validation | Result |
| --- | --- |
| Source Format | PASS |
| Module Registration | NO COVERAGE |
| Java Unit Tests | PASS |

Module Registration had nothing to run. The diff removes `.lfrbuild-ci` from `apps:blogs:blogs-api`, which drops the module from CI's deploy pass and breaks no build, so whether CI still needs it is the developer's judgment.
```

A row still `NOT RUN` when Pass 2 ends is a validation that matched the diff and never ran, which is how LPD-100427 shipped, so it stays in the table as `NOT RUN` and fails the run.

The overall state is `FAIL` when any row is `FAIL` or `NOT RUN`, and `PASS` otherwise. A `NO COVERAGE` or `NOT VERIFIED` row leaves the overall state alone, and the marker the `pr-check-publish` skill writes still records `success`, since the webhook accepts only `failure`, `skipped`, and `success` and silently discards anything else.

A validation may qualify its verdict, as **Baseline** does when it names the universe it compared, and the qualifier follows the verdict in the same cell rather than in a note. The overall state reads the verdict alone, so a qualified `PASS` is still a `PASS`.

Every row whose validation returned a note appends it below the table, separated by a blank line. A `FAIL`, a `NO COVERAGE`, and a `NOT VERIFIED` always carry one. A `PASS` carries one only when the run left something unexamined, as **Module Registration** does when a diff pairs an addition it verified with a removal it can only report. A `PASS` that examined the whole change carries none. The notes travel verbatim into the PR description through the `pr` skill and into any comment the `pr-check-publish` skill posts, so keep each to one short sentence naming what failed or what went unexamined. Leave out why a gap is acceptable or what else covers it, and leave out how the run went, such as the commands, flags, task counts, and modules built. Nothing else goes below the table.

After the Results Summary block, tell the developer to run the `test-plan` skill in either of these cases. This message is not part of the block.

- **Java Unit Tests** names a changed class that has no unit test.
- The diff changes a `*.batch-engine-data.json` file, a `resource-actions/*.xml` file, or a file under `src/main/resources/site-initializer`.