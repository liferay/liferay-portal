locals {
	cluster_secret_store_provider=local.default_secret_manager_enabled ? {
		gcpsm={
			projectID=var.project_id
		}
	} : var.cluster_secret_store.provider_hcl
	default_secret_manager_enabled=var.cluster_secret_store.provider_hcl == null
	deployment_context={
		deploymentName=var.deployment_name
		projectId=var.project_id
		projectNumber=local.project_number
		region=var.region
		vpcName=basename(data.google_container_cluster.gke.network)
	}
	first_zone=data.google_compute_zones.available.names[0]
	keda_enabled=var.keda_config.enabled && var.observability_config.enabled
	project_number=data.google_project.project.number
	secret_prefixes=[
		"liferay-certificates-",
		"liferay-credentials-",
		"liferay-licenses-",
	]
	workload_identity_bindings=merge([
		for identity, roles in local.workload_identity_roles : {
			for role in roles : "${identity}/${role}" => {
				identity=identity
				role=role
			}
		}
	]...)
	workload_identity_members={
		for identity, service_account in local.workload_identity_service_accounts : identity => "principal://iam.googleapis.com/projects/${local.project_number}/locations/global/workloadIdentityPools/${var.project_id}.svc.id.goog/subject/ns/${split(":", service_account)[0]}/sa/${split(":", service_account)[1]}"
	}
	workload_identity_roles=merge(
		{
			crossplane_data=[
				"roles/cloudkms.admin",
				"roles/cloudsql.admin",
				"roles/storage.admin",
			]
			crossplane_iam=[
				"roles/iam.serviceAccountAdmin",
				"roles/resourcemanager.projectIamAdmin",
			]
		},
		local.keda_enabled ? {
			keda=["roles/monitoring.viewer"]
		} : {},
		var.observability_config.enabled ? {
			grafana=["roles/monitoring.viewer"]
		} : {})
	workload_identity_service_accounts=merge(
		{
			backup_controller="crossplane-system:backup-controller"
			crossplane_data="crossplane-system:crossplane-data"
			crossplane_iam="crossplane-system:crossplane-iam"
			external_secrets="external-secrets-system:external-secrets"
		},
		local.keda_enabled ? {
			keda="${var.keda_config.namespace}:${var.keda_config.service_account_name}"
		} : {},
		var.observability_config.enabled ? {
			grafana="observability:grafana"
		} : {})
}