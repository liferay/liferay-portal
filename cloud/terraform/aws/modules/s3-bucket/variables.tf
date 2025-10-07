variable "deployment_name" {
	default="liferay-self-hosted"
}
variable "tags" {
	default={}
	type=map(string)
}