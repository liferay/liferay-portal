#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

source "$(dirname "${BASH_SOURCE[0]}")/_chart_version_common.sh"

function main {
	local chart_dir

	if [[ "${#}" -eq 0 ]]
	then
		for chart_dir in "${_ROOT_CLOUD_DIR}"/helm/*/
		do
			if [ -f "${chart_dir}Chart.yaml" ]
			then
				_check_chart_yaml "${chart_dir%/}"
			fi
		done
	else
		for chart_dir in "${@}"
		do
			_check_chart_yaml "${chart_dir}"
		done
	fi

	while has_modified_charts
	do
		bump_modified_charts
	done
}

function _check_chart_yaml {
	local helm_dir="${1}"

	local helm_chart_yaml="${helm_dir}/Chart.yaml"

	if [ ! -f "${helm_chart_yaml}" ]
	then
		echo "The chart file ${helm_chart_yaml} does not exist." >&2

		exit 1
	fi

	local blame_sha

	blame_sha=$(git_blame_sha "^version: .*$" "${helm_chart_yaml}")

	if ! is_commit "${blame_sha}"
	then
		echo "The blame boundary commit for ${helm_chart_yaml} cannot be resolved." >&2

		return
	fi

	local commit_count

	commit_count=$(git rev-list --count "${blame_sha}..HEAD" -- "${helm_dir}")

	if [[ "${commit_count}" -gt 0 ]]
	then
		git --no-pager log --date=short --format="%h %ad %an %s" "${blame_sha}..HEAD" -- "${helm_dir}"

		echo "The version in ${helm_chart_yaml} is outdated." >&2
		echo "" >&2

		bump_chart_version "${helm_dir}"
	fi
}

main "${@}"