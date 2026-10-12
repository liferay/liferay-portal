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
run "should_create_the_ebs_csi_driver_role_and_policy" {
	assert {
		condition=aws_iam_role.ebs_csi_driver.name == "liferay-test-ebs_csi_driver"
		error_message="The EBS CSI driver role name must be derived from deployment_name"
	}
	assert {
		condition=endswith(aws_iam_role_policy_attachment.role_policy_attachment_ebs_csi_driver.policy_arn, "AmazonEBSCSIDriverPolicy")
		error_message="The EBS CSI driver role must attach the AmazonEBSCSIDriverPolicy"
	}
	assert {
		condition=jsondecode(aws_iam_role.ebs_csi_driver.assume_role_policy).Statement[0].Condition.StringEquals["oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE:sub"] == "system:serviceaccount:kube-system:ebs-csi-controller-sa"
		error_message="The EBS CSI driver trust policy must scope to the ebs-csi-controller-sa service account"
	}
	command=plan
}
run "should_create_the_gp3_default_storage_class" {
	assert {
		condition=kubernetes_storage_class_v1.gp3_storage_class.metadata[0].annotations["storageclass.kubernetes.io/is-default-class"] == "true"
		error_message="The gp3 storage class must be marked as the default class"
	}
	assert {
		condition=kubernetes_storage_class_v1.gp3_storage_class.parameters["encrypted"] == "true" && kubernetes_storage_class_v1.gp3_storage_class.parameters["type"] == "gp3"
		error_message="The gp3 storage class must provision encrypted gp3 volumes"
	}
	assert {
		condition=kubernetes_storage_class_v1.gp3_storage_class.volume_binding_mode == "WaitForFirstConsumer"
		error_message="The gp3 storage class must bind volumes on the first consumer"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	region="us-east-1"
}