#!/bin/sh

set -o errexit
set -o nounset

function main {
	if [ -z "$(ls /restore-sanitization/*.sql 2>/dev/null)" ]
	then
		echo "No sanitization scripts are mounted. Skipping the database sanitization."

		exit 0
	fi

	local sql_file

	for sql_file in /restore-sanitization/*.sql
	do
		{{- if eq (include "backupRestore.databaseEngine" .) "mysql" }}
		mysql \
			--host 127.0.0.1 \
			--port 3306 \
			--user "${DATABASE_USERNAME}" \
			lportal < "${sql_file}"
		{{- else }}
		PGPASSWORD=iam psql \
			--dbname lportal \
			--file "${sql_file}" \
			--host 127.0.0.1 \
			--port 5432 \
			--set ON_ERROR_STOP=1 \
			--single-transaction \
			--username "${DATABASE_USERNAME}"
		{{- end }}
	done
}

main