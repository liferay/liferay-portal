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
run "should_cap_availability_zones_at_2_by_default" {
	assert {
		condition=length(local.default_private_subnets) == 2
		error_message="The default private subnet list must follow the AZ cap"
	}
	assert {
		condition=length(local.selected_azs) == 2
		error_message="The selected_azs list must be capped at max_availability_zones (default 2) even when more AZs are available"
	}
	command=plan
}
run "should_compute_default_subnet_cidrs" {
	assert {
		condition=local.default_private_subnets == ["10.0.1.0/24", "10.0.2.0/24"]
		error_message="The default_private_subnets list must be cidrsubnet(vpc_cidr, 8, i+1) for each AZ"
	}
	assert {
		condition=local.default_public_subnets == ["10.0.101.0/24", "10.0.102.0/24"]
		error_message="The default_public_subnets list must be cidrsubnet(vpc_cidr, 8, i+101) for each AZ"
	}
	command=plan
}
run "should_extend_subnets_when_max_availability_zones_increases" {
	assert {
		condition=length(local.default_private_subnets) == 3 && local.default_private_subnets[2] == "10.0.3.0/24"
		error_message="Raising max_availability_zones must extend the computed private subnet list"
	}
	assert {
		condition=length(local.default_public_subnets) == 3 && local.default_public_subnets[2] == "10.0.103.0/24"
		error_message="Raising max_availability_zones must extend the computed public subnet list"
	}
	assert {
		condition=length(local.selected_azs) == 3
		error_message="Raising max_availability_zones must select more AZs"
	}
	command=plan
	variables {
		max_availability_zones=3
	}
}
run "should_not_accept_invalid_api_cidr" {
	command=plan
	expect_failures=[var.eks_api_additional_allowed_cidr_blocks]
	variables {
		eks_api_additional_allowed_cidr_blocks=["not-a-cidr"]
	}
}
run "should_not_accept_mixed_valid_and_invalid_api_cidrs" {
	command=plan
	expect_failures=[var.eks_api_additional_allowed_cidr_blocks]
	variables {
		eks_api_additional_allowed_cidr_blocks=["10.0.0.0/16", "bad"]
	}
}
run "should_open_api_to_all_cidrs_by_default" {
	assert {
		condition=length(local.eks_api_public_access_cidrs) == 1 && local.eks_api_public_access_cidrs[0] == "0.0.0.0/0"
		error_message="Without additional allowed CIDRs, the EKS API access list must be 0.0.0.0/0"
	}
	command=plan
}
run "should_override_default_api_cidrs_with_custom_cidrs" {
	assert {
		condition=length(local.eks_api_public_access_cidrs) == 2 && local.eks_api_public_access_cidrs[0] == "10.10.0.0/16"
		error_message="Explicit allowed CIDRs must override the 0.0.0.0/0 default"
	}
	command=plan
	variables {
		eks_api_additional_allowed_cidr_blocks=["10.10.0.0/16", "192.168.0.0/24"]
	}
}
run "should_shift_subnets_when_vpc_cidr_changes" {
	assert {
		condition=local.default_private_subnets[0] == "172.16.1.0/24"
		error_message="A custom vpc_cidr must drive the computed subnet ranges"
	}
	assert {
		condition=local.default_public_subnets[0] == "172.16.101.0/24"
		error_message="A custom vpc_cidr must drive the computed public subnet ranges"
	}
	command=plan
	variables {
		vpc_cidr="172.16.0.0/16"
	}
}
variables {
	deployment_name="liferay-test"
	region="us-east-1"
}