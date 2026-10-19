#!/bin/sh

set -o errexit
set -o nounset

function main {
	local liferay_infrastructure_json

	liferay_infrastructure_json=$( \
		kubectl get liferayinfrastructure \
			--output json \
			| jq ".items[0]")

	local restore_phase

	restore_phase=$(echo "${liferay_infrastructure_json}" | jq --raw-output ".spec.restorePhase")

	if [ "${restore_phase}" == "promoting" ] || [ "${restore_phase}" == "provisioning" ]
	then
		echo "The LiferayInfrastructure spec.restorePhase is set to ${restore_phase}. A restore is in progress." >&2

		exit 1
	fi

	echo "${liferay_infrastructure_json}" | jq --raw-output ".metadata.name" > /tmp/liferay-infrastructure-name.txt

	local data_plane_active

	data_plane_active=$(echo "${liferay_infrastructure_json}" | jq --raw-output ".spec.targetActiveDataPlane // \"blue\"")

	echo "${data_plane_active}" > /tmp/data-plane-active.txt

	local data_plane_inactive

	if [ "${data_plane_active}" == "blue" ]
	then
		data_plane_inactive="green"
	else
		data_plane_inactive="blue"
	fi

	echo "${data_plane_inactive}" > /tmp/data-plane-inactive.txt

	if [ -n "$( \
		kubectl get flexibleservers.{{ include "backupRestore.databaseApiGroup" . }} \
			--output jsonpath="{.items[*].metadata.name}" \
			--selector "dataPlane=${data_plane_inactive}")" ]
	then
		echo "The ${data_plane_inactive} data plane still holds a database server that is being released. Retry the restore once it is gone." >&2

		exit 1
	fi

	local storage_account_json_inactive

	storage_account_json_inactive=$( \
		kubectl get accounts.storage.azure.m.upbound.io \
			--output json \
			--selector "dataPlane=${data_plane_inactive}" \
			| jq ".items[0]")

	if [ "${storage_account_json_inactive}" != "null" ]
	then
		if echo "${storage_account_json_inactive}" | jq --exit-status '.metadata.deletionTimestamp != null and ([.status.conditions[]?.message // ""] | any(contains("ScopeLocked")))' > /dev/null
		then
			echo "The ${data_plane_inactive} storage account $(echo "${storage_account_json_inactive}" | jq --raw-output ".metadata.name") cannot be deleted while Azure Backup locks it. Suspend backups on the backup instance that protects it with az dataprotection backup-instance suspend-backup, then retry the restore." >&2

			exit 1
		fi

		echo "The ${data_plane_inactive} data plane still holds a storage account that is being released. Retry the restore once it is gone." >&2

		exit 1
	fi

	local restore_generation

	restore_generation=$( \
		echo "{{ "{{" }}workflow.uid}}" \
			| jq --raw-input --raw-output ".[0:6]")

	if echo "${liferay_infrastructure_json}" \
		| jq \
			--arg data_plane_key "${data_plane_inactive}-${restore_generation}" \
			--exit-status \
			'.spec.retainedDataPlanes // {} | has($data_plane_key)' > /dev/null
	then
		echo "The generation ${restore_generation} is already retained on the ${data_plane_inactive} data plane. Retry the restore to draw another one." >&2

		exit 1
	fi

	echo "${restore_generation}" > /tmp/restore-generation.txt

	kubectl get backupvaults.dataprotection.azure.m.upbound.io \
		--output jsonpath="{.items[0].metadata.name}" \
		> /tmp/backup-vault-name.txt

	local data_plane_key_active

	data_plane_key_active=$( \
		kubectl get flexibleservers.{{ include "backupRestore.databaseApiGroup" . }} \
			--output jsonpath="{.items[0].metadata.labels.dataPlaneKey}" \
			--selector "dataPlane=${data_plane_active}")

	if [ -z "${data_plane_key_active}" ]
	then
		data_plane_key_active=${data_plane_active}
	fi

	echo "${data_plane_key_active}" > /tmp/data-plane-key-active.txt

	kubectl get flexibleservers.{{ include "backupRestore.databaseApiGroup" . }} \
		--output json \
		| jq \
			--arg data_plane_active "${data_plane_active}" \
			--arg data_plane_key_active "${data_plane_key_active}" \
			--argjson retained_data_planes "$(echo "${liferay_infrastructure_json}" | jq --compact-output ".spec.retainedDataPlanes // {}")" \
			--compact-output \
			'[.items[] | if .metadata.labels.dataPlane == $data_plane_active then {key: $data_plane_key_active, server: .metadata.name} elif ((.metadata.labels.retainedDataPlane // "") != "") and ((($retained_data_planes[.metadata.labels.retainedDataPlane] // "1970-01-01T00:00:00Z") | fromdateiso8601) > (now + 3600)) then {key: .metadata.labels.retainedDataPlane, server: .metadata.annotations["crossplane.io/external-name"]} else empty end]' \
		> /tmp/database-candidates.txt

	jq \
		--arg data_plane_key "${data_plane_key_active}" \
		--compact-output \
		--null-input \
		'{($data_plane_key): null}' \
		> /tmp/retained-data-plane-release.txt

	kubectl get backupinstanceblobstorages.dataprotection.azure.m.upbound.io \
		--output jsonpath="{.items[0].metadata.name}" \
		--selector "dataPlane=${data_plane_active}" \
		> /tmp/backup-instance-name-active.txt 2> /dev/null || true

	kubectl get flexibleservers.{{ include "backupRestore.databaseApiGroup" . }} \
		--output jsonpath="{.items[0].spec.forProvider.resourceGroupName}" \
		--selector "dataPlane=${data_plane_active}" \
		> /tmp/resource-group-name.txt

	kubectl get statefulset \
		--output jsonpath="{.items[0].metadata.name}" \
		--selector "component=liferay" \
		> /tmp/liferay-workload-name.txt
}

main