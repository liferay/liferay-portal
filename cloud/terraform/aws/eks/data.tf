data "aws_availability_zones" "available" {
	filter {
		name="opt-in-status"
		values=["opt-in-not-required"]
	}
}
data "aws_caller_identity" "current" {
}
data "aws_eks_cluster_versions" "available" {
	region=var.region
	version_status="STANDARD_SUPPORT"
}