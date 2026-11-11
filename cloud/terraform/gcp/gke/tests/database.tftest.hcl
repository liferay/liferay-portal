mock_provider "google" {}
mock_provider "google-beta" {}
mock_provider "null" {}
mock_provider "time" {}
override_data {
	target=data.google_compute_zones.available
	values={
		names=["us-central1-a", "us-central1-b", "us-central1-c"]
	}
}
override_data {
	target=data.google_netblock_ip_ranges.health_checkers
	values={
		cidr_blocks_ipv4=["35.191.0.0/16"]
	}
}
override_data {
	target=data.google_netblock_ip_ranges.legacy_health_checkers
	values={
		cidr_blocks_ipv4=["130.211.0.0/22"]
	}
}
override_data {
	target=data.google_project.project
	values={
		number="1234567890"
	}
}
run "setup_database_engine" {
	command=apply
	plan_options {
		target=[
			terraform_data.database_engine,
		]
	}
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
	project_id="liferay-test-project"
	region="us-central1"
}