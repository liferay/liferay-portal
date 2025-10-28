output "arn_partition" {
	value=var.arn_partition
}
output "cluster_name" {
	value=module.eks.cluster_name
}
output "deployment_name" {
	value=var.deployment_name
}
output "deployment_namespace" {
	value=var.deployment_namespace
}
output "liferay_sa_role_arn" {
	value=aws_iam_role.irsa.arn
}
output "liferay_sa_role_name" {
	value=aws_iam_role.irsa.name
}
output "private_subnet_ids" {
	value=module.vpc.private_subnets
}
output "region" {
	value=var.region
}