resource "aws_iam_policy" "provider_aws_backup_policy" {
	name="${local.cluster_name}-provider-aws-backup"
	policy=data.aws_iam_policy_document.provider_aws_backup_policy_document.json
}
resource "aws_iam_policy" "provider_aws_ec2_policy" {
	name="${local.cluster_name}-provider-aws-ec2"
	policy=data.aws_iam_policy_document.provider_aws_ec2_policy_document.json
}
resource "aws_iam_policy" "provider_aws_iam_policy" {
	name="${local.cluster_name}-provider-aws-iam"
	policy=data.aws_iam_policy_document.provider_aws_iam_policy_document.json
}
resource "aws_iam_policy" "provider_aws_opensearch_policy" {
	name="${local.cluster_name}-provider-aws-opensearch"
	policy=data.aws_iam_policy_document.provider_aws_opensearch_policy_document.json
}
resource "aws_iam_policy" "provider_aws_rds_policy" {
	name="${local.cluster_name}-provider-aws-rds"
	policy=data.aws_iam_policy_document.provider_aws_rds_policy_document.json
}
resource "aws_iam_policy" "provider_aws_s3_policy" {
	name="${local.cluster_name}-provider-aws-s3"
	policy=data.aws_iam_policy_document.provider_aws_s3_policy_document.json
}
resource "aws_iam_role" "provider_aws_backup_role" {
	assume_role_policy=data.aws_iam_policy_document.provider_aws_backup_assume_role_policy_document.json
	name="${local.cluster_name}-provider-aws-backup-role"
}
resource "aws_iam_role" "provider_aws_ec2_role" {
	assume_role_policy=data.aws_iam_policy_document.provider_aws_ec2_assume_role_policy_document.json
	name="${local.cluster_name}-provider-aws-ec2-role"
}
resource "aws_iam_role" "provider_aws_iam_role" {
	assume_role_policy=data.aws_iam_policy_document.provider_aws_iam_assume_role_policy_document.json
	name="${local.cluster_name}-provider-aws-iam-role"
}
resource "aws_iam_role" "provider_aws_opensearch_role" {
	assume_role_policy=data.aws_iam_policy_document.provider_aws_opensearch_assume_role_policy_document.json
	name="${local.cluster_name}-provider-aws-opensearch-role"
}
resource "aws_iam_role" "provider_aws_rds_role" {
	assume_role_policy=data.aws_iam_policy_document.provider_aws_rds_assume_role_policy_document.json
	name="${local.cluster_name}-provider-aws-rds-role"
}
resource "aws_iam_role" "provider_aws_s3_role" {
	assume_role_policy=data.aws_iam_policy_document.provider_aws_s3_assume_role_policy_document.json
	name="${local.cluster_name}-provider-aws-s3-role"
}
resource "aws_iam_role_policy_attachment" "provider_aws_backup_attachment" {
	policy_arn=aws_iam_policy.provider_aws_backup_policy.arn
	role=aws_iam_role.provider_aws_backup_role.name
}
resource "aws_iam_role_policy_attachment" "provider_aws_ec2_attachment" {
	policy_arn=aws_iam_policy.provider_aws_ec2_policy.arn
	role=aws_iam_role.provider_aws_ec2_role.name
}
resource "aws_iam_role_policy_attachment" "provider_aws_iam_attachment" {
	policy_arn=aws_iam_policy.provider_aws_iam_policy.arn
	role=aws_iam_role.provider_aws_iam_role.name
}
resource "aws_iam_role_policy_attachment" "provider_aws_opensearch_attachment" {
	policy_arn=aws_iam_policy.provider_aws_opensearch_policy.arn
	role=aws_iam_role.provider_aws_opensearch_role.name
}
resource "aws_iam_role_policy_attachment" "provider_aws_rds_attachment" {
	policy_arn=aws_iam_policy.provider_aws_rds_policy.arn
	role=aws_iam_role.provider_aws_rds_role.name
}
resource "aws_iam_role_policy_attachment" "provider_aws_s3_attachment" {
	policy_arn=aws_iam_policy.provider_aws_s3_policy.arn
	role=aws_iam_role.provider_aws_s3_role.name
}
resource "aws_iam_service_linked_role" "opensearch_linked_role" {
	aws_service_name="opensearchservice.amazonaws.com"
	count=local.should_create_opensearch_linked_role ? 1 : 0
}
resource "kubernetes_manifest" "function_auto_ready" {
	manifest={
		apiVersion="pkg.crossplane.io/v1beta1"
		kind="Function"
		metadata={
			name="function-auto-ready"
		}
		spec={
			package="xpkg.upbound.io/upbound/function-auto-ready:v0.6.0"
			runtimeConfigRef={
				name="function-auto-ready-runtime-config"
			}
		}
	}
	provider=kubernetes
}
resource "kubernetes_manifest" "function_auto_ready_runtime_config" {
	manifest={
		apiVersion="pkg.crossplane.io/v1beta1"
		kind="DeploymentRuntimeConfig"
		metadata={
			name="function-auto-ready-runtime-config"
		}
		spec={
			deploymentTemplate={
				spec={
					selector={
						matchLabels={
							"pkg.crossplane.io/function"="function-auto-ready"
						}
					}
					template={
						spec={
							containers=[
								{
									name="package-runtime"
									resources={
										limits={
											cpu="500m"
											memory="512Mi"
										}
										requests={
											cpu="20m"
											memory="128Mi"
										}
									}
								},
							]
						}
					}
				}
			}
		}
	}
	provider=kubernetes
}
resource "kubernetes_manifest" "function_go_templating" {
	manifest={
		apiVersion="pkg.crossplane.io/v1beta1"
		kind="Function"
		metadata={
			name="function-go-templating"
		}
		spec={
			package="xpkg.upbound.io/crossplane-contrib/function-go-templating:v0.11.3"
			runtimeConfigRef={
				name="function-go-templating-runtime-config"
			}
		}
	}
	provider=kubernetes
}
resource "kubernetes_manifest" "function_go_templating_runtime_config" {
	manifest={
		apiVersion="pkg.crossplane.io/v1beta1"
		kind="DeploymentRuntimeConfig"
		metadata={
			name="function-go-templating-runtime-config"
		}
		spec={
			deploymentTemplate={
				spec={
					selector={
						matchLabels={
							"pkg.crossplane.io/function"="function-go-templating"
						}
					}
					template={
						spec={
							containers=[
								{
									name="package-runtime"
									resources={
										limits={
											cpu="500m"
											memory="512Mi"
										}
										requests={
											cpu="20m"
											memory="128Mi"
										}
									}
								},
							]
						}
					}
				}
			}
		}
	}
	provider=kubernetes
}
resource "kubernetes_manifest" "function_tag_manager" {
	manifest={
		apiVersion="pkg.crossplane.io/v1beta1"
		kind="Function"
		metadata={
			name="function-tag-manager"
		}
		spec={
			package="xpkg.upbound.io/crossplane-contrib/function-tag-manager:v0.6.0"
			runtimeConfigRef={
				name="function-tag-manager-runtime-config"
			}
		}
	}
	provider=kubernetes
}
resource "kubernetes_manifest" "function_tag_manager_runtime_config" {
	manifest={
		apiVersion="pkg.crossplane.io/v1beta1"
		kind="DeploymentRuntimeConfig"
		metadata={
			name="function-tag-manager-runtime-config"
		}
		spec={
			deploymentTemplate={
				spec={
					selector={
						matchLabels={
							"pkg.crossplane.io/function"="function-tag-manager"
						}
					}
					template={
						spec={
							containers=[
								{
									name="package-runtime"
									resources={
										limits={
											cpu="500m"
											memory="512Mi"
										}
										requests={
											cpu="20m"
											memory="128Mi"
										}
									}
								},
							]
						}
					}
				}
			}
		}
	}
	provider=kubernetes
}