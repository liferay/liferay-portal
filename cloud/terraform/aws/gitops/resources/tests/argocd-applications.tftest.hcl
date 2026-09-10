mock_provider "aws" {
	mock_data "aws_iam_policy_document" {
		defaults={
			json="{\"Statement\": [], \"Version\": \"2012-10-17\"}"
		}
	}
	mock_resource "aws_iam_policy" {
		defaults={
			arn="arn:aws:iam::123456789012:policy/mock"
		}
	}
}
mock_provider "helm" {}
mock_provider "kubernetes" {}
override_data {
	target=data.aws_caller_identity.current
	values={
		account_id="123456789012"
	}
}
override_data {
	target=data.aws_eks_cluster.cluster
	values={
		identity=[{
			oidc=[{
				issuer="https://oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE"
			}]
		}]
		vpc_config=[{
			cluster_security_group_id="sg-0123456789abcdef0"
			endpoint_private_access=true
			endpoint_public_access=true
			public_access_cidrs=["0.0.0.0/0"]
			security_group_ids=[]
			subnet_ids=["subnet-aaa", "subnet-bbb"]
			vpc_id="vpc-0123456789abcdef0"
		}]
	}
}
override_data {
	target=data.aws_iam_role.envoy_proxy_role
	values={
		arn="arn:aws:iam::123456789012:role/liferay-test-envoy-proxy"
	}
}
override_data {
	target=data.aws_iam_role.liferay_irsa
	values={
		arn="arn:aws:iam::123456789012:role/liferay-test-irsa"
		id="liferay-test-irsa"
	}
}
override_data {
	target=data.aws_subnets.private
	values={
		ids=["subnet-aaa", "subnet-bbb"]
	}
}
override_data {
	target=data.aws_vpc.current
	values={
		cidr_block="10.0.0.0/16"
	}
}
run "should_include_required_prefixes_for_the_marketplace_chart_to_gateway_name" {
	assert {
		condition=length([
			for p in kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.sources[0].helm.parameters : p
			if p.name == "liferay-aws.liferay-default.network.gatewayName"
		]) == 1
		error_message="The liferay-aws-marketplace chart must include prefixes for both liferay-aws and liferay-default charts to gatewayName Helm parameter"
	}
	command=plan
	variables {
		liferay_helm_chart_name="liferay-aws-marketplace"
	}
}
run "should_leave_the_operator_settings_to_the_chart_by_default" {
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.heartbeatInterval" || startswith(p.name, "liferay-dxp-operator.image.") || p.name == "liferay-dxp-operator.provisioning.baseURL" || p.name == "liferay-dxp-operator.retry.maxDelay"
		]) == 0
		error_message="The infrastructure provider Application must not override operator settings unless they are configured"
	}
	command=plan
}
run "should_leave_the_replica_count_to_the_operator" {
	assert {
		condition=length([
			for difference in kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.ignoreDifferences : difference
			if difference.kind == "StatefulSet" && contains(difference.managedFieldsManagers, "liferay-dxp-operator")
		]) == 1
		error_message="The Liferay ApplicationSet template must ignore spec.replicas on the workload, since the operator caps it against the licensed ceiling"
	}
	command=plan
}
run "should_name_the_appprojects" {
	assert {
		condition=kubernetes_manifest.infrastructure_applicationset.manifest.spec.template.spec.project == "liferay-infrastructure"
		error_message="The infrastructure ApplicationSet template must target the liferay-infrastructure project"
	}
	assert {
		condition=kubernetes_manifest.infrastructure_appproject.manifest.metadata.name == "liferay-infrastructure"
		error_message="The infrastructure AppProject must be named liferay-infrastructure"
	}
	assert {
		condition=kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.project == "liferay-application"
		error_message="The Liferay ApplicationSet template must target the liferay-application project"
	}
	assert {
		condition=kubernetes_manifest.liferay_appproject.manifest.metadata.name == "liferay-application"
		error_message="The Liferay AppProject must be named liferay-application"
	}
	command=plan
}
run "should_not_permit_the_observability_chart_when_observability_is_disabled" {
	assert {
		condition=length([
			for repo in kubernetes_manifest.infrastructure_appproject.manifest.spec.sourceRepos : repo
			if strcontains(repo, "observability")
		]) == 0
		error_message="The infrastructure AppProject must not widen its allowed repositories when observability is disabled"
	}
	command=plan
}
run "should_pass_cluster_identity_to_the_provider_application" {
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "aws.clusterName" && p.value == "liferay-test-eks"
		]) == 1
		error_message="The infrastructure provider Application must pass the cluster name as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "deploymentName" && p.value == "liferay-test"
		]) == 1
		error_message="The infrastructure provider Application must pass the deployment name as a Helm parameter"
	}
	command=plan
}
run "should_pass_the_configured_operator_settings_to_the_provider_application" {
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.image.repository" && p.value == "registry.example.com/liferay-dxp-operator"
		]) == 1
		error_message="The infrastructure provider Application must pass the configured operator image repository as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.image.tag" && p.value == "1.2.3"
		]) == 1
		error_message="The infrastructure provider Application must pass the configured operator image tag as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.heartbeatInterval" && p.value == "90s"
		]) == 1
		error_message="The infrastructure provider Application must pass the configured heartbeat interval as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.provisioning.baseURL" && p.value == "https://provisioning.example.com"
		]) == 1
		error_message="The infrastructure provider Application must pass the configured provisioning base URL as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.retry.maxDelay" && p.value == "4m"
		]) == 1
		error_message="The infrastructure provider Application must pass the configured retry maximum delay as a Helm parameter"
	}
	command=plan
	variables {
		dxp_operator_config={
			heartbeat_interval="90s"
			image={
				repository="registry.example.com/liferay-dxp-operator"
				tag="1.2.3"
			}
			provisioning_base_url="https://provisioning.example.com"
			retry_max_delay="4m"
		}
	}
}
run "should_permit_the_observability_chart_when_observability_is_enabled" {
	assert {
		condition=contains(kubernetes_manifest.infrastructure_appproject.manifest.spec.sourceRepos, "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/observability")
		error_message="The infrastructure AppProject must permit the observability chart, since the observability Application it hosts is sourced from it"
	}
	assert {
		condition=contains(kubernetes_manifest.infrastructure_appproject.manifest.spec.sourceRepos, "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/observability/*")
		error_message="The infrastructure AppProject must permit the observability chart subpaths, the way it does for every other chart it allows"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
run "should_permit_the_overridden_observability_chart_url" {
	assert {
		condition=contains(kubernetes_manifest.infrastructure_appproject.manifest.spec.sourceRepos, "oci://example.test/liferay-helm-chart/observability")
		error_message="The infrastructure AppProject must permit whatever observability chart URL is configured, not only the default"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
		observability_helm_chart_config={
			chart_url="oci://example.test/liferay-helm-chart/observability"
		}
	}
}
run "should_retry_a_sync_that_lost_a_race_with_the_infrastructure_provider" {
	assert {
		condition=kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.syncPolicy.retry.limit == 10
		error_message="The Liferay ApplicationSet template must retry a failed sync, since ArgoCD does not retry a revision whose sync already failed"
	}
	assert {
		condition=kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.syncPolicy.retry.backoff.maxDuration == "5m"
		error_message="The Liferay ApplicationSet template must cap the retry backoff, so a custom resource definition that arrives late is still picked up"
	}
	assert {
		condition=!contains(kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.syncPolicy.syncOptions, "SkipDryRunOnMissingResource=true")
		error_message="The Liferay ApplicationSet template must keep the dry run for every resource, since tolerating a missing resource belongs on the one resource that needs it"
	}
	command=plan
}
run "should_scope_liferay_applicationset_helm_values_by_prefix" {
	assert {
		condition=length([
			for p in kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.sources[0].helm.parameters : p
			if p.name == "liferay-default.network.gatewayName"
		]) == 1
		error_message="The liferay-aws chart must include prefix for liferay-default chart at gatewayName Helm parameter"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	infrastructure_helm_chart_version="0.4.9"
	infrastructure_provider_helm_chart_version="0.3.12"
	liferay_git_repo_url="https://github.com/example/liferay-gitops.git"
	liferay_helm_chart_version="0.4.20"
	region="us-east-1"
}