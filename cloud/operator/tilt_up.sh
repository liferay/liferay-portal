#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

function main {
	if k3d cluster list operator-dev > /dev/null 2>&1
	then
		k3d cluster start operator-dev
	else
		k3d cluster create operator-dev --registry-create liferay-registry:0.0.0.0:5001
	fi

	kubectl config use-context k3d-operator-dev

	tilt up
}

main "${@}"