#!/usr/bin/env bash

SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

ROOT_CLOUD_DIR=$(cd "${SCRIPTS_DIR}/.." && pwd)

readonly ROOT_CLOUD_DIR SCRIPTS_DIR

function az_login {
	local configuration_json_file="${1}"

	local subscription_id

	subscription_id="$(jq --raw-output '.subscription_id' "${configuration_json_file}")"

	export ARM_SUBSCRIPTION_ID="${subscription_id}"

	local tenant_id

	tenant_id="$(jq --raw-output '.tenant_id' "${configuration_json_file}")"

	export ARM_TENANT_ID="${tenant_id}"

	echo "Attempting to login to your Azure account."

	AZURE_CORE_LOGIN_EXPERIENCE_V2=off az login --output none --tenant "${tenant_id}"

	az account set --subscription "${subscription_id}"
}

function check_utils {
	for util in "${@}"
	do
		if (! command -v "${util}" &> /dev/null)
		then
			echo "The utility ${util} is not installed."

			exit 1
		fi
	done
}

function connect_to_cluster {
	push_directory "${ROOT_CLOUD_DIR}/terraform/azure/aks"

	echo "Connecting to the AKS cluster."

	terraform init

	export KUBE_CONFIG_PATH="${HOME}/.kube/config"

	az aks get-credentials \
		--name "$(terraform output -raw cluster_name)" \
		--overwrite-existing \
		--resource-group "$(terraform output -raw resource_group_name)"

	pop_directory
}

function generate_local_backend_overrides {
	local directory

	for directory in aks platform
	do
		cat > "${ROOT_CLOUD_DIR}/terraform/azure/${directory}/backend_override.tf" <<EOF
terraform {
	backend "local" {}
}
EOF
	done
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

function generate_tfvars {
	local configuration_json_file="${1}"
	local module="${2}"

	local tfvars_file="${ROOT_CLOUD_DIR}/terraform/azure/${module}/config.auto.tfvars.json"

	echo "Generating ${tfvars_file} from ${configuration_json_file}."

	jq --arg module "${module}" '(.terraform.common // {}) * (.terraform[$module] // {})' "${configuration_json_file}" > "${tfvars_file}"

	echo "${tfvars_file} was generated successfully."
}

function get_terraform_args {
	local configuration_json_file="${1}"

	local auto_approve

	auto_approve=$(jq --raw-output '.options.auto_approve // false' "${configuration_json_file}")

	local terraform_args=()

	if [[ ${auto_approve} == true ]]
	then
		terraform_args+=("-auto-approve")
	fi

	local parallelism

	parallelism=$(jq --raw-output '.options.parallelism | numbers' "${configuration_json_file}")

	if [[ -n ${parallelism} ]]
	then
		terraform_args+=("-parallelism=${parallelism}")
	fi

	if [ ${#terraform_args[@]} -gt 0 ]
	then
		printf '%s\n' "${terraform_args[@]}"
	fi
}

function pop_directory {
	popd > /dev/null
}

function push_directory {
	pushd "${1}" > /dev/null
}

function validate_config_json {
	local configuration_json_file="${1}"

	if [[ ! -f ${configuration_json_file} ]]
	then
		echo "Configuration JSON file ${configuration_json_file} does not exist." >&2

		exit 1
	fi

	if ! jq empty "${configuration_json_file}" &> /dev/null
	then
		echo "Configuration JSON file ${configuration_json_file} is not valid JSON." >&2

		exit 1
	fi

	local required_keys=(
		".subscription_id"
		".tenant_id"
	)

	if jq --exit-status '.tfstate | objects' "${configuration_json_file}" &> /dev/null
	then
		required_keys+=(
			".terraform.common.deployment_name"
			".terraform.common.region"
			".tfstate.container_name"
			".tfstate.resource_group_name"
			".tfstate.storage_account_name"
		)
	fi

	local required_key

	for required_key in "${required_keys[@]}"
	do
		if ! jq --exit-status "${required_key}" "${configuration_json_file}" &> /dev/null
		then
			echo "The configuration JSON file must contain a key named \"${required_key#.}\"." >&2

			exit 1
		fi
	done
}