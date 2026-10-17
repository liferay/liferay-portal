#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

function main {
	local diff_range=""
	local dry_run=false
	local json=false
	local staged=false

	while [ ${#} -gt 0 ]
	do
		if [[ ${1} == --dry-run ]]
		then
			dry_run=true
		elif [[ ${1} == --help ]]
		then
			_print_help

			exit 0
		elif [[ ${1} == --json ]]
		then
			json=true
		elif [[ ${1} == --staged ]]
		then
			staged=true
		elif [[ ${1} == --* ]]
		then
			echo "Unable to recognize the option ${1}." >&2

			_print_help >&2

			exit 2
		else
			diff_range=${1}
		fi

		shift
	done

	if ${staged} && [[ ! -z ${diff_range} ]]
	then
		echo "Unable to review both the staged changes and a range. Pass one of them." >&2

		exit 2
	fi

	local repo_root

	repo_root=$(git rev-parse --show-toplevel 2> /dev/null || true)

	if [[ -z ${repo_root} ]]
	then
		echo "Unable to find a Git repository." >&2

		exit 2
	fi

	cd ${repo_root}

	local reviewer_dir=${repo_root}/pr-reviewer

	if [ ! -d ${reviewer_dir}/rules ]
	then
		reviewer_dir=${LIFERAY_PR_REVIEWER_HOME:-${repo_root%/*}/liferay-portal/pr-reviewer}
	fi

	if [ ! -d ${reviewer_dir}/rules ] || [ ! -f ${reviewer_dir}/STYLE.md ]
	then
		echo "Unable to find pr-reviewer/rules and pr-reviewer/STYLE.md under ${repo_root} or LIFERAY_PR_REVIEWER_HOME." >&2

		exit 2
	fi

	local diff_args=--cached
	local diff_label="the staged changes"

	if ! ${staged}
	then
		if [[ -z ${diff_range} ]]
		then
			local base_branch=${LIFERAY_PR_BASE_BRANCH:-master}

			if ! git rev-parse --quiet --verify ${base_branch} > /dev/null
			then
				echo "Unable to find the base branch ${base_branch}. Set LIFERAY_PR_BASE_BRANCH or pass a range." >&2

				exit 2
			fi

			diff_range="$(git merge-base ${base_branch} HEAD)..HEAD"
		fi

		diff_args=${diff_range}
		diff_label="range ${diff_range}"
	fi

	local work_dir

	work_dir=$(mktemp -d)

	trap "rm -fr ${work_dir}" EXIT

	local diff_file=${work_dir}/review.diff

	_write_diff_file ${diff_args} > ${diff_file}

	if [ ! -s ${diff_file} ]
	then
		if ${json}
		then
			echo '{"chance": 0, "reviewed_files": [], "violations": []}'
		else
			echo "Nothing to review: every changed file is generated, frontend, or ignored."
		fi

		exit 0
	fi

	local reviewed_files

	reviewed_files=$(awk '/^diff --git /{file = $3; sub(/^a\//, "", file); print file}' ${diff_file})

	if ${dry_run}
	then
		echo "Range:  ${diff_label}"
		echo "Rules:  ${reviewer_dir}/rules"
		echo "Groups: $(_get_group_labels "${reviewed_files}" | tr "\n" " ")"
		echo "Files:"
		echo "${reviewed_files}" | sed "s/^/  /"
		echo ""

		cat ${diff_file}

		exit 0
	fi

	if ! command -v claude > /dev/null 2>&1
	then
		echo "Unable to find the claude CLI on the PATH." >&2

		exit 2
	fi

	local group_label
	local group_rule_files
	local pids=()
	local reviewed_rule_files=""

	for group_label in $(_get_group_labels "${reviewed_files}")
	do
		group_rule_files=$(_get_group_rule_files ${group_label} ${reviewer_dir}/rules)
		reviewed_rule_files+=" ${group_rule_files}"

		_review_group ${group_label} "${group_rule_files}" ${repo_root} ${reviewer_dir} ${work_dir} &

		pids+=(${!})
	done

	local reviewed_rules

	reviewed_rules=$(echo ${reviewed_rule_files} | tr " " "\n" | sed -e "s#.*/##" | cut -c 1-3 | jq --raw-input --slurp 'split("\n") | map(select(. != ""))')

	local pid

	for pid in ${pids[@]+"${pids[@]}"}
	do
		wait ${pid} || true
	done

	local merged_json

	merged_json=$(jq --slurp '{chance: (map(.chance) | max // 0), groups: (map({(.group): {chance: .chance, seconds: .seconds, violations: (.violations | length)}}) | add // {}), reviewed_files: $reviewed_files, rules: $reviewed_rules, cost: (map(.cost) | add // 0), tokens: {cache_read: (map(.tokens.cache_read) | add // 0), cache_write: (map(.tokens.cache_write) | add // 0), input: (map(.tokens.input) | add // 0), output: (map(.tokens.output) | add // 0)}, violations: ([.[].violations[]] | unique_by([.file, .line, .rule]) | sort_by([.file, .line, .rule]))}' --argjson reviewed_rules "${reviewed_rules}" --argjson reviewed_files "$(echo "${reviewed_files}" | jq --raw-input --slurp 'split("\n") | map(select(. != ""))')" ${work_dir}/*.json)

	local violations_count

	violations_count=$(echo "${merged_json}" | jq ".violations | length")

	if ${json}
	then
		echo "${merged_json}"
	else
		_print_report "${merged_json}" "${diff_label}"
	fi

	if ls ${work_dir}/*.failed > /dev/null 2>&1
	then
		echo "Unable to review the diff because these groups returned no verdict: $(basename -a -s .failed ${work_dir}/*.failed | tr "\n" " ")" >&2

		if [ ${violations_count} -eq 0 ]
		then
			exit 2
		fi
	fi

	if [ ${violations_count} -gt 0 ]
	then
		exit 1
	fi
}

function _get_group_labels {
	local reviewed_files=${1}

	local group
	local kinds

	for group in "${_GROUPS[@]}"
	do
		kinds=${group##*|}

		if [[ ${kinds} == all ]] || _has_file_kind "${reviewed_files}" ${kinds}
		then
			echo ${group%%|*}
		fi
	done
}

function _get_group_rule_files {
	local group_label=${1}
	local rules_dir=${2}

	local group
	local prefix
	local prefixes

	for group in "${_GROUPS[@]}"
	do
		if [[ ${group%%|*} != ${group_label} ]]
		then
			continue
		fi

		prefixes=${group#*|}

		for prefix in ${prefixes%|*}
		do
			ls ${rules_dir}/${prefix}*-*.md 2> /dev/null || true
		done
	done
}

function _has_file_kind {
	local reviewed_files=${1}

	shift

	local kind

	for kind in "${@}"
	do
		if [[ ${kind} == java ]] && echo "${reviewed_files}" | grep --quiet "[.]java$"
		then
			return 0
		fi

		if [[ ${kind} == sh ]] && echo "${reviewed_files}" | grep --quiet "[.]sh$"
		then
			return 0
		fi

		if [[ ${kind} == test ]] && echo "${reviewed_files}" | grep --quiet --regexp "/src/test/" --regexp "/src/testIntegration/" --regexp "Test[.]java$"
		then
			return 0
		fi
	done

	return 1
}

function _print_help {
	echo "Usage: review.sh [--dry-run] [--json] [--staged | <range>]"
	echo ""
	echo "  --dry-run  Print the filtered diff and the rule groups without calling the model."
	echo "  --json     Print the merged verdict as JSON."
	echo "  --staged   Review the staged changes instead of the branch."
	echo "  <range>    Review a Git range, such as HEAD~3..HEAD. The default is the merge base with the base branch to HEAD."
}

function _print_report {
	local merged_json=${1}
	local diff_label=${2}

	echo "${merged_json}" | jq --arg diff_label "${diff_label}" --raw-output '"Reviewed \(.reviewed_files | length) file(s) for \($diff_label) against \(.rules | length) rules in \(.groups | length) group(s), \(.tokens.input + .tokens.cache_write + .tokens.cache_read) input tokens (\(.tokens.cache_read) read from the cache), \(.tokens.output) output tokens, and $\(.cost * 100 | round / 100)."'
	echo ""

	echo "${merged_json}" | jq --raw-output '.groups | to_entries[] | "  \(.key): \(.value.violations) violation(s), \(.value.chance)% chance, \(.value.seconds)s"'
	echo ""

	local chance

	chance=$(echo "${merged_json}" | jq ".chance")

	local violations_count

	violations_count=$(echo "${merged_json}" | jq ".violations | length")

	if [ ${violations_count} -eq 0 ]
	then
		echo "No violations found (${chance}% chance of rejection)."

		return 0
	fi

	echo "Found ${violations_count} violation(s) (${chance}% chance of rejection):"
	echo ""

	echo "${merged_json}" | jq --raw-output '.violations[] | "\(.file):\(.line) [rule \(.rule)] \(.message)\n    Fix: \(.fix)\n"'
}

function _review_group {
	local group_label=${1}
	local rule_files=${2}
	local repo_root=${3}
	local reviewer_dir=${4}
	local work_dir=${5}

	local group_json=${work_dir}/${group_label}.json
	local seconds=$(date +%s)

	echo "{\"chance\": 0, \"group\": \"${group_label}\", \"seconds\": 0, \"cost\": 0, \"tokens\": {\"cache_read\": 0, \"cache_write\": 0, \"input\": 0, \"output\": 0}, \"violations\": []}" > ${group_json}

	local prompt

	prompt="Review the diff below against the rules below, on behalf of the reviewer whose style guide follows. Work through the rules one at a time and check the whole diff against each before moving to the next, so no rule is skipped. Flag violations of these rules only: a rule outside this set is covered by another reviewer, so leave its violations out even when you notice them.

For a naming, ordering, or convention question the diff cannot settle, run \`git grep --cached <pattern>\` against the checkout at ${repo_root} before deciding, and read a changed file only when the diff lacks the context to decide. Always pass --cached. Use at most eight tool calls in total, and decide from the diff when they run out.

Report each violation with the path of the file as it appears in the diff, the line number in the new file taken from the hunk headers, the number of the rule it violates (the three digit number in the heading of the rule whose text you are applying, never a related rule), a one sentence message that names what is wrong, and a fix that says exactly what to change. When your confidence is partial, still include the violation and start the fix with 'Verify:' so a human can confirm it. Do not flag anything inside a file whose contents include an @generated marker.

When a violation is a mechanical formatting or layout matter that SourceFormatter also governs (collapsing or expanding a boolean return or a ternary, line wrapping, whitespace, blank lines, import order, or the ordering of members, parameters, or declarations), end the fix with 'SourceFormatter is authoritative: if applying this fails SourceFormatter, ignore it.'

The chance is your confidence, from 0 to 100, that the reviewer closes a pull request with this diff over these violations.

# Style guide

$(cat ${reviewer_dir}/STYLE.md)

# Rules

$(awk 'FNR == 1 && NR > 1 {print ""} {print}' ${rule_files})

# Diff

$(cat ${work_dir}/review.diff)"

	local raw

	raw=$(echo "${prompt}" | claude \
		--add-dir ${repo_root} \
		--allowed-tools "Bash(git grep:*)" Grep Read \
		--json-schema "${_JSON_SCHEMA}" \
		--model ${_MODEL} \
		--no-session-persistence \
		--output-format json \
		--print \
		--tools Bash Grep Read 2> ${work_dir}/${group_label}.err || true)

	if ! echo "${raw}" | jq --exit-status ".structured_output | has(\"violations\")" > /dev/null 2>&1
	then
		echo "Unable to get a verdict from the ${group_label} group: $(echo "${raw}" | jq --raw-output '.result // .error // empty' 2> /dev/null | head -c 300)" >&2

		touch ${work_dir}/${group_label}.failed

		return 0
	fi

	echo "${raw}" | jq --arg group ${group_label} --argjson seconds $(($(date +%s) - seconds)) '{chance: (if ((.structured_output.violations // []) | length) > 0 then (.structured_output.chance // 0) else 0 end), group: $group, seconds: $seconds, cost: (.total_cost_usd // 0), tokens: {cache_read: (.usage.cache_read_input_tokens // 0), cache_write: (.usage.cache_creation_input_tokens // 0), input: (.usage.input_tokens // 0), output: (.usage.output_tokens // 0)}, violations: (.structured_output.violations // [])}' > ${group_json}
}

function _write_diff_file {
	local diff_args=${1}

	local generated_files=""
	local grep_ref=${diff_args##*..}
	local reviewed_file

	if [[ -z ${grep_ref} ]]
	then
		grep_ref=HEAD
	fi

	for reviewed_file in $(git diff --name-only ${diff_args} -- .)
	do
		if git grep --ignore-case --quiet "@generated" ${grep_ref} -- ":(top)${reviewed_file}" 2> /dev/null
		then
			generated_files+="|${reviewed_file}"
		fi
	done

	git diff --unified=1 ${diff_args} -- . | awk \
		-v generated_files="${generated_files}|" \
		-v ignored_filenames="${_IGNORED_FILENAMES}" \
		-v ignored_patterns="${_IGNORED_PATTERNS}" \
		-v ignored_suffixes="${_IGNORED_SUFFIXES}" \
		-v name_only_suffixes="${_NAME_ONLY_SUFFIXES}" '
		BEGIN {
			split(ignored_filenames, filenames, " ")
			split(ignored_patterns, patterns, " ")
			split(ignored_suffixes, suffixes, " ")
			split(name_only_suffixes, name_only_list, " ")
		}
		/^diff --git / {
			file = substr($4, 3)

			skip = index(generated_files, "|" file "|") > 0
			name_only = 0

			for (i in filenames) {
				if (file ~ ("(^|/)" filenames[i] "$")) {
					skip = 1
				}
			}

			for (i in patterns) {
				if (file ~ patterns[i]) {
					skip = 1
				}
			}

			for (i in suffixes) {
				if (file ~ ("[.]" suffixes[i] "$")) {
					skip = 1
				}
			}

			for (i in name_only_list) {
				if (file ~ ("[.]" name_only_list[i] "$")) {
					name_only = 1
				}
			}

			if (! skip) {
				print
			}

			next
		}
		! skip && ! name_only
	'
}

_GROUPS=(
	"convention|00|all"
	"naming-1|10|all"
	"naming-2|11|all"
	"ordering-1|201 202 203 204|all"
	"ordering-2|205 206 207 208 209|all"
	"utility-methods|3|java"
	"control-flow|4|java sh"
	"redundancy-and-visibility|5 8|java"
	"tests|6|test"
	"prose-1|70|all"
	"prose-2|71|all"
	"formatting|9|all"
)
_IGNORED_FILENAMES="CHANGELOG.md package-lock.json package.json"
_IGNORED_PATTERNS="(^|/)Language_.*[.]properties$"
_IGNORED_SUFFIXES="css js jsx lock lockfile macro path scss snap testcase ts tsx"
_JSON_SCHEMA='{"type": "object", "properties": {"chance": {"type": "integer"}, "violations": {"type": "array", "items": {"type": "object", "properties": {"file": {"type": "string"}, "line": {"type": "integer"}, "rule": {"type": "string"}, "message": {"type": "string"}, "fix": {"type": "string"}}, "required": ["file", "line", "rule", "message", "fix"]}}}, "required": ["chance", "violations"]}'
_MODEL=${LIFERAY_PR_REVIEWER_MODEL:-sonnet}
_NAME_ONLY_SUFFIXES="bmp gif ico jpeg jpg png svg webp"

main ${@+"${@}"}