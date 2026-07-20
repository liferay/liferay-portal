#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function main {
	if [[ "${BASH_SOURCE[0]}" != "${0}" ]]
	then
		return
	fi

	_check_utils curl jq tar terraform

	_check_terraform_version "1.5.0"

	local config_file

	config_file=$(_get_config_file "${1:-}")

	local branch

	branch=$(_get_branch "${config_file}")

	local provider

	provider=$(_get_provider "${config_file}")

	local version

	version=$(_get_version "${config_file}");

	local extracted_dir

	extracted_dir=$(_download_and_extract_files "${branch}" "${provider}" "${version}")

	"${extracted_dir}/cloud/scripts/setup_${provider}.sh" "${config_file}" "${extracted_dir}/cloud/scripts/versions_${provider}.tfvars"
}

function _check_terraform_version {
	local found_version

	found_version=$(terraform --version | awk '/^Terraform v/ {print $2; exit}')
	found_version="${found_version#v}"

	local required_version="${1}"

	local lowest_version

	lowest_version=$(printf "%s\n%s\n" "${required_version}" "${found_version}" | sort --version-sort | head -n 1)

	if [ "${lowest_version}" != "${required_version}" ]
	then
		echo "The installed Terraform version ${found_version} is older than ${required_version}." >&2

		exit 1
	fi
}

function _check_utils {
	for util in "${@}"
	do
		if (! command -v "${util}" &> /dev/null)
		then
			echo "The utility ${util} is not installed."

			exit 1
		fi
	done
}

function _download_and_extract_files {
	local branch="${1}"
	local provider="${2}"
	local version="${3}"

	local bucket_name="liferay-cloud-native-bootstrap"
	local download_base_url="https://cdn.liferay.cloud"
	local prefix="bootstrap/liferay-${provider}-bootstrap"

	if [ -n "${branch}" ]
	then
		bucket_name="liferay-cloud-native-bootstrap-nonprd"
		download_base_url="https://cdn.liferay.sh"

		local sanitized_branch

		sanitized_branch=$(echo "${branch}" | tr '/' '-')

		prefix="bootstrap/${sanitized_branch}/liferay-${provider}-bootstrap"
	fi

	local json

	json=$( \
		curl \
			--location \
			--silent \
			"https://storage.googleapis.com/storage/v1/b/${bucket_name}/o?prefix=${prefix}/&projection=noAcl")

	if [ ! -n "${json}" ]
	then
		echo "Unable to get metadata from gs://${bucket_name}/${prefix}" >&2

		exit 1
	fi

	local output_path

	if [ "${version}" == "latest" ]
	then
		output_path=$( \
			jq \
				--raw-output \
				'.items
				| map(select(.name | endswith(".tar.gz")))
				| sort_by(.updated)
				| last
				| .name' <<< "${json}")

	else
		output_path=$( \
			jq \
				--arg sn "${prefix}/liferay-${provider}-bootstrap-${version}.tar.gz" \
				--raw-output \
				'.items[]
				| select(.name == $sn)
				| .name' <<< "${json}")
	fi

	local output_file

	output_file=$(basename "${output_path}")

	if [ "${output_file}" == "null" ] || [ -z "${output_file}" ]
	then
		echo "There are no files in gs://${bucket_name}/${prefix}/ for the version \"${version}\"" >&2

		exit 1
	fi

	if [ -e "${output_file}" ]
	then
		rm "${output_file}"
	fi

	curl \
		--fail \
		--location \
		--output "${output_file}" \
		--silent \
		--show-error \
		"${download_base_url}/${output_path}"

	local checksum_file="${output_file}.sha256"

	if [ -e "${checksum_file}" ]
	then
		rm "${checksum_file}"
	fi

	if ! curl \
		--fail \
		--location \
		--output "${checksum_file}" \
		--silent \
		--show-error \
		"${download_base_url}/${output_path}.sha256"
	then
		echo "Unable to download checksum from ${download_base_url}/${output_path}.sha256." >&2

		exit 1
	fi

	_verify_checksum "${output_file}" "${checksum_file}"

	local output_dir="${output_file%.tar.gz}"

	if [ ! -d "${output_dir}" ]
	then
		mkdir "${output_dir}"
	fi

	tar \
		--directory "${output_dir}" \
		--extract \
		--file "${output_file}"

	echo "${output_dir}"
}

function _get_branch {
	local config_file="${1}"

	local branch

	branch=$(jq -r ".options.branch // empty" "${config_file}")

	echo "${branch}"
}

function _get_config_file {
	local config_file="${1}"

	if [ -z "${config_file}" ]
	then
		config_file="config.json"
	fi

	if [ ! -f "${config_file}" ]
	then
		echo "The configuration file ${config_file} does not exist." >&2

		exit 1
	fi

	echo "${config_file}"
}

function _get_provider {
	local config_file="${1}"

	local provider

	provider=$(jq -r ".options.provider // empty" "${config_file}")

	if [ -z "${provider}" ]
	then
		provider=$(jq -r ".provider // empty" "${config_file}")

		if [ -z "${provider}" ]
		then
			echo "No provider is specified in ${config_file}." >&2

			exit 1
		fi
	fi

	_is_supported_provider "${provider}"

	if ! _is_supported_provider "${provider}"
	then
		echo "Unsupported provider ${provider} was specified in ${config_file}." >&2

		exit 1
	fi

	echo "${provider}"
}

function _get_version {
	local config_file="${1}"

	local version

	version=$(jq -r ".options.version // empty" "${config_file}")

	if [ -z "${version}" ]
	then
		version="latest"
	fi

	echo "${version}"
}

function _is_supported_provider {
	case "${1}" in
		aws|azure|gcp)
			return 0
			;;
		*)
			return 1
			;;
	esac
}

function _sha256 {
	if command -v sha256sum &> /dev/null
	then
		sha256sum "${@}"
	elif command -v shasum &> /dev/null
	then
		shasum --algorithm 256 "${@}"
	else
		echo "Neither sha256sum nor shasum is installed." >&2

		exit 1
	fi
}

function _verify_checksum {
	local checksum_file="${2}"
	local output_file="${1}"

	local expected_digest

	expected_digest=$(awk '{print $1; exit}' "${checksum_file}")

	if [[ ! "${expected_digest}" =~ ^[0-9a-f]{64}$ ]]
	then
		echo "Invalid expected checksum format in ${checksum_file}." >&2

		exit 1
	fi

	local sha256_output

	sha256_output=$(_sha256 "${output_file}") || exit 1

	local actual_digest="${sha256_output%% *}"

	if [ "${expected_digest}" != "${actual_digest}" ]
	then
		echo "Checksum verification failed for ${output_file}." >&2

		exit 1
	fi
}

main ${1+"$@"}