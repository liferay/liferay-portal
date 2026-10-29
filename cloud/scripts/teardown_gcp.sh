#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

source "$(dirname "${BASH_SOURCE[0]}")/_gcp_common.sh"

function main {
	if [ ${#} -eq 0 ]
	then
		echo "Usage: ${0} <configuration-json-file>" >&2
		echo "" >&2
		echo "See cloud/scripts/config.json.example_gcp for a sample." >&2

		exit 1
	fi

	check_utils gcloud helm jq kubectl terraform

	validate_config_json "${1}"

	local configuration_json_file

	configuration_json_file=$(resolve_config_json "${1}")

	confirm_teardown "GKE"

	prepare_terraform_stacks "gcp" "gke" "${configuration_json_file}"

	log_in "${configuration_json_file}"

	tear_down "gcp" "gke" "${configuration_json_file}"

	if ! has_remote_tfstate "${configuration_json_file}"
	then
		return
	fi

	local bucket_name

	bucket_name=$(jq --raw-output '.tfstate.bucket_name' "${configuration_json_file}")

	local project_id

	project_id=$(jq --raw-output '.project_id' "${configuration_json_file}")

	local tfstate_prefix

	tfstate_prefix=$(get_tfstate_prefix "${configuration_json_file}")

	if _has_other_deployment_tfstate "${bucket_name}" "${project_id}" "${tfstate_prefix}"
	then
		echo "Bucket ${bucket_name} was kept because it still holds the Terraform state of other deployments."

		return
	fi

	if ! is_confirmed "delete the Terraform state bucket ${bucket_name}"
	then
		return
	fi

	_delete_tfstate_bucket "${bucket_name}" "${project_id}"

	_destroy_tfstate_kms_key_versions "${bucket_name}" "${configuration_json_file}" "${project_id}"
}

function _delete_tfstate_bucket {
	local bucket_name="${1}"
	local project_id="${2}"

	echo "Deleting bucket ${bucket_name}."

	gcloud storage buckets update \
		"gs://${bucket_name}" \
		--clear-retention-period \
		--project "${project_id}"

	gcloud storage rm \
		"gs://${bucket_name}" \
		--project "${project_id}" \
		--recursive

	echo "Bucket ${bucket_name} was deleted successfully."
}

function _destroy_tfstate_kms_key_versions {
	local bucket_name="${1}"
	local configuration_json_file="${2}"
	local project_id="${3}"

	local key_name="tfstate-${bucket_name}"

	local region

	region=$(jq --raw-output '.region' "${configuration_json_file}")

	if ! gcloud kms keys describe \
		"${key_name}" \
		--keyring "${key_name}" \
		--location "${region}" \
		--project "${project_id}" \
		&> /dev/null
	then
		echo "KMS key ${key_name} does not exist. Skipping the destruction process."

		return
	fi

	local key_versions

	key_versions=$( \
		gcloud kms keys versions list \
			--filter "state:(DISABLED OR ENABLED)" \
			--format "value(name.basename())" \
			--key "${key_name}" \
			--keyring "${key_name}" \
			--location "${region}" \
			--project "${project_id}")

	local key_version

	for key_version in ${key_versions}
	do
		gcloud kms keys versions destroy \
			"${key_version}" \
			--key "${key_name}" \
			--keyring "${key_name}" \
			--location "${region}" \
			--project "${project_id}" \
			--quiet \
			> /dev/null
	done

	echo "The versions of KMS key ${key_name} were scheduled for destruction. The key ring and key ${key_name} remain in the project ${project_id}."
}

function _has_other_deployment_tfstate {
	local bucket_name="${1}"
	local project_id="${2}"
	local tfstate_prefix="${3}"

	local object_urls

	object_urls=$( \
		gcloud storage ls \
			--all-versions \
			--project "${project_id}" \
			"gs://${bucket_name}/**") || exit 1

	grep \
		--invert-match \
		--quiet \
		"^gs://${bucket_name}/${tfstate_prefix}" <<< "${object_urls}"
}

main "${@}"