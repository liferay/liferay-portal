---

description: Add a new rule to pr-reviewer/rules, derived from a Git commit or a review comment.
name: pr-reviewer-add-rule

---

# PR Reviewer: Add Rule

Add a new rule to `pr-reviewer/rules`, derived from a Git commit or from a reviewer's comment on a pull request. Anyone can use this to encode a convention they spotted in a commit or were asked to follow in review. Rules can apply to any file type the reviewer reads (Java, `.properties`, XML, JSP, Markdown, Gradle, YAML, shell, and so on), so do not bias toward Java.

## Inputs

- A Git commit SHA, or the text of a review comment together with the commit or pull request it was made on.

- Optional: a hint about which aspect of the commit to encode. Commits often touch multiple things, and a hint narrows the scope.

## Workflow

### Inspect the Source

Inspect the commit's full diff, or the comment and the code it points at. Identify a single learnable convention that a future reviewer could apply mechanically to other code. Stop and ask the user to clarify when:

- Multiple distinct rules are present. Ask the user to pick one, or run the skill once per rule.

- The commit mixes convention changes with logic or refactoring changes that cannot be separated.

- The pattern is not generalizable beyond the specific file or context.

### Check for Duplicates

Read the existing rules in `pr-reviewer/rules` and `pr-reviewer/STYLE.md` and compare them against the pattern. When the pattern duplicates or substantially overlaps an existing rule, flag it to the user and ask whether to skip, extend the existing rule, or proceed anyway. When the pattern is enforced by SourceFormatter already, say so and stop, since the reviewer would only repeat what the formatter fixes.

### Write the Rule

Pick the category by the leading digit of the existing files (`0xx` convention, `1xx` naming, `2xx` ordering, `3xx` utility methods, `4xx` control flow, `5xx` redundancy, `6xx` tests, `7xx` prose, `8xx` visibility, `9xx` formatting) and take the next free number in it. Create `pr-reviewer/rules/<number>-<kebab-case-imperative-title>.md` with this shape, matching the existing files:

1. `# <number>: <Title Case Imperative Title>`

1. One or two paragraphs stating the rule, with code in inline backticks.

1. A paragraph starting `**Rationale:**` that explains what consistency the rule buys, not what the rule does.

1. A paragraph starting `A violation is ...` that defines the boundary of the rule and names what not to flag.

1. A paragraph starting `**Example:**` that cites the commit or pull request in prose. Follow it with a `diff` fence when a before and after reads faster than prose, using abstract identifiers (`foo`, `Bar`, `methodA`) rather than the commit's literal code, so the example illustrates the pattern instead of overfitting it to one case.

Write the file the way the rules themselves demand: no hyphens in prose (rule 701), each paragraph on one line (rule 708), complete sentences, and no trailing newline at the end of the file (rule 908).

When the rule belongs to a section of `pr-reviewer/STYLE.md`, add a bracketed reference to it there.

### Self Test the Rule

Validate that the reviewer catches the violation with the new rule in place. Review the reverse of the commit, which reintroduces the violation:

```bash
pr-reviewer/review.sh --json <sha>..<sha>~1
```

The output must include a violation whose `rule` is the new number, on the file and line the commit changed. When it does not, revise the rule and repeat. Then review the commit itself, `<sha>~1..<sha>`, and confirm the new rule reports nothing, so the rule does not flag the fix it describes.

### Commit the Rule

After the self test passes, stage the new rule file and any `STYLE.md` change and commit with this message:

```
<ticket> Add rule <number> derived from <sha>
```