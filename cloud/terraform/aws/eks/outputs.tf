output "cluster_name" {
	value=module.eks.cluster_name
}
output "deployment_name" {
	value=var.deployment_name
}
output "private_subnet_ids" {
	value=module.vpc.private_subnets
}
output "region" {
	value=var.region
}