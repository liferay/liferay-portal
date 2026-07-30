#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

function main {
	if [[ -z ${ACTIVATION_CODE:-} ]]
	then
		echo "Usage: ${0}"
		echo ""
		echo "The script reads the following environment variables:"
		echo ""
		echo "    ACTIVATION_CODE: Liferay DXP activation code stored in the dev-activation secret"

		exit 1
	fi

	if k3d cluster list ${_CLUSTER_NAME} > /dev/null 2>&1
	then
		k3d cluster start ${_CLUSTER_NAME}
	else
		k3d cluster create ${_CLUSTER_NAME} --registry-create liferay-registry:0.0.0.0:5001
	fi

	kubectl config use-context ${_KUBE_CONTEXT}

	tilt up
}

_CLUSTER_NAME=operator-dev
_KUBE_CONTEXT=k3d-operator-dev

main "${@}"