#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

if ((BASH_VERSINFO[0] < 4)) || ((BASH_VERSINFO[0] == 4 && BASH_VERSINFO[1] < 4))
then
	echo "This workspace's scripts require bash 4.4 or newer (found ${BASH_VERSION})." >&2
	echo "On macOS: brew install bash, then make sure Homebrew's bin directory precedes /bin in PATH." >&2

	exit 1
fi

function docker_compose {
	local dir

	dir="$(dirname "${BASH_SOURCE[0]}")/.."

	local compose_files

	compose_files=(--file "${dir}/docker-compose.yaml")

	local overrides=${LIFERAY_COMPOSE_OVERRIDES:-}

	local override
	local dash_override_file
	local dot_override_file

	# Dotted files (docker-compose.<name>.yaml) are copied from the sample
	# workspace. Hyphenated files (docker-compose-<name>.yaml) are specific to
	# a workspace and are added last so they take precedence.

	for override in ${overrides//,/ }
	do
		dot_override_file="${dir}/docker-compose.${override}.yaml"

		if [[ -f "${dot_override_file}" ]]
		then
			compose_files+=(--file "${dot_override_file}")
		fi

		dash_override_file="${dir}/docker-compose-${override}.yaml"

		if [[ -f "${dash_override_file}" ]]
		then
			compose_files+=(--file "${dash_override_file}")
		fi

		if [[ ! -f "${dot_override_file}" ]] && [[ ! -f "${dash_override_file}" ]]
		then
			_die "No compose override file found for \"${override}\"."
		fi
	done

	if [[ -f "${dir}/docker-compose-env.yaml" ]]
	then
		compose_files+=(--file "${dir}/docker-compose-env.yaml")
	fi

	docker compose "${compose_files[@]}" "${@}"
}

function get_gradle_property {
	local key=${1}

	local value

	value=$(_read_property "${key}" "$(dirname "${BASH_SOURCE[0]}")/../gradle-local.properties")

	if [[ -z ${value} ]]
	then
		value=$(_read_property "${key}" "$(dirname "${BASH_SOURCE[0]}")/../gradle.properties")
	fi

	if [[ -z ${value} ]]
	then
		echo "Property \"${key}\" was not found." >&2

		return 1
	fi

	echo "${value}"
}

function _die {
	_print_error "${*}"

	exit 1
}

function _print_error {
	echo "${*}" >&2
}

function _read_property {
	local key=${1}
	local file=${2}

	if [[ -f ${file} ]]
	then
		grep "^${key}=" "${file}" | \
			cut --delimiter "=" --fields 2- | \
			tr --delete "[:space:]"
	fi
}