#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function main {
	local requested_chart="${1:-}"

	local script_dir

	script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

	local cloud_dir

	cloud_dir=$(cd "${script_dir}/../.." && pwd)

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
		platform
		platform-components
	)

	if [[ -n ${requested_chart} ]]
	then
		if [[ ! -d ${cloud_dir}/helm/${requested_chart} ]]
		then
			echo "Unable to find chart ${requested_chart}"

			exit 1
		fi

		charts=("${requested_chart}")
	fi

	for chart in "${charts[@]}"
	do
		helm dependency update "${cloud_dir}/helm/${chart}"

		local helm_template_args=()

		if [[ -f ${script_dir}/render-values/${chart}.yaml ]]
		then
			helm_template_args=("--values" "${script_dir}/render-values/${chart}.yaml")
		fi

		helm template liferay "${cloud_dir}/helm/${chart}" "${helm_template_args[@]}" | kubeconform \
			--schema-location default \
			--schema-location 'https://raw.githubusercontent.com/datreeio/CRDs-catalog/main/{{.Group}}/{{.ResourceKind}}_{{.ResourceAPIVersion}}.json' \
			--skip ClusterProviderConfig,LiferayEnvironment,LiferayInfrastructure \
			--strict \
			--summary
	done
}

main "${@}"