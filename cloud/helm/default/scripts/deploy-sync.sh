#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function main {
	local dest="${LIFERAY_SYNC_DEST:?LIFERAY_SYNC_DEST must be set}"
	local interval="${LIFERAY_SYNC_INTERVAL:-600}"
	local marker="${LIFERAY_SYNC_MARKER:?LIFERAY_SYNC_MARKER must be set}"
	local pattern="${LIFERAY_SYNC_PATTERN:-*}"
	local source="${LIFERAY_SYNC_SOURCE:?LIFERAY_SYNC_SOURCE must be set}"

	_log_json "Watching \"${source}\" every ${interval}s. Syncing to \"${dest}\" on content change."

	while true
	do
		_sync "${dest}" "${marker}" "${pattern}" "${source}"

		sleep "${interval}"
	done
}

function _log_json {
	local escaped_message

	escaped_message=$(echo "${1}" | sed --expression 's/"/\\"/g')

	local script_name

	script_name=$(basename "${0}")

	local severity="${2:-INFO}"

	local timestamp

	timestamp=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

	printf '{"message": "%s", "script": "%s", "severity": "%s", "timestamp": "%s"}\n' "${escaped_message}" "${script_name}" "${severity}" "${timestamp}"
}

function _sync {
	local dest=${1}
	local marker=${2}
	local pattern=${3}
	local source=${4}

	if [ -d "${source}" ]
	then
		_sync_directory "${dest}" "${marker}" "${pattern}" "${source}"
	elif [ -f "${source}" ]
	then
		_sync_file "${dest}" "${marker}" "${source}"
	fi
}

function _sync_directory {
	local dest=${1}
	local marker=${2}
	local pattern=${3}
	local source=${4}

	mkdir --parents "${dest}" "${marker}"

	local source_file

	for source_file in "${source}"/${pattern}
	do
		if [ ! -f "${source_file}" ]
		then
			continue
		fi

		local name

		name=$(basename "${source_file}")

		local current_hash

		current_hash=$(sha256sum "${source_file}" | awk '{print $1}')

		local marker_file="${marker}/${name}.sha"

		local last_hash=""

		if [ -f "${marker_file}" ]
		then
			last_hash=$(cat "${marker_file}")
		fi

		if [ "${current_hash}" = "${last_hash}" ]
		then
			continue
		fi

		cp "${source_file}" "${dest}/${name}"

		echo "${current_hash}" > "${marker_file}"

		_log_json "Content of \"${source_file}\" was changed. Copied to \"${dest}/${name}\"."
	done
}

function _sync_file {
	local dest=${1}
	local marker=${2}
	local source=${3}

	local current_hash

	current_hash=$(sha256sum "${source}" | awk '{print $1}')

	local last_hash=""

	if [ -f "${marker}" ]
	then
		last_hash=$(cat "${marker}")
	fi

	if [ "${current_hash}" = "${last_hash}" ]
	then
		return 0
	fi

	mkdir --parents "$(dirname "${dest}")"

	cp "${source}" "${dest}"

	echo "${current_hash}" > "${marker}"

	_log_json "Content of \"${source}\" was changed. Copied to \"${dest}\"."
}

main