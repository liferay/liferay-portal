resource "aws_efs_file_system" "this" {
	creation_token="${var.deployment_name}-lpkg"
	encrypted=true
	tags={
		Name="${var.deployment_name}-lpkg"
	}
}
resource "aws_efs_mount_target" "this" {
	file_system_id=aws_efs_file_system.this.id
	for_each=toset(module.vpc.private_subnets)
	security_groups=[aws_security_group.efs.id]
	subnet_id=each.value
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
