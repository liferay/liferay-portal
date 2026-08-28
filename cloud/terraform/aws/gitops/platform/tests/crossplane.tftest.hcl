mock_provider "aws" {}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
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
	}
}
run "should_apply_crossplane_security_contexts" {
	assert {
		condition=yamldecode(helm_release.crossplane.values[0]).podSecurityContextCrossplane.runAsNonRoot == true
		error_message="The Crossplane pod security context must run as nonroot"
	}
	assert {
		condition=yamldecode(helm_release.crossplane.values[0]).securityContextCrossplane.readOnlyRootFilesystem == true
		error_message="The Crossplane container security context must use a readonly root filesystem"
	}
	assert {
		condition=contains(yamldecode(helm_release.crossplane.values[0]).securityContextCrossplane.capabilities.drop, "ALL")
		error_message="The Crossplane container security context must drop all capabilities"
	}
	command=plan
}
run "should_configure_crossplane_with_defaults" {
	assert {
		condition=helm_release.crossplane.version == var.crossplane_helm_chart_version
		error_message="The Crossplane Helm release must use the configured chart version"
	}
	assert {
		condition=helm_release.crossplane.namespace == "crossplane-system"
		error_message="The Crossplane Helm release must default to the crossplane-system namespace"
	}
	assert {
		condition=helm_release.crossplane.atomic == true && helm_release.crossplane.cleanup_on_fail == true && helm_release.crossplane.wait == true
		error_message="The Crossplane Helm release must be atomic, clean up on failure, and wait"
	}
	assert {
		condition=helm_release.crossplane.create_namespace == false
		error_message="The Crossplane Helm release must not create its namespace; Terraform manages it"
	}
	assert {
		condition=kubernetes_namespace.crossplane.metadata[0].labels["pod-security.kubernetes.io/enforce"] == "restricted"
		error_message="The Crossplane namespace must enforce the restricted Pod Security Standard"
	}
	assert {
		condition=kubernetes_namespace.crossplane.metadata[0].labels["app.kubernetes.io/managed-by"] == "liferay-cloud-native-terraform"
		error_message="The Crossplane namespace should carry the Terraform manager label from local.common_labels"
	}
	command=plan
}
variables {
	ack_prometheusservice_helm_chart_version="1.4.1"
	argo_workflows_helm_chart_version="2.0.3"
	argocd_helm_chart_version="9.5.16"
	crossplane_helm_chart_version="2.1.3"
	deployment_name="liferay-test"
	external_secrets_helm_chart_version="1.0.0"
	keda_helm_chart_version="2.19.0"
	region="us-east-1"
}