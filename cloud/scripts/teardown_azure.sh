#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

source "$(dirname "${BASH_SOURCE[0]}")/_azure_common.sh"

function main {
	if [ ${#} -eq 0 ]
	then
		echo "Usage: ${0} <configuration-json-file>" >&2
		echo "" >&2
		echo "See cloud/scripts/config.json.example_azure for a sample." >&2

		exit 1
	fi

	check_utils az helm jq kubectl terraform

	validate_config_json "${1}"

	local configuration_json_file

	configuration_json_file=$(resolve_config_json "${1}")

	export_database_engine "${configuration_json_file}"

	confirm_teardown "AKS"

	prepare_terraform_stacks "azure" "aks" "${configuration_json_file}"

	log_in "${configuration_json_file}"

	tear_down "azure" "aks" "${configuration_json_file}"

	if has_remote_tfstate "${configuration_json_file}"
	then
		_delete_tfstate_storage "${configuration_json_file}"
	fi
}

function _delete_tfstate_storage {
	local configuration_json_file="${1}"

	local container_name

	container_name=$(jq --raw-output '.tfstate.container_name' "${configuration_json_file}")

	local deployment_name

	deployment_name=$(jq --raw-output '.deployment_name' "${configuration_json_file}")

	local region

	region=$(jq --raw-output '.region' "${configuration_json_file}")

	local resource_group_name

	resource_group_name=$(jq --raw-output '.tfstate.resource_group_name' "${configuration_json_file}")

	local storage_account_name

	storage_account_name=$(jq --raw-output '.tfstate.storage_account_name' "${configuration_json_file}")

	if ! az storage account show --name "${storage_account_name}" --resource-group "${resource_group_name}" &> /dev/null
	then
		echo "Storage account ${storage_account_name} does not exist. Skipping the deletion process."

		return
	fi

	local foreign_container_count

	foreign_container_count=$( \
		az storage container-rm list \
			--output tsv \
			--query "length([?name != '${container_name}'])" \
			--resource-group "${resource_group_name}" \
			--storage-account "${storage_account_name}")

	local foreign_blob_count

	foreign_blob_count=$( \
		az storage blob list \
			--account-name "${storage_account_name}" \
			--auth-mode login \
			--container-name "${container_name}" \
			--include v \
			--num-results "*" \
			--output tsv \
			--query "length([?!starts_with(name, '${deployment_name}/${region}/')])")

	if [ ${foreign_container_count} -gt 0 ] || [ ${foreign_blob_count} -gt 0 ]
	then
		echo "Storage account ${storage_account_name} was kept because it still holds the Terraform state of other deployments."

		return
	fi

	if ! is_confirmed "delete the Terraform state storage account ${storage_account_name}"
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

	local resource_count

	resource_count=$(az resource list --output tsv --query "length(@)" --resource-group "${resource_group_name}")

	if [ ${resource_count} -eq 0 ]
	then
		echo "Deleting resource group ${resource_group_name}."

		az group delete --name "${resource_group_name}" --yes

		echo "Resource group ${resource_group_name} was deleted successfully."
	else
		echo "Resource group ${resource_group_name} was kept because it still holds other resources."
	fi
}

main "${@}"