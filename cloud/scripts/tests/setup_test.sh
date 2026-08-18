#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

source "$(dirname "${BASH_SOURCE[0]}")/../_azure_common.sh"

function main {
	local fail=0
	local pass=0

	local scripts_dir

	scripts_dir=$(cd "$(dirname "${0}")/.." && pwd)

	for script in "${scripts_dir}/setup_aws.sh" "${scripts_dir}/setup_gcp.sh"
	do
		_run_test "${script}" _test_aborts_with_config_missing_variables_object
		_run_test "${script}" _test_aborts_with_old_terraform_version
	done

	for script in "${scripts_dir}/setup_aws.sh" "${scripts_dir}/setup_azure.sh" "${scripts_dir}/setup_gcp.sh"
	do
		_run_test "${script}" _test_aborts_with_malformed_config_json
		_run_test "${script}" _test_aborts_with_missing_config_file
		_run_test "${script}" _test_aborts_with_missing_required_utility
		_run_test "${script}" _test_aborts_with_no_arguments
	done

	_run_test "${scripts_dir}/_azure_common.sh" _test_fills_observability_parameters_from_terraform_outputs
	_run_test "${scripts_dir}/_azure_common.sh" _test_omits_unavailable_observability_parameters

	echo ""
	echo "Results: ${pass} passed, ${fail} failed."

	if [ ${fail} -eq 0 ]
	then
		return 0
	fi

	return 1
}

function _make_stub_path {
	local stub_dir

	stub_dir=$(mktemp -d)

	for util in aws az gcloud helm jq kubectl
	do
		local real_path

		real_path=$(command -v "${util}" 2>/dev/null || true)

		if [[ -n ${real_path} ]]
		then
			ln -s "${real_path}" "${stub_dir}/${util}"
		else
			cat > "${stub_dir}/${util}" <<'EOF'
#!/usr/bin/env bash
exit 0
EOF
			chmod +x "${stub_dir}/${util}"
		fi
	done

	for util in awk basename bash dirname env head mktemp sed sort tr
	do
		local real_path

		real_path=$(command -v "${util}" 2>/dev/null || true)

		if [[ -n ${real_path} ]]
		then
			ln -s "${real_path}" "${stub_dir}/${util}"
		fi
	done

	echo "${stub_dir}"
}

function _make_terraform_stub {
	local stub_dir="${1}"
	local version="${2}"

	cat > "${stub_dir}/terraform" <<EOF
#!/usr/bin/env bash

if [[ \${1:-} == --version ]]
then
	echo "Terraform v${version}"

	exit 0
fi

exit 0
EOF

	chmod +x "${stub_dir}/terraform"
}

function _run_setup_test {
	local config_content="${2}"
	local script="${1}"
	local terraform_version="${3:-1.10.0}"
	local utility_to_remove="${4:-}"

	local stub_dir

	stub_dir=$(_make_stub_path)

	_make_terraform_stub "${stub_dir}" "${terraform_version}"

	if [[ -n ${utility_to_remove} ]]
	then
		rm "${stub_dir}/${utility_to_remove}"
	fi

	local tmpdir

	tmpdir=$(mktemp -d)

	printf '%s' "${config_content}" > "${tmpdir}/config.json"

	local exit_code=0
	local output

	output=$(PATH="${stub_dir}" bash "${script}" "${tmpdir}/config.json" 2>&1) || exit_code="${?}"

	rm -rf "${stub_dir}" "${tmpdir}"

	echo "${exit_code}"
	echo "${output}"
}

function _run_test {
	local script="${1}"
	local test_function="${2}"

	local script_name

	script_name=$(basename "${script}")

	local description

	description=$(echo "${test_function}" | sed "s/^_test_//; s/_/ /g")

	local exit_code=0

	"${test_function}" "${script}" || exit_code="${?}"

	if [ ${exit_code} -eq 0 ]
	then
		echo "PASS: [${script_name}] ${description}."

		pass=$((pass + 1))
	else
		echo "FAIL: [${script_name}] ${description}."

		fail=$((fail + 1))
	fi
}

function _test_aborts_with_config_missing_variables_object {
	local config_json
	local exit_code
	local output
	local result

	config_json=$(jq --null-input '{options: {provider: "aws"}}')
	result=$(_run_setup_test "${1}" "${config_json}")
	exit_code=$(echo "${result}" | head -n 1)
	output=$(echo "${result}" | tail -n +2)

	if [ ${exit_code} -ne 0 ] && [[ ${output} == *'must contain a root object named "variables"'* ]]
	then
		return 0
	fi

	return 1
}

function _test_aborts_with_malformed_config_json {
	local exit_code
	local output
	local result

	result=$(_run_setup_test "${1}" '{not valid json')
	exit_code=$(echo "${result}" | head -n 1)
	output=$(echo "${result}" | tail -n +2)

	if [ ${exit_code} -ne 0 ] && [[ ${output} == *"is not valid JSON"* ]]
	then
		return 0
	fi

	return 1
}

function _test_aborts_with_missing_config_file {
	local exit_code=0
	local output
	local stub_dir

	stub_dir=$(_make_stub_path)

	_make_terraform_stub "${stub_dir}" "1.10.0"

	output=$(PATH="${stub_dir}" bash "${1}" /does/not/exist.json 2>&1) || exit_code="${?}"

	rm -rf "${stub_dir}"

	if [ ${exit_code} -ne 0 ] && [[ ${output} == *"Configuration JSON file"*"does not exist"* ]]
	then
		return 0
	fi

	return 1
}

function _test_aborts_with_missing_required_utility {
	local config_json
	local exit_code
	local output
	local result

	config_json=$(jq --null-input '{variables: {}}')
	result=$(_run_setup_test "${1}" "${config_json}" "1.10.0" jq)
	exit_code=$(echo "${result}" | head -n 1)
	output=$(echo "${result}" | tail -n +2)

	if [ ${exit_code} -ne 0 ] && [[ ${output} == *"utility jq is not installed"* ]]
	then
		return 0
	fi

	return 1
}

function _test_aborts_with_no_arguments {
	local exit_code=0
	local output

	output=$(bash "${1}" 2>&1) || exit_code="${?}"

	if [ ${exit_code} -ne 0 ] && [[ ${output} == *"Usage:"* ]]
	then
		return 0
	fi

	return 1
}

function _test_aborts_with_old_terraform_version {
	local config_json
	local exit_code
	local output
	local result

	config_json=$(jq --null-input '{variables: {}}')
	result=$(_run_setup_test "${1}" "${config_json}" "1.9.9")
	exit_code=$(echo "${result}" | head -n 1)
	output=$(echo "${result}" | tail -n +2)

	if [ ${exit_code} -ne 0 ] && [[ ${output} == *"is older than"* ]]
	then
		return 0
	fi

	return 1
}

function _test_fills_observability_parameters_from_terraform_outputs {
	local aks_outputs

	aks_outputs=$(jq --null-input '{
		observability_identity_client_id: {
			value: "27ed4e1e-8c11-43e1-810f-afb22a5a2418"
		},
		prometheus_data_collection_rule_id: {
			value: "dcr-9fb40355a2cc4e50a20a82171a15e33a"
		},
		prometheus_metrics_ingestion_endpoint: {
			value: "https://liferay-test-dce.eastus-1.metrics.ingest.monitor.azure.com"
		}
	}')

	local expected_parameters

	expected_parameters=$(jq --compact-output --null-input --sort-keys '[
		{
			name: "alloy.iam.azureClientId",
			value: "27ed4e1e-8c11-43e1-810f-afb22a5a2418"
		},
		{
			name: "azure.remoteWrite.dataCollectionRuleId",
			value: "dcr-9fb40355a2cc4e50a20a82171a15e33a"
		},
		{
			name: "azure.remoteWrite.metricsIngestionEndpoint",
			value: "https://liferay-test-dce.eastus-1.metrics.ingest.monitor.azure.com"
		},
		{
			name: "azure.remoteWrite.tenantId",
			value: "86315286-b8fe-4db7-abd0-cc8f6421c133"
		},
		{
			name: "cloudProvider",
			value: "azure"
		}
	]')

	local parameters

	parameters=$(get_observability_parameters "${aks_outputs}" "86315286-b8fe-4db7-abd0-cc8f6421c133" | jq --compact-output --sort-keys '.')

	if [ "${parameters}" == "${expected_parameters}" ]
	then
		return 0
	fi

	return 1
}

function _test_omits_unavailable_observability_parameters {
	local expected_parameters

	expected_parameters=$(jq --compact-output --null-input --sort-keys '[
		{
			name: "azure.remoteWrite.tenantId",
			value: "86315286-b8fe-4db7-abd0-cc8f6421c133"
		},
		{
			name: "cloudProvider",
			value: "azure"
		}
	]')

	local parameters

	parameters=$(get_observability_parameters "{}" "86315286-b8fe-4db7-abd0-cc8f6421c133" | jq --compact-output --sort-keys '.')

	if [ "${parameters}" == "${expected_parameters}" ]
	then
		return 0
	fi

	return 1
}

main "${@}"