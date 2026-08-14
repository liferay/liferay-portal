# Baseline

## Trigger

Always. The bnd baseline task diffs each exported API against the last release and fails on a missing, excessive, or insufficient `Bundle-Version` or `packageinfo` bump.

Do not narrow it to the branch diff. The comparison target is resolved from Nexus on every run, so a module that the branch never touched can start failing between one run and the next.

## Match

`.`

## Command

```bash
(cd "${REPO_ROOT}" && ant baseline-all)
```

Leave `baseline.all.ant.projects` at its default of `true`. Passing `false` drops the seven top level Ant projects — `portal-kernel`, `portal-impl`, `portal-test`, `util-bridges`, `util-java`, `util-slf4j`, and `util-taglib` — from the run. Pass it only when **Full Portal Build** ran in this same pr-check: `ant all` starts with `clean`, so each of those jars is rebuilt, and the `jar` target baselines it on the way through.

Two prerequisites:

- Each baseline resolves the last released artifact from Nexus, so the run needs network access. Use the local check below when there is none.
- Treat both a finding and a pass against a project that has not been cleanly built on this branch as unproven. Rerun after `ant all`.

### Interpretation

Read `git status`, not the exit code. The baseline task repairs what it finds, so the first run rewrites the tree and every run after it passes.

Baseline has five warnings, and they do not all reach the tree in the same shape:

| Warning | What Lands in the Tree |
| --- | --- |
| `VERSION INCREASE REQUIRED` | version raised |
| `VERSION INCREASE SUGGESTED` | version raised |
| `EXCESSIVE VERSION INCREASE` | version **lowered** |
| `PACKAGE ADDED, MISSING PACKAGEINFO` | **new, untracked** packageinfo |
| `PACKAGE REMOVED, UNNECESSARY PACKAGEINFO` | packageinfo **deleted** |

All five concern the version of an exported package, recorded in its `packageinfo`. `Bundle-Version` is not among them, so do not read a passing run as evidence that it is right. An inflated one is caught only by the classification below.

List each changed file with both of its versions. Do not read a bare diff of the version lines, which drops the filename and cannot tell an addition, a deletion, and a lowering apart:

```bash
version() {
	sed -nE 's/^(Bundle-Version:[[:space:]]*|version[[:space:]]+)//p' | head -1
}

git status --porcelain -uall -- '*bnd.bnd' '*packageinfo' |
while read -r status path; do
	case "${status}" in
	D)    old=$(git show "HEAD:${path}" | version) ; new="<removed>" ;;
	'??') old="<added>" ; new=$(version < "${path}") ;;
	*)    old=$(git show "HEAD:${path}" | version) ; new=$(version < "${path}") ;;
	esac

	printf '%s\t%s\t%s\n' "${old:-<none>}" "${new:-<none>}" "${path}"
done
```

Keep `-uall`, or a new packageinfo is invisible under `status.showUntrackedFiles=no`. Keep reading removal from the status letter, not from a missing version line.

Classify each row, comparing segments as numbers so that `9.5.1` to `10.0.0` counts as a rise and `1.9.0` to `1.10.0` does not:

- Identical versions are not a finding.
- The first segment rises: **major**.
- The second or third rises: **minor** or **micro**.
- Any segment falls: **lowered**.
- `<added>`: a newly exported package.
- `<removed>`: an exported package is gone.
- `<none>` on either side: the file held no version line. Repeat the run rather than classifying it.

### Local Version Check

This needs no network and reports an advisory note, never a PASS or FAIL. Use it when the baseline run above cannot reach Nexus.

Look at each changed `.java` under an `*-api` module's `src/main/java`, `portal-impl/src`, or `portal-kernel/src`. When its diff adds or removes a `public` or `protected` line, the exported API changed, so the version should be bumped too. The bump shows up in the diff as a changed `packageinfo`, or a changed `bnd.bnd` `Bundle-Version` for an `*-api` module. If neither changed, flag the package.

Flag a lowered `packageinfo` or `Bundle-Version` that has no matching `public` or `protected` removal.

## Autocommit

**Minor or micro only, and only when the run produced nothing else.** Stage those files and commit them, resolving `<TICKET>` from the branch name the way [commit.md](../../../rules/commit.md) does. `${paths}` is the classified subset from the Interpretation step, one path per line — not a rescan of `git status`, which would sweep in the findings below:

```bash
printf '%s\n' "${paths}" | git add --pathspec-from-file=-

git commit --message "${TICKET} Semantic versioning"
```

Collect the paths into a variable first, rather than passing the globs to `git add`, which fails when one of them matches nothing. Skip the commit when `${paths}` is empty; `git commit` with nothing staged fails, and there is nothing to record.

Commit a bump owed to a package that this branch never touched under this branch's ticket as well, and report it, since the pull request then carries a semantic versioning fix that its author did not write.

**Major, lowered, or removed.** Do not commit. Fail this validation and report each one with its file and both versions. Each is a breaking change that the developer has to decide on:

- **Major**: report it as needing a breaking change section in the commit message.
- **Lowered**: report it as `EXCESSIVE VERSION INCREASE`.
- **Removed**: report it as `PACKAGE REMOVED, UNNECESSARY PACKAGEINFO`. It arrives as a bare file deletion carrying no version, so name the file.

**A newly exported package.** Report it and leave it uncommitted. It belongs in the commit that adds the package.

When several appear in one run, fail on the strictest and leave every safe bump uncommitted with it, so the whole set is reviewed together.

## Checklist

```
- [ ] Baseline
```

## Time Estimate

~30 sec for the whole repository on a warm Gradle daemon whose module jars are already built. Around 45 sec when jars must be rebuilt first, and around 80 sec on a cold daemon.

Those figures assume a tree that has been built before, where most of the roughly 590 exporting modules resolve as up to date. A first run in a tree that has never been built pays the jar build for every one of them and takes far longer.