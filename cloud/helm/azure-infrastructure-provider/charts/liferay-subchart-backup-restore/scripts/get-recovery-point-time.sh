#!/bin/sh

set -o errexit
set -o nounset

function main {
	az extension add \
		--name dataprotection \
		--version {{ .Values.images.azureCli.dataprotectionExtensionVersion }} \
		--yes > /dev/null

	az login \
		--federated-token "$(cat "${AZURE_FEDERATED_TOKEN_FILE}")" \
		--service-principal \
		--tenant "${AZURE_TENANT_ID}" \
		--username "${AZURE_CLIENT_ID}" > /dev/null

	_BACKUP_VAULT_NAME="{{ "{{" }}inputs.parameters.backup-vault-name}}"

	_RESOURCE_GROUP_NAME="{{ "{{" }}inputs.parameters.resource-group-name}}"

	_find_recovery_point

	_pick_restore_source "$(cat /tmp/recovery-point-time.txt)"
}

function _find_recovery_point {
	local backup_instance_name

	backup_instance_name=""

	local recovery_point_time

	recovery_point_time=""

	for candidate_backup_instance_name in $( \
		az dataprotection backup-instance list \
			--output tsv \
			--query "[].name" \
			--resource-group "${_RESOURCE_GROUP_NAME}" \
			--vault-name "${_BACKUP_VAULT_NAME}")
	do
		recovery_point_time=$( \
			az dataprotection recovery-point show \
				--backup-instance-name "${candidate_backup_instance_name}" \
				--output tsv \
				--query properties.recoveryPointTime \
				--recovery-point-id "{{ "{{" }}workflow.parameters.recovery-point-id}}" \
				--resource-group "${_RESOURCE_GROUP_NAME}" \
				--vault-name "${_BACKUP_VAULT_NAME}" 2> /dev/null || echo "")

		if [ -n "${recovery_point_time}" ]
		then
			backup_instance_name=${candidate_backup_instance_name}

			break
		fi
	done

	if [ -z "${backup_instance_name}" ]
	then
		echo "The recovery point {{ "{{" }}workflow.parameters.recovery-point-id}} was not found in the backup vault ${_BACKUP_VAULT_NAME}." >&2

		exit 1
	fi

	echo "${backup_instance_name}" > /tmp/backup-instance-name.txt

	echo "${recovery_point_time}" > /tmp/recovery-point-time.txt
}

function _pick_restore_source {
	local recovery_point_time

	recovery_point_time=${1}

	local restore_source_data_plane

	restore_source_data_plane=""

	local restore_source_earliest_restore_date

	restore_source_earliest_restore_date=""

	for database_candidate in $(echo '{{ "{{" }}inputs.parameters.database-candidates}}' | jq --compact-output ".[]")
	do
		local database_server_name

		database_server_name=$(echo "${database_candidate}" | jq --raw-output ".server")

		local earliest_restore_date

		earliest_restore_date=$( \
			az {{ include "backupRestore.databaseCommand" . }} flexible-server show \
				--name "${database_server_name}" \
				--output tsv \
				--query backup.earliestRestoreDate \
				--resource-group "${_RESOURCE_GROUP_NAME}")

		if [ -z "${earliest_restore_date}" ]
		then
			echo "The server ${database_server_name} reports no earliest restore date, so its point in time window has not opened yet."
		elif [[ "${earliest_restore_date:0:19}" > "${recovery_point_time:0:19}" ]]
		then
			echo "The server ${database_server_name} opened its point in time window at ${earliest_restore_date}, after the recovery point time ${recovery_point_time}."
		elif [[ "${earliest_restore_date}" > "${restore_source_earliest_restore_date}" ]]
		then
			restore_source_data_plane=$(echo "${database_candidate}" | jq --raw-output ".key")
			restore_source_earliest_restore_date=${earliest_restore_date}
		fi
	done

	if [ -z "${restore_source_data_plane}" ]
	then
		echo "No database server holds the recovery point time ${recovery_point_time} in its point in time window, so the database cannot be paired with it." >&2

		exit 1
	fi

	echo "The database is restored from the ${restore_source_data_plane} data plane."

	echo "${restore_source_data_plane}" > /tmp/restore-source-data-plane.txt
}

main