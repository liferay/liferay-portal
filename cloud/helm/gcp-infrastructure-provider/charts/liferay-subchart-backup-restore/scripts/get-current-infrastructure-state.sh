#!/bin/sh

set -o errexit
set -o nounset

function main {
	local liferay_backup_name="{{ "{{" }}workflow.parameters.liferay-backup-name}}"
	local liferay_backup_namespace="{{ "{{" }}inputs.parameters.liferay-backup-namespace}}"

	local liferay_backup_json

	liferay_backup_json=$( \
		kubectl \
			get \
			liferaybackups.backup.liferay.cloud \
			"${liferay_backup_name}" \
			--namespace "${liferay_backup_namespace}" \
			--output json)

	local database_snapshot_uuid

	database_snapshot_uuid=$(echo "${liferay_backup_json}" | jq --raw-output ".status.databaseSnapshot.uuid // \"\"")

	if [ -z "${database_snapshot_uuid}" ]
	then
		echo "The LiferayBackup \"${liferay_backup_name}\" has no status.databaseSnapshot.uuid." >&2

		exit 1
	fi

	echo "${database_snapshot_uuid}" > /tmp/database-snapshot-uuid.txt

	local vault_bucket

	vault_bucket=$(echo "${liferay_backup_json}" | jq --raw-output ".status.documentLibrary.destinationBucket // \"\"")

	if [ -z "${vault_bucket}" ]
	then
		echo "The LiferayBackup \"${liferay_backup_name}\" has no status.documentLibrary.destinationBucket." >&2

		exit 1
	fi

	echo "${vault_bucket}" > /tmp/vault-bucket.txt

	local vault_path

	vault_path=$(echo "${liferay_backup_json}" | jq --raw-output ".status.documentLibrary.destinationPath // \"\"")

	echo "${vault_path}" > /tmp/vault-path.txt

	local liferay_infrastructure_json

	liferay_infrastructure_json=$( \
		kubectl \
			get \
			liferayinfrastructure \
			--output json \
			| jq ".items[0]")

	local restore_phase

	restore_phase=$(echo "${liferay_infrastructure_json}" | jq --raw-output ".spec.restorePhase // \"none\"")

	if [ "${restore_phase}" = "promoting" ] || [ "${restore_phase}" = "provisioning" ] || [ "${restore_phase}" = "provisioning-database-user" ]
	then
		echo "The LiferayInfrastructure spec.restorePhase is set to ${restore_phase}. A restore is in progress." >&2

		exit 1
	fi

	local data_plane_active

	data_plane_active=$(echo "${liferay_infrastructure_json}" | jq --raw-output ".spec.targetActiveDataPlane // \"blue\"")

	echo "${data_plane_active}" > /tmp/data-plane-active.txt

	local data_plane_inactive

	if [ "${data_plane_active}" = "blue" ]
	then
		data_plane_inactive="green"
	else
		data_plane_inactive="blue"
	fi

	echo "${data_plane_inactive}" > /tmp/data-plane-inactive.txt

	local liferay_infrastructure_name

	liferay_infrastructure_name=$(echo "${liferay_infrastructure_json}" | jq --raw-output ".metadata.name")

	echo "${liferay_infrastructure_name}" > /tmp/liferay-infrastructure-name.txt

	local liferay_workload_name

	liferay_workload_name=$( \
		kubectl \
			get \
			statefulset \
			--output jsonpath="{.items[0].metadata.name}" \
			--selector "component=liferay")

	echo "${liferay_workload_name}" > /tmp/liferay-workload-name.txt
}

main