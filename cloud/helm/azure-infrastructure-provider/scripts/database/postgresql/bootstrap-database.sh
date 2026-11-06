#!/bin/sh

set -o errexit
set -o nounset
set -o pipefail

function main {
	PGPASSWORD=$(cat /entra/token)

	export PGPASSWORD

	_wait_for_server

	psql \
		--dbname "${DATABASE_NAME}" \
		--file "$(dirname "${0}")/bootstrap-database.sql" \
		--set ON_ERROR_STOP=1 \
		--set bootstrap_username="${PGUSER}" \
		--set database_name="${DATABASE_NAME}" \
		--set liferay_database_password="${LIFERAY_PASSWORD}" \
		--set liferay_database_username="${LIFERAY_DATABASE_USERNAME}"

	_verify_login
}

function _verify_login {
	local timeout=$(($(date +%s) + 600))

	until PGCONNECT_TIMEOUT=10 PGPASSWORD="${LIFERAY_PASSWORD}" psql \
		--command "SELECT 1" \
		--dbname "${DATABASE_NAME}" \
		--username "${LIFERAY_DATABASE_USERNAME}" >/dev/null 2>&1
	do
		if [ $(date +%s) -ge ${timeout} ]
		then
			echo "${LIFERAY_DATABASE_USERNAME} could not be authenticated" >&2

			exit 1
		fi

		sleep 2
	done

	echo "${LIFERAY_DATABASE_USERNAME} authenticated successfully"
}

function _wait_for_server {
	local timeout=$(($(date +%s) + 600))

	until pg_isready --host "${PGHOST}" --port "${PGPORT}" --username "${PGUSER}"
	do
		if [ $(date +%s) -ge ${timeout} ]
		then
			echo "${PGHOST} did not become ready" >&2

			exit 1
		fi

		sleep 2
	done
}

main