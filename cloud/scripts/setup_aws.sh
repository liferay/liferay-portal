#!/bin/bash

set -eux

root_cloud_dir="$(dirname "$0")/.."

deployment_name=""
region=""

function main {
	echo "Attempting to login to your AWS account via SSO..."

	aws sso login

	setup_aws_eks

	setup_aws_gitops

	port_forward_argocd
}

function port_forward_argocd {
	local argocd_password=$(kubectl get secret argocd-initial-admin-secret --namespace argocd --output jsonpath="{.data.password}" | base64 --decode)

	echo "Port-forwarding the ArgoCD service at localhost:8080...."
	echo "Login with Username: admin and Password: ${argocd_password} for further setup monitoring."

	kubectl port-forward --namespace argocd service/argocd-server 8080:443
}

function setup_aws_eks {
	pushd "${root_cloud_dir}/terraform/aws/eks"

	echo "Setting up the AWS EKS cluster..."

	terraform_init_and_apply "."

	deployment_name=$(terraform output -raw deployment_name)
	region=$(terraform output -raw region)

	local cluster_name=$(terraform output -raw cluster_name)

	aws eks update-kubeconfig --name "${cluster_name}" --region "${region}"

	export KUBE_CONFIG_PATH="${HOME}/.kube/config"

	echo "AWS EKS cluster setup complete."

	popd
}

function setup_aws_gitops {
	pushd "${root_cloud_dir}/terraform/aws/gitops"

	echo "Setting up GitOps Infrastructure..."

	terraform_init_and_apply "./platform"

	terraform_init_and_apply "./resources" "-var=deployment_name=${deployment_name} -var=region=${region}"

	echo "GitOps Infrastructure setup complete."

	popd
}

function terraform_init_and_apply {
	pushd "$1"

	terraform init > /dev/null

	if [ -n "${2-}" ]
	then
		terraform apply $2
	else
		terraform apply
	fi

	popd
}

main