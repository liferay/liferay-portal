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

	prepare_terraform_stacks "gcp" "gke" "${configuration_json_file}"

	log_in "${configuration_json_file}"

	if has_remote_tfstate "${configuration_json_file}"
	then
		_create_tfstate_bucket "${configuration_json_file}"
	fi

	set_up "gcp" "gke" "${configuration_json_file}"
}

function _configure_gcs_bucket {
	local bucket_name="${1}"
	local project_id="${2}"
	local region="${3}"

	local key_name="tfstate-${bucket_name}"

	if ! gcloud kms keyrings describe \
		"${key_name}" \
		--location "${region}" \
		--project "${project_id}" \
		&> /dev/null
	then
		log_tfstate_setup "Creating KMS keyring ${key_name}."

		gcloud kms keyrings create \
			"${key_name}" \
			--location "${region}" \
			--project "${project_id}"

		log_tfstate_setup "KMS keyring ${key_name} was created successfully."
	else
		log_tfstate_setup "KMS keyring ${key_name} already exists. Skipping creation process."
	fi

	if ! gcloud kms keys describe \
		"${key_name}" \
		--keyring "${key_name}" \
		--location "${region}" \
		--project "${project_id}" \
		&> /dev/null
	then
		log_tfstate_setup "Creating KMS key ${key_name}."

		gcloud kms keys create \
			"${key_name}" \
			--keyring "${key_name}" \
			--location "${region}" \
			--project "${project_id}" \
			--purpose "encryption"

		log_tfstate_setup "KMS key ${key_name} was created successfully."
	else
		log_tfstate_setup "KMS key ${key_name} already exists. Skipping creation process."

		_ensure_enabled_kms_key_version "${key_name}" "${project_id}" "${region}"
	fi

	local service_agent

	service_agent=$(gcloud storage service-agent --format "value(.)" --project "${project_id}")

	gcloud kms keys add-iam-policy-binding \
		"${key_name}" \
		--keyring "${key_name}" \
		--location "${region}" \
		--member "serviceAccount:${service_agent}" \
		--project "${project_id}" \
		--role "roles/cloudkms.cryptoKeyEncrypterDecrypter" \
		> /dev/null

	gcloud storage buckets update \
		"gs://${bucket_name}" \
		--default-encryption-key "projects/${project_id}/locations/${region}/keyRings/${key_name}/cryptoKeys/${key_name}" \
		--project "${project_id}" \
		--retention-period "90d"
}

function _create_gcs_bucket {
	local bucket_name="${1}"
	local project_id="${2}"
	local region="${3}"

	gcloud storage buckets create \
		"gs://${bucket_name}" \
		--location "${region}" \
		--project "${project_id}" \
		--public-access-prevention \
		--uniform-bucket-level-access \
		1> /dev/null

	gcloud storage buckets update \
		"gs://${bucket_name}" \
		--project "${project_id}" \
		--versioning
}

function _create_tfstate_bucket {
	local configuration_json_file="${1}"

	local bucket_name

	bucket_name=$(jq --raw-output '.tfstate.bucket_name' "${configuration_json_file}")

	local project_id

	project_id=$(jq --raw-output '.project_id' "${configuration_json_file}")

	local region

	region=$(jq --raw-output '.region' "${configuration_json_file}")

	if ! gcloud storage buckets describe "gs://${bucket_name}" --project "${project_id}" &> /dev/null
	then
		log_tfstate_setup "Creating bucket ${bucket_name}."

		_create_gcs_bucket "${bucket_name}" "${project_id}" "${region}"

		log_tfstate_setup "Bucket ${bucket_name} was created successfully."
	else
		log_tfstate_setup "Bucket ${bucket_name} already exists. Skipping creation process."
	fi

	log_tfstate_setup "Configuring bucket ${bucket_name}."

	_configure_gcs_bucket "${bucket_name}" "${project_id}" "${region}"

	log_tfstate_setup "Bucket ${bucket_name} was configured successfully."
}

function _ensure_enabled_kms_key_version {
	local key_name="${1}"
	local project_id="${2}"
	local region="${3}"

	local enabled_key_versions

	enabled_key_versions=$( \
		gcloud kms keys versions list \
			--filter "state:ENABLED" \
			--format "value(name)" \
			--key "${key_name}" \
			--keyring "${key_name}" \
			--location "${region}" \
			--project "${project_id}")

	if [[ -n ${enabled_key_versions} ]]
	then
		return
	fi

	log_tfstate_setup "KMS key ${key_name} has no enabled version. Creating a primary version."

	gcloud kms keys versions create \
		--key "${key_name}" \
		--keyring "${key_name}" \
		--location "${region}" \
		--primary \
		--project "${project_id}" \
		> /dev/null

	log_tfstate_setup "A primary version of KMS key ${key_name} was created successfully."
}

main "${@}"