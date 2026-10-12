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
		cluster_id="liferay-test-eks"
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
run "should_harden_the_kms_key" {
	assert {
		condition=aws_kms_alias.eks_kms_alias.name == "alias/liferay-test-eks_kms"
		error_message="The KMS alias must embed local.cluster_name (\"<deployment_name>-eks\")"
	}
	assert {
		condition=aws_kms_key.eks_secrets.deletion_window_in_days == 7
		error_message="The EKS secrets KMS key must use a 7 day deletion window"
	}
	assert {
		condition=aws_kms_key.eks_secrets.enable_key_rotation == true
		error_message="The EKS secrets KMS key must have rotation enabled"
	}
	assert {
		condition=jsondecode(aws_kms_key.eks_secrets.policy).Statement[1].Principal.Service == "eks.amazonaws.com"
		error_message="The KMS key policy must grant the EKS service access"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	region="us-east-1"
}