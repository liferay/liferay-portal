#!/bin/bash

CURRENT_DIR_NAME=$(dirname "${BASH_SOURCE[0]}")

echo "CURRENT_DIR_NAME=${CURRENT_DIR_NAME}"

source "${CURRENT_DIR_NAME}/../../../../env/common.sh"

DATA_ARCHIVE_TYPE="data-archive-portal-partition"
PORTAL_VERSION="7.4.13.u33"

function main {
	set -ex

	update_portal_ext_properties

	cd "${_PORTAL_PROJECT_DIR}"

	ant -f build-test.xml \
		-Ddata.archive.type="${DATA_ARCHIVE_TYPE}" \
		-Dkeep.cached.app.server.data=true \
		-Dportal.version="${PORTAL_VERSION}" \
		-Dskip.get.testcase.database.properties=true \
		rebuild-legacy-database

	ant -f build-test.xml upgrade-legacy-database

	assert_clean_upgrade_log

	default_set_up
}

main "${@}"