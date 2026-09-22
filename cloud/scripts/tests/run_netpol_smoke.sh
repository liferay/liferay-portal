#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

#
# Asserts that the paths our ingress NetworkPolicies must leave open are still
# open, against a real cluster. Rendered-YAML tests cannot do this: a rule can
# name the right port and a well-formed source and still match no traffic.
#
# Every check is a server-side dry run. The only thing created is the crossplane
# fixture, which is removed again.
#
# The manifests live beside this script rather than in heredocs. A heredoc body
# indented for readability is rewritten by the source formatter, which collapses
# the embedded YAML and makes every check fail for the wrong reason.
#

function main {
	local context="${1:-}"

	if [[ -z ${context} ]]
	then
		echo "Usage: ${0##*/} <kube-context>"

		exit 2
	fi

	local script_dir

	script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

	local manifest_dir="${script_dir}/netpol-smoke"

	local failed=0

	check_external_secrets_webhook "${context}" "${manifest_dir}" || failed=1

	check_eck_webhook "${context}" "${manifest_dir}" || failed=1

	check_crossplane_webhook "${context}" "${manifest_dir}" || failed=1

	if [[ ${failed} -ne 0 ]]
	then
		echo
		echo "FAIL: a webhook the API server must reach is unreachable."
		echo "On GKE the API server tunnels through kube-system/konnectivity-agent,"
		echo "so an ipBlock on the control plane CIDR alone never matches."

		exit 1
	fi

	echo
	echo "PASS: every webhook path checked is still open."
}

#
# Crossplane's webhook only fires on DELETE of objects labelled
# crossplane.io/in-use=true, so the check creates one. It is failurePolicy
# Fail, so an unreachable webhook surfaces as a webhook-call error rather than
# as the in-use denial we want to see.
#
function check_crossplane_webhook {
	local context="${1}"
	local manifest_dir="${2}"

	if ! kubectl --context "${context}" get crd usages.protection.crossplane.io > /dev/null 2>&1
	then
		echo "SKIP  crossplane: Usage CRD not installed"

		return 0
	fi

	if ! kubectl --context "${context}" apply -f "${manifest_dir}/crossplane-fixture.yaml" > /dev/null 2>&1
	then
		echo "ERROR crossplane: could not create the fixture - nothing was verified"

		return 1
	fi

	local attempt

	for attempt in 1 2 3 4 5 6
	do
		if [[ $(kubectl --context "${context}" -n crossplane-system get configmap netpol-smoke-cm -o jsonpath='{.metadata.labels.crossplane\.io/in-use}' 2>/dev/null) == "true" ]]
		then
			break
		fi

		sleep 5
	done

	local output

	output=$(kubectl --context "${context}" -n crossplane-system delete configmap netpol-smoke-cm --dry-run=server 2>&1 || true)

	cleanup_crossplane_fixture "${context}"

	if [[ ${output} == *"failed calling webhook"* ]]
	then
		echo "FAIL  crossplane: the API server could not call the webhook"

		return 1
	fi

	if [[ ${output} != *"in-use"* ]]
	then
		echo "ERROR crossplane: expected an in-use denial, got: ${output:0:100}"

		return 1
	fi

	echo "ok    crossplane: in-use delete denied, webhook reachable"
}

#
# ECK's webhook is failurePolicy Ignore, so a blocked webhook returns SUCCESS
# and validation silently stops. The check requires an explicit denial from the
# webhook: a rejection for any other reason, such as a malformed manifest,
# proves nothing and is reported as an error rather than as a pass.
#
function check_eck_webhook {
	local context="${1}"
	local manifest_dir="${2}"

	if ! kubectl --context "${context}" get crd elasticsearches.elasticsearch.k8s.elastic.co > /dev/null 2>&1
	then
		echo "SKIP  eck: CRD not installed"

		return 0
	fi

	local output

	output=$(kubectl --context "${context}" apply --dry-run=server -f "${manifest_dir}/eck-invalid.yaml" 2>&1 || true)

	if [[ ${output} == *"denied the request"* ]]
	then
		echo "ok    eck: invalid resource denied by the webhook"

		return 0
	fi

	if [[ ${output} == *"created"* || ${output} == *"configured"* ]]
	then
		echo "FAIL  eck: an invalid Elasticsearch was ADMITTED - the webhook is not being called"

		return 1
	fi

	echo "ERROR eck: no webhook denial and no admission, got: ${output:0:100}"

	return 1
}

#
# ESO's webhook is failurePolicy Fail, so an unreachable webhook surfaces as an
# admission error on a resource that should be accepted.
#
function check_external_secrets_webhook {
	local context="${1}"
	local manifest_dir="${2}"

	if ! kubectl --context "${context}" get crd secretstores.external-secrets.io > /dev/null 2>&1
	then
		echo "SKIP  external-secrets: CRD not installed"

		return 0
	fi

	local output

	output=$(kubectl --context "${context}" apply --dry-run=server -f "${manifest_dir}/external-secrets-valid.yaml" 2>&1 || true)

	if [[ ${output} == *"failed calling webhook"* ]]
	then
		echo "FAIL  external-secrets: the API server could not call the webhook"

		return 1
	fi

	if [[ ${output} != *"created"* && ${output} != *"configured"* ]]
	then
		echo "ERROR external-secrets: a valid SecretStore was not admitted, got: ${output:0:100}"

		return 1
	fi

	echo "ok    external-secrets: valid resource admitted, webhook reachable"
}

function cleanup_crossplane_fixture {
	local context="${1}"

	kubectl --context "${context}" -n crossplane-system delete usages.protection.crossplane.io netpol-smoke-usage > /dev/null 2>&1 || true

	sleep 3

	kubectl --context "${context}" -n crossplane-system delete configmap netpol-smoke-cm > /dev/null 2>&1 || true
}

main "${@}"