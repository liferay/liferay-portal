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

function confirm {
	local reply

	read -p "Type \"yes\" to ${1}: " -r reply

	[[ ${reply} == yes ]]
}

function confirm_teardown {
	local cluster="${1}"

	echo "This will destroy the ${cluster} cluster and the Liferay platform."
	echo ""

	if ! confirm "continue"
	then
		echo "The teardown was canceled."

		exit 1
	fi
}

function has_remote_tfstate {
	jq --exit-status '.tfstate | objects' "${1}" &> /dev/null
}

function pop_directory {
	popd > /dev/null
}

function prepare_terraform_stacks {
	local cloud="${1}"
	local cluster_terraform_stack="${2}"
	local configuration_json_file="${3}"

	_generate_tfvars "${cloud}" "${configuration_json_file}" "${cluster_terraform_stack}"

	_generate_tfvars "${cloud}" "${configuration_json_file}" "platform"

	if has_remote_tfstate "${configuration_json_file}"
	then
		generate_remote_backend_overrides "${configuration_json_file}"
	else
		_generate_local_backend_overrides "${cloud}" "${cluster_terraform_stack}" "platform"
	fi
}

function push_directory {
	pushd "${1}" > /dev/null
}

function resolve_config_json {
	local configuration_json_file="${1}"

	local resolved_configuration_json_file

	resolved_configuration_json_file=$(mktemp)

	jq --slurp '.[0] * .[1]' "${SCRIPTS_DIR}/config.json.defaults" "${configuration_json_file}" > "${resolved_configuration_json_file}"

	echo "${resolved_configuration_json_file}"
}

function set_up {
	local cloud="${1}"
	local cluster_terraform_stack="${2}"
	local configuration_json_file="${3}"

	local terraform_args=()

	while IFS= read -r terraform_arg
	do
		terraform_args+=("${terraform_arg}")
	done < <(_get_terraform_args "${configuration_json_file}")

	echo "Setting up the Kubernetes cluster."

	_run_terraform_stack "${cloud}" "apply" "${cluster_terraform_stack}" "${terraform_args[@]}"

	echo "Kubernetes cluster setup complete."

	_connect_to_cluster "${cloud}" "${cluster_terraform_stack}"

	echo "Setting up the Liferay platform."

	_run_terraform_stack "${cloud}" "apply" "platform" "${terraform_args[@]}"

	echo "Liferay platform setup complete."

	_install_liferay_platform_chart "${cloud}" "${configuration_json_file}"
}

function tear_down {
	local cloud="${1}"
	local cluster_terraform_stack="${2}"
	local configuration_json_file="${3}"

	local terraform_args=()

	while IFS= read -r terraform_arg
	do
		terraform_args+=("${terraform_arg}")
	done < <(_get_terraform_args "${configuration_json_file}")

	if _connect_to_cluster "${cloud}" "${cluster_terraform_stack}"
	then
		_uninstall_liferay_platform_chart

		_delete_persistent_volume_claims
	else
		echo "Skipping the Liferay platform root application uninstall."
	fi

	echo "Destroying the Liferay platform."

	_run_terraform_stack "${cloud}" "destroy" "platform" "${terraform_args[@]}"

	echo "Liferay platform teardown complete."

	echo "Destroying the Kubernetes cluster."

	_run_terraform_stack "${cloud}" "destroy" "${cluster_terraform_stack}" "${terraform_args[@]}"

	echo "Kubernetes cluster teardown complete."
}

function _connect_to_cluster {
	local cloud="${1}"
	local cluster_terraform_stack="${2}"

	push_directory "${ROOT_CLOUD_DIR}/terraform/${cloud}/${cluster_terraform_stack}"

	echo "Connecting to the Kubernetes cluster."

	terraform init

	local cluster_name

	cluster_name=$(terraform output -raw cluster_name)

	if [[ -z ${cluster_name} ]]
	then
		echo "There is no Kubernetes cluster in the Terraform state." >&2

		pop_directory

		return 1
	fi

	export KUBE_CONFIG_PATH="${HOME}/.kube/config"

	if ! update_kubeconfig "${cluster_name}"
	then
		echo "The Kubernetes cluster ${cluster_name} could not be reached." >&2

		pop_directory

		return 1
	fi

	pop_directory
}

function _delete_persistent_volume_claims {
	echo "Deleting the persistent volume claims."

	if ! kubectl \
		delete \
		persistentvolumeclaims \
		--all \
		--all-namespaces \
		--timeout 10m0s \
		--wait
	then
		echo "The persistent volume claims were not deleted after 10 minutes." >&2

		exit 1
	fi
}

function _generate_local_backend_overrides {
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

function _generate_tfvars {
	local cloud="${1}"
	local configuration_json_file="${2}"
	local terraform_stack="${3}"

	local tfvars_file="${ROOT_CLOUD_DIR}/terraform/${cloud}/${terraform_stack}/config.auto.tfvars.json"

	echo "Generating ${tfvars_file} from ${configuration_json_file}."

	jq --arg terraform_stack "${terraform_stack}" '(.terraform.common // {}) * (.terraform[$terraform_stack] // {}) * {deployment_name, region}' "${configuration_json_file}" > "${tfvars_file}"

	echo "${tfvars_file} was generated successfully."
}

function _get_terraform_args {
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

function _get_terraform_outputs {
	local cloud="${1}"
	local terraform_stack="${2}"

	push_directory "${ROOT_CLOUD_DIR}/terraform/${cloud}/${terraform_stack}"

	terraform output -json

	pop_directory
}

function _install_liferay_platform_chart {
	local cloud="${1}"
	local configuration_json_file="${2}"

	local platform_repo_url
	local platform_target_revision
	local platform_values

	platform_repo_url=$(jq --raw-output '"oci://\(.artifacts.charts."liferay-platform".registry // error("artifacts.charts.liferay-platform.registry is missing"))/liferay-platform"' "${configuration_json_file}")
	platform_target_revision=$(jq --raw-output '.artifacts.charts."liferay-platform".version // error("artifacts.charts.liferay-platform.version is missing")' "${configuration_json_file}")
	platform_values=$(_get_terraform_outputs "${cloud}" "platform" | jq --from-file "${SCRIPTS_DIR}/_${cloud}_platform_values.jq" --slurpfile configuration "${configuration_json_file}")

	echo "Applying the Liferay platform root application."

	helm \
		upgrade \
		liferay-platform \
		"${platform_repo_url}" \
		--install \
		--namespace argocd-system \
		--values <(echo "${platform_values}") \
		--version "${platform_target_revision}"
}

function _run_terraform_stack {
	local cloud="${1}"
	local terraform_command="${2}"
	local terraform_stack="${3}"

	push_directory "${ROOT_CLOUD_DIR}/terraform/${cloud}/${terraform_stack}"

	terraform init

	terraform "${terraform_command}" -input=false "${@:4}"

	pop_directory
}

function _uninstall_liferay_platform_chart {
	echo "Uninstalling the Liferay platform root application."

	if ! helm \
		uninstall \
		liferay-platform \
		--ignore-not-found \
		--namespace argocd-system \
		--timeout 30m0s \
		--wait
	then
		echo "The liferay-platform Helm release was not uninstalled after 30 minutes." >&2

		exit 1
	fi
}