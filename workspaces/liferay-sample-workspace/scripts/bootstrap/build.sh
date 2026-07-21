#!/bin/bash

cd "$(dirname "${BASH_SOURCE[0]}")"

source ../_common.sh

function main {
	local product

	product=$(get_gradle_property liferay.workspace.product)

	local version_tag=$(echo "${product}" | sed --expression "s/^dxp-//")

	if [ -f pre-build.sh ]
	then
		source pre-build.sh
	fi

	../../gradlew --project-dir ../.. clean

	./extract_hotfix.sh
	./extract_license.sh

	echo "Building Docker image."
	../../gradlew --project-dir ../.. buildDockerImage

	local workspace_name

	workspace_name=$(basename "$(dirname "$(dirname "${PWD}")")")

	echo "Tagging ${workspace_name}-liferay:${version_tag} as liferay:local."
	docker tag "${workspace_name}-liferay:${version_tag}" "liferay:local"

	if [ -f post-build.sh ]
	then
		source post-build.sh
	fi
}

main "${@}"