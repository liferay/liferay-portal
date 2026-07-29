#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

CLUSTER_NAME="operator-dev"
KUBE_CONTEXT="k3d-operator-dev"

function main {
	if [ -z "${ACTIVATION_CODE:-}" ]
	then
		echo "ERROR: ACTIVATION_CODE is not set." >&2
		echo "Usage: export ACTIVATION_CODE=<code> ./tilt_up.sh" >&2

		exit 1
	fi

	if k3d cluster list "${CLUSTER_NAME}" >/dev/null 2>&1; then
		k3d cluster start "${CLUSTER_NAME}"
	else
		k3d cluster create "${CLUSTER_NAME}" --registry-create liferay-registry:0.0.0.0:5001
	fi

	kubectl config use-context "${KUBE_CONTEXT}"

	tilt up
}

main "${@}"