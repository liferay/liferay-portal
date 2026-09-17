#!/bin/bash

CURRENT_DIR_NAME=$(dirname "${BASH_SOURCE[0]}")

echo "CURRENT_DIR_NAME=${CURRENT_DIR_NAME}"

source "${CURRENT_DIR_NAME}/../../../../env/common.sh"

DATA_ARCHIVE_TYPE="data-archive-afs-store"
PORTAL_VERSION="7.4.13"

function assert_advanced_file_system_store_root_dir {
	local document_library_dir="${LIFERAY_HOME}/data/document_library1"

	if [[ ! -f ${document_library_dir}/README.txt ]]
	then
		echo "Unable to confirm the advanced file system store used ${document_library_dir}."

		find "${LIFERAY_HOME}/data" -maxdepth 2 -name README.txt

		exit 1
	fi
}

function main {
	set -ex

	update_portal_ext_properties

	deploy_project_osgi_configs

	upgrade_legacy_database_set_up "${DATA_ARCHIVE_TYPE}" "${PORTAL_VERSION}"

	assert_advanced_file_system_store_root_dir
}

main "${@}"