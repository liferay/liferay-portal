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

	check_utils aws helm jq terraform

	validate_config_json "${1}"

	local configuration_json_file

	configuration_json_file=$(resolve_config_json "${1}")

	prepare_terraform_stacks "aws" "eks" "${configuration_json_file}"

	log_in "${configuration_json_file}"

	if has_remote_tfstate "${configuration_json_file}"
	then
		_create_tfstate_bucket "${configuration_json_file}"
	fi

	_set_up_aws_service_linked_roles

	set_up "aws" "eks" "${configuration_json_file}"
}

function _configure_s3_bucket {
	local bucket_name="${1}"
	local region="${2}"

	local account_id

	account_id="$(aws sts get-caller-identity --output text --query "Account")"

	local alias_name="alias/tfstate-${bucket_name}"

	local kms_key_id

	kms_key_id=$(get_kms_key_id "${alias_name}" "${region}")

	if [[ -z ${kms_key_id} ]]
	then
		_log "Creating KMS key for bucket ${bucket_name}."

		kms_key_id=$( \
			aws kms create-key \
				--description "Terraform State Storage Key" \
				--output text \
				--policy "{
					\"Statement\": [{
						\"Action\": \"kms:*\",
						\"Effect\": \"Allow\",
						\"Principal\": {\"AWS\": \"arn:aws:iam::${account_id}:root\"},
						\"Resource\": \"*\"
					}],
					\"Version\": \"2012-10-17\"
				}" \
				--query 'KeyMetadata.KeyId' \
				--region "${region}")

		aws kms create-alias \
			--alias-name "${alias_name}" \
			--region "${region}" \
			--target-key-id "${kms_key_id}"

		_log "KMS key for bucket ${bucket_name} was created successfully."
	else
		_log "KMS key for bucket ${bucket_name} already exists. Skipping creation process."
	fi

	aws s3api put-bucket-encryption \
		--bucket "${bucket_name}" \
		--region "${region}" \
		--server-side-encryption-configuration "{
			\"Rules\": [
				{
					\"ApplyServerSideEncryptionByDefault\": {
						\"KMSMasterKeyID\": \"${kms_key_id}\",
						\"SSEAlgorithm\": \"aws:kms\"
					}
				}
			]
		}"

	aws s3api put-object-lock-configuration \
		--bucket "${bucket_name}" \
		--object-lock-configuration '{
			"ObjectLockEnabled": "Enabled",
			"Rule": {
				"DefaultRetention": {
					"Days": 90,
					"Mode": "GOVERNANCE"
				}
			}
		}'\
		--region "${region}"

	aws s3api put-public-access-block \
		--bucket "${bucket_name}" \
		--public-access-block-configuration '{
			"BlockPublicAcls": true,
			"BlockPublicPolicy": true,
			"IgnorePublicAcls": true,
			"RestrictPublicBuckets": true
		}' \
		--region "${region}"
}

function _create_s3_bucket {
	local bucket_name="${1}"
	local region="${2}"

	if [[ ${region} == us-east-1 ]]
	then
		aws s3api create-bucket \
			--bucket "${bucket_name}" \
			--object-lock-enabled-for-bucket \
			--region "${region}" 1> /dev/null
	else
		aws s3api create-bucket \
			--bucket "${bucket_name}" \
			--create-bucket-configuration LocationConstraint="${region}" \
			--object-lock-enabled-for-bucket \
			--region "${region}" 1> /dev/null
	fi

	aws s3api put-bucket-versioning \
		--bucket "${bucket_name}" \
		--region "${region}" \
		--versioning-configuration Status=Enabled
}

function _create_tfstate_bucket {
	local configuration_json_file="${1}"

	local bucket_name

	bucket_name=$(jq --raw-output '.tfstate.bucket_name' "${configuration_json_file}")

	local region

	region=$(jq --raw-output '.region' "${configuration_json_file}")

	if ! aws s3api head-bucket --bucket "${bucket_name}" --region "${region}" &> /dev/null
	then
		_log "Creating bucket ${bucket_name}."

		_create_s3_bucket "${bucket_name}" "${region}"

		_log "Bucket ${bucket_name} was created successfully."
	else
		_log "Bucket ${bucket_name} already exists. Skipping creation process."
	fi

	_log "Configuring bucket ${bucket_name}."

	_configure_s3_bucket "${bucket_name}" "${region}"

	_log "Bucket ${bucket_name} was configured successfully."
}

function _log {
	echo "[Tfstate bucket configuration] ${1}"
}

function _set_up_aws_service_linked_roles {
	local service_linked_roles=(
		"opensearchservice.amazonaws.com:AWSServiceRoleForAmazonOpenSearchService"
		"rds.amazonaws.com:AWSServiceRoleForRDS"
	)

	echo "Setting up AWS service-linked roles."

	for service_linked_role in "${service_linked_roles[@]}"
	do
		local role_name="${service_linked_role##*:}"
		local service_name="${service_linked_role%%:*}"

		if ! aws iam get-role --role-name "${role_name}" &> /dev/null
		then
			echo "Setting up AWS service-linked role for ${service_name}."

			aws iam create-service-linked-role --aws-service-name "${service_name}" --no-cli-pager

			echo "AWS service-linked role for ${service_name} setup complete."
		else
			echo "AWS service-linked role for ${service_name} already exists."
		fi
	done

	echo "AWS service-linked roles setup complete."
}

main "${@}"