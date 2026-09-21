#!/bin/sh

set -o errexit
set -o nounset

function main {
	local expected_generation

	expected_generation="{{ "{{" }}inputs.parameters.expected-generation}}"

	local timeout

	timeout=$(($(date +%s) + {{ .Values.liferayInfrastructure.waitTimeoutSeconds }}))

	while [[ "$(date +%s)" -lt "${timeout}" ]]
	do
		local ready_condition

		ready_condition=$( \
			kubectl get liferayinfrastructure \
				--output jsonpath="{.items[0].status.conditions[?(@.type==\"Ready\")]}" 2> /dev/null || echo "{}")

		local observed_generation

		observed_generation=$(echo "${ready_condition}" | jq --raw-output ".observedGeneration // 0")

		local status

		status=$(echo "${ready_condition}" | jq --raw-output ".status // \"False\"")

		if [[ "${observed_generation}" -ge "${expected_generation}" ]] && [ "${status}" == "True" ]
		then
			exit 0
		fi

		sleep 30
	done

	echo "The system timed out waiting for the LiferayInfrastructure to be ready." >&2

	exit 1
}

main