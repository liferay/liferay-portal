#!/bin/bash

CURRENT_DIR_NAME=$(dirname ${BASH_SOURCE[0]})
TEST_SMTP_SERVER_DIR="/tmp"
TEST_SMTP_SERVER_URL="https://repository-cdn.liferay.com/nexus/service/local/repo_groups/public/content/com/liferay/com.mockmock/1.4.0/com.mockmock-1.4.0.jar"

echo CURRENT_DIR_NAME=${CURRENT_DIR_NAME}
echo TEST_SMTP_SERVER_DIR=${TEST_SMTP_SERVER_DIR}
echo TEST_SMTP_SERVER_URL=${TEST_SMTP_SERVER_URL}

source ${CURRENT_DIR_NAME}/../../../../env/common.sh

function start_mockmock_server {
	local sleep_duration=60
	local sleep_interval=2
	local total_duration=0

	java --add-opens java.base/java.lang=ALL-UNNAMED -jar ${TEST_SMTP_SERVER_DIR}/MockMock.jar -p 25000 &

	while ! curl --output /dev/null --silent --head --fail http://localhost:8282
	do
		if [ ${total_duration} -ge ${sleep_duration} ]; then
			echo "Unable to start MockMock smtp server."
			exit 1
		fi

		sleep ${sleep_interval}
		total_duration=$((total_duration + sleep_interval))
	done

	echo "Started MockMock smtp server."
}

function download_mockmock {
	local attempt=1
	local max_attempts=5
	local sleep_interval=20

	while ! wget --no-check-certificate "${TEST_SMTP_SERVER_URL}" -O "${TEST_SMTP_SERVER_DIR}/MockMock.jar";
	do
		if [ $attempt -ge $max_attempts ]; then
			echo "Failed to download MockMock.jar after $max_attempts attempts."
			exit 1
		fi

		echo "Download failed. Retrying in $sleep_interval seconds... (Attempt $((attempt+1))/$max_attempts)"
		attempt=$((attempt+1))
		sleep ${sleep_interval}
	done

	echo "Download MockMock successful."
}

function main {
	default_set_up

	download_mockmock

	start_mockmock_server
}

main "${@}"