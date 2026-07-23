#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function main {
	local deploy_file="${LIFERAY_LICENSE_DEPLOY_FILE:-/liferay-home/deploy/license.xml}"
	local interval="${LIFERAY_LICENSE_SYNC_INTERVAL:-60}"
	local marker_file="${LIFERAY_LICENSE_MARKER_FILE:-/liferay-home/.liferay-license-sync.sha}"
	local secret_file="${LIFERAY_LICENSE_SECRET_FILE:-/secret/license.xml}"

	_log_json "Watching \"${secret_file}\" every ${interval}s. Syncing to \"${deploy_file}\" on content change."

	while true
	do
		_sync_license "${deploy_file}" "${marker_file}" "${secret_file}"

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

function _sync_license {
	local deploy_file=${1}
	local marker_file=${2}
	local secret_file=${3}

	if [ ! -f "${secret_file}" ]
	then
		return 0
	fi

	local current_hash

	current_hash=$(sha256sum "${secret_file}" | awk '{print $1}')

	local last_hash=""

	if [ -f "${marker_file}" ]
	then
		last_hash=$(cat "${marker_file}")
	fi

	if [ "${current_hash}" = "${last_hash}" ]
	then
		return 0
	fi

	mkdir --parents "$(dirname "${deploy_file}")"

	cp "${secret_file}" "${deploy_file}"

	echo "${current_hash}" > "${marker_file}"

	_log_json "License content was changed. Copied to \"${deploy_file}\"."
}

main