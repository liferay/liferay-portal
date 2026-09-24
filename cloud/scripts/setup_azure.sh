#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

source "$(dirname "${BASH_SOURCE[0]}")/_azure_common.sh"

_CONFIG_JSON_DEFAULTS_FILE="${SCRIPTS_DIR}/config.json.defaults"

readonly _CONFIG_JSON_DEFAULTS_FILE

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

	configuration_json_file=$(_resolve_config_json "${1}")

	generate_tfvars "${configuration_json_file}" "aks"

	generate_tfvars "${configuration_json_file}" "platform"

	az_login "${configuration_json_file}"

	local terraform_args=()

	while IFS= read -r terraform_arg
	do
		terraform_args+=("${terraform_arg}")
	done < <(get_terraform_args "${configuration_json_file}")

	if jq --exit-status '.tfstate | objects' "${configuration_json_file}" &> /dev/null
	then
		local container_name
		local deployment_name
		local region
		local resource_group_name
		local storage_account_name

		container_name="$(jq --raw-output '.tfstate.container_name' "${configuration_json_file}")"
		deployment_name="$(jq --raw-output '.deployment_name' "${configuration_json_file}")"
		region="$(jq --raw-output '.region' "${configuration_json_file}")"
		resource_group_name="$(jq --raw-output '.tfstate.resource_group_name' "${configuration_json_file}")"
		storage_account_name="$(jq --raw-output '.tfstate.storage_account_name' "${configuration_json_file}")"

		_create_tfstate_storage "${container_name}" "${region}" "${resource_group_name}" "${storage_account_name}"

		ensure_tfstate_access "${container_name}" "${resource_group_name}" "${storage_account_name}"

		generate_remote_backend_overrides "${container_name}" "${deployment_name}" "${region}" "${resource_group_name}" "${storage_account_name}"
	else
		generate_local_backend_overrides
	fi

	_set_up_azure_aks "${terraform_args[@]}"

	connect_to_cluster

	_set_up_azure_platform "${terraform_args[@]}"

	_install_liferay_platform_chart "${configuration_json_file}"
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

function _get_artifact_values {
	local configuration_json_file="${1}"

	jq \
		'.artifacts
		| "oci://\(.registries.charts)" as $charts_registry
		| {
			platformComponents: {
				repoURL: "\($charts_registry)/liferay-platform-components",
				targetRevision: .charts."liferay-platform-components",
				values: {
					infrastructure: {
						repoURL: "\($charts_registry)/liferay-infrastructure",
						targetRevision: .charts."liferay-infrastructure"
					},
					infrastructureProvider: {
						repoURL: "\($charts_registry)/liferay-azure-infrastructure-provider",
						targetRevision: .charts."liferay-azure-infrastructure-provider"
					},
					liferay: {
						repoURL: "\($charts_registry)/liferay-azure",
						targetRevision: .charts."liferay-azure"
					},
					observability: {
						repoURL: "\($charts_registry)/observability",
						targetRevision: .charts.observability
					},
					operatorApplications: {
						dxpOperator: {
							repoURL: "\($charts_registry)/liferay-dxp-operator",
							targetRevision: .charts."liferay-dxp-operator",
							values: {
								image: {
									repository: "\(.registries.images)/liferay-dxp-operator",
									tag: .images."liferay-dxp-operator"
								}
							}
						}
					}
				}
			}
		}' \
		"${configuration_json_file}"
}

function _get_keda_operator_application {
	local platform_module_outputs=${1}
	local tenant_id=${2}

	jq \
		--arg tenant_id "${tenant_id}" \
		--argjson platform_module_outputs "${platform_module_outputs}" \
		--null-input \
		'($platform_module_outputs.keda_identity_client_id.value // "") as $client_id
		| if $client_id == "" then
			{}
		else
			{
				namespace: ($platform_module_outputs.keda_service_account_namespace.value // "keda-system"),
				values: {
					podIdentity: {
						azureWorkload: {
							clientId: $client_id,
							enabled: true,
							tenantId: $tenant_id
						}
					}
				}
			}
		end'
}

function _get_liferay_parameters {
	local platform_module_outputs=${1}

	jq \
		--argjson platform_module_outputs "${platform_module_outputs}" \
		--null-input \
		'[
			{
				name: "global.azure.prometheusWorkspaceEndpoint",
				value: ($platform_module_outputs.prometheus_workspace_endpoint.value // "")
			}
		]
		| map(select(.value != ""))'
}

function _get_observability_parameters {
	local platform_module_outputs=${1}
	local tenant_id=${2}

	jq \
		--arg tenant_id "${tenant_id}" \
		--argjson platform_module_outputs "${platform_module_outputs}" \
		--null-input \
		'[
			{
				name: "alloy.iam.azureClientId",
				value: ($platform_module_outputs.observability_identity_client_id.value // "")
			},
			{
				name: "azure.location",
				value: ($platform_module_outputs.deployment_context.value.region // "")
			},
			{
				name: "azure.prometheusWorkspaceEndpoint",
				value: ($platform_module_outputs.prometheus_workspace_endpoint.value // "")
			},
			{
				name: "azure.prometheusWorkspaceId",
				value: ($platform_module_outputs.prometheus_workspace_id.value // "")
			},
			{
				name: "azure.remoteWrite.dataCollectionRuleId",
				value: ($platform_module_outputs.prometheus_data_collection_rule_id.value // "")
			},
			{
				name: "azure.remoteWrite.metricsIngestionEndpoint",
				value: ($platform_module_outputs.prometheus_metrics_ingestion_endpoint.value // "")
			},
			{
				name: "azure.remoteWrite.tenantId",
				value: $tenant_id
			},
			{
				name: "azure.resourceGroupName",
				value: ($platform_module_outputs.deployment_context.value.resourceGroupName // "")
			},
			{
				name: "azure.subscriptionId",
				value: ($platform_module_outputs.deployment_context.value.subscriptionId // "")
			},
			{
				name: "cloudProvider",
				value: "azure"
			},
			{
				name: "grafana.grafana\\.ini.azure.workload_identity_client_id",
				value: ($platform_module_outputs.observability_identity_client_id.value // "")
			},
			{
				name: "grafana.grafana\\.ini.azure.workload_identity_tenant_id",
				value: $tenant_id
			},
			{
				name: "grafana.serviceAccount.annotations.azure\\.workload\\.identity/client-id",
				value: ($platform_module_outputs.observability_identity_client_id.value // "")
			}
		]
		| map(select(.value != ""))'
}

function _install_liferay_platform_chart {
	local configuration_json_file="${1}"

	local platform_repo_url
	local platform_target_revision

	platform_repo_url=$(jq --raw-output '"oci://\(.artifacts.registries.charts)/liferay-platform"' "${configuration_json_file}")
	platform_target_revision=$(jq --raw-output '.artifacts.charts."liferay-platform"' "${configuration_json_file}")

	echo "Applying the Liferay platform root application."

	local platform_module_outputs

	push_directory "${ROOT_CLOUD_DIR}/terraform/azure/platform"

	platform_module_outputs=$(terraform output -json)

	pop_directory

	local tenant_id

	tenant_id=$(jq --raw-output '.tenant_id' "${configuration_json_file}")

	local artifact_values

	artifact_values=$(_get_artifact_values "${configuration_json_file}")

	local keda_operator_application

	keda_operator_application=$(_get_keda_operator_application "${platform_module_outputs}" "${tenant_id}")

	local liferay_parameters

	liferay_parameters=$(_get_liferay_parameters "${platform_module_outputs}")

	local observability_parameters

	observability_parameters=$(_get_observability_parameters "${platform_module_outputs}" "${tenant_id}")

	jq \
		--argjson artifact_values "${artifact_values}" \
		--argjson keda_operator_application "${keda_operator_application}" \
		--argjson liferay_parameters "${liferay_parameters}" \
		--argjson observability_parameters "${observability_parameters}" \
		--argjson platform_module_outputs "${platform_module_outputs}" \
		--null-input \
		--slurpfile configuration "${configuration_json_file}" \
		'{
			platformComponents: {
				values: (($configuration[0].platformComponents.values // {}) * {
					clusterSecretStore: {
						enabled: true,
						name: ($platform_module_outputs.deployment_context.value.deploymentName + "-secret-store"),
						provider: $platform_module_outputs.cluster_secret_store_provider.value
					},
					deploymentContext: $platform_module_outputs.deployment_context.value,
					infrastructure: {
						parameters: [
							{
								name: "persistence.storageClassName",
								value: "managed-csi-premium-v2"
							}
						]
					},
					liferay: {
						parameters: $liferay_parameters
					},
					observability: {
						parameters: $observability_parameters
					},
					operatorApplications: {
						externalSecrets: {
							values: {
								serviceAccount: {
									annotations: {
										"azure.workload.identity/client-id": $platform_module_outputs.external_secrets_client_id.value
									}
								}
							}
						},
						keda: $keda_operator_application
					}
				})
			}
		} * $artifact_values' \
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

function _resolve_config_json {
	local configuration_json_file="${1}"

	local resolved_configuration_json_file

	resolved_configuration_json_file=$(mktemp)

	jq --slurp '.[0] * .[1]' "${_CONFIG_JSON_DEFAULTS_FILE}" "${configuration_json_file}" > "${resolved_configuration_json_file}"

	echo "${resolved_configuration_json_file}"
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