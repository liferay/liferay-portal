#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

function main {
	local reference

	reference=$(echo "${4}" | sed -e "s|^cxdescriptor://||")

	if ! _check_registry "${reference}"
	then
		return 1
	fi

	_IMAGE_FILE=$(mktemp)

	trap 'rm -f "${_IMAGE_FILE}"' EXIT

	export DOCKER_CONFIG="/cxdescriptor-docker-config"

	if ! "${HELM_PLUGIN_DIR}/bin/crane" export -- "${reference}" "${_IMAGE_FILE}"
	then
		return 1
	fi

	tar \
		--extract \
		--file "${_IMAGE_FILE}" \
		--to-stdout \
		client-extension-descriptor.json | \
		"${HELM_PLUGIN_DIR}/bin/jq" 'if type == "object" and keys == ["descriptor"] then . else error("client-extension-descriptor.json may only have a descriptor key") end'
}

function _check_registry {
	if [[ -z ${CXDESCRIPTOR_REGISTRIES} ]]
	then
		return 0
	fi

	local reference=${1}
	local registry

	for registry in ${CXDESCRIPTOR_REGISTRIES}
	do
		if [[ "${reference}" == "${registry%/}/"* ]]
		then
			return 0
		fi
	done

	echo "${reference} is not in an allowed client extension registry: ${CXDESCRIPTOR_REGISTRIES}" >&2

	return 1
}

main "${@}"