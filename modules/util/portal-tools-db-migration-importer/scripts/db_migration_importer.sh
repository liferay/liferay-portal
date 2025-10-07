#!/bin/bash

#
# Ignore SIGHUP to keep the script running if the terminal disconnects.
#

trap '' 1

if [ -e /proc/$$/fd/255 ]
then
	DB_MIGRATION_IMPORTER_PATH=`readlink /proc/$$/fd/255 2>/dev/null`
fi

if [ ! -n "${DB_MIGRATION_IMPORTER_PATH}" ]
then
	DB_MIGRATION_IMPORTER_PATH="$0"
fi

cd "$(dirname "${DB_MIGRATION_IMPORTER_PATH}")"

#
# Run database migration importer tool.
#

java -cp com.liferay.portal.tools.db.migration.importer.jar:lib/* com.liferay.portal.tools.db.migration.importer.DBMigrationImport "$@"