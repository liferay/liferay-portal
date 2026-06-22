#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

function main {
	if [[ $(uname --nodename) == sandbox ]]
	then
		echo "Do not run this inside the AI sandbox."

		exit 1
	fi

	mkdir --parents output

	local pr_subdir

	for pr_subdir in output/*
	do
		if [ -d ${pr_subdir} ] && _is_older_than ${pr_subdir} $((3 * 24 * 3600))
		then
			rm --force --recursive ${pr_subdir}
		fi
	done

	local closed_prs=""
	local command=${1:-}
	local dry_run=false
	local pr_closed=false
	local pr_locked=false
	local pr_number=${2:-}

	if [[ ${command} == --dry-run ]]
	then
		dry_run=true

		shift

		command=${1:-}
		pr_number=${2:-}
	fi

	local pr_dir=output/${pr_number}

	if [[ ${command} == check ]]
	then
		if [[ -z ${pr_number} ]]
		then
			clear

			while true
			do
				local closed_prs_count=0
				local reviewed=false

				for pr_number in $(gh api --paginate "repos/${_REPO}/pulls?state=open" | jq --raw-output ".[].number")
				do
					pr_dir=output/${pr_number}

					_check_pr

					if ${pr_closed}
					then
						((++closed_prs_count))
					fi
				done

				if [ ${closed_prs_count} -eq 1 ]
				then
					echo "Closed 1 PR at $(date +"%-l:%M %P")."
				else
					echo "Closed ${closed_prs_count} PRs at $(date +"%-l:%M %P")."
				fi

				if ! ${reviewed}
				then
					sleep $(((5 * 60) + (RANDOM % (5 * 60))))
				fi
			done
		else
			local reviewed=false

			_check_pr
		fi
	elif [[ ${command} == kill ]]
	then
		local pid

		for pid in $(pgrep --full "run\.sh")
		do
			if [ ${pid} -ne $$ ] && [[ $(readlink /proc/${pid}/cwd 2> /dev/null) = ${PWD} ]]
			then
				echo "Killing ${pid}."

				kill ${pid} 2> /dev/null || true
			fi
		done

		for pr_subdir in output/*
		do
			if [ -d ${pr_subdir}/.lock ]
			then
				echo "Removing ${pr_subdir}."

				rm --force --recursive ${pr_subdir}
			fi
		done

		local ref

		for ref in $(git for-each-ref --format="%(refname)" refs/pr-reviewer/ 2> /dev/null)
		do
			if [ ! -d output/${ref#refs/pr-reviewer/} ]
			then
				echo "Removing orphan Git reference ${ref}."

				git update-ref -d ${ref}
			fi
		done
	elif [[ ${command} == review ]]
	then
		if [[ -z ${pr_number} ]]
		then
			_print_help
		fi

		_get_automatic_code_review_json
	else
		_print_help
	fi
}

function _acquire_pr_lock {
	mkdir --parents ${pr_dir}

	if _is_older_than ${pr_dir}/.lock $(((_REVIEW_TIMEOUT_MINUTES + 1) * 60))
	then
		rmdir ${pr_dir}/.lock 2> /dev/null || true
	fi

	if ! mkdir ${pr_dir}/.lock 2> /dev/null
	then
		return 1
	fi

	pr_locked=true
}

function _check_pr {
	pr_closed=false

	if [[ " ${closed_prs} " == *" ${pr_number} "* ]]
	then
		return 0
	fi

	if [ -f ${pr_dir}/.reviewed ]
	then
		return 0
	fi

	if ! _acquire_pr_lock
	then
		echo "Skipping locked ${pr_number}."

		return 0
	fi

	trap _release_pr_lock RETURN

	echo -n "Checking ${pr_number}."

	local mergeable="" mergeable_state="" pr_json="" rebaseable="" state=""

	_retry 5 _check_pr_mergeability || true

	if [[ -z ${state} ]]
	then
		echo ""
		echo "Unable to fetch: $(echo "${pr_json}" | tr "\n" " " | head --bytes=200)"
		echo ""

		return 0
	fi

	if [[ ${state} != open ]]
	then
		closed_prs+=" ${pr_number}"

		echo ""
		echo "Skipping closed ${pr_number}."
		echo ""

		return 0
	fi

	if [[ ${mergeable} == false ]] || \
	   [[ ${mergeable_state} == dirty ]] || \
	   [[ ${rebaseable} == false ]]
	then
		echo ""
		echo "Closing unmergeable ${pr_number}:"

		local base_ref=$(echo "${pr_json}" | jq --raw-output ".base.ref")

		_fetch_pr || true

		local conflicts

		conflicts=$(git merge-tree --name-only --write-tree origin/${base_ref} refs/pr-reviewer/${pr_number} 2> /dev/null | sed --quiet "2,/^$/{/^$/!p}" | head --lines=30) || true

		if [[ ! -z ${conflicts} ]]
		then
			echo "${conflicts}" | sed "s/^/    /"
		fi

		echo ""

		if ! ${dry_run}
		then
			gh pr close ${pr_number} --comment "Resend this PR because it has rebase errors." --repo ${_REPO}

			closed_prs+=" ${pr_number}"
		fi

		echo ""

		pr_closed=true

		return 0
	fi

	if gh api "repos/${_REPO}/issues/${pr_number}/comments" \
		--jq ".[].body" 2>&1 | grep --quiet "#bchan-bot-pr-review"
	then
		touch ${pr_dir}/.reviewed

		return 0
	fi

	reviewed=true

	local automatic_code_review_json

	if ! automatic_code_review_json=$(_get_automatic_code_review_json)
	then
		return 0
	fi

	local index
	local max_chance=$(echo "${automatic_code_review_json}" | jq "map(.chance) | max")
	local models_count=$(echo "${automatic_code_review_json}" | jq "length")

	local usernames

	usernames=$(
		{
			gh api "repos/${_REPO}/issues/${pr_number}/comments" --jq ".[].user.login | select(. != null)" 2> /dev/null || true
			gh api "repos/${_REPO}/pulls/${pr_number}/comments" --jq ".[].user.login | select(. != null)" 2> /dev/null || true
			gh api "repos/${_REPO}/pulls/${pr_number}/commits" --jq ".[].author.login | select(. != null)" 2> /dev/null || true
			gh api "repos/${_REPO}/pulls/${pr_number}/reviews" --jq ".[].user.login | select(. != null)" 2> /dev/null || true
		} | grep --invert-match liferay-continuous-integration | sort --unique | tr "[:upper:]" "[:lower:]"
	) || true

	local at_usernames=$(echo "${usernames}" | sed "s/^/@/" | tr "\n" " " | sed "s/ *$//")

	if [ ${models_count} -eq 0 ]
	then
		local body="${at_usernames}"$'\n\n'"Brian will most likely merge this PR."

		echo ""
		echo "${body}"
		echo ""

		if ! ${dry_run}
		then
			gh pr comment ${pr_number} \
				--body "${body}"$'\n\n'"#bchan-bot-pr-review" \
				--repo ${_REPO}

			touch ${pr_dir}/.reviewed

			echo ""
		fi

		return 0
	fi

	if [[ $(echo "${automatic_code_review_json}" | jq "all(.failure_type)") == true ]]
	then
		if [[ $(echo "${automatic_code_review_json}" | jq "all(.transient // false)") == true ]]
		then
			echo "${pr_number}: every model hit a transient API error." >&2

			return 0
		fi

		local body="${at_usernames}"$'\n\n'"The automated review could not be completed."

		echo ""
		echo "${body}"
		echo ""

		if ! ${dry_run}
		then
			gh pr comment ${pr_number} \
				--body "${body}"$'\n\n'"#bchan-bot-pr-review" \
				--repo ${_REPO}

			touch ${pr_dir}/.reviewed
		fi

		return 0
	fi

	local body="${at_usernames}"$'\n\n'"There is $(_get_indefinite_article_for_number "${max_chance}") ${max_chance}% chance that Brian will reject this PR."

	for ((index = 0; index < models_count; index++))
	do
		local chance=$(echo "${automatic_code_review_json}" | jq --raw-output ".[${index}].chance")

		if [ ${chance} -eq 0 ]
		then
			continue
		fi

		local input_tokens=$(echo "${automatic_code_review_json}" | jq --raw-output ".[${index}].input_tokens // 0")
		local model=$(echo "${automatic_code_review_json}" | jq --raw-output ".[${index}].model")
		local output_tokens=$(echo "${automatic_code_review_json}" | jq --raw-output ".[${index}].output_tokens // 0")
		local seconds=$(echo "${automatic_code_review_json}" | jq --raw-output ".[${index}].seconds // 0")
		local violations=$(echo "${automatic_code_review_json}" | jq --raw-output ".[${index}].violations[]" | sed "s/^/- /")

		if [[ ! -z ${body} ]]
		then
			body+=$'\n\n'
		fi

		body+="${model} (${chance}% chance of rejection, $(_format_tokens ${input_tokens})/$(_format_tokens ${output_tokens}) tokens, ${seconds}s):"$'\n'"${violations}"
	done

	echo ""
	echo "${body}"
	echo ""

	if ! ${dry_run}
	then
		gh pr comment ${pr_number} \
			--body "${body}"$'\n\n'"#bchan-bot-pr-review" \
			--repo ${_REPO}

		touch ${pr_dir}/.reviewed

		echo ""
	fi

	if true
	then
		return 0
	elif [ ${max_chance} -gt 50 ]
	then
		echo "Closing ${pr_number} (${max_chance}% dirty)."
		echo ""

		if ! ${dry_run}
		then
			gh pr close ${pr_number} \
				--comment "Closing PR because ${max_chance}% dirty exceeds the 50% threshold. ${at_usernames} lose 5 points."$'\n\n'"#bchan-bot-pr-review" \
				--repo ${_REPO}

			closed_prs+=" ${pr_number}"

			echo ""

			_update_points -5 "${usernames}"
		fi
	elif [ ${max_chance} -lt 10 ]
	then
		if ! ${dry_run}
		then
			gh pr comment ${pr_number} \
				--body "${at_usernames} gain 1 point for a clean PR."$'\n\n'"#bchan-bot-pr-review" \
				--repo ${_REPO}

			echo ""

			_update_points 1 "${usernames}"
		fi
	fi

	return 0
}

function _check_pr_mergeability {
	if ! pr_json=$(gh api "repos/${_REPO}/pulls/${pr_number}" 2>&1)
	then
		return 1
	fi

	state=$(echo "${pr_json}" | jq --raw-output ".state")

	if [[ ${state} != open ]]
	then
		echo ""

		return 0
	fi

	mergeable=$(echo "${pr_json}" | jq --raw-output ".mergeable")
	mergeable_state=$(echo "${pr_json}" | jq --raw-output ".mergeable_state")
	rebaseable=$(echo "${pr_json}" | jq --raw-output ".rebaseable")

	if [[ ${mergeable} != null ]]
	then
		echo ""

		return 0
	fi

	echo -n .

	return 1
}

function _classify_failure {
	local raw=${1}
	local response=${2}

	if [[ $(echo "${raw}" | jq --raw-output ".is_error // false" 2> /dev/null) == true ]]
	then
		echo api_error
	elif [[ -z ${response} ]]
	then
		echo empty
	elif echo "${response}" | grep --quiet '"chance"'
	then
		echo malformed_json
	else
		echo no_verdict
	fi
}

function _extract_last_json_block {
	local candidate result=""
	local text=$(cat)

	while IFS= read -r -d "" candidate
	do
		if echo "${candidate}" | jq --exit-status 'objects | has("chance")' > /dev/null 2>&1
		then
			result=${candidate}
		fi
	done < <(printf "%s" "${text}" | awk '
		{
			data = data $0 "\n"
		}

		END {
			data_length = length(data)

			for (start_index = 1; start_index <= data_length; start_index++) {
				if (substr(data, start_index, 1) != "{") {
					continue
				}

				brace_depth = 0
				inside_string = 0
				is_escaped = 0

				for (scan_index = start_index; scan_index <= data_length; scan_index++) {
					character = substr(data, scan_index, 1)

					if (inside_string) {
						if (is_escaped) {
							is_escaped = 0
						}
						else if (character == "\\") {
							is_escaped = 1
						}
						else if (character == "\"") {
							inside_string = 0
						}

						continue
					}

					if (character == "\"") {
						inside_string = 1
					}
					else if (character == "{") {
						brace_depth = brace_depth + 1
					}
					else if (character == "}") {
						brace_depth = brace_depth - 1

						if (brace_depth == 0) {
							printf "%s%c", substr(data, start_index, scan_index - start_index + 1), 0

							break
						}
					}
				}
			}
		}
	')

	if [[ ! -z ${result} ]]
	then
		echo "${result}"
	fi
}

function _fetch_pr {
	timeout 60 git fetch --quiet --force origin pull/${pr_number}/head:refs/pr-reviewer/${pr_number} 2> /dev/null
}

function _format_tokens {
	local number=${1}

	if ((number >= 1000))
	then
		echo "$(((number + 500) / 1000))k"
	else
		echo ${number}
	fi
}

function _get_automatic_code_review_json {
	if ! ${pr_locked}
	then
		if ! _acquire_pr_lock
		then
			echo "Another process is reviewing ${pr_number}." >&2

			exit 1
		fi

		trap _release_pr_lock RETURN
	fi

	if ! _retry 5 _fetch_pr
	then
		echo "Unable to fetch ${pr_number}." >&2

		rm --force ${pr_dir}/*

		return 1
	fi

	local diff_file

	local diff_range=refs/pr-reviewer/${pr_number}..refs/pr-reviewer/${pr_number}

	local from_commit=$(git merge-base master refs/pr-reviewer/${pr_number} 2> /dev/null)

	if [[ ! -z ${from_commit} ]]
	then
		diff_range="${from_commit}..refs/pr-reviewer/${pr_number}"
	fi

	local generated_files=""

	for diff_file in $(git diff --name-only ${diff_range} || true)
	do
		if git grep --ignore-case --quiet "@generated" refs/pr-reviewer/${pr_number} -- ":(top)${diff_file}" 2> /dev/null
		then
			generated_files+="|${diff_file}"
		fi
	done

	rm --force ${pr_dir}/*

	git diff --unified=1 ${diff_range} | awk \
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

			#
			# Match the b/ destination, not the a/ source, since a rename makes the two
			# differ and generated_files holds the destination reported by --name-only
			#

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
	' > ${pr_dir}/pr.diff

	if [ ! -s ${pr_dir}/pr.diff ]
	then
		echo "[]"

		return 0
	fi

	local pids=()

	for model in "${_MODELS[@]}"
	do
		_write_model_json_file ${model} &

		pids+=($!)
	done

	for pid in "${pids[@]}"
	do
		wait "${pid}" || true
	done

	local automatic_code_review_json="[]"

	for model in "${_MODELS[@]}"
	do
		local model_json=$(cat ${pr_dir}/${model}.json 2> /dev/null) || model_json=""

		if [[ -z ${model_json} ]] || ! echo "${model_json}" | jq . > /dev/null 2>&1
		then
			model_json="{\"chance\": 0, \"seconds\": 0, \"violations\": []}"
		fi

		automatic_code_review_json=$(echo "${automatic_code_review_json}" | jq --arg model "${model}" --argjson model_json "${model_json}" ". + [\$model_json + {model: \$model}]")
	done

	echo "${automatic_code_review_json}"
}

function _get_indefinite_article_for_number {
	local number=${1}

	if [[ ${number} =~ ^(8|11|18|8[0-9])$ ]]
	then
		echo an
	else
		echo a
	fi
}

function _invoke_model {
	local model=${1}
	local prompt=${2}

	if [ ${model} = sonnet-4.6 ]
	then
		_review_in_sandbox \
			env \
				HTTPS_PROXY=localhost:8118 \
				HTTP_PROXY=localhost:8118 \
				\
				claude \
					--add-dir /review \
					--dangerously-skip-permissions \
					--model sonnet \
					--output-format json \
					--print "Read the PR diff at /review/pr.diff, every rule file under /review/rules, and the style guide /review/STYLE.md, then review the diff against every rule. ${prompt}" || true
	else
		_review_in_sandbox \
			opencode run \
				--dangerously-skip-permissions \
				--file /review/STYLE.md \
				--file /review/pr.diff \
				--file /review/rules \
				--format json \
				--model "opencode-go/${model}" \
				"Review this PR diff against every attached rule. ${prompt}" || true
	fi
}

function _is_older_than {
	local file=${1}
	local seconds=${2}

	local modified_time=$(stat --format=%Y ${file} 2> /dev/null)

	[ $(($(date +%s) - ${modified_time:-0})) -gt ${seconds} ]
}

function _is_transient_error {
	local raw=${1}

	local api_error_status=$(echo "${raw}" | jq --raw-output ".api_error_status // empty" 2> /dev/null || true)
	local error=$(echo "${raw}" | jq --raw-output ".is_error // false" 2> /dev/null || echo false)

	[[ ${error} == true ]] && [[ ${api_error_status} =~ ^(429|5[0-9][0-9])$ ]]
}

function _print_help {
	echo "Usage:"
	echo "    ${0} [--dry-run] check [pr]    Check all open PRs or just [pr]"
	echo "    ${0} kill                      Kill running processes and remove locked PR dirs"
	echo "    ${0} [--dry-run] review <pr>   Review <pr> and print JSON"

	exit 1
}

function _release_pr_lock {
	rmdir ${pr_dir}/.lock 2> /dev/null || true

	pr_locked=false
}

function _retry {
	local attempts=${1}

	shift

	local index

	for ((index = 0; index < attempts; index++))
	do
		if "$@"
		then
			return 0
		fi

		sleep $((5 * (index + 1)))
	done

	return 1
}

function _review_in_sandbox {
	timeout \
		--kill-after=10s ${_REVIEW_TIMEOUT_MINUTES}m \
		\
		bwrap \
			--as-pid-1 \
			--bind /home/me/.ai_sandbox/home /home/me \
			--chdir /tmp \
			--clearenv \
			--dev /dev \
			--die-with-parent \
			--hostname sandbox \
			--proc /proc \
			--ro-bind "$(pwd)/STYLE.md" /review/STYLE.md \
			--ro-bind "$(pwd)/rules" /review/rules \
			--ro-bind "$(pwd)/sandbox-bin" /review/sandbox-bin \
			--ro-bind /home/me/dev/projects/liferay-portal /review/liferay-portal \
			--ro-bind "$(pwd)/${pr_dir}/pr.diff" /review/pr.diff \
			--ro-bind /etc /etc \
			--ro-bind /usr /usr \
			--setenv HOME /home/me \
			--setenv LANG en_US.UTF-8 \
			--setenv PATH /review/sandbox-bin:/home/me/.local/bin:/home/me/.npm-global/bin:/usr/bin:/bin \
			--setenv TERM xterm-256color \
			--setenv USER me \
			--symlink usr/bin /bin \
			--symlink usr/lib /lib \
			--symlink usr/lib64 /lib64 \
			--symlink usr/sbin /sbin \
			--tmpfs /run \
			--tmpfs /tmp \
			--ro-bind $(readlink --canonicalize /etc/resolv.conf) $(readlink --canonicalize /etc/resolv.conf) \
			--unshare-cgroup \
			--unshare-ipc \
			--unshare-pid \
			--unshare-uts \
			"$@"
}

function _salvage_chance {
	grep --extended-regexp --only-matching "chance[*\"]*[[:space:]]*:[[:space:]]*[0-9]+" | tail --lines=1 | grep --extended-regexp --only-matching "[0-9]+$"
}

function _salvage_violations {
	local text=$(cat)

	#
	# A reply that fails to parse often keeps the model's analysis as readable
	# prose above the JSON verdict, which is more useful than the mangled verdict
	#

	if printf "%s" "${text}" | grep --extended-regexp --quiet "^[[:space:]]*\{.*\"chance\""
	then
		local prose=$(printf "%s" "${text}" | sed --regexp-extended "/^[[:space:]]*\{.*\"chance\"/,\$d")

		if [[ ! -z ${prose//[[:space:]]/} ]]
		then
			printf "%s\n" "${prose}"

			return
		fi
	fi

	local region=$(printf "%s" "${text}" | grep --extended-regexp --only-matching "\"violations\"[[:space:]]*:[[:space:]]*\[.*\]" | tail --lines=1)

	if [[ ! -z ${region} ]]
	then
		region=${region#*\[}

		echo "${region%\]}"

		return
	fi

	printf "%s" "${text}" | sed --regexp-extended --quiet "/[*\"]*violations[*\"]*[[:space:]]*:/,\$p" | tail --lines=+2 | sed "/./,\$!d"
}

function _update_points {
	local delta=${1}
	local usernames=${2}

	for username in ${usernames}
	do
		local current_points=$(grep "^${username}=" points.properties 2> /dev/null | cut --delimiter== --fields=2)

		if [[ -z ${current_points} ]]
		then
			current_points=80
		fi

		local new_points=$((current_points + delta))

		if [ ${new_points} -lt 0 ]
		then
			new_points=0
		elif [ ${new_points} -gt 100 ]
		then
			new_points=100
		fi

		if grep --quiet "^${username}=" points.properties 2> /dev/null
		then
			sed --in-place "s/^${username}=.*/${username}=${new_points}/" points.properties
		else
			echo "${username}=${new_points}" >> points.properties
		fi
	done

	sort --output points.properties points.properties

	truncate --size=-1 points.properties
}

function _write_model_json_file {
	local attempt
	local backoffs=(30 150 300)
	local model=${1}
	local raw
	local seconds=$(date +%s)

	local prompt='Review the PR diff thoroughly. For any naming, ordering, or convention question, run `git grep --cached <pattern>` against /review/liferay-portal before deciding — never short circuit by returning empty after only reading the diff. Always pass --cached: plain `git grep` hangs the VM (a wrapper auto rewrites it, but pass --cached yourself).

For any claim about where a line sits in the diff (between X and Y, after X, before Y, at line N), name X and Y exactly as they appear on the lines immediately above and below the `+` line in the diff. Re-read the diff before writing the claim; do not name neighbors from memory or paraphrase. Position claims that misname the neighbors are hallucinations — the alphabetical reasoning can be right while the position is fabricated, and that combination reads as well supported when it is not.

Flag every rule violation you find. When confidence in a flag is partial, still include it and append `verify against <source>` so the human can confirm — do not drop the catch.

Generated files are the one exception where you do drop the catch. A file whose contents include an `@generated` marker — Service Builder output such as model, persistence, and `*PersistenceTest` classes — is machine generated and never edited by hand, so it is out of scope: never flag a violation inside it, not even with a `verify against` note.

When a violation is a mechanical formatting or layout suggestion that SourceFormatter also governs (collapsing or expanding a boolean return or a ternary, method chaining, line wrapping, whitespace, blank lines, import order, or the ordering of members, parameters, or declarations), append the sentence "SourceFormatter is authoritative: if applying this suggestion fails SourceFormatter, ignore it." to that violation description. Do not add this sentence to correctness, naming, test, or prose violations.

Output ONLY valid JSON, with no Markdown code fence and no surrounding prose: {"chance": <0-100>, "violations": ["one description per violation"]} where chance is your confidence that Brian Chan would close this PR for these violations. Do not use double quote characters inside a violation string; quote code, rule names, and phrases with backticks or single quotes instead. Make sure the result parses as JSON: escape every special character so the object is valid. Your entire reply must be the raw JSON object and nothing else: no introductory sentence, and no Markdown such as **bold**, headings, horizontal rules, or numbered lists.'

	for ((attempt = 0; attempt <= 3; attempt++))
	do
		raw=$(_invoke_model ${model} "${prompt}")

		if ! _is_transient_error "${raw}" || [[ ${attempt} -eq 3 ]]
		then
			break
		fi

		echo "${model} hit a transient API error; retry $((attempt + 1)) of 3 in ${backoffs[attempt]}s." >&2

		sleep ${backoffs[attempt]}
	done

	echo "${raw}" > "${pr_dir}/${model}.raw"

	local input_tokens
	local output_tokens
	local response

	if [ ${model} = sonnet-4.6 ]
	then
		input_tokens=$(echo "${raw}" | jq --raw-output "(.usage.input_tokens // 0) + (.usage.cache_creation_input_tokens // 0)" 2> /dev/null || echo 0)
		input_tokens=${input_tokens:-0}
		output_tokens=$(echo "${raw}" | jq --raw-output ".usage.output_tokens // 0" 2> /dev/null || echo 0)
		output_tokens=${output_tokens:-0}
		response=$(echo "${raw}" | jq --raw-output ".result // empty" 2> /dev/null || true)
	else
		input_tokens=$(echo "${raw}" | jq --raw-output --slurp "[.[] | select(.type == \"step_finish\") | (.part.tokens.input // 0) + (.part.tokens.cache.write // 0)] | add // 0" 2> /dev/null || echo 0)
		input_tokens=${input_tokens:-0}
		output_tokens=$(echo "${raw}" | jq --raw-output --slurp "[.[] | select(.type == \"step_finish\") | (.part.tokens.output // 0) + (.part.tokens.reasoning // 0)] | add // 0" 2> /dev/null || echo 0)
		output_tokens=${output_tokens:-0}
		response=$(echo "${raw}" | jq --raw-output --slurp "map(select(.type == \"text\")) | last | .part.text // empty" 2> /dev/null || true)
	fi

	local model_json=$(echo "${response}" | _extract_last_json_block)

	if model_json=$(echo "${model_json}" | jq \
		--argjson input_tokens "${input_tokens}" \
		--argjson output_tokens "${output_tokens}" \
		--argjson seconds "$(($(date +%s) - seconds))" \
		--slurp "(.[-1] // empty) | {chance: (.chance // 0), input_tokens: \$input_tokens, output_tokens: \$output_tokens, seconds: \$seconds, violations: (.violations // [])}" 2> /dev/null) && [[ ! -z ${model_json} ]]
	then
		echo "${model_json}" > "${pr_dir}/${model}.json"
	else
		local chance=$(echo "${response}" | _salvage_chance)

		if [[ ! -z ${chance} ]]
		then
			local violations=$(echo "${response}" | _salvage_violations)

			if [[ -z ${violations} ]]
			then
				violations="No violations were recovered."
			fi

			echo "${model} salvaged chance ${chance} from a malformed reply after $(($(date +%s) - seconds))s." >&2

			jq \
				--arg violations "(recovered from a malformed reply) ${violations}" \
				--argjson chance "${chance}" \
				--argjson input_tokens "${input_tokens}" \
				--argjson output_tokens "${output_tokens}" \
				--argjson seconds "$(($(date +%s) - seconds))" \
				--null-input \
				'{chance: $chance, input_tokens: $input_tokens, output_tokens: $output_tokens, salvaged: true, seconds: $seconds, violations: [$violations]}' > "${pr_dir}/${model}.json"
		else
			local failure_type=$(_classify_failure "${raw}" "${response}")
			local transient=$(_is_transient_error "${raw}" && echo true || echo false)

			echo "${model} failed (${failure_type}) after $(($(date +%s) - seconds))s: $(echo "${response}" | tr "\n" " " | head --bytes=300)" >&2

			jq \
				--arg error "${response}" \
				--arg failure_type "${failure_type}" \
				--argjson input_tokens "${input_tokens}" \
				--argjson output_tokens "${output_tokens}" \
				--argjson seconds "$(($(date +%s) - seconds))" \
				--argjson transient "${transient}" \
				--null-input \
				"{chance: 0, error: \$error, failure_type: \$failure_type, input_tokens: \$input_tokens, output_tokens: \$output_tokens, seconds: \$seconds, transient: \$transient, violations: []}" > "${pr_dir}/${model}.json"
		fi
	fi
}

_IGNORED_FILENAMES="CHANGELOG.md package-lock.json package.json"
_IGNORED_PATTERNS="(^|/)Language_.*[.]properties$"
_IGNORED_SUFFIXES="css js jsx lock lockfile macro path scss snap testcase ts tsx"
#_MODELS=(deepseek-v4-flash deepseek-v4-pro glm-5 mimo-v2-5 minimax-m2-7)
_MODELS=(sonnet-4.6)
_NAME_ONLY_SUFFIXES="bmp gif ico jpeg jpg png svg webp"
_REPO=brianchandotcom/liferay-portal
_REVIEW_TIMEOUT_MINUTES=40

if [[ ${BASH_SOURCE[0]} == "${0}" ]]
then
	main "${@}"
fi