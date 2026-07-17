#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

function main {
	refresh_workspaces "${PWD}"
	refresh_workspaces "${PWD}/../../liferay-portal-master-private/workspaces"
}

function refresh_workspaces {
	local workspaces_dir=${1}

	if [[ ! -d ${workspaces_dir} ]]
	then
		return
	fi

	local workspace_dir

	for workspace_dir in "${workspaces_dir}/"*
	do
		if [[ ${workspace_dir} = "${PWD}/liferay-sample-workspace" ]] ||
		   [[ ! -d ${workspace_dir} ]]
		then
			continue
		fi

		rsync \
			--archive \
			--delete \
			--exclude Jenkinsfile \
			--exclude README.md \
			--exclude build.gradle \
			--exclude client-extensions \
			--exclude docker-compose-*.yaml \
			--exclude gradle-local.properties \
			--exclude gradle.properties \
			--exclude language \
			--exclude modules \
			--exclude node_modules \
			--exclude node_modules_cache \
			--exclude package.json \
			--exclude poshi \
			--exclude quickstart \
			--exclude test.properties \
			--exclude themes \
			--exclude yarn.lock \
			"${PWD}/liferay-sample-workspace/" "${workspace_dir}"
	done
}

main "${@}"