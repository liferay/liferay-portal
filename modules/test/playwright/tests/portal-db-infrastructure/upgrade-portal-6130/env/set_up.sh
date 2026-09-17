#!/bin/bash

CURRENT_DIR_NAME=$(dirname "${BASH_SOURCE[0]}")

echo "CURRENT_DIR_NAME=${CURRENT_DIR_NAME}"

source "${CURRENT_DIR_NAME}/../../../../env/common.sh"

CUSTOM_UPGRADE_PROPERTIES="feature.flag.LPS-157670=true"
DATA_ARCHIVE_TYPE="data-archive-portal"
PORTAL_VERSION="6.1.30"

function main {
	set -ex

	upgrade_legacy_database_set_up "${DATA_ARCHIVE_TYPE}" "${PORTAL_VERSION}" "${CUSTOM_UPGRADE_PROPERTIES}"
}

main "${@}"