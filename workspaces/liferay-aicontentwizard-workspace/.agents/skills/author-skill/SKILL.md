---

description: Create a new workspace skill from source code or a spec document, matching the conventions of the skills already present in the workspace. Use when the user asks to author, write, or scaffold a new skill, or wants to turn a spec, an API, or a body of code into a reusable skill.
name: author-skill

---

# Author Skill

Generate a new skill under `.agents/skills/<name>/` that reads like the skills already shipped in this workspace. The conventions are **derived by reading those skills at invoke time**, not codified here — so the output stays correct as those skills evolve.

## When to Invoke

- "Write a skill for <X>"
- "Turn this spec into a skill"
- "We have code that does <X>, make a skill out of it"
- "Create a skill that teaches an agent to <X>"

## Prerequisites

### Establish the Workspace Root

The workspace root is the directory holding `gradle.properties` and `settings.gradle`. Every path below is relative to it.

This skill runs in whatever workspace it was shipped into. Resolve every path against that root, and never write outside it.

### Locate the Skill Tree

`.agents/` is the source of truth. Each other agent directory holds a byte identical copy. Discover that set rather than hardcoding it — today only `.claude/` exists, and more are expected:

```bash
for dir in .*/; do
	[ -d "${dir}skills" ] && [ "${dir}" != ".agents/" ] && echo "${dir}"
done
```

## Workflow

### Phase 1: Survey the Existing Skills

Do this first, every time, before asking the user anything about form. The skills already in `.agents/skills/` **are** the specification for what a skill looks like.

```bash
# Which skills exist, and how long they run

wc -l .agents/skills/*/SKILL.md | sort -n

# The section vocabulary, ranked — near universal sections appear in most skills

grep -h "^## " .agents/skills/*/SKILL.md | sort | uniq -c | sort -rn

# The exact frontmatter shape, including blank lines inside the fence

head -8 .agents/skills/<neighbor>/SKILL.md | cat -A
```

Then read in full the two or three skills closest in kind to the one being written.

Carry the findings into the draft: section order, which sections are near universal and which are occasional, how `## When to Invoke` is phrased, how shell blocks are formatted and commented, how rules cards are cited, how uncertainty is marked. Details worth confirming by inspection rather than memory include frontmatter key order, tab versus space indentation, and whether files end without a trailing newline.

Apply what the survey found. Do not restate it inside the generated skill as rules.

### Phase 2: Collect the Source Material

Ask for the source. Never infer it from the working directory — the code being described usually lives in a different checkout from the workspace being written into.

| Source | Ask For | How to Read It |
| --- | --- | --- |
| Code in a repo | An absolute path to the repo or the specific module | Read the entry points and the public API, not the whole tree. Prefer what the code does over what its comments claim |
| Spec document | A path or a URL | Read it in full. Note the version or date it describes |
| Neither | — | Interview only. Say plainly that the result is unverified, and mark it in the draft |

When both a spec and code are offered, read both — the gap between them is the most valuable thing to surface in Phase 3.

### Phase 3: Interview

Resolve these with the user before drafting anything:

1. **Trigger phrases** — the user requests that should load this skill. These become `## When to Invoke` and the `description` frontmatter.

1. **Scope** — one workflow, or several. Several means several skills.

1. **Prerequisites** — feature flags, product version, auth, a running server, another skill that must run first.

1. **Success signal** — the observable check that the workflow worked. A command and its expected output, not a feeling.

1. **Known failure modes** — what silently does the wrong thing, and how it presents.

1. **Neighbors** — which existing skills this one calls, and which should call it.

Then surface every conflict explicitly, as a list, with both readings and a question. Do not silently pick a side. The recurring kinds:

- The spec describes behavior the code does not implement, or the reverse.
- Two sources disagree on a path, a field name, or a default.
- The source targets a different product version than the workspace.
- The source contradicts an existing skill or a rules card in `.agents/rules/`.

Proceed only once each is answered, or the user explicitly says to proceed on a stated assumption — in which case write the assumption into the draft.

### Phase 4: Draft

Write `.agents/skills/<name>/SKILL.md`. This is the working copy for the review rounds; nothing is mirrored yet.

- **Name**: kebab case, verb first, matching the neighbors surveyed in Phase 1.
- **Verified content only.** Every command should be one that was run, or one taken directly from the source. Do not invent API paths, flag keys, field names, or CLI options.
- **Mark what is inferred** using the convention the surveyed skills already use rather than a new one — see how they word an unverified `## Success Signal`.
- **Cite rules cards, do not restate them.** A fact that belongs to `.agents/rules/` is referenced from there. A new fact of that kind belongs in a card, with the skill citing it.
- **End the file without a trailing newline.** Every skill and rules card in the workspace does; check the last byte with `tail -c 1 <file> | xxd`, where `0a` means the newline is still there.

### Phase 5: Review Gate

Present the complete draft to the user before publishing anything:

- The full text of the skill.
- Its name and path.
- The Skill Router row proposed for Phase 6.
- Anything left inferred, assumed, or unverified, named individually.

Revise in place on the `.agents/` copy. Do not create vendor copies and do not touch the Skill Router until the user approves. Waiting keeps each revision round to a single file.

### Phase 6: Publish Into the Workspace

Only after approval:

1. Mirror `.agents/skills/<name>/SKILL.md` byte for byte into each agent directory found in Prerequisites.

1. Add a row to the **Skill Router** table in the entry point file of each tree — `.agents/liferay-rules.md` and its twin in each vendor directory. These files are identical, so apply the identical edit to each.

1. When the skill must be loaded *before* authoring an artifact type, add a row to the **Preflight Rule** table as well. It sits in the same file but is a separate table with its own columns (`| About to author | Load first |`).

1. Verify each copy matches the original:

	```bash
	diff .agents/skills/<name>/SKILL.md .claude/skills/<name>/SKILL.md && echo "identical"
	```

### Phase 7: Hand Off for Contribution

A skill published by Phase 6 exists only in this workspace. Nobody else gets it, and a later workspace update can overwrite it if a skill of the same name is published upstream. Contributing it is what makes it durable and shared.

Tell the user this, and point them at `commit-skill`, which collapses the duplicated workspace trees into the repository's symlinked layout and commits the result. Recommend it; do not invoke it. Contributing is the user's call, and this skill's work ends at the workspace boundary.

## Patterns and Gotchas

- **The surveyed skills outrank this file.** When Phase 1's survey disagrees with anything written here, follow the survey and report the divergence — this skill describes a convention it does not own.
- **A skill missing from the Skill Router is nearly invisible.** Without its Phase 6 row it loads only when named exactly. Publishing the file is not publishing the skill.
- **One workflow per skill.** A skill that needs two unrelated `## When to Invoke` lists is two skills.
- **Vendor copies drift silently.** Nothing regenerates them and nothing fails when they differ; the vendor simply reads a stale skill. Always mirror, always diff.
- **A generated skill is not evidence.** Producing a plausible skill from a spec proves nothing about the product. Anything not executed stays marked as inferred through Phase 5.

## Success Signal

This skill is done when the new skill is **published**, not when it is proven to work. The observable checks are structural — the file exists under `.agents/skills/<name>/`, every agent directory holds an identical copy, and the Skill Router row is present in each entry point file:

```bash
for dir in .*/; do
	[ -d "${dir}skills/<name>" ] && diff "${dir}skills/<name>/SKILL.md" .agents/skills/<name>/SKILL.md
done

grep --count "<name>" .agents/liferay-rules.md .claude/CLAUDE.md
```

Whether the skill does its job is for the user to judge, by invoking it on a real task. Do not exercise the generated skill's workflow to prove it out, and do not report it as working — say what was published, and what remains unverified.

## See Also

- `.agents/liferay-rules.md` — holds the Skill Router and Preflight Rule tables a new skill is added to.
- `skills/commit-skill/SKILL.md` — commits a published skill to liferay-portal. Phase 7 recommends it; it is never invoked automatically.
- `skills/scaffold-client-extension/SKILL.md` — the closest existing example of collecting inputs, generating files, and handing off to another skill.