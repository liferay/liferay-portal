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

	local backup_vault_name

	backup_vault_name="{{ "{{" }}inputs.parameters.backup-vault-name}}"

	local resource_group_name

	resource_group_name="{{ "{{" }}inputs.parameters.resource-group-name}}"

	local storage_account_id

	storage_account_id="{{ "{{" }}inputs.parameters.storage-account-id}}"

	local backup_instance_name

	backup_instance_name=$( \
		az dataprotection backup-instance list \
			--output tsv \
			--query "[?properties.dataSourceInfo.resourceID=='${storage_account_id}'].name | [0]" \
			--resource-group "${resource_group_name}" \
			--vault-name "${backup_vault_name}")

	if [ -z "${backup_instance_name}" ]
	then
		echo "No backup instance protects the storage account ${storage_account_id}." >&2

		exit 1
	fi

	echo "${backup_instance_name}" > /tmp/backup-instance-name.txt

	local recovery_point_time

	recovery_point_time=$( \
		az dataprotection recovery-point show \
			--backup-instance-name "${backup_instance_name}" \
			--output tsv \
			--query properties.recoveryPointTime \
			--recovery-point-id "{{ "{{" }}workflow.parameters.recovery-point-id}}" \
			--resource-group "${resource_group_name}" \
			--vault-name "${backup_vault_name}")

	if [ -z "${recovery_point_time}" ]
	then
		echo "The recovery point {{ "{{" }}workflow.parameters.recovery-point-id}} has no recovery point time." >&2

		exit 1
	fi

	local earliest_restore_date

	earliest_restore_date=$( \
		az postgres flexible-server show \
			--name "{{ "{{" }}inputs.parameters.database-server-name}}" \
			--output tsv \
			--query backup.earliestRestoreDate \
			--resource-group "${resource_group_name}")

	if [ -z "${earliest_restore_date}" ]
	then
		echo "The server {{ "{{" }}inputs.parameters.database-server-name}} reports no earliest restore date, so its point in time window has not opened yet." >&2

		exit 1
	fi

	local recovery_point_second

	recovery_point_second=$(echo "${recovery_point_time}" | cut --characters=1-19)

	local earliest_restore_second

	earliest_restore_second=$(echo "${earliest_restore_date}" | cut --characters=1-19)

	if [ "$(printf "%s\n%s\n" "${earliest_restore_second}" "${recovery_point_second}" | sort | head --lines=1)" != "${earliest_restore_second}" ]
	then
		echo "The recovery point time ${recovery_point_time} falls before the earliest restore date ${earliest_restore_date}, so the database cannot be paired with it." >&2

		exit 1
	fi

	echo "${recovery_point_time}" > /tmp/recovery-point-time.txt
}

main