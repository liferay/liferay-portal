#!/bin/sh

set -o errexit
set -o nounset
set -o pipefail

function delete_search_api {
	echo "Deleting ${1}"

	local status_code

	status_code=$(
		curl \
			--output /dev/stderr \
			--request DELETE \
			--show-error \
			--silent \
			--user "${SEARCH_USERNAME}:${SEARCH_PASSWORD}" \
			--write-out "%{http_code}" \
			"${SEARCH_URL}/${1}")

	echo ""

	if [[ ${status_code} != 200 ]] && [[ ${status_code} != 404 ]]
	then
		echo "Unable to delete ${1}. The status code was ${status_code}."

		return 1
	fi
}

function main {
	if [[ ${SNAPSHOT_ENABLED} == true ]]
	then
		wait_for_snapshot_config

		put_search_api "_snapshot/snapshots" "$(cat /etc/search-snapshots/repository)"

		put_search_api "_slm/policy/daily-snapshots" "$(cat /etc/search-snapshots/policy)"
	else
		delete_search_api "_slm/policy/daily-snapshots"

		delete_search_api "_snapshot/snapshots"
	fi
}

function put_search_api {
	echo "Updating ${1}"

	curl \
		--data "${2}" \
		--fail-with-body \
		--header "Content-Type: application/json" \
		--request PUT \
		--show-error \
		--silent \
		--user "${SEARCH_USERNAME}:${SEARCH_PASSWORD}" \
		"${SEARCH_URL}/${1}"

	echo ""
}

function wait_for_snapshot_config {
	for _ in $(seq 60)
	do
		if [[ "$(cat /etc/search-snapshots/checksum 2>/dev/null)" == "${SNAPSHOT_CHECKSUM}" ]]
		then
			return 0
		fi

		sleep 10
	done

	echo "The search-snapshots ConfigMap does not match the current snapshot settings"

	return 1
}

main