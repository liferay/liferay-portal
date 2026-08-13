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

	generate_tfvars "${1}" "aks"

	generate_tfvars "${1}" "platform"

	az_login "${1}"

	local terraform_args=()

	while IFS= read -r terraform_arg
	do
		terraform_args+=("${terraform_arg}")
	done < <(get_terraform_args "${1}")

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

		_create_tfstate_storage "${container_name}" "${region}" "${resource_group_name}" "${storage_account_name}"

		generate_remote_backend_overrides "${container_name}" "${deployment_name}" "${region}" "${resource_group_name}" "${storage_account_name}"
	else
		generate_local_backend_overrides
	fi

	_set_up_azure_aks "${terraform_args[@]}"

	connect_to_cluster

	_set_up_azure_platform "${terraform_args[@]}"

	_install_liferay_platform_chart "${1}"
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

function _install_liferay_platform_chart {
	local configuration_json_file="${1}"

	local platform_repo_url
	local platform_target_revision

	platform_repo_url=$(jq --raw-output '.platform.repoURL // "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/liferay-platform"' "${configuration_json_file}")
	platform_target_revision=$(jq --raw-output --slurpfile chart_versions "${SCRIPTS_DIR}/chart_versions.json" '.platform.targetRevision // $chart_versions[0]."liferay-platform"' "${configuration_json_file}")

	echo "Applying the Liferay platform root application."

	local terraform_outputs

	push_directory "${ROOT_CLOUD_DIR}/terraform/azure/platform"

	terraform_outputs=$(terraform output -json)

	pop_directory

	jq \
		--argjson terraform_outputs "${terraform_outputs}" \
		--null-input \
		--slurpfile configuration "${configuration_json_file}" \
		'($configuration[0].platform.values // {}) * {
			platformComponents: {
				values: (($configuration[0].platformComponents.values // {}) * {
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
		"${platform_repo_url}" \
		--install \
		--namespace argocd-system \
		--values - \
		--version "${platform_target_revision}"
}

function _log {
	echo "[Tfstate storage configuration] ${1}"
}

function _set_up_azure_aks {
	push_directory "${ROOT_CLOUD_DIR}/terraform/azure/aks"

	echo "Setting up the Azure AKS cluster."

	terraform init

	terraform apply -input=false "${@}"

	echo "Azure AKS cluster setup complete."

	pop_directory
}

function _set_up_azure_platform {
	push_directory "${ROOT_CLOUD_DIR}/terraform/azure/platform"

	echo "Setting up the Liferay platform."

	terraform init

	terraform apply -input=false "${@}"

	echo "Liferay platform setup complete."

	pop_directory
}

main "${@}"