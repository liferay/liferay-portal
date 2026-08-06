#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

_SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

_ROOT_CLOUD_DIR=$(cd "${_SCRIPTS_DIR}/.." && pwd)

readonly _ROOT_CLOUD_DIR _SCRIPTS_DIR

function main {
	if [ "${#}" -eq 0 ]
	then
		echo "Usage: ${0} <configuration-json-file>" >&2
		echo "" >&2
		echo "See cloud/scripts/config.json.example_azure for a sample." >&2

		exit 1
	fi

	_check_utils az helm jq terraform

	_check_terraform_version "1.10.0"

	_validate_config_json "${1}"

	_generate_tfvars "${1}" "${_SCRIPTS_DIR}/global_terraform.tfvars"

	local subscription_id

	subscription_id="$(jq --raw-output '.variables.subscription_id' "${1}")"

	export ARM_SUBSCRIPTION_ID="${subscription_id}"

	local tenant_id

	tenant_id="$(jq --raw-output '.variables.tenant_id' "${1}")"

	export ARM_TENANT_ID="${tenant_id}"

	echo "Attempting to login to your Azure account."

	AZURE_CORE_LOGIN_EXPERIENCE_V2=off az login --output none --tenant "${tenant_id}"

	az account set --subscription "${subscription_id}"

	local deployment_name

	deployment_name="$(jq --raw-output '.variables.deployment_name' "${1}")"

	_check_key_vault "${1}" "${deployment_name}"

	local terraform_args=()

	while IFS= read -r terraform_arg
	do
		terraform_args+=("${terraform_arg}")
	done < <(_get_terraform_apply_args "${1}")

	if jq --exit-status '.variables.tfstate_storage_account_name' "${1}" &> /dev/null
	then
		local container_name
		local region
		local resource_group_name
		local storage_account_name

		container_name="$(jq --raw-output '.variables.tfstate_container_name' "${1}")"
		region="$(jq --raw-output '.variables.region' "${1}")"
		resource_group_name="$(jq --raw-output '.variables.tfstate_resource_group_name' "${1}")"
		storage_account_name="$(jq --raw-output '.variables.tfstate_storage_account_name' "${1}")"

		_create_tfstate_storage "${container_name}" "${region}" "${resource_group_name}" "${storage_account_name}"

		_generate_remote_backend_overrides "${container_name}" "${deployment_name}" "${region}" "${resource_group_name}" "${storage_account_name}"
	else
		_generate_local_backend_overrides
	fi

	_set_up_azure_aks "${terraform_args[@]}"

	_set_up_azure_platform "${terraform_args[@]}"

	_install_liferay_platform_chart "${1}"
}

function _check_key_vault {
	local configuration_json_file="${1}"
	local deployment_name="${2}"

	if jq --exit-status '.variables.cluster_secret_store_provider_hcl' "${configuration_json_file}" &> /dev/null
	then
		return 0
	fi

	local key_vault_name

	key_vault_name="$(jq --arg default_name "${deployment_name}-vault" --raw-output '.variables.key_vault_name // $default_name' "${configuration_json_file}")"

	local key_vault_resource_group_name

	key_vault_resource_group_name="$(jq --arg default_name "${deployment_name}-vault" --raw-output '.variables.key_vault_resource_group_name // $default_name' "${configuration_json_file}")"

	if ! az keyvault show --name "${key_vault_name}" --resource-group "${key_vault_resource_group_name}" &> /dev/null
	then
		echo "The default cluster secret store requires an Azure key vault named ${key_vault_name} in the resource group ${key_vault_resource_group_name}, holding the GitOps repository credentials secret (liferay-credentials-gitops by default)." >&2
		echo "Create the key vault, point at an existing one with \"variables.key_vault_name\" and \"variables.key_vault_resource_group_name\" in the configuration JSON file, or set \"variables.cluster_secret_store_provider_hcl\" to bring your own secret store." >&2

		exit 1
	fi

	local rbac_authorization_enabled

	rbac_authorization_enabled=$( \
		az keyvault show \
			--name "${key_vault_name}" \
			--output tsv \
			--query properties.enableRbacAuthorization \
			--resource-group "${key_vault_resource_group_name}")

	if [ "${rbac_authorization_enabled}" != "true" ]
	then
		echo "The key vault ${key_vault_name} uses the access policy permission model, but the Liferay platform grants vault access through Azure RBAC roles, so the External Secrets operator would be denied access." >&2
		echo "Run \"az keyvault update --enable-rbac-authorization true --name ${key_vault_name}\" to switch the permission model, and run this script again." >&2

		exit 1
	fi
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
	local configuration_json_file="${1}"

	local auto_approve

	auto_approve=$(jq --raw-output '.options.auto_approve // false' "${configuration_json_file}")

	local apply_args=(
		"-var-file=${_SCRIPTS_DIR}/global_terraform.tfvars")

	if [[ "${auto_approve}" == "true" ]]
	then
		apply_args+=("-auto-approve")
	fi

	local parallelism

	parallelism=$(jq --raw-output '.options.parallelism | numbers' "${configuration_json_file}")

	if [ -n "${parallelism}" ]
	then
		apply_args+=("-parallelism=${parallelism}")
	fi

	printf '%s\n' "${apply_args[@]}"
}

function _install_liferay_platform_chart {
	local configuration_json_file="${1}"

	local platform_helm_chart_version

	platform_helm_chart_version=$(jq --raw-output '."liferay-platform"' "${_SCRIPTS_DIR}/chart_versions.json")

	echo "Applying the Liferay platform root application."

	local terraform_outputs

	_pushd "${_ROOT_CLOUD_DIR}/terraform/azure/platform"

	terraform_outputs=$(terraform output -json)

	_popd

	jq \
		--argjson terraform_outputs "${terraform_outputs}" \
		--null-input \
		--slurpfile chart_versions "${_SCRIPTS_DIR}/chart_versions.json" \
		--slurpfile configuration "${configuration_json_file}" \
		'{
			platformComponents: {
				values: ({
					infrastructure: {
						chart: "liferay-azure-infrastructure",
						repoURL: "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart",
						targetRevision: $chart_versions[0]."liferay-azure-infrastructure"
					},
					infrastructureProvider: {
						chart: "liferay-azure-infrastructure-provider",
						repoURL: "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart",
						targetRevision: $chart_versions[0]."liferay-azure-infrastructure-provider"
					},
					liferay: {
						chart: "liferay-azure",
						repoURL: "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart",
						targetRevision: $chart_versions[0]."liferay-azure"
					}
				} * ($configuration[0].values // {}) * {
					clusterIdentity: $terraform_outputs.cluster_identity.value,
					clusterSecretStore: {
						enabled: true,
						provider: $terraform_outputs.cluster_secret_store_provider.value
					},
					operatorApplications: {
						externalSecrets: {
							values: {
								serviceAccount: {
									annotations: {
										"azure.workload.identity/client-id": $terraform_outputs.external_secrets_client_id.value
									}
								}
							}
						}
					}
				})
			}
		}' \
	| helm \
		upgrade \
		liferay-platform \
		oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/liferay-platform \
		--install \
		--namespace argocd-system \
		--values - \
		--version "${platform_helm_chart_version}"
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

function _set_up_azure_aks {
	_pushd "${_ROOT_CLOUD_DIR}/terraform/azure/aks"

	echo "Setting up the Azure AKS cluster."

	terraform init

	terraform apply "${@}"

	export KUBE_CONFIG_PATH="${HOME}/.kube/config"

	az aks get-credentials \
		--name "$(terraform output -raw cluster_name)" \
		--overwrite-existing \
		--resource-group "$(terraform output -raw resource_group_name)"

	echo "Azure AKS cluster setup complete."

	_popd
}

function _set_up_azure_platform {
	_pushd "${_ROOT_CLOUD_DIR}/terraform/azure/platform"

	echo "Setting up the Liferay platform."

	terraform init

	terraform apply "${@}"

	echo "Liferay platform setup complete."

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

	local required_variables=(deployment_name region subscription_id tenant_id)

	if jq --exit-status '.variables.tfstate_storage_account_name' "${configuration_json_file}" &> /dev/null
	then
		required_variables+=(tfstate_container_name tfstate_resource_group_name)
	fi

	local required_variable

	for required_variable in "${required_variables[@]}"
	do
		if ! jq --exit-status ".variables.${required_variable}" "${configuration_json_file}" &> /dev/null
		then
			echo "The configuration JSON file must contain a key named \"variables.${required_variable}\"." >&2

			exit 1
		fi
	done
}

main "${@}"