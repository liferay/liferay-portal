resource "kubernetes_cluster_role" "eso_secret_writer" {
	metadata {
		labels=merge(
			local.common_labels,
			{
				"app.kubernetes.io/name"="eso-secret-writer"
			})
		name="eso-cluster-secret-writer"
	}
	rule {
		api_groups=[""]
		resources=["secrets"]
		verbs=[
			"create",
			"delete",
			"get",
			"list",
			"patch",
			"update",
			"watch",
		]
	}
}
resource "kubernetes_cluster_role_binding" "eso_secret_writer_binding" {
	metadata {
		labels=merge(
			local.common_labels,
			{
				"app.kubernetes.io/name"="eso-secret-writer-binding"
			})
		name="eso-cluster-secret-writer-binding"
	}
	role_ref {
		api_group="rbac.authorization.k8s.io"
		kind="ClusterRole"
		name=kubernetes_cluster_role.eso_secret_writer.metadata[0].name
	}
	subject {
		kind="ServiceAccount"
		name="external-secrets"
		namespace=var.external_secrets_namespace
	}
}
resource "kubernetes_manifest" "secret_store" {
	field_manager {
		force_conflicts=true
		name=local.terraform_manager_name
	}
	manifest={
		apiVersion="external-secrets.io/v1"
		kind="ClusterSecretStore"
		metadata={
			labels=merge(
				local.common_labels,
				{
					"app.kubernetes.io/name"=local.secret_store_name
				})
			name=local.secret_store_name
		}
		spec={
			provider=yamldecode(
				jsonencode(local.secret_store_provider_hcl))
		}
	}
}