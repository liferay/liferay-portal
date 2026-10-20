mock_provider "azurerm" {
	source="./tests/mocks"
}
run "setup_database_engine" {
	command=apply
}
run "should_fail_database_changed" {
	command=plan
	expect_failures=[
		terraform_data.database_engine,
	]
	variables {
		database_engine="mysql"
	}
}
run "should_pass_database_unchanged" {
	assert {
		condition=terraform_data.database_engine.output == "postgresql"
		error_message="The database engine must stay \"postgresql\" when it is unchanged"
	}
	command=plan
}
variables {
	database_engine="postgresql"
	deployment_name="liferay-test"
	region="eastus"
}