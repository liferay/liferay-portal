#!/usr/bin/env bash

function main {
	set -o pipefail

	if ! git rev-parse --quiet --verify "${1}^{commit}" > /dev/null
	then
		_exit_with_error "Unable to resolve the merge base \"${1}\""
	fi

	local regex

	regex=$( \
		sed -e "/^## Match$/{n;n;p;}" -n "${2}" | \
			sed -e "s/^.//" -e "s/.$//")

	if [[ -z ${regex} ]]
	then
		_exit_with_error "Unable to find a \"## Match\" section in ${2}"
	fi

	local exclude=$(echo "${regex}" | sed -e "s/^.* &! //p" -n)

	if [[ -z ${exclude} ]]
	then
		exclude="^$"
	fi

	local folder_name

	folder_name=$(basename "$(dirname "${2}")")

	if [[ $(type -t "_get_${folder_name}_paths") != function ]]
	then
		_exit_with_error "Unable to select paths for a validation in the ${folder_name} folder"
	fi

	git diff --name-only --no-renames "${1}...HEAD" | \
		"_get_${folder_name}_paths" "${2}" "${3}" | \
		grep --extended-regexp -- "$(echo "${regex}" | sed -e "s/ &! .*$//")" | \
		grep --extended-regexp --invert-match -- "${exclude}" | \
		sed -e "s#^workspaces/${3}/##"
}

function _exit_with_error {
	echo "${*}" >&2

	exit 1
}

function _get_branch_paths {
	cat
}

function _get_portal_paths {
	grep --extended-regexp --invert-match "^workspaces/[^/]+-workspace/"
}

function _get_workspaces_paths {
	if [[ -z ${2} ]]
	then
		_exit_with_error "Unable to select paths for ${1} without a workspace name"
	fi

	local owned_paths_regex="^workspaces/${2}/"
	local refresh_script

	refresh_script="$(git rev-parse --show-toplevel)/workspaces/refresh_other_workspaces.sh"

	if [[ ${2} != liferay-sample-workspace ]] && [[ -f ${refresh_script} ]]
	then
		local names

		names=$( \
			sed \
				-e "s/^[[:space:]]*--exclude[[:space:]]\{1,\}\([^[:space:]\\\\]*\).*/\1/p" \
				-n \
				"${refresh_script}" | \
			sed -e "s/\./\\\\./g" -e "s/\*/[^\/]*/g" | \
			paste -d "|" -s -)

		if [[ -z ${names} ]]
		then
			_exit_with_error "Unable to find an \"--exclude\" pattern in workspaces/refresh_other_workspaces.sh"
		fi

		owned_paths_regex="^workspaces/${2}/(.*/)?(${names})(/|$)"
	fi

	if [[ $(basename "${1}") == generated-file.md ]]
	then
		grep "^workspaces/${2}/" | grep --extended-regexp --invert-match "${owned_paths_regex}"
	else
		grep --extended-regexp "${owned_paths_regex}"
	fi
}

main "${@}"