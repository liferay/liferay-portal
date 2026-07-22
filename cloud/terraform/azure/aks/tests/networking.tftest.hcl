mock_provider "azurerm" {
	source="./tests/mocks"
}
run "should_compute_subnet_cidr_from_vpc_cidr" {
	assert {
		condition=local.subnet_cidr == "10.0.0.0/20"
		error_message="The subnet CIDR must be a valid CIDR"
	}
	assert {
		condition=one(azurerm_subnet.main.address_prefixes) == "10.0.0.0/20"
		error_message="The subnet must use the computed subnet CIDR"
	}
	assert {
		condition=azurerm_subnet.main.name == "liferay-test-subnet"
		error_message="The subnet name must be derived from deployment_name"
	}
	command=plan
}
run "should_configure_cilium_overlay_network_profile" {
	assert {
		condition=azurerm_kubernetes_cluster.main.network_profile[0].network_plugin == "azure"
		error_message="The network profile must use the azure network plugin"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.network_profile[0].network_plugin_mode == "overlay"
		error_message="The network profile must use overlay mode"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.network_profile[0].network_policy == "cilium"
		error_message="The network profile must enforce the cilium network policy"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.network_profile[0].network_data_plane == "cilium"
		error_message="The network profile must use the cilium data plane"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.network_profile[0].outbound_type == "userAssignedNATGateway"
		error_message="The network profile must route egress through the user-assigned NAT gateway"
	}
	command=plan
}
run "should_create_network_security_group" {
	assert {
		condition=azurerm_network_security_group.main.name == "liferay-test-nsg"
		error_message="The network security group name must be derived from deployment_name"
	}
	command=plan
}
run "should_create_vnet_with_default_cidr" {
	assert {
		condition=one(azurerm_virtual_network.main.address_space) == "10.0.0.0/16"
		error_message="The virtual network must default to the 10.0.0.0/16 address space"
	}
	assert {
		condition=azurerm_virtual_network.main.name == "liferay-test-vnet"
		error_message="The virtual network name must be derived from deployment_name"
	}
	command=plan
}
run "should_expose_one_private_subnet" {
	assert {
		condition=length(output.private_subnet_ids) == 1
		error_message="The module must expose exactly one private subnet"
	}
	command=plan
}
run "should_override_pod_and_service_cidrs" {
	assert {
		condition=azurerm_kubernetes_cluster.main.network_profile[0].pod_cidr == "10.10.0.0/16"
		error_message="A custom pod_cidr must drive the network profile pod CIDR"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.network_profile[0].service_cidr == "10.20.0.0/16"
		error_message="A custom service_cidr must drive the network profile service CIDR"
	}
	command=plan
	variables {
		pod_cidr="10.10.0.0/16"
		service_cidr="10.20.0.0/16"
	}
}
run "should_provision_nat_gateway_for_egress" {
	assert {
		condition=azurerm_nat_gateway.main.name == "liferay-test-nat"
		error_message="The NAT gateway name must be derived from deployment_name"
	}
	assert {
		condition=azurerm_nat_gateway.main.sku_name == "Standard"
		error_message="The NAT gateway must use the Standard SKU"
	}
	assert {
		condition=azurerm_public_ip.nat.allocation_method == "Static"
		error_message="The NAT public IP must use static allocation"
	}
	assert {
		condition=azurerm_public_ip.nat.name == "liferay-test-nat-ip"
		error_message="The NAT public IP name must be derived from deployment_name"
	}
	assert {
		condition=azurerm_public_ip.nat.sku == "Standard"
		error_message="The NAT public IP must use the Standard SKU"
	}
	command=plan
}
run "should_shift_subnet_cidr_when_vpc_cidr_changes" {
	assert {
		condition=local.subnet_cidr == "172.16.0.0/20"
		error_message="A custom vpc_cidr must drive the computed subnet CIDR"
	}
	assert {
		condition=one(azurerm_virtual_network.main.address_space) == "172.16.0.0/16"
		error_message="A custom vpc_cidr must drive the virtual network address space"
	}
	command=plan
	variables {
		vpc_cidr="172.16.0.0/16"
	}
}
run "should_use_default_pod_and_service_cidrs" {
	assert {
		condition=azurerm_kubernetes_cluster.main.network_profile[0].pod_cidr == "10.244.0.0/16"
		error_message="The pod CIDR must default to 10.244.0.0/16"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.network_profile[0].service_cidr == "10.245.0.0/16"
		error_message="The service CIDR must default to 10.245.0.0/16"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.network_profile[0].dns_service_ip == "10.245.0.10"
		error_message="The DNS service IP must default to 10.245.0.10"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	region="eastus"
}