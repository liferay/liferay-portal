#!/bin/sh

set -o errexit
set -o nounset
set -o pipefail

function main {
	local activation_code="{{ "{{" }}workflow.parameters.activation-code}}"
	local secret_key="{{ "{{" }}inputs.parameters.secret-key}}"
	local secret_name="{{ "{{" }}inputs.parameters.secret-name}}"

	kubectl \
		create \
		secret \
		generic \
		"${secret_name}" \
		--dry-run=client \
		--from-literal="${secret_key}=${activation_code}" \
		--output yaml | \
		kubectl \
			apply \
			--field-manager online-activation-workflow \
			--filename - \
			--force-conflicts \
			--server-side

	echo "The activation code was written to the secret ${secret_name}."
}

main