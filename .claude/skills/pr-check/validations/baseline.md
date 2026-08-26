# Baseline

## Trigger

Always. The bnd baseline task diffs each exported API against the last release and fails on a missing, excessive, or insufficient `Bundle-Version` or `packageinfo` bump.

Do not narrow the run to the branch diff. The comparison target is resolved from Nexus on every run, so a module the branch never touched can start failing between one run and the next. Narrow the verdict instead, as **Command** sets out: only a module the branch changed can fail it. Otherwise one stale version on master fails every pull request at once, stopping the developer least able to judge whether the bump is right.

## Match

`.`

## Command

Install the portal snapshot, then baseline:

```bash
(cd "${REPO_ROOT}" && ant compile install-portal-snapshots)
```

Every module baselines against that snapshot, which installs under `${REPO_ROOT}/.m2` rather than the home Maven repository, and with no `repository` segment in the path. Check for it at `${REPO_ROOT}/.m2/com/liferay/portal/com.liferay.portal.impl`, since probing the conventional `.m2/repository/com/liferay/...` reports a healthy tree as missing. A genuinely missing or stale one fails the module run with `Could not find com.liferay.portal.impl`, an environment failure rather than a versioning one.

```bash
(cd "${REPO_ROOT}" && ant baseline-all)
```

Leave `baseline.all.ant.projects` at its default of `true`. Passing `false` drops the seven top level Ant projects — `portal-kernel`, `portal-impl`, `portal-test`, `util-bridges`, `util-java`, `util-slf4j`, and `util-taglib` — from the run. Pass it only when **Full Portal Build** ran in this same pr-check: `ant all` starts with `clean`, so each of those jars is rebuilt, and the `jar` target baselines it on the way through.

Prerequisites:

- Each baseline resolves the last released artifact from Nexus, so the run needs network access. Use the local check below when there is none.
- A project that has not been cleanly built on this branch cannot be baselined at all. Rerun after `ant all`, which rebuilds all seven and baselines each one through the `jar` target.
- Silence is not a pass, but a `baseline-all` that reports something is worth reading. [build.xml](../../../../build.xml) passes `--quiet` to both of its Gradle calls, which suppresses lifecycle output and so deletes every `> Task` line. Warning rows are logged at `WARN` and survive, so a run that finds something prints its table and names the failing task. Take the findings from there when they are present.

	A `BUILD SUCCESSFUL` from `baseline-all` is what proves nothing. The modules half runs `--continue --parallel` with no `--rerun`, so a cached `UP-TO-DATE` verdict is indistinguishable from a comparison, the seven Ant projects run under `failonerror="false"`, and `<parallel threadCount="2">` interleaves the two halves so nothing in the output can be attributed to a project. Use the standalone runs below to prove a project compared something, not to find what it found.

	**Do not rerun a module `baseline-all` already reported.** The task repairs in place, so the second run compares the repaired tree and passes clean, and a reader who takes that as the verdict ends with no findings, an empty `${paths}`, no autocommit, and a Local Version Check that then fails.

Confirm each Ant project actually baselined by running it alone, where nothing is suppressed and nothing is swallowed. Do this whatever `baseline-all` reported, since its output cannot tell you which projects it reached.

```bash
("${REPO_ROOT}/gradlew" --console=plain --project-dir "${REPO_ROOT}/<project>" baseline --rerun)
```

Keep `--rerun`. Without it the task reports `UP-TO-DATE` and exits 0 in half a second, a cached verdict rather than a comparison. A genuine run prints `1 executed`. Fail when one of the seven is missing its jar, reports `Could not resolve`, or never prints `1 executed` — a baseline that did not run is not one that passed. A nonzero exit is not itself the verdict, since a project that ran and found something exits nonzero too.

Confirm the branch's own modules the same way, keeping `--rerun`, and passing each changed module whose `bnd.bnd` carries `Export-Package:` as the project directory. Take those modules from the diff, walking each changed file up to its nearest ancestor holding a `bnd.bnd`:

```bash
MERGE_BASE=$(git merge-base HEAD master)

git diff --name-only "${MERGE_BASE}...HEAD"
```

`baseline-all` compares those too, and they are where a branch's changes land, so confirming only the seven leaves the half that matters unevidenced. A module builds its dependencies first and so reports an aggregate count rather than `1 executed`. Judge it by the `> Task :<path>:baseline` line, which a run that never compared anything does not print.

A finding in a module the branch changed is the branch's to resolve, and **Autocommit** below decides which kind is repaired and passes and which fails. A finding in any other module is **inherited**: report it with both versions and do not fail the branch, whatever its severity. Identify the finding's module from the failed task's Gradle path, since module depth varies and deriving module directories from the diff lands on the app group instead.

### Interpretation

Take the findings from the run's own output, the warning rows and the failed `:baseline` tasks, not from the tree. The task **repairs what it finds**, so afterwards a live finding and an already repaired one both read as modified, and a restored tree reads clean while the finding stands.

Baseline has five warnings, and they do not all reach the tree in the same shape:

| Warning | What Lands in the Tree |
| --- | --- |
| `VERSION INCREASE REQUIRED` | version raised |
| `VERSION INCREASE SUGGESTED` | version raised |
| `EXCESSIVE VERSION INCREASE` | version **lowered** |
| `PACKAGE ADDED, MISSING PACKAGEINFO` | **new, untracked** packageinfo |
| `PACKAGE REMOVED, UNNECESSARY PACKAGEINFO` | packageinfo **deleted** |
| `Bundle Version Change Recommended` | `bnd.bnd` `Bundle-Version` raised |

The first five concern the version of an exported package, recorded in its `packageinfo`. The sixth is the module's own `Bundle-Version` in `bnd.bnd`, which the task raises in the tree along with them. It arrives as a bare `[Baseline Warning]` line rather than a table row, so read it from the output as well as the rows.

For the paths those warnings named, list each changed file with both of its versions. This decides what to commit or restore, and it catches a `Bundle-Version` raised further than the recommendation, which no row reports.

Warning rows name packages, so match a `packageinfo` by the package path it sits under rather than by the directory holding the `.java`. The two live in different source roots, `src/main/java/com/liferay/...` against `src/main/resources/com/liferay/...`, so no `packageinfo` is ever a sibling of the class it versions.

A `packageinfo` that no row named is a repair left by an earlier aborted run, so restore it and do not count it as a finding. No row ever names a `bnd.bnd`, so judge one by its module instead of restoring it as debris.

Take the file list from `git status --porcelain --untracked-files=all -- '*bnd.bnd' '*packageinfo'`, and for each one read the version in `HEAD` against the version in the tree, from the `Bundle-Version:` or `version` line. Keep `--untracked-files=all`, or a new packageinfo is invisible under `status.showUntrackedFiles=no`, and read an addition or a removal from the status letter rather than from a missing version line — a bare diff of the version lines drops the filename and cannot tell an addition, a deletion, and a lowering apart.

Classify each row by comparing segments as numbers rather than as text. Both `9.5.1` to `10.0.0` and `1.9.0` to `1.10.0` are rises, and a string comparison calls neither one, since it reads `9` as greater than `1` in the first segment and greater than `10` in the second:

- Identical versions are not a finding.
- The first segment rises: **major**.
- The second or third rises: **minor** or **micro**.
- Any segment falls: **lowered**.
- `<added>`: a newly exported package.
- `<removed>`: an exported package is gone.
- `<none>` on either side: the file held no version line. Repeat the run rather than classifying it.

### Local Version Check

This needs no network and **fails** rather than advises. Run it on every pass, not only when the baseline cannot reach Nexus, since it is the check that cannot silently compare nothing.

Run it **after** the Autocommit step below, and read it from the branch diff as it stands then. Run before, it reports the very bump the autocommit exists to supply, so it would fail by construction on every branch that changed an exported API. Run after, a bump that landed is in the diff and a bump that is missing still is not.

Look at each changed `.java` under an `*-api` module's `src/main/java`, `portal-impl/src`, or `portal-kernel/src`. When its diff adds or removes a `public` or `protected` line, the exported API changed, so the version has to be bumped too. The bump shows up in the diff as a changed `packageinfo` under that package's path in `src/main/resources`, or a changed `bnd.bnd` `Bundle-Version` for an `*-api` module. Look it up by package path and not beside the `.java`, which never holds one. When neither changed, fail and name the package.

Fail as well on a lowered `packageinfo` or `Bundle-Version` that has no matching `public` or `protected` removal. Read this from the branch diff rather than the tree, or the baseline task's own repair reads as exactly that lowering and gets counted a second time.

A newly exported package needs its own `packageinfo`, so a diff adding the package without one fails here too.

## Autocommit

**A module the branch changed, a minor or micro rise, and only when the run produced nothing else.** Stage those files and commit them, resolving `<TICKET>` from the branch name the way [commit.md](../../../rules/commit.md) does, which is the leading ticket pattern rather than the whole branch name, so `LRCI-8065-rules` yields `LRCI-8065`. `${paths}` is the classified subset from the Interpretation step, one path per line — not a rescan of `git status`, which would sweep in the findings below:

```bash
printf '%s\n' "${paths}" | git add --pathspec-from-file=-

git commit --message "${TICKET} Semantic versioning"
```

Build `${paths}` from the rows you classified, one path per line:

```bash
paths=$(printf '%s\n' "${minor_and_micro_rows[@]}")
```

Collect them into a variable first, rather than passing the globs to `git add`, which fails when one of them matches nothing. Skip the `git add` and the commit both when `${paths}` is empty, since `printf '%s\n' ""` pipes a blank line that `--pathspec-from-file` rejects, and `git commit` with nothing staged fails anyway.

**Never commit a repair for a module outside the branch diff.** Report it with both versions, restore the file, and name the path restored. The bump belongs to whoever owns that module, every developer who runs the check would otherwise commit another copy of it, and the task rewrites in place, so anything left behind is swept into the next `git add --all` under the wrong ticket.

**Major, lowered, or removed, in a module the branch changed.** Do not commit. Restore each file, fail this validation, and report it with both versions. Each is a breaking change that the developer has to decide on:

- **Major**: report it as needing a breaking change section in the commit message.
- **Lowered**: report it as `EXCESSIVE VERSION INCREASE`.
- **Removed**: report it as `PACKAGE REMOVED, UNNECESSARY PACKAGEINFO`. It arrives as a bare file deletion carrying no version, so name the file.

**A newly exported package.** Report it and leave it uncommitted. It belongs in the commit that adds the package.

When several appear in one run, fail on the strictest the branch owns and leave every safe bump uncommitted with it, so the whole set is reviewed together.

## Checklist

```
- [ ] Setup: ant compile install-portal-snapshots
- [ ] Baseline
```

## Time Estimate

~30 sec for the whole repository on a warm Gradle daemon whose module jars are already built. Around 45 sec when jars must be rebuilt first, and around 80 sec on a cold daemon.

Those figures assume a tree that has been built before, where most of the roughly 590 exporting modules resolve as up to date. A first run in a tree that has never been built pays the jar build for every one of them and takes far longer.