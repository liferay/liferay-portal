#!/bin/sh

set -o errexit
set -o nounset
set -o pipefail

function main {
	_wait_for_server

	local liferay_password

	liferay_password=$(printf '%s' "${LIFERAY_PASSWORD}" | sed -e 's/\\/\\\\/g' -e "s/'/''/g")

	MYSQL_PWD="${ADMIN_PASSWORD}" mysql \
		--host "${MYSQL_HOST}" \
		--ssl-mode REQUIRED \
		--user "${DATABASE_ADMIN_USERNAME}" <<EOF
CREATE USER IF NOT EXISTS '${LIFERAY_DATABASE_USERNAME}'@'%' IDENTIFIED BY '${liferay_password}';

ALTER USER '${LIFERAY_DATABASE_USERNAME}'@'%' IDENTIFIED BY '${liferay_password}';

GRANT ALL PRIVILEGES ON ${DATABASE_NAME}.* TO '${LIFERAY_DATABASE_USERNAME}'@'%';
EOF

	_verify_login
}

function _verify_login {
	local timeout=$(($(date +%s) + 600))

	until MYSQL_PWD="${LIFERAY_PASSWORD}" mysql \
		--connect-timeout 10 \
		--execute "SELECT 1" \
		--host "${MYSQL_HOST}" \
		--ssl-mode REQUIRED \
		--user "${LIFERAY_DATABASE_USERNAME}" \
		"${DATABASE_NAME}" >/dev/null 2>&1
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

	until MYSQL_PWD="${ADMIN_PASSWORD}" mysqladmin \
		--connect-timeout 10 \
		--host "${MYSQL_HOST}" \
		--ssl-mode REQUIRED \
		--user "${DATABASE_ADMIN_USERNAME}" \
		ping
	do
		if [ $(date +%s) -ge ${timeout} ]
		then
			echo "${MYSQL_HOST} did not become ready" >&2

			exit 1
		fi

		sleep 2
	done
}

main