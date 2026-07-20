#!/bin/bash

cd "$(dirname "${BASH_SOURCE[0]}")"

source _common.sh

function main {
	local hotfix_url=${1:-}

	if [[ -z ${hotfix_url} ]]
	then
		hotfix_url=$(get_gradle_property liferay.workspace.hotfix.url || true)
	fi

	if [[ -z ${hotfix_url} ]]
	then
		exit 0
	fi

	mkdir --parents ../build/docker/patching

	cd ../build/docker/patching

	local hotfix_file

	hotfix_file=$(basename "${hotfix_url%%\?*}")

	if [[ -f ${hotfix_file} ]]
	then
		echo "Hotfix is already present at ${hotfix_file}."

		exit 0
	fi

	echo "Downloading ${hotfix_file}."

	curl --fail --location --output "${hotfix_file}" "${hotfix_url}"

	echo "Downloaded ${hotfix_file}."
}

main "${@}"