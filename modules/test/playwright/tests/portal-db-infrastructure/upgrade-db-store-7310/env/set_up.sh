#!/bin/bash

CURRENT_DIR_NAME=$(dirname "${BASH_SOURCE[0]}")

echo "CURRENT_DIR_NAME=${CURRENT_DIR_NAME}"

source "${CURRENT_DIR_NAME}/../../../../env/common.sh"

DATA_ARCHIVE_TYPE="data-archive-db-store"
PORTAL_VERSION="7.3.10"

function assert_document_library_not_populated {
	local document_library_dir="${LIFERAY_HOME}/data/document_library"

	if [[ ! -d ${document_library_dir} ]]
	then
		return 0
	fi

	if [ $(ls -A "${document_library_dir}" | wc -l) -gt 1 ]
	then
		echo "Unable to confirm the database store was used for ${document_library_dir}."

		exit 1
	fi
}

function main {
	set -ex

	update_portal_ext_properties

	upgrade_legacy_database_set_up "${DATA_ARCHIVE_TYPE}" "${PORTAL_VERSION}"

	assert_document_library_not_populated
}

main "${@}"