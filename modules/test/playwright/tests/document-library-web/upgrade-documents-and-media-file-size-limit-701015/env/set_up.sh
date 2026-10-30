#!/bin/bash

CURRENT_DIR_NAME=$(dirname "${BASH_SOURCE[0]}")

echo "CURRENT_DIR_NAME=${CURRENT_DIR_NAME}"

source "${CURRENT_DIR_NAME}/../../../../env/common.sh"

DATA_ARCHIVE_TYPE="data-archive-documents-and-media-file-size-limit"
PORTAL_VERSION="7.0.10.15"

function main {
	set -ex

	rebuild_legacy_database "${DATA_ARCHIVE_TYPE}" "${PORTAL_VERSION}"

	mkdir -p "${LIFERAY_HOME}/data/document_library/20115"

	ant -f build-test.xml \
		-Dportal.version="${PORTAL_VERSION}" \
		upgrade-legacy-database

	assert_clean_upgrade_log

	default_set_up
}

main "${@}"