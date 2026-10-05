# 410: Fail Fast in Cloud Bash Scripts

Every shell script under `cloud/` opens with `set -o errexit`, `set -o nounset`, and `set -o pipefail`, in that order, on the lines directly after the shebang and set off from the first command by a blank line. The three directives make a failing command, a reference to an unset variable, and a failed stage in a pipeline stop the script at once instead of letting the next command run on bad state.

This rule is exclusive to `liferay-portal`. Shell scripts elsewhere in the repository follow the Bash style guide maintained in `liferay-docker`, which has no counterpart for it, and the `format-source` skill defers their formatting to that guide. The preincrement that rule 405 asks for, `((++var))` over `((var++))`, assumes `set -o errexit` is already in force; this rule is what puts it there.

**Rationale:** Without an explicit fail fast directive, a failing command silently passes control to the next one, so a broken build step, a typo in a variable name, or a `curl` that failed in the middle of a pipeline surfaces only when a later step trips over the result, far from the cause. Declaring the three options at the top makes each failure surface on the line that produced it, and it does so uniformly across every script in `cloud/`, so a reader never has to check which scripts are strict.

A violation is a `*.sh` file under `cloud/` that lacks any of the three `set -o` lines or places them after the first command. Do not flag a script outside `cloud/`, since the `liferay-docker` style guide governs it, and do not flag a script that relaxes one option around a single command with a matching `set +o` and `set -o` pair.

**Example:** a `cloud/` script that ran `_execute "step-1"` and `_execute "step-2"` straight after its shebang gained the three directives, so a failure in the first step no longer lets the second one run.

```diff
 #!/usr/bin/env bash

+set -o errexit
+set -o nounset
+set -o pipefail
+
 _execute "step-1"
 _execute "step-2"
```