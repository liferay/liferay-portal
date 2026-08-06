#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

function main {
	local mode=${1:-full}

	cd resources

	if [[ ${mode} == "generate" ]]
	then
		_generate

		return
	fi

	if [[ ${mode} == "fast" ]]
	then
		_generate

		mkdir --parents build

		CGO_ENABLED=0 GOOS=linux go build -o build/manager .

		return
	fi

	go mod download

	go mod verify

	_generate

	CGO_ENABLED=0 go build -a --ldflags="-s -w" -o manager main.go
}

function _format_generated {
	if ! command -v java > /dev/null
	then
		echo "java is required to format the generated CRD"

		exit 1
	fi

	local version

	version=$(curl \
		--fail \
		--silent \
		"${_SOURCE_FORMATTER_URL}/maven-metadata.xml" | \
		grep --extended-regexp --only-matching '<release>[^<]+' | \
		sed --regexp-extended 's,<release>,,')

	local dist_dir="${HOME}/.liferay/source-formatter/source-formatter-${version}"

	if [ ! -x "${dist_dir}/bin/source-formatter" ]
	then
		local temp_dir

		temp_dir=$(mktemp --directory)

		curl \
			--fail \
			--location \
			--output "${temp_dir}/source-formatter.zip" \
			--silent \
			"${_SOURCE_FORMATTER_URL}/${version}/com.liferay.source.formatter-${version}.zip"

		mkdir --parents "${HOME}/.liferay/source-formatter"

		unzip -o -q "${temp_dir}/source-formatter.zip" -d "${HOME}/.liferay/source-formatter"

		rm --recursive --force "${temp_dir}"
	fi

	"${dist_dir}/bin/source-formatter" \
		source.auto.fix=true \
		source.files="${_CRD_FILE}"
}

function _generate {
	go generate ./...

	if [[ "${LIFERAY_GO_BUILD_SKIP_SOURCE_FORMATTER:-false}" == "true" ]]
	then
		return
	fi

	_format_generated
}

_CRD_FILE="$(cd .. && pwd)/helm/dxp-operator/crds/licensing.liferay.com_liferayenvironments.yaml"

_SOURCE_FORMATTER_URL="https://repository-cdn.liferay.com/nexus/content/groups/public/com/liferay/com.liferay.source.formatter"

main "${@}"