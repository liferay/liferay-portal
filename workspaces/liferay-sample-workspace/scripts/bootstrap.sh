#!/bin/bash

cd "$(dirname "${BASH_SOURCE[0]}")"

source _common.sh

function main {
	local reset="false"

	for arg in "${@}"
	do
		if [ "${arg}" == "--reset" ]
		then
			reset="true"
		fi
	done

	if [ "${reset}" == "true" ]
	then
		echo "Tearing down containers and volumes."
		docker_compose down --volumes
	fi

	./bootstrap/build.sh

	./bootstrap/start.sh
}

main "${@}"