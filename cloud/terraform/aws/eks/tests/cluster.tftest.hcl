mock_provider "aws" {}
mock_provider "kubernetes" {}
mock_provider "time" {}
override_data {
	target=data.aws_availability_zones.available
	values={
		names=["us-east-1a", "us-east-1b", "us-east-1c"]
	}
}
override_data {
	target=data.aws_caller_identity.current
	values={
		account_id="123456789012"
	}
}
override_module {
	outputs={
		cluster_addons={}
		cluster_certificate_authority_data="dGVzdA=="
		cluster_endpoint="https://example.eks.amazonaws.com"
		cluster_name="liferay-test-eks"
		cluster_primary_security_group_id="sg-0123456789abcdef0"
		cluster_version="1.31"
		oidc_provider="oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE"
		oidc_provider_arn="arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE"
	}
	target=module.eks
}
override_module {
	outputs={
		private_subnets=["subnet-aaa", "subnet-bbb"]
		vpc_id="vpc-0123456789abcdef0"
	}
	target=module.vpc
}
run "should_accept_deployment_name_with_3_characters" {
	assert {
		condition=local.cluster_name == "abc-eks"
		error_message="The deployment_name with 3 lowercase characters must be accepted"
	}
	command=plan
	variables {
		deployment_name="abc"
	}
}
run "should_derive_cluster_name_from_deployment_name" {
	assert {
		condition=local.cluster_name == "liferay-test-eks"
		error_message="The local.cluster_name must be \"<deployment_name>-eks\""
	}
	command=plan
}
run "should_not_accept_deployment_name_longer_than_24_characters" {
	command=plan
	expect_failures=[var.deployment_name]
	variables {
		deployment_name="abcdefghijklmnopqrstuvwxy"
	}
}
run "should_not_accept_deployment_name_shorter_than_3_characters" {
	command=plan
	expect_failures=[var.deployment_name]
	variables {
		deployment_name="ab"
	}
}
run "should_not_accept_deployment_name_starting_with_a_digit" {
	command=plan
	expect_failures=[var.deployment_name]
	variables {
		deployment_name="1iferay-test"
	}
}
run "should_not_accept_uppercase_deployment_name" {
	command=plan
	expect_failures=[var.deployment_name]
	variables {
		deployment_name="Liferay-Test"
	}
}
variables {
	deployment_name="liferay-test"
	region="us-east-1"
}