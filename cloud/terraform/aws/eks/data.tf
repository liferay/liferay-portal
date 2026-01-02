data "aws_availability_zones" "available" {
	filter {
		name="opt-in-status"
		values=["opt-in-not-required"]
	}
}
data "aws_caller_identity" "current" {
}
data "aws_eks_addon_version" "s3_csi" {
  addon_name="aws-mountpoint-s3-csi-driver"
  kubernetes_version=module.eks.cluster_version
}
data "aws_eks_cluster_versions" "available" {
	region=var.region
	version_status="STANDARD_SUPPORT"
}