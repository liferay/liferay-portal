resource "aws_ecr_repository" "this" {
	for_each=toset(var.ecr_repository_names)
	force_delete=true
	image_scanning_configuration {
		scan_on_push=true
	}
	image_tag_mutability="IMMUTABLE"
	name=each.value
}