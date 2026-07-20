#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

_SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

_ROOT_CLOUD_DIR=$(cd "${_SCRIPTS_DIR}/.." && pwd)

function main {
	if [ "${#}" -ne 2 ]
	then
		echo "Usage: ${0} <configuration-json-file> <versions-tfvars-file>" >&2
		echo "" >&2
		echo "See cloud/scripts/config.json.example_azure for a sample." >&2

		exit 1
	fi

	_check_utils az jq terraform

	_check_terraform_version "1.10.0"

	_validate_config_json "${1}"

	_validate_versions_tfvars "${2}"

	_generate_tfvars "${1}" "${_SCRIPTS_DIR}/global_terraform.tfvars"

	local subscription_id

	subscription_id="$(jq --raw-output '.variables.subscription_id // empty' "${1}")"

	local tenant_id

	tenant_id="$(jq --raw-output '.variables.tenant_id // empty' "${1}")"

	echo "Attempting to login to your Azure account."

	AZURE_CORE_LOGIN_EXPERIENCE_V2=off az login --output none --tenant "${tenant_id}"

	az account set --subscription "${subscription_id}"

	local container_name=""
	local deployment_name=""
	local region=""
	local resource_group_name=""
	local storage_account_name=""

	local terraform_args=()

	while IFS= read -r terraform_arg
	do
		terraform_args+=("${terraform_arg}")
	done < <(_get_terraform_apply_args "${1}" "${2}")

	if jq --exit-status '.variables.tfstate_storage_account_name' "${1}" &> /dev/null
	then
		if ! jq --exit-status '.variables.deployment_name' "${1}" &> /dev/null
		then
			echo "The configuration JSON file must contain a key named \"variables.deployment_name\"." >&2

			exit 1
		fi

		if ! jq --exit-status '.variables.region' "${1}" &> /dev/null
		then
			echo "The configuration JSON file must contain a key named \"variables.region\"." >&2

			exit 1
		fi

		if ! jq --exit-status '.variables.tfstate_container_name' "${1}" &> /dev/null
		then
			echo "The configuration JSON file must contain a key named \"variables.tfstate_container_name\"." >&2

			exit 1
		fi

		if ! jq --exit-status '.variables.tfstate_resource_group_name' "${1}" &> /dev/null
		then
			echo "The configuration JSON file must contain a key named \"variables.tfstate_resource_group_name\"." >&2

			exit 1
		fi

		container_name="$(jq --raw-output '.variables.tfstate_container_name' "${1}")"
		deployment_name="$(jq --raw-output '.variables.deployment_name' "${1}")"
		region="$(jq --raw-output '.variables.region' "${1}")"
		resource_group_name="$(jq --raw-output '.variables.tfstate_resource_group_name' "${1}")"
		storage_account_name="$(jq --raw-output '.variables.tfstate_storage_account_name' "${1}")"

		_create_tfstate_storage "${container_name}" "${region}" "${resource_group_name}" "${storage_account_name}"
	fi

	_set_up_azure_aks "${container_name}" "${deployment_name}" "${region}" "${resource_group_name}" "${storage_account_name}" "${terraform_args[@]}"
}

function _check_terraform_version {
	local found_version

	found_version=$(terraform --version | awk '/^Terraform v/ {print $2; exit}')
	found_version="${found_version#v}"

	local required_version="${1}"

	local lowest_version

	lowest_version=$(printf "%s\n%s\n" "${required_version}" "${found_version}" | sort --version-sort | head -n 1)

	if [ "${lowest_version}" != "${required_version}" ]
	then
		echo "The installed Terraform version ${found_version} is older than ${required_version}." >&2

		exit 1
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

function _create_tfstate_storage {
	local container_name="${1}"
	local region="${2}"
	local resource_group_name="${3}"
	local storage_account_name="${4}"

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

function _generate_tfvars {
	local configuration_json_file="${1}"
	local tfvars_file="${2}"

	echo "Generating ${tfvars_file} from ${configuration_json_file}."

	local tfvars_content

	tfvars_content=$( \
		jq --raw-output '.variables
		| to_entries[]
		| if (.value | type) == "string"
		  then
		  	"\(.key) = \"\(.value)\""
		  elif (.value | type) == "array" or (.value | type) == "object"
		  then
		  	"\(.key) = \(.value | @json)"
		  else
		  	"\(.key) = \(.value)"
		  end' "${configuration_json_file}")

	if [ -z "${tfvars_content}" ]
	then
		echo "The \"variables\" object in the configuration JSON file is empty. You will be prompted for all required variables."

		> "${tfvars_file}"
	else
		echo "${tfvars_content}" > "${tfvars_file}"
	fi

	echo "${tfvars_file} was generated successfully."
}

function _get_terraform_apply_args {
	local auto_approve="false"

	local configuration_json_file="${1}"

	if jq --exit-status '.options.auto_approve' "${configuration_json_file}" > /dev/null
	then
		auto_approve=$(jq --raw-output '.options.auto_approve' "${configuration_json_file}")
	fi

	local versions_tfvars_file="${2}"

	local versions_tfvars_file_path

	versions_tfvars_file_path=$(_resolve_path "${versions_tfvars_file}")

	local apply_args=(
		"-var-file=${versions_tfvars_file_path}"
		"-var-file=${_SCRIPTS_DIR}/global_terraform.tfvars")

	if [[ "${auto_approve}" == "true" ]]
	then
		apply_args+=("-auto-approve")
	fi

	if jq --exit-status '.options.parallelism | numbers' "${configuration_json_file}" > /dev/null
	then
		local parallelism

		parallelism=$(jq --raw-output '.options.parallelism' "${configuration_json_file}")

		apply_args+=("-parallelism=${parallelism}")
	fi

	printf '%s\n' "${apply_args[@]}"
}

function _log {
	echo "[Tfstate storage configuration] ${1}"
}

function _popd {
	popd > /dev/null
}

function _pushd {
	pushd "${1}" > /dev/null
}

function _resolve_path {
	local file_path="${1}"

	if [ ! -e "${file_path}" ]
	then
		echo "Path ${file_path} does not exist." >&2

		exit 1
	fi

	local dir_path

	if ! dir_path=$(cd "$(dirname "${file_path}")" && pwd)
	then
		echo "Failed to resolve directory for ${file_path}." >&2

		exit 1
	fi

	printf '%s/%s\n' "${dir_path}" "$(basename "${file_path}")"
}

function _set_up_azure_aks {
	local container_name="${1}"
	local deployment_name="${2}"
	local region="${3}"
	local resource_group_name="${4}"
	local storage_account_name="${5}"

	_pushd "${_ROOT_CLOUD_DIR}/terraform/azure/aks"

	echo "Setting up the Azure AKS cluster."

	_terraform_init_and_apply "." "aks" "${container_name}" "${deployment_name}" "${region}" "${resource_group_name}" "${storage_account_name}" "${@:6}"

	export KUBE_CONFIG_PATH="${HOME}/.kube/config"

	az aks get-credentials \
		--name "$(terraform output -raw cluster_name)" \
		--overwrite-existing \
		--resource-group "$(terraform output -raw resource_group_name)"

	echo "Azure AKS cluster setup complete."

	_popd
}

function _terraform_init_and_apply {
	local container_name="${3}"
	local deployment_name="${4}"
	local folder_separator="${2}"
	local region="${5}"
	local resource_group_name="${6}"
	local storage_account_name="${7}"

	_pushd "${1}"

	if [ -n "${storage_account_name}" ]
	then
		terraform init \
			-backend-config="container_name=${container_name}" \
			-backend-config="key=${deployment_name}/${region}/${folder_separator}/terraform.tfstate" \
			-backend-config="resource_group_name=${resource_group_name}" \
			-backend-config="storage_account_name=${storage_account_name}" \
			-backend-config="use_azuread_auth=true"
	else
		cat > backend_override.tf <<EOF
terraform {
	backend "local" {}
}
EOF
		terraform init
	fi

	terraform apply "${@:8}"

	_popd
}

function _validate_config_json {
	local configuration_json_file="${1}"

	if [ ! -f "${configuration_json_file}" ]
	then
		echo "Configuration JSON file ${configuration_json_file} does not exist." >&2

		exit 1
	fi

	if ! jq empty "${configuration_json_file}" &> /dev/null
	then
		echo "Configuration JSON file ${configuration_json_file} is not valid JSON." >&2

		exit 1
	fi

	if ! jq --exit-status '.variables | objects' "${configuration_json_file}" > /dev/null
	then
		echo "The configuration JSON file must contain a root object named \"variables\"." >&2

		exit 1
	fi
}

function _validate_versions_tfvars {
	local versions_tfvars_file="${1}"

	if [ ! -f "${versions_tfvars_file}" ]
	then
		echo "Versions tfvars file ${versions_tfvars_file} does not exist." >&2

		exit 1
	fi
}

main "${@}"