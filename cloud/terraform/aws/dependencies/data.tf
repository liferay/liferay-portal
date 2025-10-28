data "aws_caller_identity" "current" {
}
data "aws_eks_cluster" "cluster" {
	name=var.cluster_name
}
data "aws_eks_cluster_auth" "cluster_auth" {
	name=var.cluster_name
}
data "aws_iam_role" "opensearch_service_linked_role" {
	depends_on=[null_resource.opensearch_service_role]
	name="AWSServiceRoleForAmazonOpenSearchService"
}
data "aws_vpc" "current" {
	id=local.vpc_config.vpc_id
}