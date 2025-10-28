#!/bin/bash

set -eux

function main {
	echo "Executing run_on_boot.sh on $(date) by $(id)."

	local token=$( \
		curl \
		--header "X-aws-ec2-metadata-token-ttl-seconds: 21600" \
		--request PUT \
		"http://169.254.169.254/latest/api/token")

	local region=$( \
		curl \
			--header "X-aws-ec2-metadata-token: ${token}" \
			http://169.254.169.254/latest/meta-data/placement/region)

	echo "Region: ${region}"

	echo "Caller identity: $(aws sts get-caller-identity)"

	tree -a /opt/liferay

	local terraform_dir=/opt/liferay/terraform

	pushd "${terraform_dir}/ecr"

	terraform \
		apply \
		-auto-approve \
		-var deployment_name=lfr-ami \
		-var "region=${region}"

	terraform output > "${terraform_dir}/eks/terraform.tfvars"

	local ecr_dxp_repository_url=$( \
		terraform \
			output \
			-json ecr_repositories \
			| jq --raw-output '."liferay/dxp".url')

	local ecr_registry_url=${ecr_dxp_repository_url%%/*}

	aws \
		ecr \
		get-login-password \
		--region "${region}" \
		| oras login --username AWS --password-stdin "${ecr_registry_url}"

	local image_dir=/opt/liferay/image

	local dxp_image_tag=$(oras repo tags --oci-layout "${image_dir}/dxp")

	oras \
		cp \
		--from-oci-layout \
		--no-tty \
		"${image_dir}/dxp:${dxp_image_tag}" \
		"${ecr_dxp_repository_url}:${dxp_image_tag}"

	popd

	pushd "${terraform_dir}/eks"

	terraform \
		apply \
		-auto-approve \
		-var arn_partition=aws-us-gov \
		-var node_instance_type=t3.2xlarge

	terraform output > "${terraform_dir}/dependencies/terraform.tfvars"

	aws \
		eks \
		update-kubeconfig \
		--name $(terraform output -raw cluster_name) \
		--region $(terraform output -raw region)

	kubectl cluster-info

	popd

	pushd "${terraform_dir}/dependencies"

	terraform apply -auto-approve

	local values_file_argument=""

	if [ -f /opt/liferay/values.yaml ]
	then
		values_file_argument="--values /opt/liferay/values.yaml"
	fi

	local namespace=$(terraform output -raw deployment_namespace)
	local role_arn=$(terraform output -raw liferay_sa_role)

	helm \
		upgrade \
		liferay \
		/opt/liferay/chart/liferay-aws \
		--install \
		--namespace "${namespace}" \
		--set "liferay-default.image.repository=${ecr_dxp_repository_url}" \
		--set "liferay-default.image.tag=${dxp_image_tag}" \
		--set "liferay-default.ingress.className=nginx" \
		--set "liferay-default.ingress.enabled=true" \
		--set "liferay-default.ingress.rules[0].http.paths[0].backend.service.name=liferay-default" \
		--set "liferay-default.ingress.rules[0].http.paths[0].backend.service.port.name=http" \
		--set "liferay-default.ingress.rules[0].http.paths[0].path=/" \
		--set "liferay-default.ingress.rules[0].http.paths[0].pathType=ImplementationSpecific" \
		--set "liferay-default.serviceAccount.annotations.eks\.amazonaws\.com/role-arn=${role_arn}" \
		${values_file_argument}

	helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

	helm repo update

	helm \
		upgrade \
		nginx-ingress-controller \
		ingress-nginx/ingress-nginx \
		--create-namespace \
		--install \
		--namespace nginx-ingress-controller \
		--set "controller.service.annotations.service\.beta\.kubernetes\.io/aws-load-balancer-backend-protocol=tcp" \
		--set "controller.service.annotations.service\.beta\.kubernetes\.io/aws-load-balancer-scheme=internal" \
		--set "controller.service.annotations.service\.beta\.kubernetes\.io/aws-load-balancer-type=nlb" \
		--set-string "controller.service.annotations.service\.beta\.kubernetes\.io/aws-load-balancer-internal=false" \
		--version 4.13.3

	kubectl \
		rollout \
		status \
		statefulset/liferay-default \
		--namespace "${namespace}" \
		--timeout=1200s

	local public_address=$( \
		kubectl \
			get \
			ingress \
			liferay-default \
			--namespace "${namespace}" \
			--output jsonpath='{.status.loadBalancer.ingress[0].hostname}')

	echo "Open your browser to http://${public_address}."
}

main