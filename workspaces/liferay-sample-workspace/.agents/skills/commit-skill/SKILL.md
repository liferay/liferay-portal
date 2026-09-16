---

description: Commit a skill authored in this workspace to the liferay-portal repository, collapsing the workspace's duplicated agent trees into the repo's symlinked layout. Use when the user asks to commit, contribute, or upstream a skill, and as the handoff from `author-skill`.
name: commit-skill

---

# Commit Skill

Take a skill that exists only in this workspace and commit it to `liferay-portal/workspaces/liferay-sample-workspace`, the source the published skill pack is built from.

The finish line is a **local commit on a fresh branch**. No push, no pull request — the user sends it for review themselves.

## When to Invoke

- "Commit my skill"
- "Contribute this skill to liferay-portal"
- "Upstream the skill I just wrote"
- As the handoff at the end of `author-skill`, which publishes into the workspace and stops at the workspace boundary

## Prerequisites

### The Workspace Root

The directory holding `gradle.properties` and `settings.gradle`. Every workspace relative path below resolves against it.

### The Portal Repository

Ask the user for the absolute path to their `liferay-portal` clone.

The repository's own rules and skills then have to be brought into scope for the agent. Step 5 needs both: `.claude/rules/commit.md` for the message format, and the `format-source` skill that rule mandates before any commit. Reading a file out of the repository works without this, but a skill that is not in scope cannot be invoked at all — so `format-source` is the part that fails silently.

Adding a directory to a session is the user's to do, and each agent exposes it differently — a slash command, a launch flag, a setting. In Claude Code it is `/add-dir <portal-repo>`. Ask the user to add the repository root in whatever way their agent provides, wait for their reply, then confirm `format-source` is invokable before continuing.

Store the resolved path so later runs can offer it instead of asking again.

### The Canonical Remote

Resolve the remote by **URL**, never by the name `upstream`. A clone may carry several forks under names that say nothing about which is canonical:

```bash
git remote -v | awk '$3=="(fetch)" && $2 ~ /[:/]liferay\/liferay-portal(\.git)?$/ {print $1}'
```

The `[:/]` prefix matches both `git@github.com:` and `https://github.com/` forms, and `(\.git)?$` anchors past an inconsistent suffix. Anchoring is what keeps `liferay-devtools/`, `liferay-one/`, and personal forks from matching.

A clone with no such remote is not expected. When it happens, say so and ask the user whether to add it — do not guess a fork.

## Workflow

### Step 1: Determine What to Commit

Three sources, in order. Stop at the first that answers.

**Session context.** The common case is `author-skill` having just run, so the names are already known. Confirm them with the user rather than re-deriving them.

**Repository detection.** When the workspace is itself a git repository, everything below counts as a candidate:

| Candidate | How it presents |
| --- | --- |
| Untracked skill files | `git status --porcelain` lines beginning `??` |
| Uncommitted edits to an existing skill | lines beginning ` M` or `M ` |
| Already committed skill changes | `git log` against the tracking branch |

**Baseline comparison.** When the workspace is not a repository, diff it against the portal repo's own tree, which is the same layout on both sides:

```bash
diff --recursive --brief \
	"${WORKSPACE}/.agents/skills" \
	"${PORTAL_REPO}/workspaces/liferay-sample-workspace/.agents/skills"
```

Present what was found and confirm before doing anything else.

### Step 2: Assemble the Full Changeset

A skill is more than its `SKILL.md`. `author-skill` also edits the Skill Router and may add a rules card, and every one of those edits belongs in the commit:

| Part | Workspace path |
| --- | --- |
| The skill | `.agents/skills/<name>/SKILL.md` |
| Its mirrors | `<vendor>/skills/<name>/SKILL.md`, one per vendor directory |
| Skill Router row | `.agents/liferay-rules.md` and each vendor twin |
| Preflight Rule row | same files, when the skill must load before authoring an artifact type |
| New rules card | `.agents/rules/<card>.md` and its mirrors |

Discover the vendor directories rather than hardcoding them:

```bash
for dir in .*/; do
	[ -d "${dir}skills" ] && [ "${dir}" != ".agents/" ] && echo "${dir}"
done
```

A skill committed without its Skill Router row loads only when named exactly. The file is present, the skill is effectively invisible, and nothing fails.

Before collapsing, confirm the mirrors still agree with `.agents/`. Nothing regenerates them and nothing fails when they drift, so a stale mirror means Step 4 silently picks a winner:

```bash
for dir in "${WORKSPACE}"/.*/; do
	[ -d "${dir}skills/<name>" ] || continue
	[ "$(basename "${dir}")" = ".agents" ] && continue

	diff "${WORKSPACE}/.agents/skills/<name>/SKILL.md" "${dir}skills/<name>/SKILL.md"
done
```

### Step 3: Prepare the Branch

Work from a fresh upstream master so the baseline and the commit share a revision:

1. Stash any working changes in the portal repo. **Leave the stash in place** and report it — do not pop it. Capture the ref, since an existing stash stack makes `stash@{0}` ambiguous within minutes:

	```bash
	git -C "${PORTAL_REPO}" stash push --include-untracked --message "commit-skill: <name>"
	git -C "${PORTAL_REPO}" stash list | head -1
	```

1. Check out `master`, then pull from the remote resolved in Prerequisites.

1. Ask the user for the branch name, then create it.

When the workspace turns out to live **inside** the portal repo, stop. The stash would capture the very skill being contributed and the branch switch would move it out from under the user. Say so and let them decide.

### Step 4: Collapse Into the Repo Layout

The two trees are not the same shape. A workspace populated by `updateWorkspace` holds independent copies, because the published artifact contains no symlinks; the repo holds one real file and symlinks beside it:

| File | Workspace | Repo |
| --- | --- | --- |
| `.agents/skills/<name>/SKILL.md` | real file | real file |
| `<vendor>/skills/<name>/SKILL.md` | identical copy | symlink to `../../../.agents/skills/<name>/SKILL.md` |
| `.agents/liferay-rules.md` | real file | real file |
| `<vendor>/<entry-point>` | identical copy | symlink to `../.agents/liferay-rules.md` |

So committing is a **collapse**, never a straight copy: write one real file under `.agents/`, then create the symlinks. The relative depth differs between the two cases above — count it from the link's own directory rather than reusing a path.

Each vendor names its entry point differently, so read the existing one in the repo rather than assuming. Match whatever vendor directories the repository already carries; adding a tree it does not have is a separate decision, not part of contributing a skill.

Router and Preflight edits collapse the same way. Two separately edited files in the workspace become one edit to `.agents/liferay-rules.md`, since each vendor entry point already points at it.

Skills and rules cards in this repository end **without** a trailing newline. Check the last byte, where `0a` means one is still there:

```bash
tail -c 1 "${PORTAL_REPO}/workspaces/liferay-sample-workspace/.agents/skills/<name>/SKILL.md" | xxd -p
```

### Step 5: Resolve the Ticket and Commit

Follow `.claude/rules/commit.md` in the portal repo — it owns the message format and mandates `format-source` first.

Its ticket resolution reads the current branch and recent commits, which on a freshly created branch describe whatever the user was last working on rather than this contribution. Scope it instead:

| Situation | Where the ticket comes from |
| --- | --- |
| Branch was just created | Ask. When the branch name the user gave carries a ticket, offer it as the suggestion rather than asking blind |
| Branch already existed | Read that branch's own commits first, then fall back to asking |

Never take the ticket from `master`'s log or from the branch the repo happened to be on.

Commit, then stop. Report the branch, the commit, and the stash.

## Patterns and Gotchas

- **A straight copy produces the wrong tree.** The workspace's vendor copies are real files; the repo expects symlinks. Copying them in duplicates content that the publish step already duplicates on its own.
- **The Skill Router row is the difference between shipped and invisible.** It is the single most commonly dropped part of the changeset, and its absence never produces an error.
- **`commit.md`'s ticket rules read stale state here.** Both of its inference sources describe the user's previous work, not this contribution, and both answer confidently.
- **Mirror drift is silent.** When `.agents/` and a vendor copy disagree, the collapse resolves it without telling anyone. Diff before, not after.
- **The stash is the one destructive looking step.** Report its ref every time. The user decides when to restore it.
- **Do not push or open a pull request.** The commit is the deliverable; review is the user's to start.

## Success Signal

The branch exists carrying one new commit, the repository tree holds a real file with symlinks beside it, and the Skill Router names the skill:

```bash
git -C "${PORTAL_REPO}" log --oneline --name-status -1

ls -la "${PORTAL_REPO}"/workspaces/liferay-sample-workspace/.*/skills/<name>/SKILL.md

grep --count "<name>" "${PORTAL_REPO}/workspaces/liferay-sample-workspace/.agents/liferay-rules.md"
```

The symlink listing should show `lrwxrwxrwx` and the `.agents/` target. Whether the contribution is accepted is decided in review, not here.

## See Also

- `skills/author-skill/SKILL.md` — writes the skill and hands off to this one.
- `.claude/rules/commit.md` in the portal repo — the commit message format this skill follows.