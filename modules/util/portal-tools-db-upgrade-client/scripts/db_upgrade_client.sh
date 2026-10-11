#!/bin/bash

#
# Ignore SIGHUP to keep the script running if the terminal disconnects.
#

trap '' 1

if [ -e /proc/$$/fd/255 ]
then
	DB_UPGRADE_CLIENT_PATH=`readlink /proc/$$/fd/255 2>/dev/null`
fi

if [ ! -n "${DB_UPGRADE_CLIENT_PATH}" ]
then
	DB_UPGRADE_CLIENT_PATH="$0"
fi

cd "$(dirname "${DB_UPGRADE_CLIENT_PATH}")"

#
# Check running process.
#

DB_UPGRADE_CLIENT_PID=db_upgrade.pid

if [ -f "${DB_UPGRADE_CLIENT_PID}" ]
then
	if [ -s "${DB_UPGRADE_CLIENT_PID}" ]
	then
		if [ -r "${DB_UPGRADE_CLIENT_PID}" ]
		then
			PID=`cat "${DB_UPGRADE_CLIENT_PID}"`

			ps -p ${PID} >/dev/null 2>&1

			if [ $? -eq 0 ]
			then
				echo "Database upgrade client is already running with process ID ${PID}."
				echo ""
				echo "If the following process is not the database upgrade client process, remove ${DB_UPGRADE_CLIENT_PID} and try again."

				ps -f -p ${PID}

				exit 1
			else
				echo "Removing stale ${DB_UPGRADE_CLIENT_PID}."

				rm -f "${DB_UPGRADE_CLIENT_PID}" >/dev/null 2>&1

				if [ $? != 0 ]
				then
					if [ -w "${DB_UPGRADE_CLIENT_PID}" ]
					then
						cat /dev/null > "${DB_UPGRADE_CLIENT_PID}"
					else
						echo "Unable to remove stale ${DB_UPGRADE_CLIENT_PID}."

						exit 1
					fi
				fi
			fi
		else
			echo "Unable to read ${DB_UPGRADE_CLIENT_PID}."

			exit 1
		fi
	else
		rm -f "${DB_UPGRADE_CLIENT_PID}" >/dev/null 2>&1

		if [ $? != 0 ]
		then
			if [ ! -w "${DB_UPGRADE_CLIENT_PID}" ]
			then
				echo "Unable to write to ${DB_UPGRADE_CLIENT_PID}."

				exit 1
			fi
		fi
	fi
fi

echo $$ > ${DB_UPGRADE_CLIENT_PID}

#
# Run database upgrade client.
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
	-jar com.liferay.portal.tools.db.upgrade.client.jar "$@"

DB_UPGRADE_CLIENT_EXIT_CODE=$?

#
# Clean up.
#

rm ${DB_UPGRADE_CLIENT_PID}

exit "${DB_UPGRADE_CLIENT_EXIT_CODE}"