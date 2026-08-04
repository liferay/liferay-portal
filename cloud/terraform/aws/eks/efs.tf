resource "aws_efs_file_system" "this" {
	creation_token="${var.deployment_name}-lpkg"
	encrypted=true
	kms_key_id=aws_kms_key.efs.arn
	tags={
		Name="${var.deployment_name}-lpkg"
	}
}
resource "aws_efs_mount_target" "this" {
	count=length(module.vpc.private_subnets)
	file_system_id=aws_efs_file_system.this.id
	security_groups=[aws_security_group.efs.id]
	subnet_id=module.vpc.private_subnets[count.index]
}
resource "aws_kms_key" "efs" {
	deletion_window_in_days=7
	description="KMS key for EFS encryption"
	enable_key_rotation=true
	policy=jsonencode(
		{
			Statement=[
				{
					Action="kms:*"
					Effect="Allow"
					Principal={
						AWS="arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"
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
						Service="elasticfilesystem.amazonaws.com"
					}
					Resource="*"
					Sid="KMSAllowEFS"
				},
			]
			Version="2012-10-17"
		})
}
resource "aws_security_group" "efs" {
	description="Allow NFS from the cluster to the EFS mount targets"
	name="${var.deployment_name}-efs"
	vpc_id=module.vpc.vpc_id
}
resource "aws_vpc_security_group_ingress_rule" "efs_nfs_ingress" {
	cidr_ipv4=var.vpc_cidr
	from_port=2049
	ip_protocol="tcp"
	security_group_id=aws_security_group.efs.id
	to_port=2049
}
resource "kubernetes_storage_class_v1" "efs_storage_class" {
	depends_on=[time_sleep.cluster_addons_ready_time_buffer]
	metadata {
		name="efs-sc"
	}
	parameters={
		basePath="/dynamic"
		directoryPerms="755"
		ensureUniqueDirectory="false"
		fileSystemId=aws_efs_file_system.this.id
		provisioningMode="efs-ap"
	}
	reclaim_policy="Delete"
	storage_provisioner="efs.csi.aws.com"
	volume_binding_mode="Immediate"
}
