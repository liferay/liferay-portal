#!/bin/bash

cd "$(dirname "${BASH_SOURCE[0]}")"

source ../_common.sh

function main {
	echo "Deploying client extensions."

	../../gradlew --project-dir ../.. deploy -Ddeploy.docker.container.id="$(docker ps --filter name=liferay --quiet)"
}

main "${@}"