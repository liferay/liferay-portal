#!/usr/bin/env bash

SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

ROOT_CLOUD_DIR=$(cd "${SCRIPTS_DIR}/.." && pwd)

readonly ROOT_CLOUD_DIR SCRIPTS_DIR

function check_config_json {
	local configuration_json_file="${1}"

	if [[ ! -f ${configuration_json_file} ]]
	then
		echo "Configuration JSON file ${configuration_json_file} does not exist." >&2

		exit 1
	fi

	if ! jq empty "${configuration_json_file}" &> /dev/null
	then
		echo "Configuration JSON file ${configuration_json_file} is not valid JSON." >&2

		exit 1
	fi

	local required_key

	for required_key in "${@:2}"
	do
		if ! jq --exit-status "${required_key}" "${configuration_json_file}" &> /dev/null
		then
			echo "The configuration JSON file must contain a key named \"${required_key#.}\"." >&2

			exit 1
		fi
	done
}

function check_utils {
	for util in "${@}"
	do
		if (! command -v "${util}" &> /dev/null)
		then
			echo "The utility ${util} is not installed."

			exit 1
		fi
	done
}

function generate_local_backend_overrides {
	local cloud="${1}"

	local terraform_stack

	for terraform_stack in "${@:2}"
	do
		cat > "${ROOT_CLOUD_DIR}/terraform/${cloud}/${terraform_stack}/backend_override.tf" <<EOF
terraform {
	backend "local" {}
}
EOF
	done
}

function generate_tfvars {
	local cloud="${1}"
	local configuration_json_file="${2}"
	local terraform_stack="${3}"

	local tfvars_file="${ROOT_CLOUD_DIR}/terraform/${cloud}/${terraform_stack}/config.auto.tfvars.json"

	echo "Generating ${tfvars_file} from ${configuration_json_file}."

	jq --arg terraform_stack "${terraform_stack}" '(.terraform.common // {}) * (.terraform[$terraform_stack] // {}) * {deployment_name, region}' "${configuration_json_file}" > "${tfvars_file}"

	echo "${tfvars_file} was generated successfully."
}

function get_terraform_args {
	local configuration_json_file="${1}"

	local auto_approve

	auto_approve=$(jq --raw-output '.options.auto_approve // false' "${configuration_json_file}")

	local terraform_args=()

	if [[ ${auto_approve} == true ]]
	then
		terraform_args+=("-auto-approve")
	fi

	local parallelism

	parallelism=$(jq --raw-output '.options.parallelism | numbers' "${configuration_json_file}")

	if [[ -n ${parallelism} ]]
	then
		terraform_args+=("-parallelism=${parallelism}")
	fi

	if [ ${#terraform_args[@]} -gt 0 ]
	then
		printf '%s\n' "${terraform_args[@]}"
	fi
}

function pop_directory {
	popd > /dev/null
}

function push_directory {
	pushd "${1}" > /dev/null
}