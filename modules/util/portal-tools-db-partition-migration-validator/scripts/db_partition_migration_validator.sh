#!/bin/bash

#
# Ignore SIGHUP to keep the script running if the terminal disconnects.
#

trap '' 1

if [ -e /proc/$$/fd/255 ]
then
	DB_PARTITION_MIGRATION_VALIDATOR_PATH=`readlink /proc/$$/fd/255 2>/dev/null`
fi

if [ ! -n "${DB_PARTITION_MIGRATION_VALIDATOR_PATH}" ]
then
	DB_PARTITION_MIGRATION_VALIDATOR_PATH="$0"
fi

cd "$(dirname "${DB_PARTITION_MIGRATION_VALIDATOR_PATH}")"

#
# Run database partition migration validator tool.
#

java \
	--add-opens=java.base/java.lang=ALL-UNNAMED \
	--add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
	--add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
	--add-opens=java.base/java.net=ALL-UNNAMED \
	--add-opens=java.base/java.util=ALL-UNNAMED \
	--add-opens=java.base/sun.net.www.protocol.http=ALL-UNNAMED \
	--add-opens=java.base/sun.net.www.protocol.https=ALL-UNNAMED \
	--add-opens=java.base/sun.util.calendar=ALL-UNNAMED \
	--add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED \
	--add-opens=jdk.zipfs/jdk.nio.zipfs=ALL-UNNAMED \
	-Dlauncher="$0" \
	-jar com.liferay.portal.tools.db.partition.migration.validator.jar "$@"