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

	check_utils az helm jq terraform

	validate_config_json "${1}"

	local configuration_json_file

	configuration_json_file=$(resolve_config_json "${1}")

	_validate_setup_values "${configuration_json_file}"

	export_database_engine "${configuration_json_file}"

	prepare_terraform_stacks "azure" "aks" "${configuration_json_file}"

	log_in "${configuration_json_file}"

	if has_remote_tfstate "${configuration_json_file}"
	then
		_create_tfstate_storage "${configuration_json_file}"
	fi

	set_up "azure" "aks" "${configuration_json_file}"
}

function _configure_storage_account {
	local resource_group_name="${1}"
	local storage_account_name="${2}"

	az storage account update \
		--allow-blob-public-access false \
		--min-tls-version TLS1_2 \
		--name "${storage_account_name}" \
		--resource-group "${resource_group_name}" \
		--output none

	az storage account blob-service-properties update \
		--account-name "${storage_account_name}" \
		--delete-retention-days 90 \
		--enable-delete-retention true \
		--enable-versioning true \
		--resource-group "${resource_group_name}" \
		--output none
}

function _create_storage_account {
	local region="${1}"
	local resource_group_name="${2}"
	local storage_account_name="${3}"

	az storage account create \
		--allow-blob-public-access false \
		--encryption-services blob \
		--kind StorageV2 \
		--location "${region}" \
		--min-tls-version TLS1_2 \
		--name "${storage_account_name}" \
		--resource-group "${resource_group_name}" \
		--sku Standard_LRS \
		--output none
}

function _create_tfstate_storage {
	local configuration_json_file="${1}"

	local container_name

	container_name=$(jq --raw-output '.tfstate.container_name' "${configuration_json_file}")

	local region

	region=$(jq --raw-output '.region' "${configuration_json_file}")

	local resource_group_name

	resource_group_name=$(jq --raw-output '.tfstate.resource_group_name' "${configuration_json_file}")

	local storage_account_name

	storage_account_name=$(jq --raw-output '.tfstate.storage_account_name' "${configuration_json_file}")

	if ! az group show --name "${resource_group_name}" &> /dev/null
	then
		_log "Creating resource group ${resource_group_name}."

		az group create --location "${region}" --name "${resource_group_name}" --output none

		_log "Resource group ${resource_group_name} was created successfully."
	else
		_log "Resource group ${resource_group_name} already exists. Skipping creation process."
	fi

	if ! az storage account show --name "${storage_account_name}" --resource-group "${resource_group_name}" &> /dev/null
	then
		_log "Creating storage account ${storage_account_name}."

		_create_storage_account "${region}" "${resource_group_name}" "${storage_account_name}"

		_log "Storage account ${storage_account_name} was created successfully."
	else
		_log "Storage account ${storage_account_name} already exists. Skipping creation process."
	fi

	_log "Configuring storage account ${storage_account_name}."

	_configure_storage_account "${resource_group_name}" "${storage_account_name}"

	_log "Storage account ${storage_account_name} was configured successfully."

	if ! az storage container show \
		--account-name "${storage_account_name}" \
		--auth-mode login \
		--name "${container_name}" \
		&> /dev/null
	then
		_log "Creating storage container ${container_name}."

		az storage container create \
			--account-name "${storage_account_name}" \
			--auth-mode login \
			--name "${container_name}" \
			--output none

		_log "Storage container ${container_name} was created successfully."
	else
		_log "Storage container ${container_name} already exists. Skipping creation process."
	fi

	_grant_tfstate_access "${container_name}" "${resource_group_name}" "${storage_account_name}"
}

function _grant_tfstate_access {
	local container_name="${1}"
	local resource_group_name="${2}"
	local storage_account_name="${3}"

	if _has_tfstate_access "${container_name}" "${storage_account_name}"
	then
		return
	fi

	local storage_account_id

	storage_account_id=$( \
		az storage account show \
			--name "${storage_account_name}" \
			--output tsv \
			--query id \
			--resource-group "${resource_group_name}")

	local user_id

	user_id=$(az ad signed-in-user show --output tsv --query id)

	_log "Assigning the Storage Blob Data Contributor role on the storage container ${container_name} to the current Azure user."

	az role assignment create \
		--assignee-object-id "${user_id}" \
		--assignee-principal-type User \
		--output none \
		--role "Storage Blob Data Contributor" \
		--scope "${storage_account_id}/blobServices/default/containers/${container_name}"

	local timeout_minutes=5

	_log "Waiting up to ${timeout_minutes} minutes for the role assignment to take effect."

	local timeout

	timeout=$(($(date +%s) + timeout_minutes * 60))

	while [ $(date +%s) -lt ${timeout} ]
	do
		if _has_tfstate_access "${container_name}" "${storage_account_name}"
		then
			return
		fi

		sleep 10
	done

	_log "Unable to access the storage container ${container_name} after ${timeout_minutes} minutes." >&2

	return 1
}

function _has_tfstate_access {
	local container_name="${1}"
	local storage_account_name="${2}"

	az storage blob list \
		--account-name "${storage_account_name}" \
		--auth-mode login \
		--container-name "${container_name}" \
		--num-results 1 \
		--output none \
		&> /dev/null
}

function _log {
	echo "[Tfstate storage configuration] ${1}"
}

function _validate_setup_values {
	local configuration_json_file="${1}"

	local chart_registry

	chart_registry=$(jq --raw-output '.artifacts.charts."liferay-azure-infrastructure-provider".registry // error("artifacts.charts.liferay-azure-infrastructure-provider.registry is missing")' "${configuration_json_file}")

	local chart_version

	chart_version=$(jq --raw-output '.artifacts.charts."liferay-azure-infrastructure-provider".version // error("artifacts.charts.liferay-azure-infrastructure-provider.version is missing")' "${configuration_json_file}")

	helm \
		template \
		--values <(jq '.setup | ."liferay-subchart-backup-restore".database.engine = .database.engine' "${configuration_json_file}") \
		--version "${chart_version}" \
		liferay-azure-infrastructure-provider \
		"oci://${chart_registry}/liferay-azure-infrastructure-provider" \
		> /dev/null
}

main "${@}"