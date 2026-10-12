#!/usr/bin/env bash

source "$(dirname "${BASH_SOURCE[0]}")/_common.sh"

function aws_login {
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

function connect_to_cluster {
	push_directory "${ROOT_CLOUD_DIR}/terraform/aws/eks"

	echo "Connecting to the EKS cluster."

	terraform init

	local cluster_name

	cluster_name=$(terraform output -raw cluster_name)

	local region

	region=$(terraform output -raw region)

	if [[ -z ${cluster_name} ]] || [[ -z ${region} ]]
	then
		echo "There is no EKS cluster in the Terraform state." >&2

		pop_directory

		return 1
	fi

	export KUBE_CONFIG_PATH="${HOME}/.kube/config"

	if ! aws eks update-kubeconfig \
		--name "${cluster_name}" \
		--region "${region}"
	then
		echo "The EKS cluster ${cluster_name} could not be reached." >&2

		pop_directory

		return 1
	fi

	pop_directory
}

function generate_remote_backend_overrides {
	local bucket_name="${1}"
	local deployment_name="${2}"
	local region="${3}"

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

function validate_config_json {
	local configuration_json_file="${1}"

	local required_keys=(
		".account_id"
		".deployment_name"
		".region"
	)

	if jq --exit-status '.tfstate | objects' "${configuration_json_file}" &> /dev/null
	then
		required_keys+=(
			".tfstate.bucket_name"
		)
	fi

	check_config_json "${configuration_json_file}" "${required_keys[@]}"
}