#!/bin/bash

CURRENT_DIR_NAME=$(dirname "${BASH_SOURCE[0]}")

echo "CURRENT_DIR_NAME=${CURRENT_DIR_NAME}"

source "${CURRENT_DIR_NAME}/../../../../env/common.sh"

DATA_ARCHIVE_TYPE="data-archive-portal"
PORTAL_VERSION="6.2.5"

function main {
	set -ex

	rebuild_legacy_database "${DATA_ARCHIVE_TYPE}" "${PORTAL_VERSION}"

	ant -f build-test.xml upgrade-legacy-database

	assert_clean_upgrade_log

	default_set_up
}

main "${@}"