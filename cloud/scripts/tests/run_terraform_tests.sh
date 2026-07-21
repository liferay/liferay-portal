#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function main {
	local requested_module="${1:-}"

	local terraform_modules=(
		aws/eks
		aws/gitops/platform
		aws/gitops/resources
		azure/aks
		gcp/gitops/platform
		gcp/gitops/resources
		gcp/gke
	)

	if [[ -n "${requested_module}" ]]
	then
		terraform_modules=("${requested_module}")
	fi

	local terraform_dir

	terraform_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")/../../terraform" && pwd)

	mkdir --parents "${terraform_dir}/test-results"

	for terraform_module in "${terraform_modules[@]}"
	do
		cd "${terraform_dir}/${terraform_module}"

		terraform init -backend=false -input=false

		local sanitized_terraform_module_name

		sanitized_terraform_module_name="$(echo "${terraform_module}" | tr '/' '-')"

		terraform test \
			-junit-xml="${terraform_dir}/test-results/${sanitized_terraform_module_name}.xml"
	done
}

main "${@}"