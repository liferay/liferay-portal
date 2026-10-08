#!/bin/bash

#
# Review a local diff against the rules in this directory and print the
# violations, so a commit or a branch satisfies the conventions before a human
# reads it.
#
# The diff is the staged changes with --staged, the commits on the current branch
# that are not on the base branch by default, or the range given as the last
# argument. Generated files, frontend sources, generated locale files, and lock
# files are dropped before the model sees the diff, with the same filter run.sh
# applies to a pull request.
#
# The rules are reviewed as groups that run in parallel, one model call per
# group, so each call applies a handful of rules exhaustively instead of spot
# checking all of them. A group only runs when the diff holds a file its rules
# apply to, so a properties change is not reviewed against the Java rules.
#
# Exit status is 0 when the diff is clean, 1 when it has violations, and 2 when
# the review could not run.
#

set -o errexit
set -o nounset
set -o pipefail

function main {
	local diff_range=""
	local dry_run=false
	local excludes=()
	local group_filter=""
	local json=false
	local single=false
	local staged=false

	while [ ${#} -gt 0 ]
	do
		case ${1} in
			--dry-run)
				dry_run=true
				;;
			--exclude)
				excludes+=(":(exclude)${2}")

				shift
				;;
			--group)
				group_filter=${2}

				shift
				;;
			--help)
				_print_help

				exit 0
				;;
			--json)
				json=true
				;;
			--single)
				single=true
				;;
			--staged)
				staged=true
				;;
			--*)
				echo "Unknown option ${1}." >&2

				_print_help >&2

				exit 2
				;;
			*)
				diff_range=${1}
				;;
		esac

		shift
	done

	if ${staged} && [[ ! -z ${diff_range} ]]
	then
		echo "Pass either --staged or a range, not both." >&2

		exit 2
	fi

	if ${single} && [[ ! -z ${group_filter} ]]
	then
		echo "Pass either --single or --group, not both." >&2

		exit 2
	fi

	_resolve_repo

	local work_dir

	work_dir=$(mktemp --directory)

	trap "rm -fr ${work_dir}" EXIT

	local diff_file=${work_dir}/review.diff

	_write_diff_file

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
		echo "Rules:  ${rules_dir}"
		echo "Groups: $(_get_group_labels | tr "\n" " ")"
		echo "Files:"
		echo "${reviewed_files}" | sed "s/^/  /"
		echo ""

		cat ${diff_file}

		exit 0
	fi

	if ! command -v claude > /dev/null 2>&1
	then
		echo "The claude CLI is not on the PATH." >&2

		exit 2
	fi

	local group_label
	local pids=()
	local reviewed_rule_files=""

	if ${single}
	then
		reviewed_rule_files=$(ls ${rules_dir}/*.md)

		_review_group all "${reviewed_rule_files}" &

		pids+=(${!})
	else
		local group_labels

		group_labels=$(_get_group_labels)

		if [[ ! -z ${group_filter} ]]
		then
			if ! echo "${group_labels}" | grep --line-regexp --quiet "${group_filter}"
			then
				echo "The ${group_filter} group does not apply to ${diff_label}. The groups that apply are: $(echo ${group_labels})." >&2

				exit 2
			fi

			group_labels=${group_filter}
		fi

		for group_label in ${group_labels}
		do
			reviewed_rule_files+=" $(_get_group_rule_files ${group_label})"

			_review_group ${group_label} "$(_get_group_rule_files ${group_label})" &

			pids+=(${!})
		done
	fi

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
		_print_report
	fi

	#
	# A group that returned no verdict reviewed nothing, so the diff is not clean
	# even when every other group found no violations.
	#

	if ls ${work_dir}/*.failed > /dev/null 2>&1
	then
		echo "Unable to review the diff because these groups returned no verdict: $(basename -a -s .failed ${work_dir}/*.failed | tr "\n" " ")" >&2

		exit 2
	fi

	if [ ${violations_count} -gt 0 ]
	then
		exit 1
	fi
}

function _get_group_labels {
	local group
	local kinds

	for group in "${_GROUPS[@]}"
	do
		kinds=${group##*|}

		if [[ ${kinds} == all ]] || _has_file_kind ${kinds}
		then
			echo ${group%%|*}
		fi
	done

	if [[ ! -z $(_get_group_rule_files ungrouped) ]]
	then
		echo ungrouped
	fi
}

function _get_group_rule_files {
	local group_label=${1}

	local group
	local prefix
	local prefixes
	local rule_file

	if [[ ${group_label} == ungrouped ]]
	then
		local grouped_prefixes=""

		for group in "${_GROUPS[@]}"
		do
			prefixes=${group#*|}

			grouped_prefixes+=" ${prefixes%|*}"
		done

		local grouped
		local rule_number

		for rule_file in ${rules_dir}/*.md
		do
			grouped=false
			rule_number=$(basename ${rule_file})

			for prefix in ${grouped_prefixes}
			do
				if [[ ${rule_number} == ${prefix}* ]]
				then
					grouped=true
				fi
			done

			if ! ${grouped}
			then
				echo ${rule_file}
			fi
		done

		return 0
	fi

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
	echo "Usage: review.sh [--dry-run] [--exclude <path>]... [--group <label>] [--json] [--single] [--staged | <range>]"
	echo ""
	echo "  --dry-run  Print the filtered diff and the rule groups without calling the model."
	echo "  --exclude  Leave a path out of the diff. Repeat the option for several paths."
	echo "  --group    Review only the rules of one group, such as naming-1. The dry run lists the groups."
	echo "  --json     Print the merged verdict as JSON."
	echo "  --single   Review every rule in one model call instead of one call per group."
	echo "  --staged   Review the staged changes instead of the branch."
	echo "  <range>    Review a Git range, such as HEAD~3..HEAD. The default is the merge base with the base branch to HEAD."
}

function _print_report {
	local chance

	chance=$(echo "${merged_json}" | jq ".chance")

	local reviewed_files_count

	reviewed_files_count=$(($(echo "${reviewed_files}" | wc -l)))

	echo "Reviewed ${reviewed_files_count} file(s) for ${diff_label} against $(($(ls ${rules_dir}/*.md | wc -l))) rules in $(($(_get_group_labels | wc -l))) group(s), $(echo "${merged_json}" | jq --raw-output '"\(.tokens.input + .tokens.cache_write + .tokens.cache_read) input tokens (\(.tokens.cache_read) read from the cache), \(.tokens.output) output tokens, and $\(.cost * 100 | round / 100)"')."
	echo ""

	echo "${merged_json}" | jq --raw-output '.groups | to_entries[] | "  \(.key): \(.value.violations) violation(s), \(.value.chance)% chance, \(.value.seconds)s"'
	echo ""

	if [ ${violations_count} -eq 0 ]
	then
		echo "No violations found (${chance}% chance of rejection)."

		return 0
	fi

	echo "Found ${violations_count} violation(s) (${chance}% chance of rejection):"
	echo ""

	echo "${merged_json}" | jq --raw-output '.violations[] | "\(.file):\(.line) [rule \(.rule)] \(.message)\n    Fix: \(.fix)\n"'
}

function _resolve_repo {
	repo_root=$(git rev-parse --show-toplevel 2> /dev/null || true)

	if [[ -z ${repo_root} ]]
	then
		echo "Not inside a Git repository." >&2

		exit 2
	fi

	cd ${repo_root}

	rules_dir=${repo_root}/pr-reviewer/rules
	style_file=${repo_root}/pr-reviewer/STYLE.md

	#
	# A liferay-portal-ee checkout ships no rules, so it reads them from a sibling
	# liferay-portal checkout or from LIFERAY_PR_REVIEWER_HOME.
	#

	if [ ! -d ${rules_dir} ]
	then
		local reviewer_home=${LIFERAY_PR_REVIEWER_HOME:-${repo_root%/*}/liferay-portal/pr-reviewer}

		rules_dir=${reviewer_home}/rules
		style_file=${reviewer_home}/STYLE.md
	fi

	if [ ! -d ${rules_dir} ] || [ ! -f ${style_file} ]
	then
		echo "Unable to find pr-reviewer/rules and pr-reviewer/STYLE.md under ${repo_root} or LIFERAY_PR_REVIEWER_HOME." >&2

		exit 2
	fi
}

function _review_group {
	local group_label=${1}
	local rule_files=${2}

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

$(cat ${style_file})

# Rules

$(cat ${rule_files})

# Diff

$(cat ${diff_file})"

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
		echo "The ${group_label} group returned no verdict: $(echo "${raw}" | jq --raw-output '.result // .error // empty' 2> /dev/null | head -c 300)" >&2

		touch ${work_dir}/${group_label}.failed

		return 0
	fi

	echo "${raw}" | jq --arg group ${group_label} --argjson seconds $(($(date +%s) - seconds)) '{chance: (if ((.structured_output.violations // []) | length) > 0 then (.structured_output.chance // 0) else 0 end), group: $group, seconds: $seconds, cost: (.total_cost_usd // 0), tokens: {cache_read: (.usage.cache_read_input_tokens // 0), cache_write: (.usage.cache_creation_input_tokens // 0), input: (.usage.input_tokens // 0), output: (.usage.output_tokens // 0)}, violations: (.structured_output.violations // [])}' > ${group_json}
}

function _write_diff_file {
	local diff_args
	local generated_files=""
	local grep_ref
	local reviewed_file

	if ${staged}
	then
		diff_args="--cached"
		diff_label="the staged changes"
		grep_ref="--cached"
	else
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
		grep_ref=${diff_range##*..}

		if [[ -z ${grep_ref} ]]
		then
			grep_ref=HEAD
		fi
	fi

	#
	# Detect generated files on the new side of the change so the filter can drop
	# them, matching run.sh.
	#

	for reviewed_file in $(git diff --name-only ${diff_args} -- . ${excludes[@]+"${excludes[@]}"})
	do
		if git grep --ignore-case --quiet "@generated" ${grep_ref} -- ":(top)${reviewed_file}" 2> /dev/null
		then
			generated_files+="|${reviewed_file}"
		fi
	done

	git diff --unified=1 ${diff_args} -- . ${excludes[@]+"${excludes[@]}"} | awk \
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
	' > ${diff_file}
}

#
# Each group is a label, the leading digits of the rule files it covers (one
# digit for a whole category, more to split one), and the kinds of file it
# applies to: all, java, sh, or test. A group holds about nine rules, since a
# call over more than that spot checks instead of applying each one.
#

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

main "${@}"