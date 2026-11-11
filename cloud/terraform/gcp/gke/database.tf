resource "terraform_data" "database_engine" {
	input=var.database_engine
	lifecycle {
		ignore_changes=[input]
		postcondition {
			condition=self.output == var.database_engine
			error_message="Unable to change the database engine of the deployment from \"${self.output}\" to \"${var.database_engine}\". Create a new deployment to use another database engine."
		}
	}
}