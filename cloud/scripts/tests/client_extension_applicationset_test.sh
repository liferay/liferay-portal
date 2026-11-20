#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

CLOUD_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)

readonly CLOUD_DIR

function main {
	_TEMP_DIR=$(mktemp -d)

	trap 'rm -fr "${_TEMP_DIR}"' EXIT

	_write_evaluation_chart

	local fail=0
	local pass=0

	_run_test _test_an_entry_overrides_the_defaults
	_run_test _test_an_entry_turns_on_a_separate_namespace
	_run_test _test_an_entry_value_replaces_the_default_value
	_run_test _test_nothing_set_stays_in_the_dxp_namespace
	_run_test _test_the_default_values_fill_in_an_entry
	_run_test _test_the_defaults_turn_on_a_separate_namespace

	echo ""
	echo "Results: ${pass} passed, ${fail} failed."

	if [ ${fail} -eq 0 ]
	then
		return 0
	fi

	return 1
}

function _assert {
	local expected="${3}"
	local key="${2}"
	local parameters="${1}"

	local actual

	actual=$(helm template evaluation "${_TEMP_DIR}/chart" --set-json "parameters=${parameters}" | sed --quiet "s/^${key}: //p")

	if [[ ${actual} != "${expected}" ]]
	then
		echo "Assertion failed for ${parameters}: expected \"${expected}\", got \"${actual}\"" >&2

		return 1
	fi
}

function _make_parameters {
	local client_extension="${1:-{\}}"
	local client_extension_defaults="${2:-{\}}"

	jq \
		--argjson client_extension "${client_extension}" \
		--argjson client_extension_defaults "${client_extension_defaults}" \
		--compact-output \
		--null-input \
		'{clientExtensionDefaults: $client_extension_defaults, clientExtensions: {"liferay-sample-etc-node": ({descriptor: "oci://ghcr.io/able/etc-node", version: "1.0.0"} + $client_extension)}, path: {segments: ["liferay", "projects", "able", "environments", "nonproduction"]}}'
}

function _run_test {
	local test_function="${1}"

	local description

	description=$(echo "${test_function}" | sed "s/^_test_//; s/_/ /g")

	if "${test_function}"
	then
		echo "PASS: ${description}"

		((++pass))
	else
		echo "FAIL: ${description}"

		((++fail))
	fi
}

function _test_an_entry_overrides_the_defaults {
	_assert "$(_make_parameters '{"separateNamespace": false}' '{"separateNamespace": true}')" namespace "liferay-sample-etc-node liferay-able-nonproduction"
}

function _test_an_entry_turns_on_a_separate_namespace {
	_assert "$(_make_parameters '{"separateNamespace": true}')" namespace "liferay-sample-etc-node liferay-able-nonproduction-cx"
}

function _test_an_entry_value_replaces_the_default_value {
	_assert "$(_make_parameters '{"values": {"imagePullSecrets": [], "network": {"enabled": false}, "replicaCount": 0}}' '{"values": {"imagePullSecrets": [{"name": "ghcr-pull"}], "network": {"enabled": true, "port": 8080}, "replicaCount": 2}}')" values '{"imagePullSecrets":[],"network":{"enabled":false},"replicaCount":0}'
}

function _test_nothing_set_stays_in_the_dxp_namespace {
	_assert "$(_make_parameters)" namespace "liferay-sample-etc-node liferay-able-nonproduction"
}

function _test_the_default_values_fill_in_an_entry {
	_assert "$(_make_parameters '{"values": {"replicaCount": 2}}' '{"values": {"imagePullSecrets": [{"name": "ghcr-pull"}], "replicaCount": 1}}')" values '{"imagePullSecrets":[{"name":"ghcr-pull"}],"replicaCount":2}'
}

function _test_the_defaults_turn_on_a_separate_namespace {
	_assert "$(_make_parameters '{}' '{"separateNamespace": true}')" namespace "liferay-sample-etc-node liferay-able-nonproduction-cx"
}

function _write_evaluation_chart {
	local application_set

	application_set=$(
		helm template \
			platform-components \
			"${CLOUD_DIR}/helm/platform-components" \
			--show-only templates/applicationset-client-extension.yaml \
			--values "${CLOUD_DIR}/scripts/tests/render-values/platform-components.yaml")

	local namespace

	namespace=$(yq '.spec.template.spec.destination.namespace' <<< "${application_set}")

	mkdir --parents "${_TEMP_DIR}/chart/templates"

	printf 'apiVersion: v2\nname: evaluation\nversion: 0.0.0\n' > "${_TEMP_DIR}/chart/Chart.yaml"

	{
		printf '{{- define "destination" }}%s{{- end }}\n' "${namespace}"
		printf '{{- define "elements" }}\n'
		cat "${CLOUD_DIR}/helm/platform-components/client-extension-elements.gotmpl"
		printf '\n{{- end }}\n'
		printf '{{- $parameters := .Values.parameters }}\n'
		printf '{{- range include "elements" $parameters | fromJsonArray }}\n'
		printf 'namespace: {{ .name }} {{ include "destination" (merge (deepCopy .) $parameters) }}\n'
		printf 'values: {{ toJson .values }}\n'
		printf '{{- end }}\n'
	} > "${_TEMP_DIR}/chart/templates/evaluation.yaml"
}

main "${@}"