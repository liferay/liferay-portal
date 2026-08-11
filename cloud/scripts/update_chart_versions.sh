#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

_SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

_ROOT_CLOUD_DIR=$(cd "${_SCRIPTS_DIR}/.." && pwd)

function main {
	local chart_dir

	if [ ${#} -eq 0 ]
	then
		for chart_dir in "${_ROOT_CLOUD_DIR}"/helm/*/
		do
			if [[ -f ${chart_dir}Chart.yaml ]]
			then
				_check_chart_yaml "${chart_dir%/}"
			fi
		done

		return
	fi

	for chart_dir in "${@}"
	do
		_check_chart_yaml "${chart_dir}"
	done
}

function _bump_chart_yaml_version {
	local helm_chart_yaml="${1}"

	local current_version

	current_version=$(yq '.version' "${helm_chart_yaml}")

	local new_version

	new_version=$(echo "${current_version}" | awk -F"." -v OFS="." '{$NF += 1; print}')

	yq --inplace ".version = \"${new_version}\"" "${helm_chart_yaml}"

	echo "${new_version}"
}

function _check_chart_yaml {
	local helm_dir="${1}"

	local helm_chart_yaml="${helm_dir}/Chart.yaml"

	if [[ ! -f ${helm_chart_yaml} ]]
	then
		echo "The chart file ${helm_chart_yaml} does not exist." >&2

		exit 1
	fi

	local git_blame_sha

	git_blame_sha=$(_git_blame_sha "^version: .*$" "${helm_chart_yaml}")

	if [[ -z ${git_blame_sha} ]] || ! git rev-parse --quiet --verify "${git_blame_sha}^{commit}" > /dev/null
	then
		echo "The blame boundary commit for ${helm_chart_yaml} cannot be resolved." >&2

		return
	fi

	local commit_count

	commit_count=$(git rev-list --count "${git_blame_sha}..HEAD" -- "${helm_dir}")

	if [ ${commit_count} -gt 0 ]
	then
		git --no-pager log --date=short --format="%h %ad %an %s" "${git_blame_sha}..HEAD" -- "${helm_dir}"

		echo "The version in ${helm_chart_yaml} is outdated." >&2
		echo "" >&2

		local new_version

		new_version=$(_bump_chart_yaml_version "${helm_chart_yaml}")

		local helm_chart_name

		helm_chart_name=$(grep "^name: " "${helm_chart_yaml}" | awk '{print $2}')

		_update_chart_dependency_version "${helm_chart_name}" "${helm_chart_yaml}" "${new_version}"
	fi
}

function _update_chart_dependency_version {
	local chart_name="${1}"
	local current_chart_yaml="${2}"
	local new_version="${3}"

	local subchart_dir

	subchart_dir=$(cd "$(dirname "${current_chart_yaml}")" && pwd)

	find "${_ROOT_CLOUD_DIR}" -name "Chart.yaml" -type f | while read -r chart_yaml_file;
	do
		if [[ ${chart_yaml_file} == ${current_chart_yaml} ]]
		then
			continue
		fi

		local dep_repository

		dep_repository=$(yq ".dependencies[]? | select(.name == \"${chart_name}\" and (.repository | test(\"^file://\"))) | .repository" "${chart_yaml_file}" | head -n 1)

		if [[ -z ${dep_repository} ]]
		then
			continue
		fi

		local parent_dir

		parent_dir=$(cd "$(dirname "${chart_yaml_file}")" && pwd)

		local resolved_dir

		resolved_dir=$(cd "${parent_dir}/${dep_repository#file://}" 2>/dev/null && pwd)

		if [[ ${resolved_dir} != ${subchart_dir} ]]
		then
			continue
		fi

		sed --in-place "/name: ${chart_name}$/,/version: / s/version: .*/version: ${new_version}/" "${chart_yaml_file}"
	done
}

function _git_blame_line {
	local pattern="${1}"
	local git_path="${2}"

	local blame_line

	blame_line=$(grep --extended-regexp --line-number "${pattern}" "${git_path}" | cut --delimiter=':' --fields=1)

	echo "${blame_line}"
}

function _git_blame_sha {
	local pattern="${1}"
	local git_path="${2}"

	local git_blame_line

	git_blame_line=$(_git_blame_line "${pattern}" "${git_path}")

	local target_sha

	target_sha=$(git blame -L "${git_blame_line}","${git_blame_line}" -- "${git_path}" | cut --delimiter=' ' --fields=1)

	echo "${target_sha#^}"
}

main ${1+"$@"}