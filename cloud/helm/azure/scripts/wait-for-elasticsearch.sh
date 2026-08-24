#!/bin/sh

set -o errexit
set -o nounset

function main {
	_log_json "Waiting for Elasticsearch health to be \"green\" or \"yellow\" at \"${SEARCH_URL}/_cluster/health\"."

	local protocol="http"

	if [ "${SEARCH_URL#https://}" != "${SEARCH_URL}" ]
	then
		protocol="https"
	fi

	local auth_header_value

	auth_header_value=$(printf '%s:%s' "${SEARCH_USERNAME}" "${SEARCH_PASSWORD}" | base64 | tr -d '\n')

	local health_url="${protocol}://${SEARCH_URL#*//}/_cluster/health"

	local wget_args=""

	if [ "${protocol}" = "https" ] && [ "${SEARCH_TLS_INSECURE:-false}" = "true" ]
	then
		wget_args="--no-check-certificate"
	fi

	until wget --header="Authorization: Basic ${auth_header_value}" ${wget_args} -qO- "${health_url}" | grep -qE "\"status\":\"(green|yellow)\""
	do
		_log_json "Waiting for Elasticsearch (current status: \"red\" or \"unreachable\")."

		sleep 5
	done

	_log_json "Elasticsearch is ready."
}

function _log_json {
	local escaped_message

	escaped_message=$(printf '%s' "${1}" | sed 's/\\/\\\\/g; s/"/\\"/g')

	local script_name

	script_name=$(basename "${0}")

	local severity="${2:-INFO}"

	local timestamp

	timestamp=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

	printf '{"message": "%s", "script": "%s", "severity": "%s", "timestamp": "%s"}\n' "${escaped_message}" "${script_name}" "${severity}" "${timestamp}"
}

main