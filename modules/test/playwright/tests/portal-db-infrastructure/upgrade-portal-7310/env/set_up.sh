#!/bin/bash

CURRENT_DIR_NAME=$(dirname "${BASH_SOURCE[0]}")

echo "CURRENT_DIR_NAME=${CURRENT_DIR_NAME}"

source "${CURRENT_DIR_NAME}/../../../../env/common.sh"

DATA_ARCHIVE_TYPE="data-archive-portal"
PORTAL_VERSION="7.3.10"

function main {
	set -ex

	upgrade_legacy_database_set_up "${DATA_ARCHIVE_TYPE}" "${PORTAL_VERSION}"
}

main "${@}"