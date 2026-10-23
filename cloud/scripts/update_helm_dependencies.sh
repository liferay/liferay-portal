#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function main {
	_update_dependencies "${1}"
}

function _update_dependencies {
	local chart_dir="${1}"

	local repository

	for repository in $(yq '.dependencies[]? | select(.repository | test("^file://")) | .repository' "${chart_dir}/Chart.yaml")
	do
		_update_dependencies "${chart_dir}/${repository#file://}"
	done

	helm dependency update "${chart_dir}"
}

main "${@}"