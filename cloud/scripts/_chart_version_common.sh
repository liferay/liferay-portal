#!/usr/bin/env bash

_BUMPED_CHART_DIRS=()

_MAXIMUM_PASSES=16

_MODIFIED_CHART_DIRS=()

_PASSES=0

_SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

_ROOT_CLOUD_DIR=$(cd "${_SCRIPTS_DIR}/.." && pwd)

readonly _MAXIMUM_PASSES _ROOT_CLOUD_DIR _SCRIPTS_DIR

function bump_chart_version {
	local chart_dir="${1}"

	local helm_chart_yaml="${chart_dir}/Chart.yaml"

	local current_version

	current_version=$(yq '.version' "${helm_chart_yaml}")

	local new_version

	new_version=$(echo "${current_version}" | awk -F "." -v OFS="." '{$NF += 1; print}')

	sed \
		--in-place \
		--regexp-extended \
		--expression "s/^version: .*$/version: ${new_version}/" \
		"${helm_chart_yaml}"

	_BUMPED_CHART_DIRS+=("${chart_dir}")

	local helm_chart_name

	helm_chart_name=$(yq '.name' "${helm_chart_yaml}")

	echo "The version of ${helm_chart_name} was bumped from ${current_version} to ${new_version}." >&2
	echo "" >&2

	_update_chart_dependency_version "${helm_chart_name}" "${helm_chart_yaml}" "${new_version}"
}

function bump_modified_charts {
	count_pass

	local chart_dirs=("${_MODIFIED_CHART_DIRS[@]}")

	_MODIFIED_CHART_DIRS=()

	local chart_dir

	for chart_dir in "${chart_dirs[@]}"
	do
		if has_array_element "${chart_dir}" "${_BUMPED_CHART_DIRS[@]}"
		then
			continue
		fi

		bump_chart_version "${chart_dir}"
	done
}

function count_pass {
	_PASSES=$((_PASSES + 1))

	if [[ "${_PASSES}" -gt "${_MAXIMUM_PASSES}" ]]
	then
		echo "The versions were unable to converge after ${_MAXIMUM_PASSES} passes." >&2

		exit 1
	fi
}

function get_file_checksum {
	local file="${1}"

	md5sum "${file}" | awk '{print $1}'
}

function git_blame_line {
	local pattern="${1}"
	local git_path="${2}"

	local blame_line

	blame_line=$(grep --extended-regexp --line-number "${pattern}" "${git_path}" | cut --delimiter=':' --fields=1)

	echo "${blame_line}"
}

function git_blame_sha {
	local pattern="${1}"
	local git_path="${2}"

	local blame_line

	blame_line=$(git_blame_line "${pattern}" "${git_path}")

	local target_sha

	target_sha=$(git blame -L "${blame_line}","${blame_line}" -- "${git_path}" | cut --delimiter=' ' --fields=1)

	echo "${target_sha#^}"
}

function has_array_element {
	local element="${1}"

	shift

	local candidate

	for candidate in "${@}"
	do
		if [ "${candidate}" == "${element}" ]
		then
			return 0
		fi
	done

	return 1
}

function has_modified_charts {
	if [[ "${#_MODIFIED_CHART_DIRS[@]}" -eq 0 ]]
	then
		return 1
	fi

	return 0
}

function is_commit {
	local sha="${1}"

	if [ -z "${sha}" ]
	then
		return 1
	fi

	if ! git rev-parse --quiet --verify "${sha}^{commit}" > /dev/null
	then
		return 1
	fi

	return 0
}

function record_chart_file_update {
	local file="${1}"

	shift

	local previous_checksum

	previous_checksum=$(get_file_checksum "${file}")

	"${@}"

	if [ "${previous_checksum}" != "$(get_file_checksum "${file}")" ]
	then
		_record_modified_chart_dir "${file}"
	fi
}

function _record_modified_chart_dir {
	local file="${1}"

	local chart_dir

	chart_dir=$(cd "$(dirname "${file}")" && pwd)

	while [[ "${chart_dir}" == "${_ROOT_CLOUD_DIR}"/* ]]
	do
		if [ -f "${chart_dir}/Chart.yaml" ]
		then
			if ! has_array_element "${chart_dir}" "${_BUMPED_CHART_DIRS[@]}" "${_MODIFIED_CHART_DIRS[@]}"
			then
				_MODIFIED_CHART_DIRS+=("${chart_dir}")
			fi

			return
		fi

		chart_dir=$(dirname "${chart_dir}")
	done
}

function _update_chart_dependency_version {
	local chart_name="${1}"
	local current_chart_yaml="${2}"
	local new_version="${3}"

	local subchart_dir

	subchart_dir=$(cd "$(dirname "${current_chart_yaml}")" && pwd)

	local chart_yaml_file

	while read -r chart_yaml_file
	do
		if [ "${chart_yaml_file}" == "${current_chart_yaml}" ]
		then
			continue
		fi

		local dep_repository

		dep_repository=$(yq ".dependencies[]? | select(.name == \"${chart_name}\" and (.repository | test(\"^file://\"))) | .repository" "${chart_yaml_file}" | head --lines=1)

		if [ -z "${dep_repository}" ]
		then
			continue
		fi

		local parent_dir

		parent_dir=$(cd "$(dirname "${chart_yaml_file}")" && pwd)

		local resolved_dir

		resolved_dir=$(cd "${parent_dir}/${dep_repository#file://}" 2> /dev/null && pwd)

		if [ "${resolved_dir}" != "${subchart_dir}" ]
		then
			continue
		fi

		record_chart_file_update \
			"${chart_yaml_file}" \
			sed \
				--expression "/name: ${chart_name}\$/,/version: / s/version: .*/version: ${new_version}/" \
				--in-place \
				"${chart_yaml_file}"
	done < <(find "${_ROOT_CLOUD_DIR}" -name "Chart.yaml" -type f)
}