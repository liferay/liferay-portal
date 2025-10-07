module "s3_bucket" {
	block_public_acls=true
	block_public_policy=true
	bucket_prefix="${var.deployment_name}-s3-bucket-"
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
	source="terraform-aws-modules/s3-bucket/aws"
	tags=merge(
		{
			Backup="true"
		},
		var.tags)
	version="~> 4.1.1"
	versioning={
		enabled=true
	}
}