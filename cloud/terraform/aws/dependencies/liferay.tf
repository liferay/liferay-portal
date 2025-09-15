locals {
	bucket_active=local.is_active_data_blue ? module.s3_bucket_blue : module.s3_bucket_green
	bucket_inactive=local.is_active_data_blue ? module.s3_bucket_green : module.s3_bucket_blue
	data_inactive=local.is_active_data_blue ? "green" : "blue"
	db_active=local.is_active_data_blue ? module.postgres_blue[0] : module.postgres_green[0]
	is_active_data_blue=var.data_active=="blue"
	is_active_data_green=var.data_active=="green"
	oidc_provider=replace(data.aws_eks_cluster.cluster.identity[0].oidc[0].issuer, "https://", "")
	oidc_provider_arn="arn:aws:iam::${data.aws_caller_identity.current.account_id}:oidc-provider/${local.oidc_provider}"
	vpc_config=data.aws_eks_cluster.cluster.vpc_config[0]
}
module "postgres_blue" {
	count=local.is_active_data_blue || var.is_restoring ? 1 : 0
	db_subnet_group_name=aws_db_subnet_group.rds.name
	identifier="${var.deployment_name}-postgres-db-blue"
	password=random_password.postgres_password.result
	snapshot_identifier=var.is_restoring && local.is_active_data_green ? var.db_restore_snapshot_identifier : null
	tags={
		"Active"=tostring(local.is_active_data_blue)
	}
	source="../modules/db-instance"
	username=random_string.postgres_username.result
	vpc_security_group_ids=[aws_security_group.rds.id]
}
module "postgres_green" {
	count=local.is_active_data_green || var.is_restoring ? 1 : 0
	db_subnet_group_name=aws_db_subnet_group.rds.name
	identifier="${var.deployment_name}-postgres-db-green"
	password=random_password.postgres_password.result
	snapshot_identifier=var.is_restoring && local.is_active_data_blue ? var.db_restore_snapshot_identifier : null
	tags={
		"Active"=tostring(local.is_active_data_green)
	}
	source="../modules/db-instance"
	username=random_string.postgres_username.result
	vpc_security_group_ids=[aws_security_group.rds.id]
}
module "s3_bucket_blue" {
	deployment_name=var.deployment_name
	tags={
		"Active"=tostring(local.is_active_data_blue)
	}
	source="../modules/s3-bucket"
}
module "s3_bucket_green" {
	deployment_name=var.deployment_name
	tags={
		"Active"=tostring(local.is_active_data_green)
	}
	source="../modules/s3-bucket"
}
resource "aws_db_subnet_group" "rds" {
	name="${var.deployment_name}-rds-sub-grp"
	subnet_ids=var.private_subnet_ids
}
resource "aws_iam_policy" "s3" {
	name="${var.deployment_name}-s3-policy"
	policy=jsonencode(
		{
			Statement=[
				{
					Action=[
						"s3:DeleteObject",
						"s3:GetObject",
						"s3:ListBucket",
						"s3:PutObject"
					]
					Effect="Allow"
					Resource=[
						module.s3_bucket_blue.s3_bucket_arn,
						"${module.s3_bucket_blue.s3_bucket_arn}/*",
						module.s3_bucket_green.s3_bucket_arn,
						"${module.s3_bucket_green.s3_bucket_arn}/*"
					]
					Sid="AllowObjectOperations"
				}
			]
			Version="2012-10-17"
		})
}
resource "aws_iam_role" "liferay" {
	assume_role_policy=jsonencode(
		{
			Statement=[
				{
					Action="sts:AssumeRoleWithWebIdentity"
					Condition={
						StringEquals={
							"${local.oidc_provider}:sub" : "system:serviceaccount:${var.deployment_namespace}:liferay-default"
						}
					}
					Effect="Allow"
					Principal={
						Federated=local.oidc_provider_arn
					}
				}
			]
			Version="2012-10-17"
		})
	name="${var.deployment_name}-irsa"
}
resource "aws_iam_role_policy_attachment" "s3" {
	policy_arn=aws_iam_policy.s3.arn
	role=aws_iam_role.liferay.name
}
resource "aws_opensearch_domain" "os" {
	access_policies=<<POLICY
{
	"Statement": [
		{
			"Action": "es:*",
			"Effect": "Allow",
			"Principal": {
				"AWS": "*"
			},
			"Resource": "arn:aws:es:${var.region}:${data.aws_caller_identity.current.account_id}:domain/${var.deployment_name}-os-d/*"
		}
	],
	"Version": "2012-10-17"
}
POLICY
	advanced_options={
		"rest.action.multi.allow_explicit_index"="true"
	}
	advanced_security_options {
		enabled=true
		internal_user_database_enabled=true
		master_user_options {
			master_user_name=random_string.opensearch_username.result
			master_user_password=random_password.opensearch_password.result
		}
	}
	cluster_config {
		instance_count=2
		instance_type="t3.small.search"
		zone_awareness_config {
			availability_zone_count=2
		}
		zone_awareness_enabled=true
	}
	domain_endpoint_options {
		enforce_https=true
		tls_security_policy="Policy-Min-TLS-1-2-2019-07"
	}
	domain_name="${var.deployment_name}-os-d"
	ebs_options {
		ebs_enabled=true
		volume_size=20
		volume_type="gp2"
	}
	encrypt_at_rest {
		enabled=true
	}
	engine_version="OpenSearch_2.17"
	node_to_node_encryption {
		enabled=true
	}
	tags={
		Name="${var.deployment_name}-os-d"
	}
	vpc_options {
		security_group_ids=[aws_security_group.os.id]
		subnet_ids=slice(var.private_subnet_ids, 0, 2)
	}
}
resource "aws_security_group" "os" {
	name="${var.deployment_name}-os-sg"
	tags={
		Name="${var.deployment_name}-os-sg"
	}
	vpc_id=local.vpc_config.vpc_id
}
resource "aws_security_group" "rds" {
	name="${var.deployment_name}-rds-sg"
	tags={
		Name="${var.deployment_name}-rds-sg"
	}
	vpc_id=local.vpc_config.vpc_id
}
resource "aws_vpc_security_group_ingress_rule" "os_ingress" {
	cidr_ipv4=data.aws_vpc.current.cidr_block
	from_port=443
	ip_protocol="tcp"
	security_group_id=aws_security_group.os.id
	to_port=443
}
resource "aws_vpc_security_group_ingress_rule" "rds_ingress" {
	cidr_ipv4=data.aws_vpc.current.cidr_block
	from_port=5432
	ip_protocol="tcp"
	security_group_id=aws_security_group.rds.id
	to_port=5432
}
resource "kubernetes_namespace" "liferay" {
	metadata {
		name=var.deployment_namespace
	}
}
resource "kubernetes_secret" "managed_service_details" {
	data={
		"DATABASE_ENDPOINT"=local.db_active.address
		"DATABASE_PASSWORD"=random_password.postgres_password.result
		"DATABASE_PORT"=local.db_active.port
		"DATABASE_USERNAME"=random_string.postgres_username.result
		"OPENSEARCH_ENDPOINT"=aws_opensearch_domain.os.endpoint
		"OPENSEARCH_PASSWORD"=random_password.opensearch_password.result
		"OPENSEARCH_USERNAME"=random_string.opensearch_username.result
		"S3_BUCKET_ID"=local.bucket_active.s3_bucket_id
		"S3_BUCKET_REGION"=var.region
	}
	metadata {
		name="managed-service-details"
		namespace=kubernetes_namespace.liferay.metadata[0].name
	}
	type="Opaque"
}