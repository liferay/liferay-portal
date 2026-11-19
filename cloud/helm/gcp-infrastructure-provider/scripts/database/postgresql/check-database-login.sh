#!/bin/sh

set -o errexit
set -o nounset
set -o pipefail

function main {
	local timeout

	timeout=$(($(date +%s) + 600))

	until PGCONNECT_TIMEOUT=10 PGPASSWORD=iam psql \
		--command "SELECT 1" \
		--dbname "${DATABASE_NAME}" \
		--host 127.0.0.1 \
		--username "${DATABASE_USERNAME}" > /dev/null 2>&1
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