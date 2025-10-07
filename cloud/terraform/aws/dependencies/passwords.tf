resource "random_password" "opensearch_password" {
	length=16
	min_lower=1
	min_numeric=1
	min_special=1
	min_upper=1
	override_special="!#%&*()-_=+[]{}<>:?"
	special=true
}
resource "random_password" "postgres_password" {
	length=16
	override_special="!#%&*()-_=+[]{}<>:?"
	special=true
}
resource "random_string" "opensearch_username" {
	length=16
	lower=true
	numeric=false
	special=false
	upper=false
}
resource "random_string" "postgres_username" {
	length=16
	lower=true
	numeric=false
	special=false
	upper=false
}