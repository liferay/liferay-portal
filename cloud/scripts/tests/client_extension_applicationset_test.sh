#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

# The client extension ApplicationSet chooses each Application's namespace in
# a Go template that only Argo CD evaluates, so helm unittest can compare its
# text but not its result. This renders the ApplicationSet, then evaluates its
# destination with Helm's sprig, the same function library Argo CD uses,
# against the parameters of each case.

CLOUD_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)

readonly CLOUD_DIR

function main {
	_TEMP_DIR=$(mktemp -d)

	trap 'rm -fr "${_TEMP_DIR}"' EXIT

	_write_evaluation_chart

	local fail=0
	local pass=0

	_run_test _test_an_entry_overrides_the_environment
	_run_test _test_an_entry_turns_on_a_separate_namespace
	_run_test _test_nothing_set_stays_in_the_dxp_namespace
	_run_test _test_the_environment_turns_on_a_separate_namespace

	echo ""
	echo "Results: ${pass} passed, ${fail} failed."

	if [ ${fail} -eq 0 ]
	then
		return 0
	fi

	return 1
}

function _assert {
	local expected="${2}"
	local parameters="${1}"

	local actual

	actual=$(helm template evaluation "${_TEMP_DIR}/chart" --set-json "parameters=${parameters}" | sed --quiet 's/^result: //p')

	if [[ ${actual} != ${expected} ]]
	then
		echo "Assertion failed for ${parameters}: expected \"${expected}\", got \"${actual}\"" >&2

		return 1
	fi
}

function _make_parameters {
	local environment="${2}"
	local project="${1}"
	local settings="${3:-{\}}"

	jq \
		--arg environment "${environment}" \
		--arg project "${project}" \
		--argjson settings "${settings}" \
		--compact-output \
		--null-input \
		'{path: {segments: ["liferay", "projects", $project, "environments", $environment]}} + $settings'
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

function _test_an_entry_overrides_the_environment {
	_assert "$(_make_parameters able nonproduction '{"clientExtensionsSeparateNamespace": true, "separateNamespace": false}')" "liferay-able-nonproduction"
}

function _test_an_entry_turns_on_a_separate_namespace {
	_assert "$(_make_parameters able nonproduction '{"separateNamespace": true}')" "liferay-able-nonproduction-cx"
}

function _test_nothing_set_stays_in_the_dxp_namespace {
	_assert "$(_make_parameters able nonproduction)" "liferay-able-nonproduction"
}

function _test_the_environment_turns_on_a_separate_namespace {
	_assert "$(_make_parameters able nonproduction '{"clientExtensionsSeparateNamespace": true}')" "liferay-able-nonproduction-cx"
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

	printf '{{- with .Values.parameters }}\nresult: %s\n{{- end }}\n' "${namespace}" > "${_TEMP_DIR}/chart/templates/evaluation.yaml"
}

main "${@}"