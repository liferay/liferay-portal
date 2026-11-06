#!/bin/sh

set -o errexit
set -o nounset
set -o pipefail

function main {
	az login \
		--allow-no-subscriptions \
		--federated-token "$(cat "${AZURE_FEDERATED_TOKEN_FILE}")" \
		--service-principal \
		--tenant "${AZURE_TENANT_ID}" \
		--username "${AZURE_CLIENT_ID}" >/dev/null

	az account get-access-token \
		--output tsv \
		--query accessToken \
		--resource-type oss-rdbms > /entra/token
}

main