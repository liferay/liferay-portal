locals {
	bucket_active=local.is_active_data_blue ? module.s3_bucket_blue : module.s3_bucket_green
	bucket_inactive=local.is_active_data_blue ? module.s3_bucket_green : module.s3_bucket_blue
	bucket_overlay=module.s3_bucket_liferay_overlay
	data_inactive=local.is_active_data_blue ? "green" : "blue"
	db_active=local.is_active_data_blue ? module.postgres_blue[0] : module.postgres_green[0]
	is_active_data_blue=var.data_active=="blue"
	is_active_data_green=var.data_active=="green"
	vpc_config=data.aws_eks_cluster.cluster.vpc_config[0]
}
module "postgres_blue" {
	arn_partition=var.arn_partition
	count=local.is_active_data_blue || var.is_restoring ? 1 : 0
	db_subnet_group_name=aws_db_subnet_group.rds.name
	identifier="${var.deployment_name}-postgres-db-blue"
	password=random_password.postgres_password.result
	snapshot_identifier=var.is_restoring && local.is_active_data_green ? var.db_restore_snapshot_identifier : null
	source="../modules/db-instance"
	tags={
		"Active"=tostring(local.is_active_data_blue)
	}
	username=random_string.postgres_username.result
	vpc_security_group_ids=[aws_security_group.rds.id]
}
module "postgres_green" {
	arn_partition=var.arn_partition
	count=local.is_active_data_green || var.is_restoring ? 1 : 0
	db_subnet_group_name=aws_db_subnet_group.rds.name
	identifier="${var.deployment_name}-postgres-db-green"
	password=random_password.postgres_password.result
	snapshot_identifier=var.is_restoring && local.is_active_data_blue ? var.db_restore_snapshot_identifier : null
	source="../modules/db-instance"
	tags={
		"Active"=tostring(local.is_active_data_green)
	}
	username=random_string.postgres_username.result
	vpc_security_group_ids=[aws_security_group.rds.id]
}
module "s3_bucket_blue" {
	deployment_name=var.deployment_name
	source="../modules/s3-bucket"
	tags={
		"Active"=tostring(local.is_active_data_blue)
	}
}
module "s3_bucket_green" {
	deployment_name=var.deployment_name
	source="../modules/s3-bucket"
	tags={
		"Active"=tostring(local.is_active_data_green)
	}
}
module "s3_bucket_liferay_overlay" {
	block_public_acls=true
	block_public_policy=true
	bucket_prefix="${var.deployment_name}-overlay-"
	control_object_ownership=true
	force_destroy=true
	ignore_public_acls=true
	object_ownership="BucketOwnerPreferred"
	restrict_public_buckets=true
	server_side_encryption_configuration={
		rule={
			apply_server_side_encryption_by_default={
				sse_algorithm="aws:kms"
			}
			bucket_key_enabled=true
		}
	}
	source="git::https://github.com/terraform-aws-modules/terraform-aws-s3-bucket.git?ref=cd61253a03de4f99c77a8e45146bd65a55ab103e"
	versioning={
		enabled=true
	}
}
resource "aws_cloudwatch_log_group" "opensearch_application" {
	name="/aws/opensearch/${var.deployment_name}-os-d/application"
	retention_in_days=365
	tags={
		Name="${var.deployment_name}-opensearch-application"
	}
}
resource "aws_cloudwatch_log_group" "opensearch_audit" {
	name="/aws/opensearch/${var.deployment_name}-os-d/audit"
	retention_in_days=365
	tags={
		Name="${var.deployment_name}-opensearch-audit"
	}
}
resource "aws_cloudwatch_log_group" "opensearch_index_slow" {
	name="/aws/opensearch/${var.deployment_name}-os-d/index-slow"
	retention_in_days=365
	tags={
		Name="${var.deployment_name}-opensearch-index-slow"
	}
}
resource "aws_cloudwatch_log_group" "opensearch_search_slow" {
	name="/aws/opensearch/${var.deployment_name}-os-d/search-slow"
	retention_in_days=365
	tags={
		Name="${var.deployment_name}-opensearch-search-slow"
	}
}
resource "aws_cloudwatch_log_resource_policy" "opensearch" {
	policy_document=jsonencode(
		{
			Statement=[
				{
					Action=[
						"logs:CreateLogStream",
						"logs:PutLogEvents",
						"logs:PutLogEventsBatch"
					]
					Effect="Allow"
					Principal={
						Service="es.amazonaws.com"
					}
					Resource="arn:${var.arn_partition}:logs:${var.region}:${data.aws_caller_identity.current.account_id}:log-group:/aws/opensearch/${var.deployment_name}-os-d/*"
				}
			]
			Version="2012-10-17"
		})
	policy_name="${var.deployment_name}-opensearch-logs"
}
resource "aws_db_subnet_group" "rds" {
	name="${var.deployment_name}-rds-sub-grp"
	subnet_ids=var.private_subnet_ids
}
resource "aws_iam_access_key" "ci_uploader" {
	user=aws_iam_user.ci_uploader.name
}
resource "aws_iam_policy" "ci_upload_only" {
	name="${var.deployment_name}-ci_upload_only"
	policy=jsonencode(
		{
			Statement=[
				{
					Action="s3:ListBucket",
					Effect="Allow",
					Resource=module.s3_bucket_liferay_overlay.s3_bucket_arn
					Sid="AllowListBucket",
				},
				{
					Action="s3:PutObject",
					Effect="Allow",
					Resource="${module.s3_bucket_liferay_overlay.s3_bucket_arn}/*"
					Sid="AllowPutObject",
				},
				{
					Action=[
						"s3:DeleteObject",
						"s3:DeleteObjectVersion"
					],
					Effect="Deny",
					Resource="${module.s3_bucket_liferay_overlay.s3_bucket_arn}/*"
					Sid="DenyDelete",
				}
			]
			Version="2012-10-17",
		}
	)
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
resource "aws_iam_role_policy_attachment" "s3" {
	policy_arn=aws_iam_policy.s3.arn
	role=var.liferay_sa_role_name
}
resource "aws_iam_user" "ci_uploader" {
	name="${var.deployment_name}-ci_uploader"
}
resource "aws_iam_user_policy_attachment" "ci_uploader_attachment" {
	policy_arn=aws_iam_policy.ci_upload_only.arn
	user=aws_iam_user.ci_uploader.name
}
resource "aws_kms_alias" "os_kms_alias" {
	depends_on=[aws_kms_key.os]
	name="alias/${var.deployment_name}-os-d_kms"
	target_key_id=aws_kms_key.os.key_id
}
resource "aws_kms_key" "os" {
	deletion_window_in_days=7
	description="KMS key for OpenSearch domain encryption"
	enable_key_rotation=true
	policy=jsonencode(
		{
			Statement=[
				{
					Action="kms:*"
					Effect="Allow"
					Principal={
						AWS="arn:${var.arn_partition}:iam::${data.aws_caller_identity.current.account_id}:root"
					}
					Resource="*"
					Sid="EnableIAMUserPermissions"
				},
				{
					Action=[
						"kms:CreateGrant",
						"kms:Decrypt",
						"kms:DescribeKey",
						"kms:Encrypt",
						"kms:GenerateDataKey*",
						"kms:ReEncrypt*",
					]
					Effect="Allow"
					Principal={
						Service="es.amazonaws.com"
					}
					Resource="*"
					Sid="KMSAllowOpenSearch"
				},
			]
			Version="2012-10-17"
		})
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
			"Resource": "arn:${var.arn_partition}:es:${var.region}:${data.aws_caller_identity.current.account_id}:domain/${var.deployment_name}-os-d/*"
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
		kms_key_id=aws_kms_key.os.arn
	}
	engine_version="OpenSearch_2.17"
	log_publishing_options {
		cloudwatch_log_group_arn=aws_cloudwatch_log_group.opensearch_audit.arn
		enabled=true
		log_type="AUDIT_LOGS"
	}
	log_publishing_options {
		cloudwatch_log_group_arn=aws_cloudwatch_log_group.opensearch_application.arn
		enabled=true
		log_type="ES_APPLICATION_LOGS"
	}
	log_publishing_options {
		cloudwatch_log_group_arn=aws_cloudwatch_log_group.opensearch_index_slow.arn
		enabled=true
		log_type="INDEX_SLOW_LOGS"
	}
	log_publishing_options {
		cloudwatch_log_group_arn=aws_cloudwatch_log_group.opensearch_search_slow.arn
		enabled=true
		log_type="SEARCH_SLOW_LOGS"
	}
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
		"S3_BUCKET_ID"=local.bucket_active.s3_bucket_id
		"S3_BUCKET_REGION"=var.region
	}
	metadata {
		name="managed-service-details"
		namespace=kubernetes_namespace.liferay.metadata[0].name
	}
	type="Opaque"
}
resource "kubernetes_secret" "search_connection_details" {
	data={
		"SEARCH_PASSWORD"=random_password.opensearch_password.result
		"SEARCH_URL"="https://${aws_opensearch_domain.os.endpoint}:443"
		"SEARCH_USERNAME"=random_string.opensearch_username.result
	}
	metadata {
		name="search-connection-details"
		namespace=kubernetes_namespace.liferay.metadata[0].name
	}
	type="Opaque"
}
resource "kubernetes_storage_class" "liferay_overlay_storage" {
	allow_volume_expansion=false
	depends_on=[
		module.s3_bucket_liferay_overlay
	]
	metadata {
		name=module.s3_bucket_liferay_overlay.s3_bucket_id
	}
	parameters={
		"bucketName"=module.s3_bucket_liferay_overlay.s3_bucket_id
	}
	storage_provisioner="s3.csi.aws.com"
	volume_binding_mode="WaitForFirstConsumer"
}
resource "null_resource" "opensearch_service_role" {
	provisioner "local-exec" {
		command=<<-EOT
			aws \
				iam \
				create-service-linked-role \
				--aws-service-name opensearchservice.amazonaws.com \
				--region "${var.region}" \
				|| true
		EOT
	}
	triggers={
		service_name="opensearchservice.amazonaws.com"
	}
}