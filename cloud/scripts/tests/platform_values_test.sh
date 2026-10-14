#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

SCRIPTS_DIR=$(cd "$(dirname "${0}")/.." && pwd)

readonly SCRIPTS_DIR

function main {
	local fail=0
	local pass=0

	_run_test _test_aws_converts_kubernetes_endpoint_cidrs
	_run_test _test_aws_enables_ack_prometheus_service_with_role
	_run_test _test_aws_enables_keda_with_role
	_run_test _test_aws_enables_observability_with_workspace
	_run_test _test_aws_selects_liferay_chart_from_marketplace
	_run_test _test_azure_enables_keda_with_identity
	_run_test _test_azure_enables_observability_with_workspace
	_run_test _test_customer_values_override_platform_values

	echo ""
	echo "Results: ${pass} passed, ${fail} failed."

	if [ ${fail} -eq 0 ]
	then
		return 0
	fi

	return 1
}

function _assert {
	local platform_values="${1}"
	local filter="${2}"

	if ! jq --exit-status ".platformComponents.values | ${filter}" <<< "${platform_values}" > /dev/null
	then
		echo "Assertion failed: ${filter}" >&2
		echo "${platform_values}" >&2

		return 1
	fi
}

function _get_aws_outputs {
	jq \
		--argjson extra_outputs "${1:-{\}}" \
		--null-input \
		'{
			cluster_secret_store_provider: {value: {aws: {region: "us-east-1", service: "SecretsManager"}}},
			deployment_context: {value: {deploymentName: "liferay-test", region: "us-east-1"}},
			external_secrets_role_arn: {value: "arn:aws:iam::111111111111:role/liferay-test-external-secrets"},
			kubernetes_endpoint_cidrs: {value: ["10.0.1.0/24", "10.0.2.0/24"]},
			marketplace_volume_handle: {value: "fs-0123456789abcdef0::fsap-0123456789abcdef0"}
		} + $extra_outputs'
}

function _get_azure_outputs {
	jq \
		--argjson extra_outputs "${1:-{\}}" \
		--null-input \
		'{
			cluster_secret_store_provider: {value: {azurekv: {vaultUrl: "https://liferay-test.vault.azure.net/"}}},
			deployment_context: {value: {deploymentName: "liferay-test", region: "eastus2", resourceGroupName: "liferay-test", subscriptionId: "subscription-id", tenantId: "tenant-id"}},
			external_secrets_client_id: {value: "external-secrets-client-id"}
		} + $extra_outputs'
}

function _render {
	local cloud="${1}"
	local outputs="${2}"
	local configuration="${3:-{\}}"

	jq \
		--argjson configuration "$(jq --slurp '[.[0] * .[1]]' "${SCRIPTS_DIR}/config.json.defaults" <(echo "${configuration}"))" \
		--from-file "${SCRIPTS_DIR}/_${cloud}_platform_values.jq" \
		<<< "${outputs}"
}

function _run_test {
	local test_function="${1}"

	local description

	description=$(echo "${test_function}" | sed "s/^_test_//; s/_/ /g")

	if "${test_function}"
	then
		echo "PASS: ${description}."

		((++pass))
	else
		echo "FAIL: ${description}."

		((++fail))
	fi
}

function _test_aws_converts_kubernetes_endpoint_cidrs {
	local platform_values

	platform_values=$(_render "aws" "$(_get_aws_outputs)")

	_assert "${platform_values}" '.networkPolicy.apiServerSources == [{ipBlock: {cidr: "10.0.1.0/24"}}, {ipBlock: {cidr: "10.0.2.0/24"}}]' &&
	_assert "${platform_values}" '.liferay.parameters[] | select(.name == "liferay-default.networkPolicy.cluster.kubernetesEndpointCidrs") | .value == "10.0.1.0/24,10.0.2.0/24"'
}

function _test_aws_enables_ack_prometheus_service_with_role {
	_assert "$(_render "aws" "$(_get_aws_outputs)")" '.operatorApplications | has("ackPrometheusService") | not' &&
	_assert "$(_render "aws" "$(_get_aws_outputs '{"ack_prometheusservice_role_arn": {"value": "ack-role-arn"}}')")" '.operatorApplications.ackPrometheusService.enabled'
}

function _test_aws_enables_keda_with_role {
	_assert "$(_render "aws" "$(_get_aws_outputs)")" '.operatorApplications.keda | has("enabled") | not' &&
	_assert "$(_render "aws" "$(_get_aws_outputs '{"keda_role_arn": {"value": "keda-role-arn"}}')")" '.operatorApplications.keda.enabled'
}

function _test_aws_enables_observability_with_workspace {
	_assert "$(_render "aws" "$(_get_aws_outputs)")" '.observability | has("enabled") | not' &&
	_assert "$(_render "aws" "$(_get_aws_outputs '{"prometheus_workspace_id": {"value": "workspace-id"}}')")" '.observability.enabled'
}

function _test_aws_selects_liferay_chart_from_marketplace {
	_assert "$(_render "aws" "$(_get_aws_outputs)" '{"marketplace": false}')" '.liferay | (.repoURL | endswith("/liferay-aws")) and .valuesPrefix == "liferay-default." and all(.parameters[]; .name | startswith("liferay-default."))' &&
	_assert "$(_render "aws" "$(_get_aws_outputs)" '{"marketplace": true}')" '.liferay | (.repoURL | endswith("/liferay-aws-marketplace")) and .valuesPrefix == "liferay-aws.liferay-default." and all(.parameters[]; .name | startswith("liferay-aws.liferay-default."))'
}

function _test_azure_enables_keda_with_identity {
	_assert "$(_render "azure" "$(_get_azure_outputs)")" '.operatorApplications.keda | has("enabled") | not' &&
	_assert "$(_render "azure" "$(_get_azure_outputs '{"keda_identity_client_id": {"value": "keda-client-id"}}')")" '.operatorApplications.keda.enabled'
}

function _test_azure_enables_observability_with_workspace {
	_assert "$(_render "azure" "$(_get_azure_outputs)")" '(.observability | has("enabled") | not) and (.liferay | has("parameters") | not)' &&
	_assert "$(_render "azure" "$(_get_azure_outputs '{"prometheus_workspace_endpoint": {"value": "workspace-endpoint"}, "prometheus_workspace_id": {"value": "workspace-id"}}')")" '.observability.enabled and any(.liferay.parameters[]; .name == "global.azure.prometheusWorkspaceEndpoint")'
}

function _test_customer_values_override_platform_values {
	_assert "$(_render "aws" "$(_get_aws_outputs)" '{"platformComponents": {"values": {"networkPolicy": {"apiServerSources": [{"ipBlock": {"cidr": "192.0.2.0/24"}}]}}}}')" '.networkPolicy.apiServerSources == [{ipBlock: {cidr: "192.0.2.0/24"}}]'
}

main "${@}"