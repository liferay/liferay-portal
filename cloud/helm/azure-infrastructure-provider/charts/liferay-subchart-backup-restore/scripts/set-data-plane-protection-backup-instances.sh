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

	local storage_account_id_demoted

	storage_account_id_demoted="{{ "{{" }}inputs.parameters.storage-account-id-demoted}}"

	local storage_account_id_promoted

	storage_account_id_promoted="{{ "{{" }}inputs.parameters.storage-account-id-promoted}}"

	local promoted_instance_name

	promoted_instance_name=""

	local timeout

	timeout=$(($(date +%s) + {{ .Values.azureBackupService.protectionWaitTimeoutSeconds }}))

	while [[ "$(date +%s)" -lt "${timeout}" ]]
	do
		promoted_instance_name=$( \
			az dataprotection backup-instance list \
				--output tsv \
				--query "[?properties.dataSourceInfo.resourceID=='${storage_account_id_promoted}'].name | [0]" \
				--resource-group "${_RESOURCE_GROUP_NAME}" \
				--vault-name "${_BACKUP_VAULT_NAME}")

		if [ -n "${promoted_instance_name}" ]
		then
			break
		fi

		echo "No backup instance protects the promoted data plane yet."

		sleep 15
	done

	if [ -z "${promoted_instance_name}" ]
	then
		echo "No backup instance was created for the promoted data plane." >&2

		exit 1
	fi

	local promoted_state

	promoted_state=$( \
		az dataprotection backup-instance show \
			--name "${promoted_instance_name}" \
			--output tsv \
			--query properties.currentProtectionState \
			--resource-group "${_RESOURCE_GROUP_NAME}" \
			--vault-name "${_BACKUP_VAULT_NAME}")

	if [ "${promoted_state}" == "BackupsSuspended" ]
	then
		az dataprotection backup-instance resume-protection \
			--name "${promoted_instance_name}" \
			--no-wait \
			--output none \
			--resource-group "${_RESOURCE_GROUP_NAME}" \
			--vault-name "${_BACKUP_VAULT_NAME}"

		if ! _wait_for_protection_state "${promoted_instance_name}" ProtectionConfigured
		then
			echo "Backups were not resumed on the promoted data plane." >&2

			exit 1
		fi

		echo "Backups were resumed on the promoted data plane."
	fi

	local demoted_instance_name

	demoted_instance_name=$( \
		az dataprotection backup-instance list \
			--output tsv \
			--query "[?properties.dataSourceInfo.resourceID=='${storage_account_id_demoted}'].name | [0]" \
			--resource-group "${_RESOURCE_GROUP_NAME}" \
			--vault-name "${_BACKUP_VAULT_NAME}")

	if [ -z "${demoted_instance_name}" ]
	then
		echo "No backup instance protects the demoted data plane, so no backups were suspended."

		exit 0
	fi

	local demoted_state

	demoted_state=$( \
		az dataprotection backup-instance show \
			--name "${demoted_instance_name}" \
			--output tsv \
			--query properties.currentProtectionState \
			--resource-group "${_RESOURCE_GROUP_NAME}" \
			--vault-name "${_BACKUP_VAULT_NAME}")

	if [ "${demoted_state}" == "ProtectionConfigured" ]
	then
		az dataprotection backup-instance suspend-backup \
			--name "${demoted_instance_name}" \
			--no-wait \
			--output none \
			--resource-group "${_RESOURCE_GROUP_NAME}" \
			--vault-name "${_BACKUP_VAULT_NAME}"

		if ! _wait_for_protection_state "${demoted_instance_name}" BackupsSuspended
		then
			echo "Backups were not suspended on the demoted data plane." >&2

			exit 1
		fi

		echo "Backups were suspended on the demoted data plane."
	fi
}

function _wait_for_protection_state {
	local state

	local deadline

	deadline=$(($(date +%s) + {{ .Values.azureBackupService.protectionWaitTimeoutSeconds }}))

	while [[ "$(date +%s)" -lt "${deadline}" ]]
	do
		state=$( \
			az dataprotection backup-instance show \
				--name "${1}" \
				--output tsv \
				--query properties.currentProtectionState \
				--resource-group "${_RESOURCE_GROUP_NAME}" \
				--vault-name "${_BACKUP_VAULT_NAME}")

		if [ "${state}" == "${2}" ]
		then
			return 0
		fi

		echo "The backup instance ${1} reports the protection state ${state}."

		sleep 15
	done

	return 1
}

main