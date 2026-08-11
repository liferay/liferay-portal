#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

_SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

_ROOT_CLOUD_DIR=$(cd "${_SCRIPTS_DIR}/.." && pwd)

readonly _ROOT_CLOUD_DIR _SCRIPTS_DIR

function main {
	if [ ${#} -eq 0 ]
	then
		echo "Usage: ${0} <configuration-json-file>" >&2
		echo "" >&2
		echo "See cloud/scripts/config.json.example_azure for a sample." >&2

		exit 1
	fi

	_check_utils az helm jq kubectl terraform

	_validate_config_json "${1}"

	echo "This will destroy the AKS cluster and the Liferay platform."
	echo ""

	local reply

	read -p "Type \"yes\" to continue: " -r reply

	if [[ ${reply} != yes ]]
	then
		echo "The teardown was canceled."

		exit 1
	fi

	_generate_tfvars "${1}" "aks"

	_generate_tfvars "${1}" "platform"

	local subscription_id

	subscription_id="$(jq --raw-output '.subscription_id' "${1}")"

	export ARM_SUBSCRIPTION_ID="${subscription_id}"

	local tenant_id

	tenant_id="$(jq --raw-output '.tenant_id' "${1}")"

	export ARM_TENANT_ID="${tenant_id}"

	echo "Attempting to login to your Azure account."

	AZURE_CORE_LOGIN_EXPERIENCE_V2=off az login --output none --tenant "${tenant_id}"

	az account set --subscription "${subscription_id}"

	local terraform_args=()

	while IFS= read -r terraform_arg
	do
		terraform_args+=("${terraform_arg}")
	done < <(_get_terraform_destroy_args "${1}")

	if jq --exit-status '.tfstate | objects' "${1}" &> /dev/null
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

		_generate_remote_backend_overrides "${container_name}" "${deployment_name}" "${region}" "${resource_group_name}" "${storage_account_name}"
	else
		_generate_local_backend_overrides
	fi

	_connect_to_cluster

	_uninstall_liferay_platform_chart

	_destroy_azure_platform "${terraform_args[@]}"

	_destroy_azure_aks "${terraform_args[@]}"

	if jq --exit-status '.tfstate | objects' "${1}" &> /dev/null
	then
		_delete_tfstate_storage "${resource_group_name}" "${storage_account_name}"
	fi
}

function _check_utils {
	for util in "${@}"
	do
		if (! command -v "${util}" &> /dev/null)
		then
			echo "The utility ${util} is not installed."

			exit 1
		fi
	done
}

function _connect_to_cluster {
	_pushd "${_ROOT_CLOUD_DIR}/terraform/azure/aks"

	echo "Connecting to the AKS cluster."

	terraform init

	export KUBE_CONFIG_PATH="${HOME}/.kube/config"

	az aks get-credentials \
		--name "$(terraform output -raw cluster_name)" \
		--overwrite-existing \
		--resource-group "$(terraform output -raw resource_group_name)"

	_popd
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
	_pushd "${_ROOT_CLOUD_DIR}/terraform/azure/aks"

	echo "Destroying the Azure AKS cluster."

	terraform destroy -input=false "${@}"

	echo "Azure AKS cluster teardown complete."

	_popd
}

function _destroy_azure_platform {
	_pushd "${_ROOT_CLOUD_DIR}/terraform/azure/platform"

	echo "Destroying the Liferay platform."

	terraform init

	terraform destroy -input=false "${@}"

	echo "Liferay platform teardown complete."

	_popd
}

function _generate_local_backend_overrides {
	local directory

	for directory in aks platform
	do
		cat > "${_ROOT_CLOUD_DIR}/terraform/azure/${directory}/backend_override.tf" <<EOF
terraform {
	backend "local" {}
}
EOF
	done
}

function _generate_remote_backend_overrides {
	local container_name="${1}"
	local deployment_name="${2}"
	local region="${3}"
	local resource_group_name="${4}"
	local storage_account_name="${5}"

	local directory

	for directory in aks platform
	do
		cat > "${_ROOT_CLOUD_DIR}/terraform/azure/${directory}/backend_override.tf" <<EOF
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

function _generate_tfvars {
	local configuration_json_file="${1}"
	local module="${2}"

	local tfvars_file="${_ROOT_CLOUD_DIR}/terraform/azure/${module}/config.auto.tfvars.json"

	echo "Generating ${tfvars_file} from ${configuration_json_file}."

	jq --arg module "${module}" '(.terraform.common // {}) * (.terraform[$module] // {})' "${configuration_json_file}" > "${tfvars_file}"

	echo "${tfvars_file} was generated successfully."
}

function _get_terraform_destroy_args {
	local configuration_json_file="${1}"

	local auto_approve

	auto_approve=$(jq --raw-output '.options.auto_approve // false' "${configuration_json_file}")

	local destroy_args=()

	if [[ ${auto_approve} == true ]]
	then
		destroy_args+=("-auto-approve")
	fi

	local parallelism

	parallelism=$(jq --raw-output '.options.parallelism | numbers' "${configuration_json_file}")

	if [[ -n ${parallelism} ]]
	then
		destroy_args+=("-parallelism=${parallelism}")
	fi

	if [ ${#destroy_args[@]} -gt 0 ]
	then
		printf '%s\n' "${destroy_args[@]}"
	fi
}

function _popd {
	popd > /dev/null
}

function _pushd {
	pushd "${1}" > /dev/null
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
		echo "The liferay-platform Helm release was not uninstalled after 10 minutes. If an application is stuck on the resources-finalizer.argocd.argoproj.io finalizer, remove the finalizer with kubectl patch and rerun this script." >&2

		exit 1
	fi
}

function _validate_config_json {
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

main "${@}"