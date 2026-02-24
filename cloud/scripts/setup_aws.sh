#!/bin/bash

set -eu

SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
ROOT_CLOUD_DIR=$(cd "${SCRIPTS_DIR}/.." && pwd)

function generate_tfvars {
	local json_file="$1"
	local tfvars_file="$2"

	if ! command -v jq &> /dev/null; then
		echo "Error: jq is not installed. Please install jq to use this script."
		exit 1
	fi

	if [ ! -f "$json_file" ]; then
		echo "Error: JSON file not found at: ${json_file}"
		exit 1
	fi

	if ! jq -e '.variables | objects' "$json_file" > /dev/null; then
		echo "Error: The JSON file must contain a root object named 'variables'."
		exit 1
	fi

	echo "Generating ${tfvars_file} from ${json_file}..."

	local tfvars_content
	tfvars_content=$(jq -r '.variables | to_entries[] |
		if (.value | type) == "string" then
			"\(.key) = \"\(.value)\""
		elif (.value | type) == "array" or (.value | type) == "object" then
			"\(.key) = \(.value | @json)"
		else
			"\(.key) = \(.value)"
		end' "$json_file")

	if [ -z "$tfvars_content" ]; then
		echo "Warning: The 'variables' object in the JSON is empty. You will be prompted for all required variables."
		> "${tfvars_file}"
	else
		echo "${tfvars_content}" > "${tfvars_file}"
	fi

	echo "${tfvars_file} generated successfully."
}

function main {
	if [ "$#" -ne 1 ]; then
		echo "Usage: $0 <path_to_config_json_file>"
		exit 1
	fi

	generate_tfvars "$1" "global_terraform.tfvars"

	echo "Attempting to login to your AWS account via SSO..."

	aws sso login

	setup_aws_eks

	setup_aws_gitops

	port_forward_argocd
}

function port_forward_argocd {
	local argocd_password=$(kubectl get secret argocd-initial-admin-secret --namespace argocd --output jsonpath="{.data.password}" | base64 --decode)

	echo "Port-forwarding the ArgoCD service at localhost:8080...."
	echo "Login with Username: admin and Password: ${argocd_password} to continue monitoring setup."
	echo "Use CTRL+C to exit when finished."

	kubectl port-forward --namespace argocd service/argocd-server 8080:443
}

function setup_aws_eks {
	_pushd "${ROOT_CLOUD_DIR}/terraform/aws/eks"

	echo "Setting up the AWS EKS cluster..."

	terraform_init_and_apply "."

	local region=$(terraform output -raw region)
	local cluster_name=$(terraform output -raw cluster_name)

	aws eks update-kubeconfig --name "${cluster_name}" --region "${region}" >&2

	echo "AWS EKS cluster setup complete."

	_popd
}

function setup_aws_gitops {
	_pushd "${ROOT_CLOUD_DIR}/terraform/aws/gitops"

	echo "Setting up GitOps Infrastructure..."

	terraform_init_and_apply "./platform"

	terraform_init_and_apply "./resources"

	echo "GitOps Infrastructure setup complete."

	_popd
}

function terraform_init_and_apply {
	_pushd "$1"

	terraform init > /dev/null

	terraform apply "-var-file=${SCRIPTS_DIR}/global_terraform.tfvars"

	_popd
}

function _popd {
	popd > /dev/null
}

function _pushd {
	pushd "$1" > /dev/null
}

main "$@"