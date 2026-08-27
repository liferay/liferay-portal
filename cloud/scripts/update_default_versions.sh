#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

_SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

_ROOT_CLOUD_DIR=$(cd "${_SCRIPTS_DIR}/.." && pwd)
_VERSIONS_JSON_FILE="${_SCRIPTS_DIR}/versions.json"

function main {
	find "${_ROOT_CLOUD_DIR}" -name "Chart.yaml" -type f | while read -r chart_yaml_file;
	do
		_update_default_chart_version "${chart_yaml_file}"
	done

	local aws_bootstrap_sources=(
		"${_ROOT_CLOUD_DIR}/scripts/setup_aws.sh"
		"${_ROOT_CLOUD_DIR}/terraform/aws/eks"
		"${_ROOT_CLOUD_DIR}/terraform/aws/gitops/platform"
		"${_ROOT_CLOUD_DIR}/terraform/aws/gitops/resources"
	)

	_check_bootstrap "aws" "${aws_bootstrap_sources[@]}"

	local azure_bootstrap_sources=(
		"${_ROOT_CLOUD_DIR}/scripts/_azure_common.sh"
		"${_ROOT_CLOUD_DIR}/scripts/chart_versions.json"
		"${_ROOT_CLOUD_DIR}/scripts/setup_azure.sh"
		"${_ROOT_CLOUD_DIR}/terraform/azure/aks"
		"${_ROOT_CLOUD_DIR}/terraform/azure/platform"
		"${_ROOT_CLOUD_DIR}/terraform/modules/argocd"
	)

	_check_bootstrap "azure" "${azure_bootstrap_sources[@]}"

	local gcp_bootstrap_sources=(
		"${_ROOT_CLOUD_DIR}/scripts/setup_gcp.sh"
		"${_ROOT_CLOUD_DIR}/terraform/gcp/gke"
		"${_ROOT_CLOUD_DIR}/terraform/gcp/gitops/platform"
		"${_ROOT_CLOUD_DIR}/terraform/gcp/gitops/resources"
	)

	_check_bootstrap "gcp" "${gcp_bootstrap_sources[@]}"

	_check_operator
}

function _bump_bootstrap_version {
	local bootstrap_name="${1}"

	local current_version

	current_version=$(jq --raw-output '."liferay-'"${bootstrap_name}"'-bootstrap"' "${_VERSIONS_JSON_FILE}")

	local new_version

	new_version=$(echo "${current_version}" | awk -F"." -v OFS="." '{$NF += 1; print}')

	sed \
		--in-place \
		--regexp-extended \
		"s/\"version\": \".*\"/\"version\": \"${new_version}\"/" \
		"${_ROOT_CLOUD_DIR}/scripts/config.json.example_${bootstrap_name}"

	local git_blame_line

	git_blame_line=$(_git_blame_line '"liferay-'"${bootstrap_name}"'-bootstrap": "[0-9]+\.[0-9]+\.[0-9]+"' "${_VERSIONS_JSON_FILE}")

	sed \
		--in-place \
		--regexp-extended \
		"${git_blame_line}s/\"liferay-${bootstrap_name}-bootstrap\": \"[0-9]+\.[0-9]+\.[0-9]+\"/\"liferay-${1}-bootstrap\": \"${new_version}\"/" \
		"${_VERSIONS_JSON_FILE}"
}

function _bump_operator_version {
	local current_version

	current_version=$(jq --raw-output '."liferay-dxp-operator"' "${_VERSIONS_JSON_FILE}")

	local new_version

	new_version=$(echo "${current_version}" | awk -F"." -v OFS="." '{$NF += 1; print}')

	local git_blame_line

	git_blame_line=$(_git_blame_line '"liferay-dxp-operator": "[0-9]+\.[0-9]+\.[0-9]+"' "${_VERSIONS_JSON_FILE}")

	sed \
		--in-place \
		--regexp-extended \
		"${git_blame_line}s/\"liferay-dxp-operator\": \"[0-9]+\.[0-9]+\.[0-9]+\"/\"liferay-dxp-operator\": \"${new_version}\"/" \
		"${_VERSIONS_JSON_FILE}"

	sed \
		--in-place \
		--regexp-extended \
		"/^image:/,/^[^[:space:]]/ s/^(    tag: ).*/\1${new_version}/" \
		"${_ROOT_CLOUD_DIR}/helm/dxp-operator/values.yaml"
}

function _check_bootstrap {
	local bootstrap_name="${1}"

	shift

	local git_blame_sha

	git_blame_sha=$(_git_blame_sha '"liferay-'"${bootstrap_name}"'-bootstrap": ".*"' "${_VERSIONS_JSON_FILE}")

	local bootstrap_sources

	mapfile -d '' bootstrap_sources < <(printf '%s\0' "$@")

	for source in "${bootstrap_sources[@]}"
	do
		local clean_source="${source%$'\0'}"

		local commit_count

		commit_count=$(git rev-list --count "${git_blame_sha}..HEAD" -- "${clean_source}")

		if [ ${commit_count} -gt 0 ]; then
			git rev-list --oneline "${git_blame_sha}..HEAD" -- "${clean_source}"

			echo "The version in ${_VERSIONS_JSON_FILE} is outdated. Updating liferay-${bootstrap_name}-bootstrap version." >&2
			echo "" >&2

			_bump_bootstrap_version "${bootstrap_name}"

			return
		fi
	done
}

function _check_operator {
	local git_blame_sha

	git_blame_sha=$(_git_blame_sha '"liferay-dxp-operator": ".*"' "${_VERSIONS_JSON_FILE}")

	git_blame_sha="${git_blame_sha#^}"

	if [[ -z ${git_blame_sha} ]] || ! git rev-parse --quiet --verify "${git_blame_sha}^{commit}" > /dev/null
	then
		echo "The blame boundary commit for liferay-dxp-operator cannot be resolved." >&2

		return
	fi

	local commit_count

	commit_count=$(git rev-list --count "${git_blame_sha}..HEAD" -- "${_ROOT_CLOUD_DIR}/operator")

	if [ ${commit_count} -gt 0 ]
	then
		git rev-list --oneline "${git_blame_sha}..HEAD" -- "${_ROOT_CLOUD_DIR}/operator"

		echo "The version in ${_VERSIONS_JSON_FILE} is outdated. Updating liferay-dxp-operator version." >&2
		echo "" >&2

		_bump_operator_version
	fi
}

function _git_blame_line {
	local pattern="${1}"
	local git_path="${2}"

	local blame_line

	blame_line=$(grep --extended-regexp --line-number "${pattern}" "${git_path}" | cut --delimiter=':' --fields=1)

	echo "${blame_line}"
}

function _git_blame_sha {
	local pattern="${1}"
	local git_path="${2}"

	local git_blame_line

	git_blame_line=$(_git_blame_line "${pattern}" "${git_path}")

	local target_sha

	target_sha=$(git blame -L "${git_blame_line}","${git_blame_line}" -- "${git_path}" | cut --delimiter=' ' --fields=1)

	echo "${target_sha}"
}

function _update_chart_versions_json {
	local chart_name="liferay-${1}"
	local new_version="${2}"

	local chart_versions_json_file="${_SCRIPTS_DIR}/chart_versions.json"

	local updated_chart_versions_json

	updated_chart_versions_json=$(jq --arg chart_name "${chart_name}" --arg version "${new_version}" --tab '.[$chart_name] = $version' "${chart_versions_json_file}")

	printf '%s' "${updated_chart_versions_json}" > "${chart_versions_json_file}"
}

function _update_default_chart_version {
	local helm_chart_yaml="${1}"

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
			sed --in-place "s/^\(    targetRevision: \).*/\1${new_version}/" "${_ROOT_CLOUD_DIR}/helm/platform/values.yaml"
			;;
	esac
}

function _update_platform_components_target_revision {
	local chart_repository_name="${1}"
	local new_version="${2}"

	sed \
		--in-place \
		"\|repoURL: .*/${chart_repository_name}\$|,/targetRevision: / s/\(targetRevision: \).*/\1${new_version}/" \
		"${_ROOT_CLOUD_DIR}/helm/platform-components/values.yaml"
}

function _update_resources_tfvars {
	local cloud="${1}"
	local new_version="${3}"
	local variable_name="${2}"

	sed --in-place "s/\(${variable_name} *= *\)\".*\"/\1\"${new_version}\"/" "${_ROOT_CLOUD_DIR}/terraform/${cloud}/gitops/resources/terraform.tfvars"
}

main "$@"