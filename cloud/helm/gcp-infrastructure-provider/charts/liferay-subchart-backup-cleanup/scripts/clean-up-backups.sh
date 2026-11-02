#!/bin/sh

set -o errexit
set -o nounset
set -o pipefail

function main {
	local retention_days="{{ "{{" }}workflow.parameters.retention-days}}"

	local retention_seconds=$((retention_days * 24 * 60 * 60))

	echo "Cleaning up backups older than ${retention_days} days."

	local backups_to_clean_up

	backups_to_clean_up=$(
		kubectl \
			get \
			liferaybackups \
			--namespace "{{ "{{" }}workflow.namespace}}" \
			--output json \
			| jq \
				--argjson retention "${retention_seconds}" \
				--raw-output \
				'
				.items[]
				| select(
					(.status.completionTime != null and (now - (.status.completionTime | fromdateiso8601)) > $retention) or
					(any(.status.conditions[]?; .status == "False" and .type == "Succeeded"))
				)
				| .metadata.name')

	if [ -z "${backups_to_clean_up}" ]
	then
		echo "There are no backups to clean up."

		exit 0
	fi

	echo "${backups_to_clean_up}" | while read -r name
	do
		echo "Deleting backup ${name}."

		kubectl delete liferaybackup "${name}" --namespace "{{ "{{" }}workflow.namespace}}"
	done
}

main