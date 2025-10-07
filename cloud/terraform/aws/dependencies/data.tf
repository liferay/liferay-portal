data "aws_caller_identity" "current" {
}
data "aws_eks_cluster" "cluster" {
	name=var.cluster_name
}
data "aws_eks_cluster_auth" "cluster_auth" {
	name=var.cluster_name
}
data "aws_vpc" "current" {
	id=local.vpc_config.vpc_id
}