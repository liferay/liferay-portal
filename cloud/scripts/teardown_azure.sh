#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

source "$(dirname "${BASH_SOURCE[0]}")/azure_common.sh"

function main {
	if [ ${#} -eq 0 ]
	then
		echo "Usage: ${0} <configuration-json-file>" >&2
		echo "" >&2
		echo "See cloud/scripts/config.json.example_azure for a sample." >&2

		exit 1
	fi

	check_utils az helm jq terraform

	validate_config_json "${1}"

	echo "This will destroy the AKS cluster and the Liferay platform."
	echo ""

	local reply

	read -p "Type \"yes\" to continue: " -r reply

	if [[ ${reply} != yes ]]
	then
		echo "The teardown was canceled."

		exit 1
	fi

	generate_tfvars "${1}" "aks"

	generate_tfvars "${1}" "platform"

	az_login "${1}"

	local terraform_args=()

	while IFS= read -r terraform_arg
	do
		terraform_args+=("${terraform_arg}")
	done < <(get_terraform_args "${1}")

	local has_remote_tfstate=false

	if jq --exit-status '.tfstate | objects' "${1}" &> /dev/null
	then
		has_remote_tfstate=true
	fi

	if [[ ${has_remote_tfstate} == true ]]
	then
		local container_name
		local deployment_name
		local region
		local resource_group_name
		local storage_account_name

		container_name="$(jq --raw-output '.tfstate.container_name' "${1}")"
		deployment_name="$(jq --raw-output '.terraform.common.deployment_name' "${1}")"
		region="$(jq --raw-output '.terraform.common.region' "${1}")"
		resource_group_name="$(jq --raw-output '.tfstate.resource_group_name' "${1}")"
		storage_account_name="$(jq --raw-output '.tfstate.storage_account_name' "${1}")"

		generate_remote_backend_overrides "${container_name}" "${deployment_name}" "${region}" "${resource_group_name}" "${storage_account_name}"
	else
		generate_local_backend_overrides
	fi

	connect_to_cluster

	_uninstall_liferay_platform_chart

	_destroy_azure_platform "${terraform_args[@]}"

	_destroy_azure_aks "${terraform_args[@]}"

	if [[ ${has_remote_tfstate} == true ]]
	then
		_delete_tfstate_storage "${resource_group_name}" "${storage_account_name}"
	fi
}

function _delete_tfstate_storage {
	local resource_group_name="${1}"
	local storage_account_name="${2}"

	local reply

	read -p "Type \"yes\" to delete the Terraform state storage account ${storage_account_name}: " -r reply

	if [[ ${reply} != yes ]]
	then
		echo "Storage account ${storage_account_name} was kept."

		return
	fi

	echo "Deleting storage account ${storage_account_name}."

	az storage account delete \
		--name "${storage_account_name}" \
		--resource-group "${resource_group_name}" \
		--yes

	echo "Storage account ${storage_account_name} was deleted successfully."

	if [[ $(az resource list --output tsv --query "length(@)" --resource-group "${resource_group_name}") -eq 0 ]]
	then
		echo "Deleting resource group ${resource_group_name}."

		az group delete --name "${resource_group_name}" --yes

		echo "Resource group ${resource_group_name} was deleted successfully."
	else
		echo "Resource group ${resource_group_name} was kept because it still holds other resources."
	fi
}

function _destroy_azure_aks {
	push_directory "${ROOT_CLOUD_DIR}/terraform/azure/aks"

	echo "Destroying the Azure AKS cluster."

	terraform init

	terraform destroy -input=false "${@}"

	echo "Azure AKS cluster teardown complete."

	pop_directory
}

function _destroy_azure_platform {
	push_directory "${ROOT_CLOUD_DIR}/terraform/azure/platform"

	echo "Destroying the Liferay platform."

	terraform init

	terraform destroy -input=false "${@}"

	echo "Liferay platform teardown complete."

	pop_directory
}

function _uninstall_liferay_platform_chart {
	if ! helm status liferay-platform --namespace argocd-system &> /dev/null
	then
		echo "The liferay-platform Helm release is not installed. Skipping the uninstall process."

		return
	fi

	echo "Uninstalling the Liferay platform root application."

	if ! helm uninstall liferay-platform --namespace argocd-system --timeout 10m0s --wait
	then
		echo "The liferay-platform Helm release was not uninstalled after 10 minutes." >&2

		exit 1
	fi
}

main "${@}"