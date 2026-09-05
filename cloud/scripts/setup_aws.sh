#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

_SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

_ROOT_CLOUD_DIR=$(cd "${_SCRIPTS_DIR}/.." && pwd)

function main {
	if [ ${#} -eq 0 ]
	then
		echo "Usage: ${0} <configuration-json-file>" >&2
		echo "" >&2
		echo "See cloud/scripts/config.json.example_aws for a sample." >&2

		exit 1
	fi

	_check_utils aws jq kubectl terraform

	_check_terraform_version "1.10.0"

	_validate_config_json "${1}"

	_generate_tfvars "${1}" "${_SCRIPTS_DIR}/global_terraform.tfvars"

	echo "Attempting to login to your AWS account via SSO."

	aws sso login

	local bucket_name=""
	local deployment_name=""
	local region=""

	local terraform_args=()

	while IFS= read -r terraform_arg
	do
		terraform_args+=("${terraform_arg}")
	done < <(_get_terraform_apply_args "${1}")

	if jq --exit-status '.variables.tfstate_bucket_name' "${1}" &> /dev/null
	then
		if ! jq --exit-status '.variables.deployment_name' "${1}" &> /dev/null
		then
			echo "The configuration JSON file must contain a key named \"variables.deployment_name\"." >&2

			exit 1
		fi

		if ! jq --exit-status '.variables.region' "${1}" &> /dev/null
		then
			echo "The configuration JSON file must contain a key named \"variables.region\"." >&2

			exit 1
		fi

		bucket_name="$(jq --raw-output '.variables.tfstate_bucket_name' "${1}")"
		deployment_name="$(jq --raw-output '.variables.deployment_name' "${1}")"
		region="$(jq --raw-output '.variables.region' "${1}")"

		_create_tfstate_bucket "${bucket_name}" "${region}"
	fi

	_set_up_aws_service_linked_roles

	_set_up_aws_eks "${bucket_name}" "${region}" "${deployment_name}" "${terraform_args[@]}"

	_set_up_aws_gitops "${bucket_name}" "${region}" "${deployment_name}" "${terraform_args[@]}"

	_port_forward_argo_cd
}

function _check_terraform_version {
	local found_version

	found_version=$(terraform --version | awk '/^Terraform v/ {print $2; exit}')
	found_version="${found_version#v}"

	local required_version="${1}"

	local lowest_version

	lowest_version=$(printf "%s\n%s\n" "${required_version}" "${found_version}" | sort --version-sort | head -n 1)

	if [[ ${lowest_version} != ${required_version} ]]
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

function _configure_s3_bucket {
	local alias_name
	local bucket_name="${1}"
	local region="${2}"

	local account_id

	account_id="$(aws sts get-caller-identity --output text --query "Account")"

	alias_name="alias/tfstate-${bucket_name}"

	local kms_key_id

	if ! kms_key_id=$( \
		aws kms describe-key --key-id "${alias_name}" \
			--output text \
			--query 'KeyMetadata.KeyId' \
			--region "${region}" 2>/dev/null)
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

function _create_tfstate_bucket {
	local bucket_name="${1}"
	local region="${2}"

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

function _generate_tfvars {
	local configuration_json_file="${1}"
	local tfvars_file="${2}"

	echo "Generating ${tfvars_file} from ${configuration_json_file}."

	local tfvars_content

	tfvars_content=$( \
		jq --raw-output '.variables
		| to_entries[]
		| if (.value | type) == "string"
		  then
		  	"\(.key) = \"\(.value)\""
		  elif (.value | type) == "array" or (.value | type) == "object"
		  then
		  	"\(.key) = \(.value | @json)"
		  else
		  	"\(.key) = \(.value)"
		  end' "${configuration_json_file}")

	if [[ -z ${tfvars_content} ]]
	then
		echo "The \"variables\" object in the configuration JSON file is empty. You will be prompted for all required variables."

		> "${tfvars_file}"
	else
		echo "${tfvars_content}" > "${tfvars_file}"
	fi

	echo "${tfvars_file} was generated successfully."
}

function _get_terraform_apply_args {
	local auto_approve="false"

	local configuration_json_file="${1}"

	if jq --exit-status '.options.auto_approve' "${configuration_json_file}" > /dev/null
	then
		auto_approve=$(jq --raw-output '.options.auto_approve' "${configuration_json_file}")
	fi

	local apply_args=(
		"-var-file=${_SCRIPTS_DIR}/global_terraform.tfvars")

	if [[ ${auto_approve} == true ]]
	then
		apply_args+=("-auto-approve")
	fi

	if jq --exit-status '.options.parallelism | numbers' "${configuration_json_file}" > /dev/null
	then
		local parallelism

		parallelism=$(jq --raw-output '.options.parallelism' "${configuration_json_file}")

		apply_args+=("-parallelism=${parallelism}")
	fi

	printf '%s\n' "${apply_args[@]}"
}

function _log {
	echo "[Tfstate bucket configuration] ${1}"
}

function _popd {
	popd > /dev/null
}

function _port_forward_argo_cd {
	_pushd "${_ROOT_CLOUD_DIR}/terraform/aws/gitops/platform"

	local argocd_namespace

	argocd_namespace=$(terraform output -raw argocd_namespace)

	local argocd_password

	argocd_password=$( \
		kubectl \
			get \
			secret \
			argocd-initial-admin-secret \
			--namespace ${argocd_namespace} \
			--output jsonpath="{.data.password}" \
		| base64 --decode)

	echo "Port-forwarding the ArgoCD service at http://localhost:8080."
	echo ""
	echo "Login with username \"admin\" and password \"${argocd_password}\" to continue monitoring setup."
	echo ""
	echo "Use CTRL+C to exit when finished."

	kubectl \
		port-forward \
		--namespace "${argocd_namespace}" \
		service/argocd-server \
		8080:443

	_popd
}

function _pushd {
	pushd "${1}" > /dev/null
}

function _set_up_aws_eks {
	local bucket_name="${1}"
	local deployment_name="${3}"
	local region="${2}"

	_pushd "${_ROOT_CLOUD_DIR}/terraform/aws/eks"

	echo "Setting up the AWS EKS cluster."

	_terraform_init_and_apply "." "eks" "${bucket_name}" "${deployment_name}" "${region}" "${@:4}"

	export KUBE_CONFIG_PATH="${HOME}/.kube/config"

	aws \
		eks \
		update-kubeconfig \
		--name "$(terraform output -raw cluster_name)" \
		--region "$(terraform output -raw region)"

	echo "AWS EKS cluster setup complete."

	_popd
}

function _set_up_aws_gitops {
	local bucket_name="${1}"
	local deployment_name="${3}"
	local region="${2}"

	_pushd "${_ROOT_CLOUD_DIR}/terraform/aws/gitops"

	echo "Setting up GitOps infrastructure."

	_terraform_init_and_apply "./platform" "gitops/platform" "${bucket_name}" "${deployment_name}" "${region}" "${@:4}"

	_terraform_init_and_apply "./resources" "gitops/resources" "${bucket_name}" "${deployment_name}" "${region}" "${@:4}"

	echo "GitOps infrastructure setup complete."

	_popd
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

function _terraform_init_and_apply {
	local bucket_name="${3}"
	local deployment_name="${4}"
	local folder_separator="${2}"
	local region="${5}"

	_pushd "${1}"

	if [[ -n ${bucket_name} ]]
	then
	terraform init \
		-backend-config="bucket=${bucket_name}" \
		-backend-config="encrypt=true" \
		-backend-config="key=${deployment_name}/${region}/${folder_separator}/terraform.tfstate" \
		-backend-config="region=${region}" \
		-backend-config="use_lockfile=true"
	else
			cat > backend_override.tf <<EOF
terraform {
	backend "local" {}
}
EOF
			terraform init
	fi

	terraform apply "${@:6}"

	_popd
}

function _validate_config_json {
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

	if ! jq --exit-status '.variables | objects' "${configuration_json_file}" > /dev/null
	then
		echo "The configuration JSON file must contain a root object named \"variables\"." >&2

		exit 1
	fi
}

main "${@}"