#!/usr/bin/env bash

source "$(dirname "${BASH_SOURCE[0]}")/_common.sh"

function export_database_engine {
	local configuration_json_file="${1}"

	TF_VAR_database_engine=$(jq --raw-output '.setup.database.engine // error("setup.database.engine is missing")' "${configuration_json_file}")

	export TF_VAR_database_engine
}

function generate_remote_backend_overrides {
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

	local directory

	for directory in aks platform
	do
		cat > "${ROOT_CLOUD_DIR}/terraform/azure/${directory}/backend_override.tf" <<EOF
terraform {
	backend "azurerm" {
		container_name="${container_name}"
		key="${deployment_name}/${region}/${directory}/terraform.tfstate"
		resource_group_name="${resource_group_name}"
		storage_account_name="${storage_account_name}"
		use_azuread_auth=true
	}
}
EOF
	done
}

function log_in {
	local configuration_json_file="${1}"

	local subscription_id

	subscription_id=$(jq --raw-output '.subscription_id' "${configuration_json_file}")

	export ARM_SUBSCRIPTION_ID="${subscription_id}"

	local tenant_id

	tenant_id=$(jq --raw-output '.tenant_id' "${configuration_json_file}")

	export ARM_TENANT_ID="${tenant_id}"

	if az account get-access-token --output none --subscription "${subscription_id}" &> /dev/null
	then
		echo "Reusing the active Azure login for the subscription ${subscription_id}."
	else
		echo "Attempting to login to your Azure account."

		AZURE_CORE_LOGIN_EXPERIENCE_V2=off az login --output none --tenant "${tenant_id}"
	fi

	az account set --subscription "${subscription_id}"
}

function update_kubeconfig {
	local cluster_name="${1}"
	local cluster_terraform_stack="${2}"

	local resource_group_name

	resource_group_name=$(run_terraform "azure" "${cluster_terraform_stack}" output -raw resource_group_name)

	az aks get-credentials \
		--name "${cluster_name}" \
		--overwrite-existing \
		--resource-group "${resource_group_name}"
}

function validate_config_json {
	local configuration_json_file="${1}"

	local required_keys=(
		".deployment_name"
		".region"
		".subscription_id"
		".tenant_id"
	)

	if has_remote_tfstate "${configuration_json_file}"
	then
		required_keys+=(
			".tfstate.container_name"
			".tfstate.resource_group_name"
			".tfstate.storage_account_name"
		)
	fi

	check_config_json "${configuration_json_file}" "${required_keys[@]}"
}