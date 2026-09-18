#!/bin/sh

set -o errexit
set -o nounset

function main {
	az extension add \
		--name dataprotection \
		--version {{ .Values.images.azureCli.dataprotectionExtensionVersion }} \
		--yes >/dev/null

	az login \
		--federated-token "$(cat "${AZURE_FEDERATED_TOKEN_FILE}")" \
		--service-principal \
		--tenant "${AZURE_TENANT_ID}" \
		--username "${AZURE_CLIENT_ID}" >/dev/null

	local storage_account_name

	storage_account_name="{{ "{{" }}inputs.parameters.storage-account-name}}"

	az storage container delete \
		--account-name "${storage_account_name}" \
		--auth-mode login \
		--name document-library >/dev/null

	local timeout

	timeout=$(($(date +%s) + 300))

	while [ $(date +%s) -lt ${timeout} ]
	do
		local exists

		exists=$( \
			az storage container exists \
				--account-name "${storage_account_name}" \
				--auth-mode login \
				--name document-library \
				--output tsv \
				--query exists \
				| tr "[:upper:]" "[:lower:]")

		if [ "${exists}" = "false" ]
		then
			echo "The document-library container was deleted from ${storage_account_name}."

			exit 0
		fi

		sleep 10
	done

	echo "The document-library container in ${storage_account_name} was not deleted before the timeout." >&2

	exit 1
}

main