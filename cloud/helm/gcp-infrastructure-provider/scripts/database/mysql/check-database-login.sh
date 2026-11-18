#!/bin/sh

set -o errexit
set -o nounset
set -o pipefail

function main {
	local timeout

	timeout=$(($(date +%s) + 600))

	until mysql \
		--connect-timeout 10 \
		--execute "SELECT 1" \
		--host 127.0.0.1 \
		--port "${DATABASE_PORT}" \
		--user "${DATABASE_USERNAME}" \
		"${DATABASE_NAME}" > /dev/null 2>&1
	do
		if [ $(date +%s) -ge ${timeout} ]
		then
			echo "${DATABASE_USERNAME} could not be authenticated" >&2

			exit 1
		fi

		sleep 2
	done
}

main