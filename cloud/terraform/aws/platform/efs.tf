resource "aws_efs_access_point" "marketplace" {
	file_system_id=data.aws_efs_file_system.marketplace.id
	posix_user {
		gid=local.marketplace_posix_id
		uid=local.marketplace_posix_id
	}
	root_directory {
		creation_info {
			owner_gid=local.marketplace_posix_id
			owner_uid=local.marketplace_posix_id
			permissions="0755"
		}
		path="/marketplace"
	}
	tags={
		Name="${var.deployment_name}-marketplace"
	}
}