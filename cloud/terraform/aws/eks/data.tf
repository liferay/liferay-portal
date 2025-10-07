data "aws_availability_zones" "available" {
	filter {
		name="opt-in-status"
		values=["opt-in-not-required"]
	}
}