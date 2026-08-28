# Module Registration

## Trigger

A `.lfrbuild-portal` or `.lfrbuild-ci` marker is added or removed. Either direction changes which modules a build deploys, and a marker only diff touches no source, so [per-module-compile.md](per-module-compile.md) does not fire.

A plain `gradlew` invocation includes every module by its directory and ignores markers. `ant all` instead runs Gradle with `-Dbuild.profile=dxp`, and the profile leaves out a module carrying no portal family marker, so a `project(":...")` reference from a module inside the set to one outside it fails the whole invocation at configuration. `.lfrbuild-portal` also puts the module in the `ant all` deploy set, per the note above `build.include.dirs` in [build.properties](../../../../build.properties). `.lfrbuild-ci` adds a `:<path>:deploy` task to the `marker.files.lfrbuild.ci.enabled` pass in [build.xml](../../../../build.xml), which CI enables and a default `ant all` does not, and that pass runs Gradle without the profile.

## Match

`(^|/)\.lfrbuild-(ci|portal(-private|-public)?)$`

## Command

Take the markers from the diff rather than from a `find`, which turns up marker copies under `node_modules` that are not modules:

```bash
HEAD_SHA=$(git rev-parse HEAD)
MERGE_BASE=$(git merge-base "${HEAD_SHA}" master) || exit 1

git diff --name-status "${MERGE_BASE}...${HEAD_SHA}" -- '*.lfrbuild-*'
```

Stop when `git merge-base` fails rather than carrying on, since an unresolvable `master` leaves `${MERGE_BASE}` empty, the diff becomes `...${HEAD_SHA}`, and it exits zero with no output, which reads as a diff carrying no markers.

Pin `${HEAD_SHA}` once here and read every later query at it, since a concurrent validation moves the working tree when it writes and the index when it stages.

A marker's module directory is the marker path with its file name stripped, so `modules/apps/blogs/blogs-api/.lfrbuild-portal` gives `modules/apps/blogs/blogs-api`. Its Gradle project path, written `<path>` below, is that directory with `modules/` stripped and every `/` replaced by `:`, so the same marker gives `apps:blogs:blogs-api`. Both branches below need it.

Split the markers by status before running anything, since the directions take different branches. Take `A` into the run below and `D` into the report further down. A rename (`R`) is both, so treat it as a removal at the old path and an addition at the new, and read its two tab separated paths rather than taking the second field, which is the old one. The pairing is an artifact rather than a move, since every marker is an empty file and git pairs any deleted marker with any added one, even across unrelated modules, so never simplify the split away by following the rename. A content change (`M`) to a marker changes no registration, so report it and run nothing.

This validation has tasks for four families. `.lfrbuild-portal`, `.lfrbuild-portal-private`, and `.lfrbuild-portal-public` are the portal family the profile reads, and `.lfrbuild-ci` is consumed without it. The filter above takes every family, so report a marker outside the four as unhandled rather than running anything for it.

For each **added** marker, deploy the module. The build the marker registers it for is going to build it, and a module that fails to build is exactly what the marker just broke. Deploying needs the portal snapshot installed first, or it fails resolving `com.liferay.portal.kernel` before it reaches the module, which is an environment failure rather than a registration one. Run the snapshot build only when the runner has not already reported it satisfied.

```bash
(cd "${REPO_ROOT}" && ant compile install-portal-snapshots)
```

Deploy a portal family addition under the profile, since the newly admitted module's own `project(":...")` references must resolve inside the profile set, and a run without the profile includes everything by directory and cannot see one that does not:

```bash
("${REPO_ROOT}/gradlew" \
	--parallel \
	--project-dir "${REPO_ROOT}/modules" \
	-Dbuild.profile=dxp \
	:<path>:deploy)
```

Deploy a `.lfrbuild-ci` addition without the profile, which is how the pass that consumes the marker runs Gradle:

```bash
("${REPO_ROOT}/gradlew" \
	--parallel \
	--project-dir "${REPO_ROOT}/modules" \
	:<path>:deploy)
```

A removal is judged by what still references the module. Start with its remaining markers, which decide whether it left the profile set at all. A module that still carries another portal family marker is still in the set, so report the removal and move on to the next marker. Name what remains, or say that nothing does:

```bash
git ls-tree -r --name-only "${HEAD_SHA}" -- '<module directory>' \
	| command grep '/\.lfrbuild'
```

Read them at the same pinned commit as the consumer search below. Give `git ls-tree` a directory and filter afterwards, because it ignores a wildcard pathspec and returns nothing at all, exit zero and silent, which reads as a module left with no markers. `git ls-files --with-tree` is not the pinned read it looks like either, since it unions the index with the commit and so shows a marker another validation has merely staged. A module left carrying only a marker from another product build, such as `.lfrbuild-cms-standalone`, has still left the profile set entirely, so report what remains rather than reading it as cover.

Then take the modules that still declare `project(":<path>")` at the same pinned commit, where reading `--cached` instead is not enough, since the index moves too:

```bash
git grep --files-with-matches --fixed-strings 'project(":<path>")' "${HEAD_SHA}" -- '*.gradle'
```

The revision follows the options. Placed before them, `git grep` reads `--files-with-matches` as a revision and dies with `unable to resolve revision`.

This exits 1 when nothing matched, which here means a module no other module declares rather than a defect. Read an empty result as genuine only when the exit status is 1, since the misordered form above exits 128 with the same empty output, and take anything else as a broken search rather than an answer. Each match is a file, so map it back to its module by stripping the file name, and expect `build.gradle`, since a match anywhere else does not name a module.

Partition the consumers by their own markers rather than by name, since membership in the profile set is what decides whether a reference still resolves. A consumer that itself carries a portal family marker stays in the set the removed module has left, so its reference dangles and the next `ant all` dies at configuration. That is a defect this validation can prove, so when at least one marked consumer exists, configure one of them under the profile:

```bash
("${REPO_ROOT}/gradlew" \
	--project-dir "${REPO_ROOT}/modules" \
	-Dbuild.profile=dxp \
	:<consumer path>:help)
```

FAIL when that run dies with `Project with path ':<path>' could not be found`, which is the same failure the next `ant all` hits, and name the marked consumers with a count. When it succeeds instead, the profile still resolves the module despite the partition, so fall back to reporting rather than failing on the grep alone.

A consumer without a portal family marker is outside the set, and its reference only evaluates in a plain invocation that still includes the removed module by directory, so nothing breaks at compile time. Report those with their own count rather than folding them into the marked group.

These are compile time consumers, which is the easier half of the question. A module that stops deploying is absent from the runtime, so a bundle whose `bnd.bnd` imports one of its exported packages resolves against something no longer installed, and no Gradle edge shows that. Say the runtime side is unexamined rather than letting the compile time list stand for the whole answer.

FAIL when a run reports `BUILD FAILED`, and report the module and the error. A failure of the form `project '<name>' not found in project ':...'` names the module itself and means the marker sits in a directory the build has no project for, so nothing registered. `Project with path ':...' could not be found` names a reference instead. In a profile run it means the referenced module is outside the profile set, which is the removal defect above when it names the removed module, and in a plain run it means the referenced directory does not exist, so the module's own `build.gradle` is broken. A diff containing no `.lfrbuild-*` marker at all should not have fired this validation, so that is a broken selection rather than a pass; report it as a FAIL too. PASS when every added marker reports `BUILD SUCCESSFUL` and no removal left a dangling reference.

A diff carrying no addition and no removal with a marked consumer runs nothing, so it has no run to pass. Report **NOT VERIFIED** and give the consumer report as the reason, since reading it as a PASS makes the empty set vacuously true and turns the one case needing a developer's judgment into the one result that stops anyone looking. When a diff carries several markers, judge each on its own branch, FAIL when any fails, and carry every report alongside whatever the verdict.

## Checklist

```
- [ ] (One subitem per added/removed marker:) Deploy or report <module path>
```

## Time Estimate

~3 min for the snapshot when a marker was added, then ~1 min per added marker. A removal reads and at most configures one consumer, so it takes seconds.