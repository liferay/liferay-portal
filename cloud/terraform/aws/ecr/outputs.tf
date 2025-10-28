output "deployment_name" {
	value=var.deployment_name
}
output "ecr_repositories" {
	value={ for k, v in aws_ecr_repository.this : k => {
		arn = v.arn
		url = v.repository_url
	} }
}
output "region" {
	value=var.region
}