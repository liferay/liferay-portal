#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

function docker_compose {
	local compose_files

	compose_files=(--file "$(dirname "${BASH_SOURCE[0]}")/../docker-compose.yaml")

	if [ -f "$(dirname "${BASH_SOURCE[0]}")/../docker-compose-env.yaml" ]
	then
		compose_files+=(--file "$(dirname "${BASH_SOURCE[0]}")/../docker-compose-env.yaml")
	fi

	docker compose "${compose_files[@]}" "${@}"
}

function get_gradle_property {
	local key=${1}

	local value

	value=$(_read_property "${key}" "$(dirname "${BASH_SOURCE[0]}")/../gradle-local.properties")

	if [ -z "${value}" ]
	then
		value=$(_read_property "${key}" "$(dirname "${BASH_SOURCE[0]}")/../gradle.properties")
	fi

	if [ -z "${value}" ]
	then
		echo "Property \"${key}\" was not found." >&2

		return 1
	fi

	echo "${value}"
}

function _read_property {
	local key=${1}
	local file=${2}

	if [ -f "${file}" ]
	then
		grep "^${key}=" "${file}" | \
			cut --delimiter "=" --fields 2- | \
			tr --delete "[:space:]"
	fi
}