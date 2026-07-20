#!/bin/bash

cd "$(dirname "${BASH_SOURCE[0]}")"

source _common.sh

function main {
	local force=false

	if [[ ${1:-} == -f ]] ||
	   [[ ${1:-} == --force ]]
	then
		force=true
	fi

	local license_file=../build/docker/deploy/license.xml

	if [[ -f ${license_file} ]] &&
	   [[ ${force} == false ]]
	then
		echo "A trial license already exists at ${license_file}."

		exit 0
	fi

	echo "Extracting the trial license from \"liferay/dxp:latest\"."

	local license_dir

	license_dir=$(dirname "${license_file}")

	mkdir --parents "${license_dir}"

	docker run \
		--entrypoint sh \
		--rm \
		--user root \
		--volume "${license_dir}:/mnt/deploy" \
		liferay/dxp:latest \
		-c \
		"cp /opt/liferay/deploy/trial-dxp-license*.xml /mnt/deploy/license.xml"
}

main "${@}"