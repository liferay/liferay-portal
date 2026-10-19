#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

source "$(dirname "${BASH_SOURCE[0]}")/_aws_common.sh"

function main {
	if [ ${#} -eq 0 ]
	then
		echo "Usage: ${0} <configuration-json-file>" >&2
		echo "" >&2
		echo "See cloud/scripts/config.json.example_aws for a sample." >&2

		exit 1
	fi

	check_utils aws helm jq kubectl terraform

	validate_config_json "${1}"

	local configuration_json_file

	configuration_json_file=$(resolve_config_json "${1}")

	confirm_teardown "EKS"

	prepare_terraform_stacks "aws" "eks" "${configuration_json_file}"

	log_in "${configuration_json_file}"

	tear_down "aws" "eks" "${configuration_json_file}"

	if has_remote_tfstate "${configuration_json_file}"
	then
		_delete_tfstate_bucket "${configuration_json_file}"
	fi
}

function _delete_object_versions {
	local bucket_name="${1}"
	local object_versions_json="${2}"
	local region="${3}"

	local delete_request_file

	delete_request_file=$(mktemp)

	local delete_request_json

	while IFS= read -r delete_request_json
	do
		echo "${delete_request_json}" > "${delete_request_file}"

		local delete_error_count

		delete_error_count=$( \
			aws s3api delete-objects \
				--bucket "${bucket_name}" \
				--bypass-governance-retention \
				--delete "file://${delete_request_file}" \
				--output text \
				--query 'length(Errors || `[]`)' \
				--region "${region}")

		if [ ${delete_error_count} -gt 0 ]
		then
			echo "Unable to delete ${delete_error_count} object versions from bucket ${bucket_name}. Check that you are allowed to bypass its object lock." >&2

			exit 1
		fi
	done < <(jq --compact-output 'range(0; length; 1000) as $index | {Objects: .[$index:$index + 1000], Quiet: true}' <<< "${object_versions_json}")

	rm "${delete_request_file}"
}

function _delete_tfstate_bucket {
	local configuration_json_file="${1}"

	local bucket_name

	bucket_name=$(jq --raw-output '.tfstate.bucket_name' "${configuration_json_file}")

	local deployment_name

	deployment_name=$(jq --raw-output '.deployment_name' "${configuration_json_file}")

	local region

	region=$(jq --raw-output '.region' "${configuration_json_file}")

	if ! aws s3api head-bucket --bucket "${bucket_name}" --region "${region}" &> /dev/null
	then
		echo "Bucket ${bucket_name} does not exist. Skipping the deletion process."

		return
	fi

	local object_versions_json

	object_versions_json=$( \
		aws s3api list-object-versions \
			--bucket "${bucket_name}" \
			--output json \
			--query '[Versions, DeleteMarkers][].{Key: Key, VersionId: VersionId}' \
			--region "${region}")

	local foreign_object_count

	foreign_object_count=$(jq --arg prefix "${deployment_name}/${region}/" '[.[] | select(.Key | startswith($prefix) | not)] | length' <<< "${object_versions_json}")

	if [ ${foreign_object_count} -gt 0 ]
	then
		echo "Bucket ${bucket_name} was kept because it still holds the Terraform state of other deployments."

		return
	fi

	if ! is_confirmed "delete the Terraform state bucket ${bucket_name}"
	then
		echo "Bucket ${bucket_name} was kept."

		return
	fi

	echo "Deleting bucket ${bucket_name}."

	_delete_object_versions "${bucket_name}" "${object_versions_json}" "${region}"

	aws s3api delete-bucket --bucket "${bucket_name}" --region "${region}"

	echo "Bucket ${bucket_name} was deleted successfully."

	_delete_tfstate_kms_key "${bucket_name}" "${region}"
}

function _delete_tfstate_kms_key {
	local bucket_name="${1}"
	local region="${2}"

	local alias_name="alias/tfstate-${bucket_name}"

	local kms_key_id

	kms_key_id=$(get_kms_key_id "${alias_name}" "${region}")

	if [[ -z ${kms_key_id} ]]
	then
		echo "KMS key alias ${alias_name} does not exist. Skipping the deletion process."

		return
	fi

	local deletion_date

	deletion_date=$( \
		aws kms schedule-key-deletion \
			--key-id "${kms_key_id}" \
			--output text \
			--query 'DeletionDate' \
			--region "${region}")

	aws kms delete-alias --alias-name "${alias_name}" --region "${region}"

	echo "KMS key ${kms_key_id} was scheduled for deletion on ${deletion_date}."
}

main "${@}"