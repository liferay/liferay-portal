#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

#
# Asserts that the paths our ingress NetworkPolicies must leave open are still
# open, against a real cluster. Rendered-YAML tests cannot do this: a rule can
# name the right port and a well-formed source and still match no traffic.
#
# Every check is a server-side dry run. Nothing is created.
#

function main {
	local context="${1:-}"

	if [[ -z ${context} ]]
	then
		echo "Usage: ${0##*/} <kube-context>"

		exit 2
	fi

	local failed=0

	check_external_secrets_webhook "${context}" || failed=1

	check_eck_webhook "${context}" || failed=1

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
# ECK's webhook is failurePolicy Ignore, so a blocked webhook returns SUCCESS
# and validation silently stops. The only way to detect it is to submit
# something invalid and require a rejection.
#
function check_eck_webhook {
	local context="${1}"

	if ! kubectl --context "${context}" get crd elasticsearches.elasticsearch.k8s.elastic.co > /dev/null 2>&1
	then
		echo "SKIP  eck: CRD not installed"

		return 0
	fi

	if kubectl --context "${context}" apply --dry-run=server -f - > /dev/null 2>&1 <<-EOF
		apiVersion: elasticsearch.k8s.elastic.co/v1
		kind: Elasticsearch
		metadata:
		  name: netpol-smoke
		  namespace: elastic-system
		spec:
		  version: 0.0.0-not-a-real-version
		  nodeSets:
		    - name: default
		      count: 1
	EOF
	then
		echo "FAIL  eck: an invalid Elasticsearch was ADMITTED - the webhook is not being called"

		return 1
	fi

	echo "ok    eck: invalid resource rejected, webhook is being called"
}

#
# ESO's webhook is failurePolicy Fail, so an unreachable webhook surfaces as an
# admission error on a resource that should be accepted.
#
function check_external_secrets_webhook {
	local context="${1}"

	if ! kubectl --context "${context}" get crd secretstores.external-secrets.io > /dev/null 2>&1
	then
		echo "SKIP  external-secrets: CRD not installed"

		return 0
	fi

	if ! kubectl --context "${context}" apply --dry-run=server -f - > /dev/null 2>&1 <<-EOF
		apiVersion: external-secrets.io/v1
		kind: SecretStore
		metadata:
		  name: netpol-smoke
		  namespace: external-secrets-system
		spec:
		  provider:
		    gcpsm:
		      projectID: netpol-smoke
	EOF
	then
		echo "FAIL  external-secrets: a valid SecretStore was REJECTED - the webhook is unreachable"

		return 1
	fi

	echo "ok    external-secrets: valid resource admitted, webhook reachable"
}

main "${@}"
