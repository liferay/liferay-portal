#!/usr/bin/env bash

source "$(dirname "${BASH_SOURCE[0]}")/_common.sh"

function az_login {
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

function connect_to_cluster {
	push_directory "${ROOT_CLOUD_DIR}/terraform/azure/aks"

	echo "Connecting to the AKS cluster."

	terraform init

	local cluster_name

	cluster_name=$(terraform output -raw cluster_name)

	local resource_group_name

	resource_group_name=$(terraform output -raw resource_group_name)

	if [[ -z ${cluster_name} ]] || [[ -z ${resource_group_name} ]]
	then
		echo "There is no AKS cluster in the Terraform state." >&2

		pop_directory

		return 1
	fi

	export KUBE_CONFIG_PATH="${HOME}/.kube/config"

	if ! az aks get-credentials \
		--name "${cluster_name}" \
		--overwrite-existing \
		--resource-group "${resource_group_name}"
	then
		echo "The AKS cluster ${cluster_name} could not be reached." >&2

		pop_directory

		return 1
	fi

	pop_directory
}

function generate_remote_backend_overrides {
	local container_name="${1}"
	local deployment_name="${2}"
	local region="${3}"
	local resource_group_name="${4}"
	local storage_account_name="${5}"

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

function validate_config_json {
	local configuration_json_file="${1}"

	local required_keys=(
		".deployment_name"
		".region"
		".subscription_id"
		".tenant_id"
	)

	if jq --exit-status '.tfstate | objects' "${configuration_json_file}" &> /dev/null
	then
		required_keys+=(
			".tfstate.container_name"
			".tfstate.resource_group_name"
			".tfstate.storage_account_name"
		)
	fi

	check_config_json "${configuration_json_file}" "${required_keys[@]}"
}