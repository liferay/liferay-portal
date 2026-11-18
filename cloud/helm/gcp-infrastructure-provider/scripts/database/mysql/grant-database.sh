#!/bin/sh

set -o errexit
set -o nounset
set -o pipefail

function main {
	local scripts_dir

	scripts_dir=$(dirname "${0}")

	sh "${scripts_dir}/check-database-login.sh"

	mysql \
		--host 127.0.0.1 \
		--init-command "SET @database_name = '${DATABASE_NAME}', @liferay_database_username = '${LIFERAY_DATABASE_USERNAME}'" \
		--port "${DATABASE_PORT}" \
		--user "${DATABASE_USERNAME}" < "${scripts_dir}/grant-database.sql"
}

main