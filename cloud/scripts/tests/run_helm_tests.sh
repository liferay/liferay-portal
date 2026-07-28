#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function main {
	local script_dir

	script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

	local cloud_dir

	cloud_dir=$(cd "${script_dir}/../.." && pwd)

	local test_reports_dir="${cloud_dir}/scripts/tests/test-results"

	mkdir --parents "${test_reports_dir}"

	local charts=(
		aws
		aws-infrastructure
		aws-infrastructure-provider
		aws-marketplace
		default
		dxp-operator
		gcp
		gcp-infrastructure
		gcp-infrastructure-provider
		observability
	)

	for chart in "${charts[@]}"
	do
		local test_files

		test_files=$(ls "${cloud_dir}/helm/${chart}/tests/"*_test.yaml 2>/dev/null || echo "")

		if [[ ${test_files} ]]
		then
			helm dependency update "${cloud_dir}/helm/${chart}"

			helm unittest \
				--output-file "${test_reports_dir}/helm-unittest-${chart}.xml" \
				--output-type JUnit \
				"${cloud_dir}/helm/${chart}"
		fi
	done
}

main "${@}"