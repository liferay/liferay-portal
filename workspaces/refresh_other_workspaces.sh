#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${0}")" || exit

function main {
	local workspace_dir

	for workspace_dir in "./"*
	do
		if [ "${workspace_dir}" = "./liferay-sample-workspace" ] ||
		   [ ! -d "${workspace_dir}" ]
		then
			continue
		fi

		rsync \
			-a --delete \
			--exclude "Jenkinsfile" \
			--exclude "README.md" \
			--exclude "build.gradle" \
			--exclude "client-extensions" \
			--exclude "gradle-local.properties" \
			--exclude "language" \
			--exclude "modules" \
			--exclude "node_modules" \
			--exclude "node_modules_cache" \
			--exclude "package.json" \
			--exclude "poshi" \
			--exclude "quickstart" \
			--exclude "test.properties" \
			--exclude "themes" \
			--exclude "yarn.lock" \
			liferay-sample-workspace/ "${workspace_dir}"
	done
}

main "${@}"