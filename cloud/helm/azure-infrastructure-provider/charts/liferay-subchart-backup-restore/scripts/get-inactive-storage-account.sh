#!/bin/sh

set -o errexit
set -o nounset

function main {
	local data_plane_inactive

	data_plane_inactive="{{ "{{" }}inputs.parameters.data-inactive}}"

	local timeout

	timeout=$(($(date +%s) + {{ .Values.liferayInfrastructure.waitTimeoutSeconds }}))

	while [[ "$(date +%s)" -lt "${timeout}" ]]
	do
		local ready

		ready=$( \
			kubectl get accounts.storage.azure.m.upbound.io \
				--output jsonpath="{.items[0].status.conditions[?(@.type=='Ready')].status}" \
				--selector "dataPlane=${data_plane_inactive}" 2> /dev/null || echo "")

		if [ "${ready}" == "True" ]
		then
			break
		fi

		echo "The ${data_plane_inactive} storage account is not ready yet."

		sleep 15
	done

	if [ "${ready}" != "True" ]
	then
		echo "The ${data_plane_inactive} storage account did not become ready before the timeout." >&2

		exit 1
	fi

	kubectl get accounts.storage.azure.m.upbound.io \
		--output jsonpath="{.items[0].status.atProvider.id}" \
		--selector "dataPlane=${data_plane_inactive}" \
		> /tmp/storage-account-id-inactive.txt

	kubectl get accounts.storage.azure.m.upbound.io \
		--output jsonpath="{.items[0].metadata.name}" \
		--selector "dataPlane=${data_plane_inactive}" \
		> /tmp/storage-account-name-inactive.txt

	kubectl get accounts.storage.azure.m.upbound.io \
		--output jsonpath="{.items[0].spec.forProvider.location}" \
		--selector "dataPlane=${data_plane_inactive}" \
		> /tmp/storage-account-location.txt
}

main