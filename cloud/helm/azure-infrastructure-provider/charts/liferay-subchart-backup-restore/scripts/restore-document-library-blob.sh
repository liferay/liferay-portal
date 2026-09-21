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

	local backup_instance_name

	backup_instance_name="{{ "{{" }}inputs.parameters.backup-instance-name}}"

	local backup_vault_name

	backup_vault_name="{{ "{{" }}inputs.parameters.backup-vault-name}}"

	local resource_group_name

	resource_group_name="{{ "{{" }}inputs.parameters.resource-group-name}}"

	az dataprotection backup-instance restore initialize-for-data-recovery \
		--datasource-type AzureBlob \
		--recovery-point-id "{{ "{{" }}workflow.parameters.recovery-point-id}}" \
		--restore-location "{{ "{{" }}inputs.parameters.storage-account-location}}" \
		--source-datastore VaultStore \
		--target-resource-id "{{ "{{" }}inputs.parameters.storage-account-id}}" \
		> /tmp/restore-request.json

	local triggered_at

	triggered_at=$(date --utc +%Y-%m-%dT%H:%M:%S)

	az dataprotection backup-instance restore trigger \
		--backup-instance-name "${backup_instance_name}" \
		--no-wait \
		--output none \
		--resource-group "${resource_group_name}" \
		--restore-request-object @/tmp/restore-request.json \
		--vault-name "${backup_vault_name}"

	local job_id

	job_id=""

	local discovery_timeout

	discovery_timeout=$(($(date +%s) + 300))

	while [[ "$(date +%s)" -lt "${discovery_timeout}" ]]
	do
		local candidate_id

		candidate_id=$( \
			az dataprotection job list \
				--output tsv \
				--query "reverse(sort_by([?properties.operationCategory=='Restore'], &properties.startTime))[0].name" \
				--resource-group "${resource_group_name}" \
				--vault-name "${backup_vault_name}")

		local candidate_started

		candidate_started=$( \
			az dataprotection job list \
				--output tsv \
				--query "reverse(sort_by([?properties.operationCategory=='Restore'], &properties.startTime))[0].properties.startTime" \
				--resource-group "${resource_group_name}" \
				--vault-name "${backup_vault_name}" \
				| cut --characters=1-19)

		if [ -n "${candidate_id}" ] && [ "$(printf "%s\n%s\n" "${triggered_at}" "${candidate_started}" | sort | head --lines=1)" == "${triggered_at}" ]
		then
			job_id=${candidate_id}

			break
		fi

		sleep 10
	done

	if [ -z "${job_id}" ]
	then
		echo "No restore job for ${backup_instance_name} appeared after ${triggered_at}." >&2

		exit 1
	fi

	echo "The restore job ${job_id} was started."

	local timeout

	timeout=$(($(date +%s) + {{ .Values.azureBackupService.restoreWaitTimeoutSeconds }}))

	while [[ "$(date +%s)" -lt "${timeout}" ]]
	do
		local status

		status=$( \
			az dataprotection job show \
				--job-id "${job_id}" \
				--output tsv \
				--query properties.status \
				--resource-group "${resource_group_name}" \
				--vault-name "${backup_vault_name}")

		if [ "${status}" == "Completed" ] || [ "${status}" == "CompletedWithWarnings" ]
		then
			echo "The restore job ${job_id} finished with status \"${status}\"."

			exit 0
		elif [ "${status}" == "Failed" ] || [ "${status}" == "Cancelled" ]
		then
			echo "The restore job ${job_id} finished with status \"${status}\"." >&2

			exit 1
		fi

		echo "The current restore job status is \"${status}\"."

		sleep 30
	done

	echo "The restore job ${job_id} did not complete before the timeout." >&2

	exit 1
}

main