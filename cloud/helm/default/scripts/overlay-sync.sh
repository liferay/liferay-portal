#!/bin/sh

set -o errexit
set -o nounset

function main {
	if [ "${#}" -ne 3 ]
	then
		_log_json "Usage: ${0} <provider-type> <from-path> <into-path>." "ERROR"

		exit 1
	fi

	local bucket_name="${LIFERAY_OVERLAY_BUCKET_NAME:-}"
	local from_path="${2}"
	local into_path="${3}"
	local provider_type="${1}"

	if [ -z "${bucket_name}" ]
	then
		_log_json "Overlay bucket does not exist (checked LIFERAY_OVERLAY_BUCKET_NAME). Skipping sync." "ERROR"

		exit 1
	fi

	local include_pattern=""

	if echo "${from_path}" | grep -q "\*"
	then
		include_pattern="${from_path##*/}"

		from_path="${from_path%/*}"
	fi

	local source_uri=":${provider_type}:${bucket_name}/${from_path}"
	local target_path="/temp/${into_path}"

	_log_json "Copying from \"${source_uri}\" to \"${target_path}\"."

	if [ -n "${include_pattern}" ]
	then
		rclone copy "${source_uri}" "${target_path}" --include "${include_pattern}" --log-level INFO --use-json-log
	else
		rclone copy "${source_uri}" "${target_path}" --log-level INFO --use-json-log
	fi

	_log_json "Copy completed successfully."
}

function _log_json {
	local escaped_message

	escaped_message=$(echo "${1}" | sed 's/"/\\"/g')

	local script_name

	script_name=$(basename "${0}")

	local severity="${2:-INFO}"
	local timestamp

	timestamp=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

	printf '{"message": "%s", "script": "%s", "severity": "%s", "timestamp": "%s"}\n' "${escaped_message}" "${script_name}" "${severity}" "${timestamp}"
}

main "${@}"