#!/usr/bin/env bash

source "$(dirname "${BASH_SOURCE[0]}")/_common.sh"

function generate_remote_backend_overrides {
	local configuration_json_file="${1}"

	local bucket_name

	bucket_name=$(jq --raw-output '.tfstate.bucket_name' "${configuration_json_file}")

	local deployment_name

	deployment_name=$(jq --raw-output '.deployment_name' "${configuration_json_file}")

	local region

	region=$(jq --raw-output '.region' "${configuration_json_file}")

	local directory

	for directory in eks platform
	do
		cat > "${ROOT_CLOUD_DIR}/terraform/aws/${directory}/backend_override.tf" <<EOF
terraform {
	backend "s3" {
		bucket="${bucket_name}"
		encrypt=true
		key="${deployment_name}/${region}/${directory}/terraform.tfstate"
		region="${region}"
		use_lockfile=true
	}
}
EOF
	done
}

function get_kms_key_id {
	local alias_name="${1}"
	local region="${2}"

	aws kms list-aliases \
		--output text \
		--query "Aliases[?AliasName=='${alias_name}'].TargetKeyId" \
		--region "${region}"
}

function log_in {
	local configuration_json_file="${1}"

	if aws sts get-caller-identity &> /dev/null
	then
		echo "Reusing the active AWS session."
	else
		echo "Attempting to login to your AWS account via SSO."

		aws sso login
	fi

	local account_id

	account_id=$(jq --raw-output '.account_id' "${configuration_json_file}")

	local session_account_id

	session_account_id=$(aws sts get-caller-identity --output text --query "Account")

	if [[ ${session_account_id} != "${account_id}" ]]
	then
		echo "The active AWS session is for the account ${session_account_id}, not the configured account ${account_id}. Set AWS_PROFILE to a profile for the account ${account_id} and run the script again." >&2

		exit 1
	fi

	echo "Using the AWS account ${account_id}."
}

function update_kubeconfig {
	local cluster_name="${1}"
	local cluster_terraform_stack="${2}"

	local region

	region=$(run_terraform "aws" "${cluster_terraform_stack}" output -raw region)

	aws eks update-kubeconfig \
		--name "${cluster_name}" \
		--region "${region}"
}

function validate_config_json {
	local configuration_json_file="${1}"

	local required_keys=(
		".account_id"
		".deployment_name"
		".region"
	)

	if has_remote_tfstate "${configuration_json_file}"
	then
		required_keys+=(
			".tfstate.bucket_name"
		)
	fi

	check_config_json "${configuration_json_file}" "${required_keys[@]}"
}