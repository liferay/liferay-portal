#!/bin/sh

set -o errexit
set -o nounset
set -o pipefail

function main {
	local scripts_dir

	scripts_dir=$(dirname "${0}")

	sh "${scripts_dir}/check-database-login.sh"

	psql \
		--dbname "${DATABASE_NAME}" \
		--file "${scripts_dir}/grant-database.sql" \
		--host 127.0.0.1 \
		--port "${DATABASE_PORT}" \
		--set ON_ERROR_STOP=1 \
		--set database_name="${DATABASE_NAME}" \
		--set liferay_database_username="${LIFERAY_DATABASE_USERNAME}" \
		--username "${DATABASE_USERNAME}"
}

main