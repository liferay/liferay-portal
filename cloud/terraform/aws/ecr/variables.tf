variable "deployment_name" {
	default="liferay-self-hosted"
}
variable "ecr_repository_names" {
	default=[
		"liferay/dxp"
	]
	type=list(string)
}
variable "region" {
	type=string
}