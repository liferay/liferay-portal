#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

source "$(dirname "${BASH_SOURCE[0]}")/_chart_version_common.sh"

_BUMPED_BOOTSTRAPS=()
_MODIFIED_BOOTSTRAPS=()

_VERSIONS_JSON_FILE="${SCRIPTS_DIR}/versions.json"

readonly _VERSIONS_JSON_FILE

function main {
	local aws_bootstrap_sources=(
		"${ROOT_CLOUD_DIR}/scripts/setup_aws.sh"
		"${ROOT_CLOUD_DIR}/terraform/aws/eks"
		"${ROOT_CLOUD_DIR}/terraform/aws/gitops/platform"
		"${ROOT_CLOUD_DIR}/terraform/aws/gitops/resources"
	)

	_check_bootstrap "aws" "${aws_bootstrap_sources[@]}"

	local azure_bootstrap_sources=(
		"${ROOT_CLOUD_DIR}/scripts/_azure_common.sh"
		"${ROOT_CLOUD_DIR}/scripts/chart_versions.json"
		"${ROOT_CLOUD_DIR}/scripts/setup_azure.sh"
		"${ROOT_CLOUD_DIR}/terraform/azure/aks"
		"${ROOT_CLOUD_DIR}/terraform/azure/platform"
		"${ROOT_CLOUD_DIR}/terraform/modules/argocd"
	)

	_check_bootstrap "azure" "${azure_bootstrap_sources[@]}"

	local gcp_bootstrap_sources=(
		"${ROOT_CLOUD_DIR}/scripts/setup_gcp.sh"
		"${ROOT_CLOUD_DIR}/terraform/gcp/gke"
		"${ROOT_CLOUD_DIR}/terraform/gcp/gitops/platform"
		"${ROOT_CLOUD_DIR}/terraform/gcp/gitops/resources"
	)

	_check_bootstrap "gcp" "${gcp_bootstrap_sources[@]}"

	_check_operator

	while true
	do
		_update_default_versions

		if has_modified_charts
		then
			bump_modified_charts

			continue
		fi

		if ! _has_modified_bootstraps
		then
			return
		fi

		_bump_modified_bootstraps
	done
}

function _bump_bootstrap_version {
	local bootstrap_name=${1}

	_BUMPED_BOOTSTRAPS+=("${bootstrap_name}")

	local current_version

	current_version=$(jq --raw-output '."liferay-'"${bootstrap_name}"'-bootstrap"' "${_VERSIONS_JSON_FILE}")

	local new_version

	new_version=$(echo "${current_version}" | awk -F "." -v OFS="." '{$NF += 1; print}')

	local config_json_example_file="${ROOT_CLOUD_DIR}/scripts/config.json.example_${bootstrap_name}"

	local updated_config_json

	updated_config_json=$(jq --arg version "${new_version}" --tab '.options.version = $version' "${config_json_example_file}")

	printf '%s' "${updated_config_json}" > "${config_json_example_file}"

	local blame_line

	blame_line=$(git_blame_line '"liferay-'"${bootstrap_name}"'-bootstrap": "[0-9]+\.[0-9]+\.[0-9]+"' "${_VERSIONS_JSON_FILE}")

	sed \
		--in-place \
		--regexp-extended \
		--expression "${blame_line}s/\"liferay-${bootstrap_name}-bootstrap\": \"[0-9]+\.[0-9]+\.[0-9]+\"/\"liferay-${bootstrap_name}-bootstrap\": \"${new_version}\"/" \
		"${_VERSIONS_JSON_FILE}"
}

function _bump_modified_bootstraps {
	count_pass

	local bootstrap_names=("${_MODIFIED_BOOTSTRAPS[@]}")

	_MODIFIED_BOOTSTRAPS=()

	local bootstrap_name

	for bootstrap_name in "${bootstrap_names[@]}"
	do
		echo "A source packaged in the liferay-${bootstrap_name}-bootstrap tarball was rewritten. Updating liferay-${bootstrap_name}-bootstrap version." >&2
		echo "" >&2

		_bump_bootstrap_version "${bootstrap_name}"
	done
}

function _bump_operator_version {
	local current_version

	current_version=$(jq --raw-output '."liferay-dxp-operator"' "${_VERSIONS_JSON_FILE}")

	local new_version

	new_version=$(echo "${current_version}" | awk -F "." -v OFS="." '{$NF += 1; print}')

	local blame_line

	blame_line=$(git_blame_line '"liferay-dxp-operator": "[0-9]+\.[0-9]+\.[0-9]+"' "${_VERSIONS_JSON_FILE}")

	sed \
		--in-place \
		--regexp-extended \
		--expression "${blame_line}s/\"liferay-dxp-operator\": \"[0-9]+\.[0-9]+\.[0-9]+\"/\"liferay-dxp-operator\": \"${new_version}\"/" \
		"${_VERSIONS_JSON_FILE}"

	local operator_values_yaml="${ROOT_CLOUD_DIR}/helm/dxp-operator/values.yaml"

	record_chart_file_update \
		"${operator_values_yaml}" \
		sed \
			--in-place \
			--regexp-extended \
			--expression "/^image:/,/^[^[:space:]]/ s/^(    tag: ).*/\1${new_version}/" \
			"${operator_values_yaml}"
}

function _check_bootstrap {
	local bootstrap_name=${1}

	shift

	local blame_sha

	blame_sha=$(git_blame_sha '"liferay-'"${bootstrap_name}"'-bootstrap": ".*"' "${_VERSIONS_JSON_FILE}")

	if ! is_commit "${blame_sha}"
	then
		echo "The blame boundary commit for liferay-${bootstrap_name}-bootstrap cannot be resolved." >&2

		return
	fi

	local bootstrap_source

	for bootstrap_source in "${@}"
	do
		local commit_count

		commit_count=$(git rev-list --count "${blame_sha}..HEAD" -- "${bootstrap_source}")

		if [[ "${commit_count}" -gt 0 ]]
		then
			git rev-list --oneline "${blame_sha}..HEAD" -- "${bootstrap_source}"

			echo "The version in ${_VERSIONS_JSON_FILE} is outdated. Updating liferay-${bootstrap_name}-bootstrap version." >&2
			echo "" >&2

			_bump_bootstrap_version "${bootstrap_name}"

			return
		fi
	done
}

function _check_operator {
	local blame_sha

	blame_sha=$(git_blame_sha '"liferay-dxp-operator": ".*"' "${_VERSIONS_JSON_FILE}")

	if ! is_commit "${blame_sha}"
	then
		echo "The blame boundary commit for liferay-dxp-operator cannot be resolved." >&2

		return
	fi

	local commit_count

	commit_count=$(git rev-list --count "${blame_sha}..HEAD" -- "${ROOT_CLOUD_DIR}/operator")

	if [[ "${commit_count}" -gt 0 ]]
	then
		git rev-list --oneline "${blame_sha}..HEAD" -- "${ROOT_CLOUD_DIR}/operator"

		echo "The version in ${_VERSIONS_JSON_FILE} is outdated. Updating liferay-dxp-operator version." >&2
		echo "" >&2

		_bump_operator_version
	fi
}

function _has_modified_bootstraps {
	if [[ "${#_MODIFIED_BOOTSTRAPS[@]}" -eq 0 ]]
	then
		return 1
	fi

	return 0
}

function _record_bootstrap_file_update {
	local bootstrap_name=${1}
	local file=${2}

	shift 2

	local previous_checksum

	previous_checksum=$(get_file_checksum "${file}")

	"${@}"

	if [ "${previous_checksum}" != "$(get_file_checksum "${file}")" ]
	then
		_record_modified_bootstrap "${bootstrap_name}"
	fi
}

function _record_modified_bootstrap {
	local bootstrap_name=${1}

	if has_array_element "${bootstrap_name}" "${_BUMPED_BOOTSTRAPS[@]}" "${_MODIFIED_BOOTSTRAPS[@]}"
	then
		return
	fi

	_MODIFIED_BOOTSTRAPS+=("${bootstrap_name}")
}

function _update_chart_versions_json {
	local chart_name="liferay-${1}"
	local new_version=${2}

	local chart_versions_json_file="${SCRIPTS_DIR}/chart_versions.json"

	_record_bootstrap_file_update \
		"azure" \
		"${chart_versions_json_file}" \
		_write_chart_versions_json "${chart_name}" "${new_version}" "${chart_versions_json_file}"
}

function _update_default_chart_version {
	local helm_chart_yaml=${1}

	local helm_chart_name

	helm_chart_name=$(basename "$(dirname "${helm_chart_yaml}")")

	local new_version

	new_version=$(yq '.version' "${helm_chart_yaml}")

	case "${helm_chart_name}" in
		"aws" | "gcp")
			_update_resources_tfvars "${helm_chart_name}" "liferay_helm_chart_version" "${new_version}"
			;;
		"aws-infrastructure" | "gcp-infrastructure")
			_update_resources_tfvars "${helm_chart_name%%-*}" "infrastructure_helm_chart_version" "${new_version}"
			;;
		"aws-infrastructure-provider" | "gcp-infrastructure-provider")
			_update_resources_tfvars "${helm_chart_name%%-*}" "infrastructure_provider_helm_chart_version" "${new_version}"
			;;
		"dxp-operator")
			_update_platform_components_target_revision "liferay-dxp-operator" "${new_version}"
			;;
		"observability")
			_update_platform_components_target_revision "observability" "${new_version}"

			_update_resources_tfvars "aws" "observability_helm_chart_version" "${new_version}"
			_update_resources_tfvars "gcp" "observability_helm_chart_version" "${new_version}"
			;;
		"platform")
			_update_chart_versions_json "${helm_chart_name}" "${new_version}"
			;;
		"platform-components")
			_update_platform_target_revision "${new_version}"
			;;
	esac
}

function _update_default_versions {
	local chart_yaml_file

	while read -r chart_yaml_file
	do
		_update_default_chart_version "${chart_yaml_file}"
	done < <(find "${ROOT_CLOUD_DIR}" -name "Chart.yaml" -type f)
}

function _update_platform_components_target_revision {
	local chart_repository_name=${1}
	local new_version=${2}

	local platform_components_values_yaml="${ROOT_CLOUD_DIR}/helm/platform-components/values.yaml"

	record_chart_file_update \
		"${platform_components_values_yaml}" \
		sed \
			--expression "\|repoURL: .*/${chart_repository_name}\$|,/targetRevision: / s/\(targetRevision: \).*/\1${new_version}/" \
			--in-place \
			"${platform_components_values_yaml}"
}

function _update_platform_target_revision {
	local new_version=${1}

	local platform_values_yaml="${ROOT_CLOUD_DIR}/helm/platform/values.yaml"

	record_chart_file_update \
		"${platform_values_yaml}" \
		sed \
			--expression "s/^\(    targetRevision: \).*/\1${new_version}/" \
			--in-place \
			"${platform_values_yaml}"
}

function _update_resources_tfvars {
	local cloud=${1}
	local variable_name=${2}
	local new_version=${3}

	local resources_tfvars_file="${ROOT_CLOUD_DIR}/terraform/${cloud}/gitops/resources/terraform.tfvars"

	_record_bootstrap_file_update \
		"${cloud}" \
		"${resources_tfvars_file}" \
		sed \
			--expression "s/\(${variable_name} *= *\)\".*\"/\1\"${new_version}\"/" \
			--in-place \
			"${resources_tfvars_file}"
}

function _write_chart_versions_json {
	local chart_name=${1}
	local new_version=${2}
	local chart_versions_json_file=${3}

	local updated_chart_versions_json

	updated_chart_versions_json=$( \
		jq \
			--arg chart_name "${chart_name}" \
			--arg version "${new_version}" \
			--tab \
			'.[$chart_name] = $version' \
			"${chart_versions_json_file}")

	printf '%s' "${updated_chart_versions_json}" > "${chart_versions_json_file}"
}

main "${@}"