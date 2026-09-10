#!/bin/bash

CURRENT_DIR_NAME=$(dirname "${BASH_SOURCE[0]}")

echo "CURRENT_DIR_NAME=${CURRENT_DIR_NAME}"

source "${CURRENT_DIR_NAME}/../../../../env/common.sh"

DATA_ARCHIVE_TYPE="data-archive-portal"
PORTAL_VERSION="6.1.30"

function main {
	set -ex

	cd "${_PORTAL_PROJECT_DIR}"

	ant -f build-test.xml \
		-Ddata.archive.type="${DATA_ARCHIVE_TYPE}" \
		-Dkeep.cached.app.server.data=true \
		-Dportal.version="${PORTAL_VERSION}" \
		-Dskip.get.testcase.database.properties=true \
		rebuild-legacy-database

	echo "Upgrade source: portal.version=${PORTAL_VERSION} data.archive.type=${DATA_ARCHIVE_TYPE}"

	default_set_up

	assert_clean_upgrade_log
}

main "${@}"